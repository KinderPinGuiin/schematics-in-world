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
    TAG_BYTE_ARRAY(TagByteArray.class),
    TAG_STRING(TagString.class),
    TAG_LIST(null),
    TAG_COMPOUND(TagCompound.class),
    TAG_INT_ARRAY(TagIntArray.class),
    TAG_LONG_ARRAY(TagLongArray.class);

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
    public static Tags getTagByOrd(int ord) {
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
    public Class<Tag> getTagClass() {
        return this.aClass;
    }

}
