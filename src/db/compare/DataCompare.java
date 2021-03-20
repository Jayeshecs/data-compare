/**
 * 
 */
package db.compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import db.compare.dstruct.CompareDataInfo;
import db.compare.dstruct.CompareInfo;
import db.compare.json.JSONHelper;
import db.compare.util.CaseInsensitiveMap;
import db.export.html.HTMLWriter;
import db.schema.impl.jdbc.JDBCLoader;
import db.schema.interfaces.IData;
import db.schema.interfaces.IDataProvider;
import error.CoreException;

/**
 * @author Prajapati
 *
 */
public class DataCompare {

	public static void main(String[] args) throws CoreException, IOException {
		
		Properties propsConfig = new Properties();
		FileInputStream fis = new FileInputStream("config.properties");
		propsConfig.load(fis);
		fis.close();
		
		CompareSettingCollection settings = new CompareSettingCollection();
		
//		IDataProvider source = CSVLoader.getCSVDataProvider("D:\\test1.csv", CSVLoaderSetting.HEADER_COMMA_DOUBLE_QUOTE);
//		IDataProvider target = CSVLoader.getCSVDataProvider("D:\\test2.csv", CSVLoaderSetting.HEADER_COMMA_DOUBLE_QUOTE);
//		settings.addCompareSetting(new CompareSetting(CSVDataProvider.DEFAULT_ENTITY, "Col1"));
		
		IDataProvider source = JDBCLoader.getJDBCDataProvider("compare.properties", true);
		IDataProvider target = JDBCLoader.getJDBCDataProvider("compare.properties", false);
		
		List<String> entities = source.getEntities();
		
		for(int i = 0; i < entities.size(); ++i) {
			
			String entity = entities.get(i);
			List<String> keys = source.getKeys(entity);
			List<String> exclusionFields = source.getExclusionFields(entity);
			settings.addCompareSetting(new CompareSetting(entity, keys, exclusionFields));
		}
		
		DataCompare dataCompare = new DataCompare(source, target, settings);
		dataCompare.setConfigurationProperties(propsConfig);
		dataCompare.compare();
	}

	
	private IDataProvider source;
	private IDataProvider target;
	private CompareSettingCollection settings;
	private Properties propsConfig;

	public DataCompare(IDataProvider source, IDataProvider target, CompareSettingCollection settings) {
		
		this.source = source;
		this.target = target;
		this.settings = settings;
	}
	
	private void setConfigurationProperties(Properties propsConfig) {
		this.propsConfig = propsConfig;
	}

	public void compare() throws IOException {
		
		List<CompareSetting> compareSettings = settings.getCompareSettings();
		
//		compareAndGenerateHTML(compareSettings);
		compareAndGenerateJSON(compareSettings);
	}

	protected void compareAndGenerateHTML(List<CompareSetting> compareSettings)
			throws FileNotFoundException, IOException {
		HTMLWriter writer = new HTMLWriter();
		writer.push("html");
		writer.push("body");
		
		for(int i = 0; i < compareSettings.size(); ++i) {
			
			CompareSetting setting = compareSettings.get(i);
			compare(writer, setting);
		}
		
		writer.pop("body");
		writer.pop("html");
		
		File reportFile = new File("compareReport.html");
		FileOutputStream fos = new FileOutputStream(reportFile);
		fos.write(writer.toString().getBytes());
		fos.flush();
		fos.close();
		
		//System.out.println(writer.toString());
		writer.close();
		
		/*Process exec = */Runtime.getRuntime().exec("cmd /C start " + reportFile.getAbsolutePath() + "");
	}

