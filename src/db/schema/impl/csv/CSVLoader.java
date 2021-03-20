/**
 * 
 */
package db.schema.impl.csv;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


import db.schema.interfaces.IData;
import db.schema.interfaces.IDataProvider;
import error.CoreException;

/**
 * @author Prajapati
 *
 */
public class CSVLoader {

	public static IDataProvider getCSVDataProvider(String fileName, CSVLoaderSetting setting) throws CoreException {
	
		FileInputStream fis = null;
		
		try {
			
			fis = new FileInputStream(fileName);
			return getCSVDataProvider(fis, setting);
		} catch (FileNotFoundException e) {

			throw new CoreException(e);
		} finally {
			
			if(fis != null) {
				
				try {
					
					fis.close();
				} catch (IOException e) {
					
					throw new CoreException(e);
				}
			}
		}
	}
	
	public static IDataProvider getCSVDataProvider(InputStream content, CSVLoaderSetting setting) throws CoreException {
	
		return getCSVDataProvider(new InputStreamReader(content), setting);
	}
	
	public static IDataProvider getCSVDataProvider(Reader content, CSVLoaderSetting setting) throws CoreException {
		
		if(content == null) {
			
			throw new CoreException("Invalid argument: content argument cannot be null!");
		}
		
		CSVLoader loader = new CSVLoader(content, setting);
		loader.load();
		return loader.getDataProvider();
	}
	
	private CSVDataProvider dataProvider = null;
	private Reader reader;
	private CSVLoaderSetting setting;
	private StringBuffer sbValue = new StringBuffer();
	private CSVData lastData;
	private List<String> fieldNames;
	
	private CSVLoader(Reader reader, CSVLoaderSetting setting) {
		
		this.reader = reader;
		this.setting = setting;
		
		dataProvider = new CSVDataProvider();
	}
	
	private void load() throws CoreException {
		
		BufferedReader bufferedReader = new BufferedReader(this.reader);
		try {
			
			String line = null;
			
			if(this.setting.isFirstRowHeader()) {
				
				line = bufferedReader.readLine();
				readHeaderFromLine(line);
			} else {
				
				createDefaultHeader();
			}
			
			boolean continu = false;
			while((line = bufferedReader.readLine()) != null) {
				
				continu = readDataFromLine(line, 0, continu);
			}
				
		} catch (IOException e) {

			throw new CoreException(e);
		}
	}

	private void createDefaultHeader() {
		
		fieldNames = new ArrayList<String>();
		dataProvider.setFieldNames(CSVDataProvider.DEFAULT_ENTITY, fieldNames);
	}

	private void readHeaderFromLine(String line) {
		
		fieldNames = new ArrayList<String>();
		dataProvider.setFieldNames(CSVDataProvider.DEFAULT_ENTITY, fieldNames);
		readHeader(line, 0);
	}

	private void readHeader(String line, int fromIndex) {
		
		int idxQuote = line.indexOf(setting.getQuote(), fromIndex);
		int idxDelim = line.indexOf(setting.getDelim(), fromIndex);
		
		if(idxQuote != -1 && idxDelim != -1) {
			
			if(idxQuote < idxDelim) {
				
				readQuotedHeader(line, idxQuote);
			} else {
				
				readNonQuotedHeader(line, fromIndex, idxDelim);
			}
		} else if(idxQuote != -1) {
			
			readQuotedHeader(line, idxQuote);
		} else if(idxDelim != -1) {
			
			readNonQuotedHeader(line, fromIndex, idxDelim);
		} else {
			
			fieldNames.add(line.substring(fromIndex));
		}
	}

	private void readNonQuotedHeader(String line, int fromIndex, int idxDelim) {
		
		fieldNames.add(line.substring(fromIndex, idxDelim));
		readHeader(line, idxDelim + 1);
	}

	private void readQuotedHeader(String line, int idxQuote) {
		
		int idxDelim;
		int idxEndQuote = line.indexOf(setting.getQuote(), idxQuote + 1);
		
		if(idxEndQuote != -1) {
			
			fieldNames.add(line.substring(idxQuote + 1, idxEndQuote));
			
			idxDelim = line.indexOf(setting.getDelim(), idxEndQuote);
			
			if(idxDelim != -1) {
				
				readHeader(line, idxDelim + 1);
			}
		} else {
			
			fieldNames.add(line.substring(idxQuote + 1));
		}
	}

