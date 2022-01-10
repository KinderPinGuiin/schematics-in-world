package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

public class TagLongArray extends TagArray {

    public TagLongArray(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        int n = this.getNbElems(buffer);
        byte[] b = buffer.read(this.getNbElems(buffer) * 8);
        byte[] restrict = new byte[8];
        long[] longArray = new long[n];
        int ind = 0;
        for (int i = 0; i < n; i += 8) {
            for (int j = 0; j < 8; ++j) {
                restrict[j] = b[i + j];
            }
            ByteBuffer wrapped = ByteBuffer.wrap(restrict);
            longArray[ind] = wrapped.getLong();
            ++ind;
        }
        this.value = longArray;
    }
}
