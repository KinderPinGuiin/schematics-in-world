package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class TagCompound extends Tag {

    public TagCompound(BytesStream buffer) throws ParserException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        // Lecture de la clé
        super.setKey(buffer);
        // Lecture des différentes valeurs du dictionnaire
        this.value = new ArrayList<Tag>();
        while (true) {
            byte id = buffer.read(1)[0];
            if (id == Tags.TAG_END.ordinal()) {
                break;
            } else {
                try {
                    // Récupère le tag associé à l'ID lu
                    Tags tagByOrd = Tags.getTagByOrd((int) id);
                    if (tagByOrd == null) {
                        throw new ParserException("Tag inconnu : " + id);
                    }
                    // Récupère la classe associé à ce tag
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
                } catch (NoSuchMethodException | ParserException |
                    InvocationTargetException | IllegalAccessException
                    | InstantiationException e) {
                    throw new ParserException(e.getMessage());
                }
            }
        }
    }

}
