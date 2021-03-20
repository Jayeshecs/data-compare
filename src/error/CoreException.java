/**
 * 
 */
package error;


/**
 * @author Prajapati
 *
 */
public final class CoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CoreException(Throwable e) {
		super(e);
	}

	public CoreException(String msg) {
		super(msg);
	}

}
