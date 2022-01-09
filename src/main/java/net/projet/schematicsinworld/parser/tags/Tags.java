package net.projet.schematicsinworld.parser.tags;

public enum Tags {
    TAG_END(""),
    TAG_BYTE("TagByte"),
    TAG_SHORT("TagShort"),
    TAG_INT("TagInt"),
    TAG_LONG("TagLong"),
    TAG_FLOAT("TagFloat"),
    TAG_DOUBLE("TagDouble"),
    TAG_BYTE_ARRAY(""),
    TAG_STRING(""),
    TAG_LIST(""),
    TAG_COMPOUND("TagCompound"),
    TAG_INT_ARRAY(""),
    TAG_LONG_ARRAY("");

    private String className;

    private Tags(String className) {
        this.className = className;
    }
}
