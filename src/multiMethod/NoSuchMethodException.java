package multiMethod;


/**
 * A general exception that represents all possible MultiMethod exceptions.
 *
 * @author Robert C Duvall
 */
public final class NoSuchMethodException extends RuntimeException {
    // for serialization
    private static final long serialVersionUID = 1L;

    /**
     * Create an exception based on an issue in our code.
     */
    public NoSuchMethodException (String message, Object... args) {
        super(format(message, args));
    }

    /**
     * Create an exception based on a caught exception.
     */
    public NoSuchMethodException (Throwable exception) {
        super(exception);
    }

    /**
     * Create an exception based on a caught exception with a different message.
     */
    public NoSuchMethodException (Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

    // remove duplicate code, also placeholder for future improvements (like logging)
    private static String format (String message, Object... args) {
        return String.format(message, args);
    }
}
