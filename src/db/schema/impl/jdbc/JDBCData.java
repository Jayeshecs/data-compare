/**
 * 
 */
package db.schema.impl.jdbc;

import java.util.ArrayList;
import java.util.List;

import db.schema.interfaces.IData;

/**
 * @author Prajapati
 *
 */
public class JDBCData implements IData {

	private List<String> data;
	private List<String> fieldNames;
	private List<String> exclusions;

	public JDBCData(List<String> fieldNames, List<String> exclusions) {
		
		this.fieldNames = fieldNames;
		this.exclusions = exclusions;
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
		int index = this.fieldNames.indexOf(fieldName.toLowerCase());
		if (index == -1) {
			index = this.fieldNames.indexOf(fieldName.toUpperCase());
		}
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
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof IData) {
			
			IData data = (IData) obj;
			// 1st check no. of fields
			if(getFieldCount() != data.getFieldCount()) {
				
				return false;
			}
			
			boolean hasExclusionFields = exclusions != null && !exclusions.isEmpty();
			// 2nd compare data
			for(int i = 0; i < this.data.size(); ++i) {
				if (hasExclusionFields && exclusions.contains(fieldNames.get(i)) || exclusions.contains(fieldNames.get(i).toLowerCase()) || exclusions.contains(fieldNames.get(i).toUpperCase())) {
					continue ;
				}
				String val1 = getData(i);
				String val2 = data.getData(i);
				
				if(val1 != null && val2 != null && !val1.equals(val2)) {
					
					return false;
				} else if((val1 == null && val2 != null) || (val1 != null && val2 == null)) {
					return false;
				}
			}
			return true;
		}
		return super.equals(obj);
	}


	public List<String> getData() {
		
		return this.data;
	}
}
