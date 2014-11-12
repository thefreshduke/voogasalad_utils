package multiMethod.example;

import multiMethod.MultiMethod;


/**
 * An example MultiMethod in which the method "use" is written as an overloaded
 * method of the class. When called, the most appropriate version is called
 * based on the run-time type of the parameters.
 *
 * @author Robert C Duvall
 */
public class Use extends MultiMethod {
    public Use () {
        // BUGBUG: this is for ugly but necessary for now -
        // let the superclass know what name you chose for
        // your overloaded methods (perhaps use annotations?)
        super("use");
    }

    public void use (Key k, Door d) {
        common();
        System.out.println(k + " in " + d);
    }

    public void use (Weapon w, Monster m) {
        common();
        System.out.println(w + " on " + m);
    }

    public void use (Item i, Monster p) {
        common();
        System.out.println(i + " for " + p);
    }

    public void use (Object i, Object p) {
        common();
        System.out.println("default");
    }

    private void common () {
        System.out.print("*** ");
    }
}
