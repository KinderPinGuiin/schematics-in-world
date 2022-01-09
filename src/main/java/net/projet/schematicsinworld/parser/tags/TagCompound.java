package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.util.ArrayList;

public class TagCompound extends Tag {

    public TagCompound(BytesStream buffer) {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        parseBuffer(buffer);
    }

    public TagCompound() {
        // Ne fait rien.
    }

    @Override
    protected void parseBuffer(BytesStream buffer) {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture des différentes valeurs du dictionnaire
        this.value = new ArrayList<Tag>();
        while (true) {
            byte[] b = buffer.read(1);
            if (b[0] == Tags.TAG_END.ordinal()) {
                break;
            } else {

            }
        }
    }

}
