/**
 * 
 */
package db.compare.json;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generically convert a Java object graph to a JSON string.
 * @author Sachin Saraf
 */
public class JSONHelper {
	
	public static final String DOUBLE_QUOTES = "\"";
	public static final String COMMA = ",";
	public static final String LEFT_SQ_BRCKT= "[";
	public static final String RIGHT_SQ_BRCKT= "]";
	public static final String LEFT_FLOWER_BRCKT= "{";
	public static final String RIGHT_FLOWER_BRCKT= "}";
	public static final String COLON= ":";
	public static final String UNDEFINED = "undefined";

	/**
	 * This method converts the value to JSON string. Supported Object types:
	 * String, Integer, Long, Float, JSONInterface, Map, List, Object[] (The
	 * objects in the array should be one of the supported object)
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toJSON(String name, Object value)
	{
		StringBuffer sb = new StringBuffer();

		// object name
		if(name != null) {
			sb.append(getField(name) + COLON);
		}
		
		// object JSON literal
		if (null == value) {
			// XXX: Jayesh - If value is null then append 'undefined' because it is equivalent keyword of null in
			// Javascript
			sb.append(UNDEFINED);
		} else {
			if (value instanceof String ) {
				sb.append(toJSONFromString(value.toString()));
			} else if (value instanceof Long
				|| value instanceof Integer || value instanceof Boolean
				|| value instanceof Float) {
				sb.append(value.toString());
			} else if (value instanceof Object[])
			{
				sb.append(toJSONFromArray((Object[]) value));
			} else if (value instanceof Map) {
				sb.append(toJSONFromMAP((Map) value));
			} else if (value instanceof List) {
				sb.append(toJSONFromList((List) value));
			} else if (value instanceof JSONInterface) {
				sb.append(((JSONInterface)value).getJSONString());
			} else {
				throw new UnsupportedOperationException("Unsupported class type for Object. " +
						"Name="+name+", Class="+value.getClass());
			}
		}
		return sb.toString();
	}

	private static String toJSONFromString(String value) {
		StringBuffer sb = new StringBuffer();
		sb.append(getField(value.toString()));
		return sb.toString();
	}

	private static String toJSONFromArray(Object[] arr) {
		StringBuffer retStr = new StringBuffer();
		retStr.append(LEFT_SQ_BRCKT);
		//first element
		retStr.append(toJSON(null, arr[0]));
		//remaining element
		for (int i=1; i<arr.length; i++)
		{
			retStr.append(COMMA + toJSON(null, arr[i]));
		}
		retStr.append(RIGHT_SQ_BRCKT);
		return retStr.toString();
	}

	private static String getField(Object obj) {
		String strObj = String.valueOf(obj);
		// Escape any backslash available in the strObj
		if(strObj.indexOf('\\') != -1) {
			strObj = strObj.replaceAll("[\\\\]", "\\\\\\\\");
		}
		// Escape \n, \r and \t
		if(strObj.indexOf('\n') != -1) {
			strObj = strObj.replaceAll("[\n]", "\\\\n");
		}
		if(strObj.indexOf('\r') != -1) {
			strObj = strObj.replaceAll("[\r]", "\\\\r");
		}
		if(strObj.indexOf('\t') != -1) {
			strObj = strObj.replaceAll("[\t]", "\\\\t");
		}
		// Escape double quote available in strObj
		if(strObj.indexOf('"') != -1) {
			strObj = strObj.replaceAll("[\"]", "\\\\\"");
		}
		// Enclose strObj with double quotes
		return DOUBLE_QUOTES + strObj + DOUBLE_QUOTES;
	}

	@SuppressWarnings("unchecked")
	private static String toJSONFromMAP(Map mapObj) {
		return toJSONFromMAP(mapObj, "");
	}
	
	@SuppressWarnings("unchecked")
	private static String toJSONFromMAP(Map mapObj, String keyPrefix) {
		StringBuffer retStr = new StringBuffer();
		retStr.append(LEFT_FLOWER_BRCKT);
		if (mapObj!=null) {

			Set keys = mapObj.keySet();
			Iterator keysIter = keys.iterator();
			Collection values = mapObj.values();
			Iterator valuesIter = values.iterator();
			for (int i = 0; i < mapObj.size(); i++) {
				if (i != 0) {
					// This will not work if keysIter.next().toString() does not
					// return proper value
					retStr.append(COMMA
							+ toJSON(keyPrefix + keysIter.next().toString(),
									valuesIter.next()));
				} else // first element
				{
					retStr.append(toJSON(
							keyPrefix + keysIter.next().toString(), valuesIter
									.next()));
				}
			}
		}
		retStr.append(RIGHT_FLOWER_BRCKT);
		
		return retStr.toString();
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String toJSONForMapWithKeyPrefix(String mapName, Map map, String keyPrefix) {
		String ret = "";
		if(mapName != null && mapName.trim().length() > 0) {
			ret += getField(mapName) + COLON; 
		}
		return ret + toJSONFromMAP(map, keyPrefix);
	}

	@SuppressWarnings("unchecked")
	private static String toJSONFromList(List listObj) {
		StringBuffer retStr = new StringBuffer();
		//first element
		retStr.append(LEFT_SQ_BRCKT);
		if (listObj.size()>0) {
			retStr.append(toJSON(null, listObj.get(0)));
			// remaining element
			for (int i = 1; i < listObj.size(); i++) {
				retStr.append(COMMA + toJSON(null, listObj.get(i)));
			}
		}
		retStr.append(RIGHT_SQ_BRCKT);
		return retStr.toString();
	}

	public static String addHeader() {
		
		return ("\n"+LEFT_FLOWER_BRCKT);
	}

	public static String addFooter() {
		return RIGHT_FLOWER_BRCKT;
	}

}
