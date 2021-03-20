/**
 * 
 */
package db.export.html;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Prajapati
 *
 */
public class HTMLWriter extends Writer {

	private StringBuffer sb = new StringBuffer();
	private int level = 0;
	
	/* (non-Javadoc)
	 * @see java.io.Writer#close()
	 */
	@Override
	public void close() throws IOException {
		sb.setLength(0);
		sb = null;
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#flush()
	 */
	@Override
	public void flush() throws IOException {
		// DO NOTHING
	}

	/* (non-Javadoc)
	 * @see java.io.Writer#write(char[], int, int)
	 */
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		sb.append(cbuf, off, len);
	}
	
	public void push(String el) {
		sb.append("\n");
		printTab();
		level++;
		sb.append("<");
		sb.append(el);
		sb.append(">");
	}
	
	private void printTab() {
		for(int i = 0; i < level; ++i) {
			sb.append('\t');
		}
	}

	public void text(String el) {
		sb.append(el);
	}
	
	public void boldText(String el) {
		push("b");
		text(el);
		pop("b");
	}
	
	public void pop(String el) {
		level--;
		sb.append("\n");
		printTab();
		sb.append("</");
		sb.append(el);
		sb.append(">");
	}
	
	@Override
	public String toString() {

		return sb.toString();
	}

}
