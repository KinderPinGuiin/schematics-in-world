package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TagString extends Tag {

    public TagString(BytesStream buffer) {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    public TagString() {
        // Ne fait rien.
    }

    @Override
    protected void parseBuffer(BytesStream buffer) {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture de la longueur de la chaîne
        byte[] b = buffer.read(2);
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        short lenInBytes = wrapped.getShort();
        // Lecture de la chaîne
        b = buffer.read(lenInBytes);
        this.value = new String(b);
    }

    @Override
    protected void renderBuffer(BytesStream buffer) throws ParserException {
        super.renderKey(buffer);
        try {
            // Convertit la longueur et la valeur en tableau de byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream dstream = new DataOutputStream(stream);
            dstream.writeShort((short) ((String) this.value).length());
            dstream.flush();
            // Ecrit la longueur
            buffer.write(stream.toByteArray());
            buffer.write(((String) this.value).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ParserException("Impossible de parser la valeur \""
                    + this.value + "\"");
        }
    }
}
