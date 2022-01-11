package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Classe mère des tags de type tableau :
 * <ul>
 *     <li>TagByteArray</li>
 *     <li>TagIntArray</li>
 *     <li>TagLongArray</li>
 * </ul>
 */
public abstract class TagArray extends Tag {

    /**
     * @param buffer Le buffer sur lequel lire la taille du tableau
     * @return Le nombre d'éléments du tableau.
     */
    protected int getNbElems(BytesStream buffer) {
        // Taille à lire
        byte[] b = buffer.read(4);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        return wrapped.getInt();
    }

}
