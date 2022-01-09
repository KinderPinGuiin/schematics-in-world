package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.nio.ByteBuffer;

public class TagByte extends Tag {

    public TagByte(BytesStream buffer) {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture de la valeur associée (1 car Tag_BYTE)
        byte[] b = buffer.read(1);
        value = b[0];
    }
}
