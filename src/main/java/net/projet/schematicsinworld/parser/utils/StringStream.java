package net.projet.schematicsinworld.parser.utils;

public class StringStream {

    private int currIndex;
    private String str;

    public StringStream(String s, int i) {
        if (s == null) {
            throw new AssertionError("string is null");
        }
        if (i < 0 || i > s.length()) {
            throw new AssertionError("invalid index");
        }
        str = s;
        currIndex = i;
    }

    public StringStream(String s) {
        this(s, 0);
    }

    public StringStream() {
        this("", 0);
    }

    public int getIndex() {
        return currIndex;
    }

    public String getString() {
        return str;
    }

    public void setString(String str) {
        if (str == NULL) {
            throw new AssertionError("La chaîne ne doit pas être nulle");
        }
        this.str = str;
        this.currIndex = 0;
    }

    public String read(int nBytes) {
        if (this.currIndex + nBytes > this.str.length() - this.currIndex) {
            throw new AssertionError("Nombre de bytes à lire trop grand");
        }
        String s = str.substring(currIndex, currIndex + nBytes);
        currIndex += nBytes;
        return s;
    }

}
