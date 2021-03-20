/**
 * 
 */
package db.schema.impl.jdbc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import db.compare.dstruct.SchemaInfo;
import db.compare.util.CaseInsensitiveLinkedMap;
import db.schema.interfaces.IData;
import db.schema.interfaces.IDataProvider;

/**
 * @author Prajapati
 *
 */
public class JDBCDataProvider implements IDataProvider {

	private Map<String, List<String>> mapFieldNames;
	private Map<String, List<IData>> mapData;
	private Map<String, List<String>> mapKeys;
	private Map<String, List<SchemaInfo>> mapSchemaInfo;
	private Map<String, List<String>> mapExclusionFields;

	public JDBCDataProvider() {
		
		this.mapFieldNames = new CaseInsensitiveLinkedMap<List<String>>();
		this.mapData = new CaseInsensitiveLinkedMap<List<IData>>();
		this.mapKeys = new CaseInsensitiveLinkedMap<List<String>>();
		this.mapSchemaInfo = new CaseInsensitiveLinkedMap<List<SchemaInfo>>();
		this.mapExclusionFields = new CaseInsensitiveLinkedMap<List<String>>();
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
		return getData(entity).size();
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IDataProvider#getEntities()
	 */
	public List<String> getEntities() {
		List<String> ret = new ArrayList<String>();
		Iterator<String> iterator = mapFieldNames.keySet().iterator();
		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();
			ret.add(entityName);
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.coresoft.db.schema.interfaces.IDataProvider#getFieldNames(java.lang.String)
	 */
	public List<String> getFieldNames(String entity) {
		
		return this.mapFieldNames.get(entity);
	}

	public List<String> getKeys(String entity) {
		
		return this.mapKeys.get(entity);
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
	}
	
	public void setSchemaInfos(String entity, List<SchemaInfo> schemaInfos) {
		
		this.mapSchemaInfo.put(entity, schemaInfos);
	}
	
	public void setKeys(String entity, List<String> keys) {
		
		this.mapKeys.put(entity, keys);
		
	}
	
	public void setData(String entity, List<IData> data) {
		
		this.mapData.put(entity, data);
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
