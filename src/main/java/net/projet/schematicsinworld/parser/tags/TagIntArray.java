package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TagIntArray extends TagArray {

    public TagIntArray(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    public TagIntArray() {
        // Ne fait rien.
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        // Récupère le nombre d'éléments du tableau
        int n = this.getNbElems(buffer);
        // Lis tous les élements du tableau
        byte[] b = buffer.read(n * 4);
        // Copie les bytes dans un tableau de int
        int[] intArray = new int[n];
        for (int i = 0; i < n; ++i) {
            ByteBuffer wrapped = ByteBuffer.wrap(Arrays.copyOfRange(
                b, i * 4, i * 4 + 4
            ));
            intArray[i] = wrapped.getInt();
        }
        this.value = intArray;
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
            for (int x : (int[]) this.value) {
                dstream.writeInt(x);
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
