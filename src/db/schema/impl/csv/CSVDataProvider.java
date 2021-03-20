/**
 * 
 */
package db.schema.impl.csv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import db.compare.dstruct.SchemaInfo;
import db.compare.util.CaseInsensitiveMap;
import db.schema.interfaces.IData;
import db.schema.interfaces.IDataProvider;

/**
 * @author Prajapati
 *
 */
public class CSVDataProvider implements IDataProvider {

	public static final String DEFAULT_ENTITY = "default";
	private Map<String, List<String>> mapFieldNames;
	private Map<String, List<IData>> mapData;
	private Map<String, List<String>> mapKeys;
	private Map<String, List<SchemaInfo>> mapSchemaInfo;
	private Map<String, List<String>> mapExclusionFields;

	public CSVDataProvider() {
		
		mapFieldNames = new CaseInsensitiveMap<List<String>>();
		mapData = new CaseInsensitiveMap<List<IData>>();
		mapKeys = new CaseInsensitiveMap<List<String>>();
		mapSchemaInfo = new CaseInsensitiveMap<List<SchemaInfo>>();
		mapExclusionFields = new CaseInsensitiveMap<List<String>>();
	}
	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IDataProvider#getData(java.lang.String)
	 */
	public List<IData> getData(String entity) {
		
		return this.mapData.get(entity);
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IDataProvider#getDataCount(java.lang.String)
	 */
	public int getDataCount(String entity) {

		return mapData.get(entity).size();
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IDataProvider#getFieldNames(java.lang.String)
	 */
	public List<String> getFieldNames(String entity) {
		
		return mapFieldNames.get(entity);
	}

	public void addData(String entity, IData data) {
		
		List<IData> list = this.mapData.get(entity);
		if(list == null) {
			
			list = new ArrayList<IData>();
			this.mapData.put(entity, list);
		}
		list.add(data);
	}
	
	public void setFieldNames(String entity, List<String> fieldNames) {
		
		this.mapFieldNames.put(entity, fieldNames);
		List<SchemaInfo> schemaInfos = new ArrayList<SchemaInfo>();
		for(int i = 0; i < fieldNames.size(); ++i) {
			
			schemaInfos.add(new SchemaInfo(fieldNames.get(i), 0)); // 0 - String
		}
		this.mapSchemaInfo.put(entity, schemaInfos);
		
	}

	public void setKeys(String entity, List<String> keys) {
		
		this.mapKeys.put(entity, keys);
		
	}
	
	public List<String> getEntities() {
		List<String> ret = new ArrayList<String>();
		Iterator<String> iterator = mapFieldNames.keySet().iterator();
		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();
			ret.add(entityName);
		}
		return ret;
	}

	public List<String> getKeys(String entity) {
		
		return this.mapKeys.get(entity);
	}
	
	public List<SchemaInfo> getSchemaInfo(String entity) {
		
		return this.mapSchemaInfo.get(entity);
	}

	public void setExclusionFields(String entity, List<String> exclusionFields) {
		this.mapExclusionFields.put(entity, exclusionFields);
	}
	
	public List<String> getExclusionFields(String entity) {
		return this.mapExclusionFields.get(entity);
	}

}
