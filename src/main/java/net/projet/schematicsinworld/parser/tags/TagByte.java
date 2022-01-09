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
        // Lecture de la longueur de la chaîne de caractères (key)
        byte[] b = buffer.read(2);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        short length = wrapped.getShort();

        // Lecture de la chaîne de caractères (num octets)
        b = buffer.read(length);
        key = b.toString();

        // Lecture de la valeur associée (1 car Tag_BYTE)
        b = buffer.read(1);
        value = b[0];
    }
}
