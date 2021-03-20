/**
 * 
 */
package db.compare.dstruct;

import java.util.Map;

import db.compare.json.JSONHelper;
import db.compare.json.JSONInterface;
import db.compare.util.CaseInsensitiveMap;

/**
 * @author Prajapati
 *
 */
public class CompareInfo implements JSONInterface {

	private Map<String, CompareDataInfo> mapCompareDataInfo;

	public CompareInfo() {
		
		this.mapCompareDataInfo = new CaseInsensitiveMap<CompareDataInfo>();
	}
	
	public void addCompareDataInfo(String entity, CompareDataInfo compareData) {
		
		this.mapCompareDataInfo.put(entity, compareData);
	}
	
	public void cleanCompareDataWithNoDifference() {
		
		String[] keys = mapCompareDataInfo.keySet().toArray(new String[]{});
		
		for(int i = 0; i < keys.length; ++i) {
			
			String key = keys[i];
			CompareDataInfo compareDataInfo = mapCompareDataInfo.get(key);
			
			if(compareDataInfo.isNoDifference()) {
				
				mapCompareDataInfo.remove(key);
			}
		}
	}

	public String getJSONString() {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(JSONHelper.addHeader());
		
		sb.append(JSONHelper.toJSON("compareData", mapCompareDataInfo));
		
		sb.append(JSONHelper.addFooter());
		
		return sb.toString();
	}
}
