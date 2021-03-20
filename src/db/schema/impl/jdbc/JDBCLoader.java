/**
 * 
 */
package db.schema.impl.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import oracle.sql.DATE;
import oracle.sql.TIMESTAMP;


import db.compare.dstruct.SchemaInfo;
import db.schema.interfaces.IData;
import db.schema.interfaces.IDataProvider;

/**
 * @author Prajapati
 *
 */
public class JDBCLoader {

	public static IDataProvider getJDBCDataProvider(String fileName, boolean source) {
	
		try {
			
			JDBCLoader loader = getJDBCLoader(fileName, source);
			
			loader.load();
			
			return loader.getDataProvider();
		} catch (FileNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static JDBCLoader getJDBCLoader(String fileName, boolean source) throws FileNotFoundException, IOException {
		
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(fileName);
		props.load(fis);
		fis.close();
		JDBCLoaderSetting setting = new JDBCLoaderSetting(props, source);
		return new JDBCLoader(setting);
	}



	private JDBCLoaderSetting setting;
	private JDBCDataProvider dataProvider;
	
	private JDBCLoader(JDBCLoaderSetting setting) {
	
		this.setting = setting;
		this.dataProvider = new JDBCDataProvider();
	}
	
	public void load() {
		
		List<String> entities = this.setting.getEntities();
		Map<String, String> whereCriterias = this.setting.getWhereCriterias();
		Map<String, List<String>> pkeys = this.setting.getPKeys();
		Map<String, List<String>> excludeFields = this.setting.getExcludeFields();
		
		for(int i = 0; i < entities.size(); ++i) {
			
			loadEntity(entities.get(i), pkeys, excludeFields, whereCriterias);
		}
	}
	
	private void loadEntity(String entity, Map<String, List<String>> pkeys, Map<String, List<String>> mapExcludeFields, Map<String, String> whereCriterias) {
		
		Connection connection = setting.getConnection();
		
		if(connection != null) {
			
			try {
				
				DatabaseMetaData metaData = connection.getMetaData();
				String tableName = entity.toUpperCase();
				ResultSet tables = metaData.getTables(null, connection.getSchema(), tableName, null);
				if (!tables.next()) {
					tableName = entity.toLowerCase();
					tables = metaData.getTables(null, connection.getSchema(), tableName, null);
					if (tables.next()) {
						entity = tableName;
					}
				} else {
					entity = tableName;
				}
				List<String> keys = pkeys.get(entity);
				List<String> excludeFields = mapExcludeFields.get(entity);
				List<String> exclusionFields = 
						excludeFields != null 
							? new ArrayList<>(excludeFields) 
									: new ArrayList<>();
				ResultSet primaryKeys = metaData.getPrimaryKeys(null, getSchemaName(), entity);
				if (keys == null || keys.isEmpty()) {
					keys = getKeys(primaryKeys);
				} else {
					// because unique identified is provided and hence there is need to exlcude primary key from comparison
					exclusionFields.addAll(getKeys(primaryKeys));
				}
				primaryKeys.close();
				
				if(keys.size() == 0) {
					
					ResultSet columns = metaData.getColumns(null, getSchemaName(), entity, null);
					
					keys.addAll(getColumns(columns));
				}
				dataProvider.setKeys(entity, keys);
				
				dataProvider.setExclusionFields(entity, exclusionFields);
				
				ResultSet columns = metaData.getColumns(null, getSchemaName(), entity, null);
				
				List<String> cols = getColumns(columns);
				columns.close();
				
				dataProvider.setFieldNames(entity, cols);
				
				columns = metaData.getColumns(null, getSchemaName(), entity, null);
				
				List<SchemaInfo> schemaInfos = getSchemaInfos(columns);
				columns.close();
				
				dataProvider.setSchemaInfos(entity, schemaInfos);
				System.out.println("Entity: " + entity);
				List<IData> data = getData(connection, entity, keys, whereCriterias.get(entity));
				
				dataProvider.setData(entity, data);
				
				connection.close();
				connection = null;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				
				if(connection != null) {
					
					try {
						connection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	}

	private List<String> getKeys(ResultSet primaryKeys) throws SQLException {
		List<String> keys = new ArrayList<String>();
		
		while(primaryKeys.next()) {
			
			// TABLE_CAT, TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, KEY_SEQ, PK_NAME
			keys.add(primaryKeys.getString("COLUMN_NAME"));
		}
		//System.out.println("keys: " + keys);
		return keys;
	}

	private List<String> getColumns(ResultSet columns) throws SQLException {
		List<String> cols = new ArrayList<String>();
		
		while(columns.next()) {
			
			// TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME,
			// COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX,
			// NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB,
			// CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE
			cols.add(columns.getString("COLUMN_NAME"));
		}
		//System.out.println("cols: " + cols);
		return cols;
	}

	private List<SchemaInfo> getSchemaInfos(ResultSet columns) throws SQLException {
		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
		
		while(columns.next()) {
			
			// TABLE_SCHEM, TABLE_NAME, COLUMN_NAME, DATA_TYPE, TYPE_NAME,
			// COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX,
			// NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB,
			// CHAR_OCTET_LENGTH, ORDINAL_POSITION, IS_NULLABLE
			schemaInfos.add(new SchemaInfo(columns.getString("COLUMN_NAME"), columns.getInt("DATA_TYPE")));
		}

		return schemaInfos;
	}

	private List<IData> getData(Connection conn, String entity, List<String> keys, String whereCriteria) throws SQLException {
		
		List<IData> data = new ArrayList<IData>();
		
		String sqlQuery = buildSelectQuery(entity, keys, whereCriteria);
		
		Statement statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		
		ResultSet rsData = statement.executeQuery(sqlQuery);
		
		List<String> fieldNames = dataProvider.getFieldNames(entity);
		
		List<String> exclusionFields = dataProvider.getExclusionFields(entity);
		
		while(rsData.next()) {
			
			JDBCData jdbcData = new JDBCData(fieldNames, exclusionFields);
			
			for(int i = 0; i < fieldNames.size(); ++i) {
				
				Object dataObj = rsData.getObject(fieldNames.get(i));
				if(dataObj != null) {
					if(dataObj instanceof java.sql.Date) {
						Date date = (Date) dataObj;
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						dataObj = format.format(date);
					} else if(dataObj instanceof Timestamp) {
						Date date = (Date) dataObj;
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dataObj = format.format(date);
					} else if(dataObj instanceof TIMESTAMP) {
						Date date = (Date) ((TIMESTAMP)dataObj).timestampValue();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						dataObj = format.format(date);
					} else if(dataObj instanceof DATE) {
						Date date = (Date) ((DATE)dataObj).dateValue();
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						dataObj = format.format(date);
					} else if (dataObj instanceof Clob) {
						Clob clob = (Clob)dataObj;
						dataObj = clob.getSubString(1L, (int)clob.length());
					}
				}
				jdbcData.addData(dataObj != null ? String.valueOf(dataObj) : (String)dataObj);
			}
			data.add(jdbcData);
		}
		
		rsData.close();
		statement.close();
		
		return data;
	}

	private String buildSelectQuery(String entity, List<String> keys, String whereCriteria) {
		
		// SELECT * FROM
		StringBuffer sql = new StringBuffer("SELECT * FROM ").append(setting.getSchema()).append(".").append(entity).append(" ");
		
		// WHERE
		if(whereCriteria != null && whereCriteria.trim().length() > 0) {
			
			sql.append(" WHERE ").append(whereCriteria).append(' ');
		}
		
		// ORDER BY
		if(keys.size() > 0) {
			
			StringBuffer orderBy = new StringBuffer("ORDER BY ");
			
			for(int i = 0; i < keys.size(); ++i) {
				
				if(i > 0) {
					
					orderBy.append(", ");
				}
				orderBy.append(keys.get(i)).append(" ASC");
			}
			
			sql.append(orderBy.toString());
		}
		
		//System.out.println("sql: " + sql);
		return sql.toString();
	}

	private String getSchemaName() {

		return setting.getSchema();
	}

	public IDataProvider getDataProvider() {
		
		return this.dataProvider;
	}
}
