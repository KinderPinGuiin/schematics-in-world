package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TagLongArray extends TagArray {

    public TagLongArray(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    public TagLongArray() {
        // Ne fait rien.
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        // Récupère le nombre d'éléments du tableau
        int n = this.getNbElems(buffer);
        // Lis tous les élements du tableau
        byte[] b = buffer.read(n * 8);
        // Copie les bytes dans un tableau de int
        long[] longArray = new long[n];
        for (int i = 0; i < n; ++i) {
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(
                b, i * 8, i * 8 + 8
            ));
            longArray[i] = wrapped.getLong();
        }
        this.value = longArray;
    }

    @Override
    protected void renderBuffer(BytesStream buffer) throws ParserException {
        super.renderKey(buffer);
        try {
            // Convertit la longueur en tableau de byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream dstream = new DataOutputStream(stream);
            dstream.writeInt(((int[]) this.value).length);
            // Ecrit chacune des valeurs du tableau
            for (long x : (long[]) this.value) {
                dstream.writeLong(x);
            }
            dstream.flush();
            // Ecrit la valeur complète dans le buffer
            buffer.write(stream.toByteArray());
        } catch (IOException e) {
            throw new ParserException("Impossible de parser la valeur "
                    + this.value);
        }
    }

}