	private void compareAndGenerateJSON(List<CompareSetting> compareSettings)
			throws FileNotFoundException, IOException {

		CompareInfo compareInfo = new CompareInfo();
		
		for (int i = 0; i < compareSettings.size(); ++i) {

			CompareSetting setting = compareSettings.get(i);
			CompareDataInfo compareData = compare(setting);
			compareInfo.addCompareDataInfo(setting.getEntity(), compareData);
		}
		
		String outputPath = copyFilesAndGetOutputPath();
		if(outputPath == null) {
			outputPath = ""; 
		}
		File fOutputPath = new File(outputPath);
		File jsonFile = new File("compareInfo.js");
		if(fOutputPath.exists() && fOutputPath.isDirectory()) {
			jsonFile = new File(fOutputPath, "compareInfo.js");
		}
		
		FileOutputStream fos = new FileOutputStream(jsonFile);
		compareInfo.cleanCompareDataWithNoDifference();
		String json = "var data = " + JSONHelper.toJSON(null, compareInfo) + ";";
		fos.write(json.getBytes());
		fos.flush();
		fos.close();
		
		File reportFile = new File(outputPath + "\\..\\html\\CompareReport.html");
		
		String strAutoLaunch = propsConfig.getProperty("auto.launch");
		if(strAutoLaunch != null && strAutoLaunch.trim().equalsIgnoreCase("true")) {
			
			System.out.println("Auto launch data comparison report " + reportFile.getAbsolutePath());
			/*Process exec = */Runtime.getRuntime().exec("cmd /C start " + reportFile.getCanonicalPath());
		} else {
			
			System.out.println("Data comparison report is saved as " + reportFile.getCanonicalPath());
		}

	}

	private String copyFilesAndGetOutputPath() throws IOException {
		String outputPath = propsConfig.getProperty("output.dir");
		if(outputPath == null) {
			System.err.println("DataCompare: WARNING: output.dir property not set in config.properties");
			return null;
		}
		String reportName = propsConfig.getProperty("report.name");
		String srcPath = propsConfig.getProperty("src.dir");
		
		File fOutputPath = new File(outputPath); 
		File fReportPath = fOutputPath;
		if(reportName!= null) {
			fReportPath = new File(fOutputPath, reportName);
		}else {
			System.err.println("DataCompare: WARNING: report.name property not set in config.properties and report will be saved at outptu.dir:" + fOutputPath.getAbsolutePath());
		}
		File fSrcPath = new File(srcPath);
		
		copyDir(fSrcPath, fReportPath);
		
		return fReportPath.getAbsolutePath() + "\\javascript";
	}

	// The method copyFiles being defined
	private void copyDir(File srcPath, File dstPath) throws IOException {

		File src = srcPath;
		File dest = dstPath;

		if (src.isDirectory()) {
			// if(dest.exists()!=true)
			dest.mkdirs();
			String list[] = src.list();

			for (int i = 0; i < list.length; i++) {
				String dest1 = dest.getAbsolutePath() + "\\" + list[i];
				String src1 = src.getAbsolutePath() + "\\" + list[i];
				copyDir(new File(src1), new File(dest1));
			}
		} else {
			FileInputStream fin = new FileInputStream(src);
			FileOutputStream fout = new FileOutputStream(dest);
			int c;
			while ((c = fin.read()) >= 0) {
				fout.write(c);
			}
			fin.close();
			fout.close();
		}
	}

	private CompareDataInfo compare(CompareSetting setting) {
		
		CompareDataInfo compareDataInfo = new CompareDataInfo(source.getSchemaInfo(setting.getEntity()));
		compareDataInfo.setKeyFields(setting.getSortFields());
		
		// populate exclude fields
		compareDataInfo.setExcludeFields(source.getExclusionFields(setting.getEntity()));
		
		List<IData> sourceData = source.getData(setting.getEntity());
		Collections.sort(sourceData, setting);
		
		List<IData> targetData = target.getData(setting.getEntity());
		Collections.sort(targetData, setting);
		
		List<String> values = new ArrayList<String>();
		Map<String, Integer> sourceIndex = addValuesAndCreateIndex(values, sourceData, setting.getSortFields());
		Map<String, Integer> targetIndex = addValuesAndCreateIndex(values, targetData, setting.getSortFields());
		Collections.sort(values);
		
		for(int i = 0; i < values.size(); ++i) {
			
			String value = values.get(i);
			Integer srcIdx = sourceIndex.get(value);
			Integer targetIdx = targetIndex.get(value);
			
			if(srcIdx != null && targetIdx != null) {
				
				if(srcIdx.intValue() != targetIdx.intValue()) {
					
					System.out.println("src: " + srcIdx + ",target: " + targetIdx + ",value: " + value);
				}
				
				IData data1 = sourceData.get(srcIdx);
				IData data2 = targetData.get(targetIdx);
				
				if(!data1.equals(data2)) {
					
					List<String>[] diffData = new List[2];
					diffData[0] = data1.getData();
					diffData[1] = data2.getData();
					compareDataInfo.addDiffData(diffData);
				}
			} else {

				if(srcIdx == null) {
					
					compareDataInfo.addMissingData(targetData.get(targetIdx).getData());
				} else if(targetIdx == null) {

					compareDataInfo.addNewData(sourceData.get(srcIdx).getData());
				}
			}
		}

		return compareDataInfo;
		
	}

