package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.nio.ByteBuffer;

public class TagDouble extends Tag {

    public TagDouble(BytesStream buffer) {
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

        // Lecture de la valeur associée (8 car Tag_DOUBLE)
        b = buffer.read(8);
        wrapped = ByteBuffer.wrap(b);
        value = wrapped.getDouble();
    }
}
