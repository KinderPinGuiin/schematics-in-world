package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

public class TagIntArray extends TagArray {

    public TagIntArray(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        int n = this.getNbElems(buffer);
        byte[] b = buffer.read(n * 4);
        byte[] restrict = new byte[4];
        int[] intArray = new int[n];

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
