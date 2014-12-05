/**
 * Team PrintStackTrace - Copyright 2014
 * @author Zachary Podbela
 * Date Created: 12/4/14
 * Date Modified: 12/4/14
 */
package sharing.twillio;

public class PSTTwillioException extends Exception {
	
	/**
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Create an exception based on an issue in our code.
     */
    public PSTTwillioException(String message) {
        super(message);
    }

    /**
     * Create an exception based on a caught exception with a different message.
     */
    public PSTTwillioException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create an exception based on a caught exception.
     */
    public PSTTwillioException(Throwable exception) {
        super(exception);
    }
}
