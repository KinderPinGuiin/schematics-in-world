package net.projet.schematicsinworld.parser.utils;

import java.util.Arrays;

/**
 * Simule un flux d'octets où il est possible de lire un nombre souhaité de
 * données.
 */
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

    /**
     * @return L'index courant du curseur sur le flux.
     */
    public int getIndex() {
        return currIndex;
    }

    /**
     * @return Le contenu initial du flux.
     */
    public byte[] getString() {
        return bytes;
    }

    /**
     * Définit le nouveau contenu du flux et replace le curseur à 0.
     *
     * @param bytes Le tableau d'octets contenant le nouveau flux.
     */
    public void setBytes(byte[] bytes) {
        if (bytes == null) {
            throw new AssertionError("La chaîne ne doit pas être nulle");
        }
        this.bytes = bytes;
        this.currIndex = 0;
    }

    /**
     * Lit nBytes octets sur le flux de l'instance.
     *
     * @param nBytes Le nombre d'octets à lire sur le flux.
     * @return Une copie des octets lus sur le flux.
     */
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
