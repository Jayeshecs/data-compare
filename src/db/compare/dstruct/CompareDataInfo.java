/**
 * 
 */
package db.compare.dstruct;

import java.util.ArrayList;
import java.util.List;

import db.compare.json.JSONHelper;
import db.compare.json.JSONInterface;

/**
 * @author Prajapati
 *
 */
public class CompareDataInfo implements JSONInterface {

	private List<String> keyFields; 
	private List<String> excludeFields; 
	private List<List<String>[]> diffData;
	private List<List<String>> missingData;
	private List<List<String>> newData;
	private List<SchemaInfo> schemaInfos;

	public CompareDataInfo(List<SchemaInfo> schemaInfos) {
		
		this.schemaInfos = schemaInfos;
		this.diffData = new ArrayList<List<String>[]>();
		this.missingData = new ArrayList<List<String>>();
		this.newData = new ArrayList<List<String>>();
	}
	
	/**
	 * @param keyField the keyField to set
	 */
	public void setKeyFields(List<String> keyFields) {
		
		this.keyFields = keyFields;
	}
	
	/**
	 * @param keyField the keyField to set
	 */
	public void setExcludeFields(List<String> excludeFields) {
		
		this.excludeFields = excludeFields;
	}

	/**
	 * @return the keyField
	 */
	public List<String> getKeyFields() {
		
		return keyFields;
	}

	/**
	 * @return the keyField
	 */
	public List<String> getExcludeFields() {
		
		return excludeFields;
	}

	public void addDiffData(List<String>[] diffData) {
		
		this.diffData.add(diffData);
	}
	
	public void addMissingData(List<String> missingData) {
		
		this.missingData.add(missingData);
	}
	
	public void addNewData(List<String> newData) {
		
		this.newData.add(newData);
	}
	
	public List<SchemaInfo> getSchemaInfo() {
		
		return schemaInfos;
	}
	
	public boolean isNoDifference() {
		
		return (diffData.size() == 0 && missingData.size() == 0 && newData.size() == 0);
	}

	public String getJSONString() {
		
		StringBuffer sb = new StringBuffer();
		
		if(diffData.size() == 0 && missingData.size() == 0 && newData.size() == 0) {
			
			sb.append(JSONHelper.UNDEFINED);
		} else {
			sb.append(JSONHelper.addHeader());
			
			sb.append(JSONHelper.toJSON("schemas", schemaInfos));
			
			sb.append(JSONHelper.COMMA);
			
			sb.append(JSONHelper.toJSON("key", getKeyFields()));
			
			sb.append(JSONHelper.COMMA);
			
			sb.append(JSONHelper.toJSON("exclude", getExcludeFields()));
			
			if(diffData.size() > 0) {
				
				sb.append(JSONHelper.COMMA);
				
				sb.append(JSONHelper.toJSON("diffData", diffData));
			}
			
			if(missingData.size() > 0) {
				
				sb.append(JSONHelper.COMMA);
				
				sb.append(JSONHelper.toJSON("missingData", missingData));
			}
			
			if(newData.size() > 0) {
				
				sb.append(JSONHelper.COMMA);
				
				sb.append(JSONHelper.toJSON("newData", newData));
			}
			
			sb.append(JSONHelper.addFooter());
		}
		
		return sb.toString();
	}
	
}
