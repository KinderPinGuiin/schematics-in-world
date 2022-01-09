package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.nio.ByteBuffer;

public class TagShort extends Tag {

    public TagShort(BytesStream buffer) {
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

        // Lecture de la valeur associée (2 car Tag_SHORT)
        b = buffer.read(2);
        wrapped = ByteBuffer.wrap(b);
        value = wrapped.getShort();
    }
}