	private void compare(HTMLWriter writer, CompareSetting setting) {
		List<IData> sourceData = source.getData(setting.getEntity());
		Collections.sort(sourceData, setting);
		
		List<IData> targetData = target.getData(setting.getEntity());
		Collections.sort(targetData, setting);
		
		List<String> values = new ArrayList<String>();
		Map<String, Integer> sourceIndex = addValuesAndCreateIndex(values, sourceData, setting.getSortFields());
		Map<String, Integer> targetIndex = addValuesAndCreateIndex(values, targetData, setting.getSortFields());
		Collections.sort(values);
		
		writer.push("table");
		// print header
		writer.push("thead");
		writer.push("tr");
		List<String> names = source.getFieldNames(setting.getEntity());
		int fieldCount = names.size();
		
		for (Iterator<String> itr = names.iterator(); itr.hasNext();) {
			String name = itr.next();
			writer.push("td");
			writer.boldText(name);
			writer.pop("td");
		}
		writer.pop("tr");
		writer.pop("thead");
		writer.push("tbody");
		for(int i = 0; i < values.size(); ++i) {
			
			String value = values.get(i);
			Integer srcIdx = sourceIndex.get(value);
			Integer targetIdx = targetIndex.get(value);
			
			if(srcIdx != null && targetIdx != null) {
			
				IData data1 = sourceData.get(srcIdx);
				IData data2 = targetData.get(targetIdx);
				
				if(!data1.equals(data2)) {
					
					writer.push("tr");
					printIData(writer, data1);
					writer.pop("tr");
					writer.push("tr");
					printIData(writer, data2);
					writer.pop("tr");
					writeRecordSeparator(writer, fieldCount);
				}
			} else {
				writer.push("tr");
				if(srcIdx == null) {
					writer.push("td colspan=" + fieldCount);
					writer.boldText(" --- Mising in Source --- ");
					writer.pop("td");
				} else {
					IData data = sourceData.get(srcIdx);
					printIData(writer, data);
				}
				writer.pop("tr");
				
				writer.push("tr");
				if(targetIdx == null) {
					writer.push("td colspan=" + fieldCount);
					writer.boldText(" --- Mising in Target --- ");
					writer.pop("td");
				} else {
					IData data = targetData.get(targetIdx);
					printIData(writer, data);
				}
				writer.pop("tr");
				writeRecordSeparator(writer, fieldCount);
			}
		}
		writer.pop("tbody");
		writer.pop("table");
	}

	private void writeRecordSeparator(HTMLWriter writer, int fieldCount) {
		writer.push("tr");
		writer.push("td colspan=" + fieldCount);
		writer.push("hr");
		writer.pop("hr");
		writer.pop("td");
		writer.pop("tr");
	}

	private void printIData(HTMLWriter writer, IData data) {
		
		for(int j = 0; j < data.getFieldCount(); ++j) {
			
			writer.push("td");
			writer.text(data.getData(j));
			writer.pop("td");
		}
	}

	private Map<String, Integer> addValuesAndCreateIndex(List<String> values, List<IData> sourceData, List<String> sortFields) {
		
		Map<String, Integer> index = new CaseInsensitiveMap<Integer>();

		for(int i = 0; i < sourceData.size(); ++i) {
			
			IData data = sourceData.get(i);
			
			StringBuffer sbValue = new StringBuffer();
			
			if(sortFields != null && sortFields.size() > 0) {
				
				for(int j = 0; j < sortFields.size(); ++j) {
					
					if(j > 0) {
						
						sbValue.append(',');
					}
					sbValue.append(data.getData(sortFields.get(j)));
				}
			} else {
				
				sbValue.append(data.getData(0));
			}
			
			String value = sbValue.toString();
			index.put(value, i);
			if(!values.contains(value)) {
				
				values.add(value);				
			}
		}
		return index;
	}
}
