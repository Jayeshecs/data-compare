/**
 * 
 */
package db.compare.util;

import java.util.LinkedHashMap;

/**
 * @author Prajapati
 *
 */
public class CaseInsensitiveLinkedMap<V> extends LinkedHashMap<String, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CaseInsensitiveLinkedMap() {
		// DO NOTHING
	}

	/**
	 * @param initialCapacity
	 */
	public CaseInsensitiveLinkedMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public V get(Object key) {
		return super.get(String.valueOf(key).toLowerCase());
	}
	
	@Override
	public V put(String key, V value) {
		return super.put(key.toLowerCase(), value);
	}
}
