package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

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
        byte[] restrict = new byte[4];
        int[] intArray = new int[n];
        // Copie les bytes dans un tableau de int
        // TODO : à optimiser
        int ind = 0;
        for (int i = 0; i < n; i += 4) {
            for (int j = 0; j < 4; ++j) {
                restrict[j] = b[i + j];
            }
            ByteBuffer wrapped = ByteBuffer.wrap(restrict);
            intArray[ind] = wrapped.getInt();
            ++ind;
        }
        this.value = intArray;
    }
}
