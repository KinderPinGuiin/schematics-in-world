package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

public class TagInt extends Tag {

    public TagInt(BytesStream buffer) {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture de la valeur associée (4 car Tag_INT)
        byte[] b = buffer.read(4);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        this.value = wrapped.getInt();
    }

    @Override
    protected void renderBuffer(BytesStream buffer) throws ParserException {

    }
}
