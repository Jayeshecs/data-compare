/**
 * 
 */
package db.schema.interfaces;

import java.util.List;

import db.compare.dstruct.SchemaInfo;

/**
 * @author Prajapati
 *
 */
public interface IDataProvider {

	/**
	 * This method is used to get count of records for given entity
	 * 
	 * @param entity
	 * @return
	 */
	int getDataCount(String entity);
	
	/**
	 * This method is used to get list of data records for given entity
	 * 
	 * @param entity
	 * @return
	 */
	List<IData> getData(String entity);
	
	/**
	 * This method is used to get list of field names for given entity
	 * 
	 * @param entity
	 * @return
	 */
	List<String> getFieldNames(String entity);
	
	/**
	 * This method is used to get list of field names for given entity
	 * 
	 * @param entity
	 * @return
	 */
	List<String> getEntities();

	/**
	 * This method is used to get key for given entity.
	 * 
	 * @param entity
	 * @return
	 */
	List<String> getKeys(String entity);
	
	/**
	 * This method is used to get schema info associated with given entity.
	 * 
	 * @param entity
	 * @return
	 */
	List<SchemaInfo> getSchemaInfo(String entity);

	/**
	 * Get list of fields which must be excluded while comaring data
	 * 
	 * @param entity
	 * @return
	 */
	List<String> getExclusionFields(String entity);
	
}
