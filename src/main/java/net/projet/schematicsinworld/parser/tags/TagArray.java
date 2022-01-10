package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.nio.ByteBuffer;

public abstract class TagArray extends Tag {

    protected int getNbElems(BytesStream buffer) {
        // Taille à lire
        byte[] b = buffer.read(4);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        return wrapped.getInt();
    }
}