	private boolean readDataFromLine(String line, int fromIndex, boolean continu) {
		
		if(continu) {
			
			sbValue.append('\n');
			
			int idxQuote = line.indexOf(setting.getQuote(), fromIndex);
			
			if(idxQuote == -1) {
				
				sbValue.append(line);
			} else {
				
				sbValue.append(line.substring(fromIndex, idxQuote));
				fromIndex = idxQuote + 1;
				lastData.addData(sbValue.toString());
				sbValue.setLength(0);
				if(!this.setting.isFirstRowHeader() && dataProvider.getDataCount(CSVDataProvider.DEFAULT_ENTITY) == 1) {
					
					fieldNames.add("Column " + (fieldNames.size() + 1));
				}
				continu = false;
			}
		} else {

			if(fromIndex == 0) {
				lastData = new CSVData(dataProvider.getFieldNames(CSVDataProvider.DEFAULT_ENTITY));
				dataProvider.addData(CSVDataProvider.DEFAULT_ENTITY, lastData);
			}
			sbValue.setLength(0);
			
			int idxQuote = line.indexOf(setting.getQuote(), fromIndex);
			int idxDelim = line.indexOf(setting.getDelim(), fromIndex);
			
			if(idxQuote != -1 && idxDelim != -1) {
				
				if(idxQuote < idxDelim) {
					
					continu = readQuotedValue(line, idxQuote);
				} else {
					
					continu = readNonQuotedValue(line, fromIndex, idxDelim);
				}
			} else if(idxQuote != -1) {
				
				continu = readQuotedValue(line, idxQuote);
			} else if(idxDelim != -1) {
				
				continu = readNonQuotedValue(line, fromIndex, idxDelim);
			} else {
				
				lastData.addData(line.substring(fromIndex));
				sbValue.setLength(0);
				if(!this.setting.isFirstRowHeader() && dataProvider.getDataCount(CSVDataProvider.DEFAULT_ENTITY) == 1) {
					
					fieldNames.add("Column " + (fieldNames.size() + 1));
				}
				continu = false;
			}
		}
		return continu;
	}

	private boolean readQuotedValue(String line, int idxQuote) {
		
		int idxDelim;
		int idxEndQuote = line.indexOf(setting.getQuote(), idxQuote + 1);
		
		if(idxEndQuote != -1) {
			
			lastData.addData(line.substring(idxQuote + 1, idxEndQuote));
			sbValue.setLength(0);
			if(!this.setting.isFirstRowHeader() && dataProvider.getDataCount(CSVDataProvider.DEFAULT_ENTITY) == 1) {
				
				fieldNames.add("Column " + (fieldNames.size() + 1));
			}
			
			idxDelim = line.indexOf(setting.getDelim(), idxEndQuote);
			
			if(idxDelim != -1) {
				
				return readDataFromLine(line, idxDelim + 1, false);
			}
		} else {
			
			sbValue.append(line.substring(idxQuote + 1));
			return true;
		}
		
		return false;
	}

	private boolean readNonQuotedValue(String line, int fromIndex, int idxDelim) {
		
		lastData.addData(line.substring(fromIndex, idxDelim));
		if(!this.setting.isFirstRowHeader() && dataProvider.getDataCount(CSVDataProvider.DEFAULT_ENTITY) == 1) {
			
			fieldNames.add("Column " + (fieldNames.size() + 1));
		}
		return readDataFromLine(line, idxDelim + 1, false);
	}

	private IDataProvider getDataProvider() {
		
		return dataProvider;
	}
	
	public static void main(String[] args) throws CoreException {
		
		IDataProvider provider = CSVLoader.getCSVDataProvider("D:\\test.csv", CSVLoaderSetting.COMMA_DOUBLE_QUOTE);
		
		List<String> entities = provider.getEntities();
		
		for(int i = 0; i < entities.size(); ++i) {
			
			printEntityDetail(provider, entities.get(i));
		}
	}

	private static void printEntityDetail(IDataProvider provider, String entityName) {
		
		List<String> fields = provider.getFieldNames(entityName);
		int recordCount = provider.getDataCount(entityName);
		List<IData> data = provider.getData(entityName);
		
		System.out.println("Entity: " + entityName);
		System.out.println();
		for(int i = 0; i < fields.size(); ++i) {
			
			if(i > 0) {
				System.out.print(", ");
			}
			System.out.print(fields.get(i));
		}
		System.out.println("");
		
		for(int i = 0; i < data.size(); ++i) {
			
			IData d = data.get(i);
			for(int j = 0; j < d.getFieldCount(); ++j) {
				
				if(j > 0) {
					System.out.print(", ");
				}
				System.out.print(d.getData(j));
			}
			System.out.println();
		}
		System.out.println("");
		System.out.println("record count: " + recordCount);
	}

}
