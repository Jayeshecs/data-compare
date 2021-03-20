/**
 * 
 */
package db.compare;

import java.util.Comparator;
import java.util.List;

import db.schema.interfaces.IData;

/**
 * @author Prajapati
 *
 */
public class CompareSetting implements Comparator<IData> {

	private String entity;
	private List<String> sortFields;
	private List<String> excludeFields;

	public CompareSetting(String entity, List<String> sortFields, List<String> excludeFields) {
		
		this.entity = entity;
		this.sortFields = sortFields;
		this.excludeFields = excludeFields;
	}
	
	public String getEntity() {
		return entity;
	}
	
	public List<String> getSortFields() {
		return sortFields;
	}
	
	public List<String> getExcludeFields() {
		return excludeFields;
	}

	public int compare(IData o1, IData o2) {
		
		String data1;
		String data2;
		
		if(sortFields != null) {
			
			for(int i = 0; i < sortFields.size(); ++i) {
				
				String sortField = sortFields.get(i);
				data1 = o1.getData(sortField);
				data2 = o2.getData(sortField);
				
				if(data1 == null && data2 != null) {
					
					return -1;
				}
				
				if(data1 != null && data2 == null) {
					
					return 1;
				}
				
				
				if(data1 == null && data2 == null) {
					
					continue;
				}
				
				int compareTo = data1.compareTo(data2);
				// if data comparison for ith field is not equal then return compare result
				if(compareTo != 0) {
					
					return compareTo;
				}
			}
			return 0;
		} 
		
		data1 = o1.getData(0);
		data2 = o2.getData(0);
		
		return data1.compareTo(data2);
	}
}
