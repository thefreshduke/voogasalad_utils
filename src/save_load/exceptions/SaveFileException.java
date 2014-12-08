package save_load.exceptions;

/**
 * 
 * @author Rahul
 *
 */
public class SaveFileException extends SaveLoadException {
    /**
     * Auto-generated default serialID
     */
    private static final long serialVersionUID = 1L;

    private static final String message = "Unable to save file at location: %s";

    public SaveFileException (Exception exception, Object... args) {
        super(message, exception);
    }

    public SaveFileException () {
        super(message);
    }

}
