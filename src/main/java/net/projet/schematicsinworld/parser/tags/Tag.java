package net.projet.schematicsinworld.parser.tags;

import net.projet.schematicsinworld.parser.utils.ByteStream;

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
    public abstract String getKey();

    @Override
    public abstract Object getValue();

    /*
     * Commandes
     */

    /**
     * Lit le bon nombre de données sur la chaîne contenu dans buffer afin de
     * définir les différentes données du tag.
     *
     * @param {StringStream} Le buffer contenant les prochaines données à lire.
     */
    protected abstract void parseBuffer(ByteStream buffer);

}
