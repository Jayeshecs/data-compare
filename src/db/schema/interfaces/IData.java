/**
 * 
 */
package db.schema.interfaces;

import java.util.List;

/**
 * @author Prajapati
 *
 */
public interface IData {

	/**
	 * This method is used to get total number of fields available.
	 * 
	 * @return
	 */
	int getFieldCount();
	
	/**
	 * This method will return data associated with field at given index. If
	 * index is not valid then it will return null.
	 * 
	 * @param index
	 * @return
	 */
	String getData(int index);
	
	/**
	 * This method will return data associated with field identified as given
	 * fieldName. If fieldName is not valid then it will return null.
	 * 
	 * @param fieldName
	 * @return
	 */
	String getData(String fieldName);

	/**
	 * This method will return list of data.
	 * 
	 * @return
	 */
	List<String> getData();

}
