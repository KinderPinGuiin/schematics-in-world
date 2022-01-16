package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TagList extends Tag {

    /*
     * Constructeurs
     */

    public TagList(BytesStream buffer) throws ParserException, NoSuchMethodException {
        if (buffer == null) {
            throw new AssertionError("buffer is null");
        }
        this.parseBuffer(buffer);
    }

    public TagList() {
        // Ne fait rien.
    }

    /*
     * Commandes
     */

    @Override
    protected void parseBuffer(BytesStream buffer) throws ParserException {
        super.setKey(buffer);
        // Type stocké dans la liste
        byte type = buffer.read(1)[0];
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
        // Longueur de la liste
        int len = ByteBuffer.wrap(buffer.read(4)).getInt();
        // Parse la liste
        this.value = new ArrayList<Tag>();
        for (int k = 0; k < len; ++k) {
            try {
                // Récupère le constructeur correspondant à la classe
                Constructor<Tag> c =
                        tagClass.getDeclaredConstructor();
                // Instancie le tag afin de le parser
                Tag tag = c.newInstance();
                // Définit la clé du tag à k
                tag.setKey(String.valueOf(k));
                // Parse le tag correspondant
                tag.parseBuffer(buffer);
                // Ajoute le tag parsé à la liste
                ((ArrayList<Tag>) this.value).add(tag);
            } catch (NoSuchMethodException | ParserException |
                    InvocationTargetException | IllegalAccessException
                    | InstantiationException e) {
                throw new ParserException(e.getMessage());
            }
        }
    }

    @Override
    protected void renderBuffer(BytesStream buffer) throws ParserException {
        super.renderKey(buffer);
        try {
            // Convertit le type et la longueur de la liste en tableau de byte
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream dstream = new DataOutputStream(stream);
            int type;
            if (((ArrayList<Tag>) this.value).size() > 0) {
                type = Tags.getOrdByClass(
                    ((ArrayList<Tag>) this.value).get(0).getClass()
                );
            } else {
                type = 0;
            }
            dstream.writeByte((byte) type);
            dstream.writeInt(((ArrayList<Tag>) this.value).size());
            dstream.flush();
            // Ecrit la valeur complète dans le buffer
            buffer.write(stream.toByteArray());
            // Ecrit chacune des valeurs du tableau dans le buffer
            for (Tag tag : (ArrayList<Tag>) this.value) {
                tag.setKeyNoRender();
                tag.renderBuffer(buffer);
            }
        } catch (IOException e) {
            throw new ParserException("Impossible de parser la valeur "
                    + this.value);
        }
    }

}
