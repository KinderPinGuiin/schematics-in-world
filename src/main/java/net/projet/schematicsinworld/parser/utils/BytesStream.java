package net.projet.schematicsinworld.parser.utils;

import java.util.Arrays;

public class BytesStream {

    private int currIndex;
    private byte[] bytes;

    public BytesStream(byte[] b, int i) {
        if (b == null) {
            throw new AssertionError("string is null");
        }
        if (i < 0 || i > b.length) {
            throw new AssertionError("invalid index");
        }
        bytes = b;
        currIndex = i;
    }

    public BytesStream(byte[] b) {
        this(b, 0);
    }

    public BytesStream() {
        this(new byte[] {}, 0);
    }

    public int getIndex() {
        return currIndex;
    }

    public byte[] getString() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        if (bytes == null) {
            throw new AssertionError("La chaîne ne doit pas être nulle");
        }
        this.bytes = bytes;
        this.currIndex = 0;
    }

    public byte[] read(int nBytes) {
        if (this.currIndex + nBytes > this.bytes.length) {
            throw new AssertionError("Nombre de bytes à lire trop grand");
        }
        byte[] b = Arrays.copyOfRange(
            this.bytes, this.currIndex, this.currIndex + nBytes
        );
        currIndex += nBytes;
        return b;
    }

}
