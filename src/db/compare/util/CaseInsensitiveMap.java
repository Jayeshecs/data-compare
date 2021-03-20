/**
 * 
 */
package db.compare.util;

import java.util.HashMap;

/**
 * @author Prajapati
 *
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CaseInsensitiveMap() {
		// DO NOTHING
	}

	/**
	 * @param initialCapacity
	 */
	public CaseInsensitiveMap(int initialCapacity) {
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
