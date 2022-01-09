package net.projet.schematicsinworld.parser.tags;

/**
 * Liste des tags disponibles dans un fichier NBT. Les tags seront associés à
 * leur classe.
 *
 * @see <a href="https://wiki.vg/NBT">Spécification des fichiers NBT</a>
 */
public enum Tags {

    TAG_END(null),
    TAG_BYTE(TagByte.class),
    TAG_SHORT(TagShort.class),
    TAG_INT(TagInt.class),
    TAG_LONG(TagLong.class),
    TAG_FLOAT(TagFloat.class),
    TAG_DOUBLE(TagDouble.class),
    TAG_BYTE_ARRAY(null),
    TAG_STRING(null),
    TAG_LIST(null),
    TAG_COMPOUND(TagCompound.class),
    TAG_INT_ARRAY(null),
    TAG_LONG_ARRAY(null);

    private Class aClass;

    private Tags(Class aClass) {
        this.aClass = aClass;
    }

    /*
     * Requêtes
     */

    /**
     * Renvoie le tag associée à l'identifiant ord.
     *
     * @param {int} L'ID du tag.
     * @return {Tags} Le tag associé. Retourne null si aucun tag n'a été trouvé.
     */
    public Tags getTagByOrd(int ord) {
        for (Tags tag : Tags.values()) {
            if (tag.ordinal() == ord) {
                return tag;
            }
        }

        return null;
    }

    /**
     * @return {Class} La classe associée au tag.
     */
    public Class getTagClass() {
        return this.aClass;
    }

}
