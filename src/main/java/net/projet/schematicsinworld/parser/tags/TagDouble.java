package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

public class TagDouble extends Tag {

    public TagDouble(BytesStream buffer) {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture de la valeur associée (8 car Tag_DOUBLE)
        byte[] b = buffer.read(8);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        this.value = wrapped.getDouble();
    }

    @Override
    protected void renderBuffer(BytesStream buffer) throws ParserException {

    }

}
