package save_load.exceptions;

/**
 * 
 * @author Rahul
 *
 */
public class JSONSaveException extends SaveLoadException {
    /**
     * Auto-generated default ID
     */
    private static final long serialVersionUID = 1L;
    private static final String myMessage = "Unable to save JSON file: %s";

    public JSONSaveException (Exception exception, Object... args) {
        super(String.format(myMessage, args), exception);
    }
}