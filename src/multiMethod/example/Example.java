package multiMethod.example;


/**
 * An example of using a MultiMethod.
 *
 * @author Robert C Duvall
 */
class Example {
    static public void main (String args[]) {
        Use mm = new Use();
        Item k = new Key();
        Door d = new Door();
        Weapon w = new Weapon();
        Player m = new Monster();
        Item t = new Treasure();
        Bartender b = new Bartender();

        // try Java's overloading versus multimethods
        mm.use(k, d);
        mm.invoke(k, d);
        mm.use(w, m);
        mm.invoke(w, m);
        mm.use(t, b);
        mm.invoke(t, b);
        mm.use(k, m);
        mm.invoke(k, m);
    }
}


/**
 * Just a bunch classes for the example -- not a good example of inheritance!
 */
class Door {
    @Override
    public String toString () {
        return "door";
    }
}

class Player {
    @Override
    public String toString () {
        return "monster";
    }
}

class Monster extends Player {
    @Override
    public String toString () {
        return "monster";
    }
}

class Bartender extends Monster {
    @Override
    public String toString () {
        return "bartender";
    }
}

class Item {
    @Override
    public String toString () {
        return "item";
    }
}

class Key extends Item {
    @Override
    public String toString () {
        return "key";
    }
}

class Treasure extends Item {
    @Override
    public String toString () {
        return "treasure";
    }
}

class Weapon extends Item {
    @Override
    public String toString () {
        return "weapon";
    }
}
