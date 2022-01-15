package net.projet.schematicsinworld.parser.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simule un flux d'octets où il est possible de lire un nombre souhaité de
 * données.
 */
public class BytesStream {

    private int currIndex;
    private ArrayList<Byte> bytes;

    public BytesStream(byte[] ba, int i) {
        if (ba == null) {
            throw new AssertionError("string is null");
        }
        if (i < 0 || i > ba.length) {
            throw new AssertionError("invalid index");
        }
        this.fillList(ba);
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
     * @return Le contenu flux (Même la partie déjà lue).
     */
    public byte[] getContent() {
        byte[] bytes = new byte[this.bytes.size()];
        int i = 0;
        for (Byte b : this.bytes) {
            bytes[i] = b;
            ++i;
        }
        return bytes;
    }

    /**
     * Définit le nouveau contenu du flux et replace le curseur à index.
     *
     * @param bytes Le tableau d'octets contenant le nouveau flux.
     * @param index Le nouveau curseur.
     */
    public void setBytes(byte[] bytes, int index) {
        if (bytes == null) {
            throw new AssertionError("La chaîne ne doit pas être nulle");
        }
        this.fillList(bytes);
        this.currIndex = index;
    }

    /**
     * Définit le nouveau contenu du flux et replace le curseur à 0.
     *
     * @param bytes Le tableau d'octets contenant le nouveau flux.
     */
    public void setBytes(byte[] bytes) {
        this.setBytes(bytes, 0);
    }

    /**
     * Lit nBytes octets sur le flux de l'instance.
     *
     * @param nBytes Le nombre d'octets à lire sur le flux.
     * @return Une copie des octets lus sur le flux.
     */
    public byte[] read(int nBytes) {
        if (this.currIndex + nBytes > this.bytes.size()) {
            throw new AssertionError("Nombre de bytes à lire trop grand");
        }
        byte[] b = new byte[nBytes];
        for (int i = 0; i < nBytes; ++i) {
            b[i] = this.bytes.get(this.currIndex + i);
        }
        currIndex += nBytes;
        return b;
    }

    /**
     * Ecrit les octets contenu dans le tableau bytes dans le flux.
     *
     * @param bytes Les octets à écrire
     */
    public void write(byte[] bytes) {

    }

    /*
     * Outils
     */

    private void fillList(byte[] ba) {
        this.bytes = new ArrayList<Byte>();
        for (byte b : ba) {
            this.bytes.add(b);
        }
    }

}
