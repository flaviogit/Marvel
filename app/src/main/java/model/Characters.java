package model;

/**
 * Created by Lillo on 21/10/2018.
 */

import java.util.List;

public class Characters extends MarvelCollection<Character> {
    public List<Character> getCharacters() {
        return getResults();
    }

    @Override public String toString() {
        return "Characters{"
                + "offset="
                + getOffset()
                + ", limit="
                + getLimit()
                + ", total="
                + getTotal()
                + ", count="
                + getCount()
                + ", characters="
                + getCharacters().toString()
                + '}';
    }
}