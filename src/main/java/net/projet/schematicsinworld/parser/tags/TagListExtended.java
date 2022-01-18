package net.projet.schematicsinworld.parser.tags;

import java.util.ArrayList;

public class TagListExtended<E> extends TagList {

    /*
     * Attributs
     */
    private byte type;
    private long nbElem;
    private ArrayList<E> values;

    public TagListExtended(byte type, long nbElem) {
        if (type < 0) {
            throw new AssertionError("invalid type");
        }
        if (nbElem <= 0) {
            throw new AssertionError("invalid number of elements");
        }
        this.type = type;
        this.nbElem = nbElem;
        values = new ArrayList<>();
    }

    public void add(E value) {
        values.add(value);
    }
}
