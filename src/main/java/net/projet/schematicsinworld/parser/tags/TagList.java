package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TagList extends Tag {

    public TagList(BytesStream buffer) throws ParserException, NoSuchMethodException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        // Type stocké dans la liste
        byte type = buffer.read(1)[0];
        // Longueur de la liste
        int len = ByteBuffer.wrap(buffer.read(4)).getInt();
        if (len <= 0) {
            //jsp trop quoi faire mais ils en parlent dans la doc
        }
        this.value = new ArrayList<Tag>();
        int k = 0;
        while (k < len) {
            try {
                Tags tagByOrd = Tags.getTagByOrd((int) type);
                if (tagByOrd == null) {
                    throw new ParserException("Tag inconnu : " + type);
                }
                // Récupère la classe associée à ce tag
                Class<Tag> tagClass = tagByOrd.getTagClass();
                if (tagClass == null) {
                    throw new ParserException(
                            "La classe associée à un tag est nulle"
                    );
                }
                // Récupère le constructeur correspondant à la classe
                Constructor<Tag> c =
                        tagClass.getDeclaredConstructor(BytesStream.class);
                // Instancie le tag afin de le parser
                Tag tag = c.newInstance(buffer);
                // Ajoute le tag parsé à la liste
                ((ArrayList<Tag>) this.value).add(tag);
                ++k;
            } catch (NoSuchMethodException | ParserException |
                    InvocationTargetException | IllegalAccessException
                    | InstantiationException e) {
                throw new ParserException(e.getMessage());
            }
        }
    }
}
