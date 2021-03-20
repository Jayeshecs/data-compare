/**
 * 
 */
package db.compare;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Prajapati
 *
 */
public class CompareSettingCollection {

	private List<CompareSetting> settings;
	
	public CompareSettingCollection() {
		
		settings = new ArrayList<CompareSetting>();
	}
	
	public void addCompareSetting(CompareSetting setting) {
		
		settings.add(setting);
	}
	
	public List<CompareSetting> getCompareSettings() {
		
		return settings;
	}
}
