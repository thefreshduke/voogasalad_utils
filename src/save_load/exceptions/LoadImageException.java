package save_load.exceptions;

/**
 * 
 * @author Rahul
 *
 */
public class LoadImageException extends SaveLoadException {
    /**
     * Auto-generated default serialID
     */
    private static final long serialVersionUID = 1L;
    private static final String myMessage = "Unable to load image %s";

    public LoadImageException (Exception exception, Object... args) {
        super(String.format(myMessage, args), exception);
    }
}
