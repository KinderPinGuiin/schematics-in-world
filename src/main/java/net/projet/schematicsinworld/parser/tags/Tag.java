package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.nio.ByteBuffer;

public abstract class Tag implements ITag {

    /*
     * Attributs
     */

    protected String key;
    protected Object value;

    /*
     * Requêtes
     */

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    /*
     * Commandes
     */

    /**
     * Lit le bon nombre de données sur la chaîne contenu dans buffer afin de
     * définir les différentes données du tag.
     *
     * @param {StringStream} Le buffer contenant les prochaines données à lire.
     */
    protected abstract void parseBuffer(BytesStream buffer) throws ParserException;

    /**
     * Lit 2 + n octets sur buffer :
     * - 2 octet représentant la longueur de la clé.
     * - n octet représentant les n caractères du nom.
     *
     * @param {BytesStream} Le buffer.
     * @return {void} Le vide.
     */
    protected void setKey(BytesStream buffer) {
        // 2 octets contenant la longueur du nom
        byte[] b = buffer.read(2);
        // Récupère la clé en convertissant les 2 octets obtenus en un short
        b = buffer.read(ByteBuffer.wrap(b).getShort());
        this.key = new String(b);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }

}
