package pathfinding;

/**
 * @author Duke Kim
 * Exception indicating invalid cost or heuristic costs.
 */
public class InvalidCostException extends IllegalArgumentException{

    private final static String myMessage = "Invalid path or heuristic cost";
    
    public InvalidCostException(){
        super(myMessage);
    }
}
