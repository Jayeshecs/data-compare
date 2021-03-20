/**
 * 
 */
package db.schema.impl.csv;

/**
 * @author Prajapati
 *
 */
public class CSVLoaderSetting {

	public static final CSVLoaderSetting COMMA_DOUBLE_QUOTE = new CSVLoaderSetting(',', '"');
	
	public static final CSVLoaderSetting COMMA_SINGLE_QUOTE = new CSVLoaderSetting(',', '\'');
	
	public static final CSVLoaderSetting TAB_DOUBLE_QUOTE = new CSVLoaderSetting('\t', '"');
	
	public static final CSVLoaderSetting TAB_SINGLE_QUOTE = new CSVLoaderSetting('\t', '\'');
	
	public static final CSVLoaderSetting HYPHEN_DOUBLE_QUOTE = new CSVLoaderSetting('-', '"');
	
	public static final CSVLoaderSetting HYPHEN_SINGLE_QUOTE = new CSVLoaderSetting('-', '\'');
	
	public static final CSVLoaderSetting PIPE_DOUBLE_QUOTE = new CSVLoaderSetting('|', '"');
	
	public static final CSVLoaderSetting PIPE_SINGLE_QUOTE = new CSVLoaderSetting('|', '\'');
	
	public static final CSVLoaderSetting SEMICOLON_DOUBLE_QUOTE = new CSVLoaderSetting(';', '"');
	
	public static final CSVLoaderSetting SEMICOLON_SINGLE_QUOTE = new CSVLoaderSetting(';', '\'');
	
	public static final CSVLoaderSetting HEADER_COMMA_DOUBLE_QUOTE = new CSVLoaderSetting(',', '"', true);
	
	public static final CSVLoaderSetting HEADER_COMMA_SINGLE_QUOTE = new CSVLoaderSetting(',', '\'', true);
	
	public static final CSVLoaderSetting HEADER_TAB_DOUBLE_QUOTE = new CSVLoaderSetting('\t', '"', true);
	
	public static final CSVLoaderSetting HEADER_TAB_SINGLE_QUOTE = new CSVLoaderSetting('\t', '\'', true);
	
	public static final CSVLoaderSetting HEADER_HYPHEN_DOUBLE_QUOTE = new CSVLoaderSetting('-', '"', true);
	
	public static final CSVLoaderSetting HEADER_HYPHEN_SINGLE_QUOTE = new CSVLoaderSetting('-', '\'', true);
	
	public static final CSVLoaderSetting HEADER_PIPE_DOUBLE_QUOTE = new CSVLoaderSetting('|', '"', true);
	
	public static final CSVLoaderSetting HEADER_PIPE_SINGLE_QUOTE = new CSVLoaderSetting('|', '\'', true);
	
	public static final CSVLoaderSetting HEADER_SEMICOLON_DOUBLE_QUOTE = new CSVLoaderSetting(';', '"', true);
	
	public static final CSVLoaderSetting HEADER_SEMICOLON_SINGLE_QUOTE = new CSVLoaderSetting(';', '\'', true);
	
	private char delim;
	
	private char quote;

	private boolean firstRowHeader;

	private CSVLoaderSetting(char delim, char quote) {

		this(delim, quote, false);
	}

	private CSVLoaderSetting(char delim, char quote, boolean firstRowHeader) {
		
		this.delim = delim;
		
		this.quote = quote;
		
		this.firstRowHeader = firstRowHeader;
	}

	/**
	 * @return the delim
	 */
	public final char getDelim() {
		return delim;
	}

	/**
	 * @return the quote
	 */
	public final char getQuote() {
		return quote;
	}

	/**
	 * @return the firstRowHeader
	 */
	public final boolean isFirstRowHeader() {
		return firstRowHeader;
	}
}
