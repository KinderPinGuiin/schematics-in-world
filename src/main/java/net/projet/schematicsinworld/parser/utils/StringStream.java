package net.projet.schematicsinworld.parser.utils;

public class StringStream {

    private long currIndex;
    private String str;

    public StringStream(String s, long i) {
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

    public long getIndex() {
        return currIndex;
    }

    public String getString() {
        return str;
    }

    public String read(long nBytes) {
        String s = str.substring((int)currIndex, (int)(currIndex + nBytes));
        currIndex += nBytes;
        return s;
    }

}
