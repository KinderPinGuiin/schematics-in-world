package net.projet.schematicsinworld.parser.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Simule un flux d'octets où il est possible de lire un nombre souhaité de
 * données.
 */
public class BytesStream {

    /*
     * Constantes
     */

    public static final char READ_MODE = 'r';
    public static final char WRITE_MODE = 'w';

    /*
     * Attributs
     */

    private char mode;
    private int currIndex;
    private ArrayList<Byte> bytes;

    /*
     * Constructeurs
     */

    public BytesStream(byte[] ba, int i, char mode) {
        if (ba == null) {
            throw new AssertionError("string is null");
        }
        if (i < 0 || i > ba.length) {
            throw new AssertionError("invalid index");
        }
        this.fillList(ba);
        this.currIndex = i;
        this.mode = mode;
    }

    public BytesStream(byte[] b) {
        this(b, 0 , READ_MODE);
    }

    public BytesStream(char mode) {
        this(new byte[] {}, 0, mode);
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
     * Définit le nouveau contenu du flux et replace le curseur à
     * ou à bytes.length si le mode du flux est WRITE_MODE.
     *
     * @param bytes Le tableau d'octets contenant le nouveau flux.
     * @param index Le nouveau curseur.
     */
    public void setBytes(byte[] bytes, int index) {
        if (bytes == null) {
            throw new AssertionError("La chaîne ne doit pas être nulle");
        }
        this.fillList(bytes);
        this.currIndex = this.mode == READ_MODE ? index : bytes.length;
    }

    /**
     * Définit le nouveau contenu du flux et replace le curseur à 0 ou à
     * bytes.length si le mode du flux est WRITE_MODE.
     *
     * @param bytes Le tableau d'octets contenant le nouveau flux.
     */
    public void setBytes(byte[] bytes) {
        this.setBytes(bytes, this.mode == READ_MODE ? 0 : bytes.length);
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
        if (this.mode == WRITE_MODE) {
            throw new AssertionError("Le flux est en mode écriture");
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
        if (this.mode == READ_MODE) {
            throw new AssertionError("Le flux est en mode lecture");
        }
        for (int i = 0; i < bytes.length; ++i) {
            this.bytes.add(bytes[i]);
        }
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
