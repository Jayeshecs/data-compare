/**
 * 
 */
package db.schema.interfaces;

/**
 * @author Prajapati
 *
 */
public interface IScriptGenerator {

	/**
	 * This method will generate INSERT script
	 * @param dataProvider
	 * @return
	 */
	String generateInsert(IDataProvider dataProvider);
}
