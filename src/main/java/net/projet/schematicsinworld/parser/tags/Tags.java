package net.projet.schematicsinworld.parser.tags;

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

    private Class className;

    private Tags(Class aClass) {
        this.className = aClass;
    }
}
