/**
 * 
 */
package db.compare;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import db.schema.impl.jdbc.JDBCLoaderSetting;

/**
 * @author Prajapati
 *
 */
public class MetaDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * 		this.propertyPrefix = source ? "source." : "target.";
		 * this.driver = props.getProperty(propertyPrefix + "driverclass");
		 * this.connectionURL = props.getProperty(propertyPrefix + "connectionURL");
		 * this.username = props.getProperty(propertyPrefix + "username");
		 * this.password = props.getProperty(propertyPrefix + "password");

		 */
		
		String testEntity = "examin_static_reference";
		Properties props = new Properties();
		// ORACLE
		props.setProperty("source.driverclass", "oracle.jdbc.driver.OracleDriver");
		props.setProperty("source.connectionURL", "jdbc:oracle:thin:@192.168.109.98:1521:FEMS001");
		props.setProperty("source.username", "examin");
		props.setProperty("source.password", "examin");
		props.setProperty("source.schema", "EXAMIN");
		printConnectionAndMetadataForEntity(props, testEntity);
		
		// POSTGRE
		props.setProperty("source.driverclass", "org.postgresql.Driver");
		props.setProperty("source.connectionURL", "jdbc:postgresql://192.168.226.52:5434/examin");
		props.setProperty("source.username", "examin");
		props.setProperty("source.password", "examin22652");
		props.setProperty("source.schema", "examin");
		printConnectionAndMetadataForEntity(props, testEntity);
	}

	/**
	 * @param props
	 * @param testEntity
	 */
	private static void printConnectionAndMetadataForEntity(Properties props, String testEntity) {
		JDBCLoaderSetting settings = new JDBCLoaderSetting(props, true);
		try (Connection connection = settings.getConnection()) {
			printConnectionInformation(connection);
			printConnectionMetadata(connection, testEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	private static void printConnectionMetadata(Connection connection, String entity) throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
		StringBuilder sb = new StringBuilder();
		
		sb
		.append("Name: ").append(metaData.getDatabaseProductName()).append(" (").append(metaData.getDatabaseProductVersion()).append("\n")
		.append("Version: ").append(metaData.getDatabaseMajorVersion()).append(".").append(metaData.getDatabaseMinorVersion()).append("\n")
		.append("Catalog terminology: ").append(metaData.getCatalogTerm()).append("\n")
		.append("Schema terminology: ").append(metaData.getSchemaTerm()).append("\n")
		.append("EXAMIN_STATIC_REFERENCE: \n");
		ResultSet columns = metaData.getColumns(null, connection.getSchema(), entity.toUpperCase(), null);
		while (columns.next()) {
			sb.append("Name: " + columns.getString("COLUMN_NAME")).append(", Data type: " + columns.getInt("DATA_TYPE")).append("\n");
		}
		sb.append("examin_static_reference: \n");
		columns = metaData.getColumns(null, connection.getSchema(), entity.toLowerCase(), null);
		while (columns.next()) {
			sb.append("Name: " + columns.getString("COLUMN_NAME")).append(", Data type: " + columns.getInt("DATA_TYPE")).append("\n");
		}
		;
		System.out.println();
		System.out.println("Connection Metadata Information: ");
		System.out.println(sb.toString());
	}

	/**
	 * @param connection
	 * @throws SQLException
	 */
	private static void printConnectionInformation(Connection connection) throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("Catalog: ").append(connection.getCatalog()).append("\n")
		.append("NetworkTimeout: ").append(connection.getNetworkTimeout()).append("\n")
		.append("Schema: ").append(connection.getSchema()).append("\n")
		.append("TypeMap: ").append(connection.getTypeMap()).append("\n")
		.append("Auto-commit: ").append(connection.getAutoCommit()).append("\n")
		;
		System.out.println("Source Connection Detail: ");
		System.out.println(sb.toString());
	}

}
