/**
 * 
 */
package db.compare.dstruct;

import db.compare.json.JSONHelper;
import db.compare.json.JSONInterface;


/**
 * @author Prajapati
 *
 */
public class SchemaInfo implements JSONInterface {

	private String fieldName;
	private int type;

	public SchemaInfo(String fieldName, int type) {
		
		this.fieldName = fieldName;
		this.type = type;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	public String getJSONString() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(JSONHelper.addHeader());
		
		sb.append(JSONHelper.toJSON("fieldName", getFieldName()));
		
		sb.append(JSONHelper.COMMA);
		
		sb.append(JSONHelper.toJSON("type", getType()));
		
		sb.append(JSONHelper.addFooter());
		
		return sb.toString();
	}
	
}
