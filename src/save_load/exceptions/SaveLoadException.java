package save_load.exceptions;

/**
 * Highest level exception of the save load exception hierarchy.
 * @author Rahul Harikrishnan
 *
 */
public class SaveLoadException extends Exception {


    private static final String SAVE_LOAD_ERROR = "Save load error";
    /**
     * Auto-generated message.
     */
    private static final long serialVersionUID = 1L;

    public SaveLoadException() {
        super (SAVE_LOAD_ERROR);
    }
    
    
    public SaveLoadException (Exception exception) {
        this(SAVE_LOAD_ERROR, exception);
    }

    public SaveLoadException (String message, Exception exception, Object... args) {
        super(String.format(message, args), exception);
    }

    
    public SaveLoadException (String message) {
        this(message, new Exception());
    }
}
