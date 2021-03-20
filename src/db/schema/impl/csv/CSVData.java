/**
 * 
 */
package db.schema.impl.csv;

import java.util.ArrayList;
import java.util.List;

import db.schema.interfaces.IData;

/**
 * @author Prajapati
 *
 */
public class CSVData implements IData {

	private List<String> data;
	private List<String> fieldNames;

	public CSVData(List<String> fieldNames) {
		
		this.fieldNames = fieldNames;
		this.data = new ArrayList<String>();
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IData#getData(int)
	 */
	public String getData(int index) {

		return this.data.size() > index ? this.data.get(index) : null;
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IData#getData(java.lang.String)
	 */
	public String getData(String fieldName) {
		
		int index = this.fieldNames.indexOf(fieldName);
		return getData(index);
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IData#getFieldCount()
	 */
	public int getFieldCount() {
		
		return this.fieldNames.size();
	}

	public void addData(String value) {
		
		this.data.add(value);
	}

	public List<String> getData() {
		
		return this.data;
	}

}
