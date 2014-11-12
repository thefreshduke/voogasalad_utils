package multiMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Multimethods are a generalization of polymorphism in object oriented programs
 * that allow a method to be called based on the run-time type of its parameters
 * instead of just the object on which it is called. In other words, a method is
 * called based on the run-time types of all its parameters, instead of their
 * compile-time types (as would be done typically). This implementation lets one
 * write a collection of overloaded methods within a subclass of this class that
 * can be called polymorphically.
 *
 * For example, suppose you wanted to write a game that needed to include
 * collision detection. The question is how do two things react when they
 * collide (explode, bounce, pass through, refuel, etc)? Using this class, you
 * could write all of your collision reactions in one place (the subclass of
 * multimethod) and it is smart enough to chose the correct action, based on the
 * participants. This means you would need to write a separate method (reaction)
 * for each pair that you cared about (i.e., ship-station, ship-bullet, etc.).
 *
 * Perhaps multi-method is a bad name (it is the "technical term" :) and
 * multi-action is better?
 *
 * @author Robert C Duvall
 */
public class MultiMethod {
    private List<Method> myPossibles;

    /**
     * Create a multimethod by specifying the name of the overloaded method
     * within its subclass(es). This simply makes it easier to find the
     * overloaded method, but restricts users to using a single method name.
     */
    protected MultiMethod (String methodToFind) {
        myPossibles = new ArrayList<Method>();
        // Create collection of only the methods those the user
        // is overriding. We could do this by removing all the methods
        // of this object and Object, but that is somewhat error-prone.
        Method[] allMethods = getClass().getMethods();
        for (Method allMethod : allMethods) {
            if (methodToFind.equals(allMethod.getName())) {
                myPossibles.add(allMethod);
            }
        }
    }

    /**
     * Call multimethod on methods expecting an arbitrary number of parameters.
     */
    public void invoke (Object... args) {
        try {
            Method toCall = findMatch(getSignature(args));
            toCall.invoke(this, args);
        } catch (NullPointerException e) {
            throw new NoSuchMethodException("No matching implementation for %s", toString(args));
        } catch (InvocationTargetException e) {
            // method called threw an exception
            throw new NoSuchMethodException(e);
        } catch (IllegalAccessException e) {
            // this should never happen
            throw new NoSuchMethodException(e);
        }
    }

    private Method findMatch (Class<?>[] actuals) {
        // find suitability of each possible method to be correct by
        // determining their formal parameters distance from actuals
        int minDistance = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int k = 0; k < myPossibles.size(); k++) {
            Class<?>[] formals = myPossibles.get(k).getParameterTypes();
            if (formals.length == actuals.length) {
                int distance = totalDistance(formals, actuals);
                if (distance < minDistance) {
                    minDistance = distance;
                    minIndex = k;
                }
            }
        }
        // return best ranked method
        return (minIndex != -1) ? myPossibles.get(minIndex) : null;
    }

    private int totalDistance (Class<?>[] formals, Class<?>[] actuals) {
        int result = 0;
        for (int k = 0; k < formals.length; k++) {
            result += (k + 1) * distance(formals[k], actuals[k]);
        }
        return result;
    }

    private int distance (Class<?> formal, Class<?> actual) {
        int distance = 0;
        while (actual.getSuperclass() != null && !formal.getName().equals(actual.getName())) {
            actual = actual.getSuperclass();
            distance++;
        }
        return distance;
    }

    private Class<?>[] getSignature (Object[] args) {
        Class<?>[] actuals = new Class[args.length];
        for (int k = 0; k < actuals.length; k++) {
            actuals[k] = args[k].getClass();
        }
        return actuals;
    }

    private String toString (Object... data) {
        StringBuffer result = new StringBuffer();
        for (Object o : data) {
            result.append(" " + o.getClass().getName());
        }
        return result.toString();
    }
}
