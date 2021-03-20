/**
 * 
 */
package db.schema.impl.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import db.compare.util.CaseInsensitiveMap;

/**
 * @author Prajapati
 *
 */
public class JDBCLoaderSetting {

	private String driver;
	private String connectionURL;
	private String username;
	private String password;
	private Properties props;
	private String propertyPrefix;

	public JDBCLoaderSetting(Properties props, boolean source) {
		this.propertyPrefix = source ? "source." : "target.";
		this.driver = props.getProperty(propertyPrefix + "driverclass");
		this.connectionURL = props.getProperty(propertyPrefix + "connectionURL");
		this.username = props.getProperty(propertyPrefix + "username");
		this.password = props.getProperty(propertyPrefix + "password");
		this.props = props;
	}
	
	public Connection getConnection() {
		
		try {
			Class.forName(this.driver);
			return DriverManager.getConnection(connectionURL, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getProperty(String property) {
		
		return this.props.getProperty(property);
	}
	
	public List<String> getEntities() {
		
		String tables = getProperty("tables");
		
		StringTokenizer tokenizer = new StringTokenizer(tables, ",");
		
		List<String> entities = new ArrayList<String>(tokenizer.countTokens());
		
		while(tokenizer.hasMoreTokens()) {
			
			String entity = tokenizer.nextToken();
			
			entities.add(entity);
		}
		
		return entities;
	}

	public Map<String,String> getWhereCriterias() {
		
		String propCriterias = getProperty("criterias");
		if(propCriterias == null) {
			return new CaseInsensitiveMap<String>();
		}
		StringTokenizer tokenizer = new StringTokenizer(propCriterias, ";");
		
		Map<String, String> criterias = new CaseInsensitiveMap<String>(tokenizer.countTokens());
		
		while(tokenizer.hasMoreTokens()) {
			
			String criteria = tokenizer.nextToken();
			int criteriaStartIdx = criteria.indexOf('[');
			int criteriaEndIdx = criteria.lastIndexOf(']');
			
			String entityName = criteria.substring(0, criteriaStartIdx).trim();
			String whereCriteria = criteria.substring(criteriaStartIdx + 1, criteriaEndIdx).trim();
			
			criterias.put(entityName, whereCriteria);
		}
		
		return criterias;
	}

	public Map<String, List<String>> getPKeys() {
		
		String propPkeys = getProperty("pkeys");
		if(propPkeys == null) {
			return new CaseInsensitiveMap<List<String>>();
		}
		StringTokenizer tokenizer = new StringTokenizer(propPkeys, ";");
		
		Map<String, List<String>> pkeys = new CaseInsensitiveMap<List<String>>(tokenizer.countTokens());
		
		while(tokenizer.hasMoreTokens()) {
			
			String pkey = tokenizer.nextToken();
			int pkeyStartIdx = pkey.indexOf('[');
			int pkeyEndIdx = pkey.lastIndexOf(']');
			
			String entityName = pkey.substring(0, pkeyStartIdx).trim();
			List<String> pkeyList = Arrays.asList(pkey.substring(pkeyStartIdx + 1, pkeyEndIdx).trim().split(","));
			
			pkeys.put(entityName, pkeyList);
		}
		
		return pkeys;
	}

	public Map<String, List<String>> getExcludeFields() {
		
		String propExcludeFields = getProperty("excludeFields");
		if(propExcludeFields == null) {
			return new CaseInsensitiveMap<List<String>>();
		}
		StringTokenizer tokenizer = new StringTokenizer(propExcludeFields, ";");
		
		Map<String, List<String>> excludeFields = new CaseInsensitiveMap<List<String>>(tokenizer.countTokens());
		
		while(tokenizer.hasMoreTokens()) {
			
			String excludeField = tokenizer.nextToken();
			int pkeyStartIdx = excludeField.indexOf('[');
			int pkeyEndIdx = excludeField.lastIndexOf(']');
			
			String entityName = excludeField.substring(0, pkeyStartIdx).trim();
			List<String> excudeFieldList = Arrays.asList(excludeField.substring(pkeyStartIdx + 1, pkeyEndIdx).trim().split(","));
			
			excludeFields.put(entityName, excudeFieldList);
		}
		
		return excludeFields;
	}
	
	public String getSchema() {
		
		return getProperty(propertyPrefix + "schema");
	}
}
