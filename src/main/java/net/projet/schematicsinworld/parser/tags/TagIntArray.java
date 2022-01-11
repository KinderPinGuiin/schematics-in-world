package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class TagIntArray extends TagArray {

    public TagIntArray(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
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
}
