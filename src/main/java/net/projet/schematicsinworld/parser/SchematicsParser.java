package net.projet.schematicsinworld.parser;

import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.projet.schematicsinworld.parser.tags.*;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Convertit des fichiers .schem générés par WorldEdit en fichier .nbt
 * générés par des structures blocs.
 */
public class SchematicsParser {

    /*
     * CONSTANTES
     */

    public final static String BLOCKS = "BlockData";
    public final static String PALETTE = "Palette";

    /*
     * ATTRIBUTS
     */

    // Les tags parsés dans le fichier passé dans le constructeur
    private ArrayList<Tag> tags = null;
    // Le fichier schematic source
    private final File file;

    /*
     * CONSTRUCTEURS
     */

    public SchematicsParser(String filepath) {
        if (filepath == null) {
            throw new AssertionError("filepath is null");
        }
        file = new File(filepath);
        if (!file.isFile() || !file.canRead()) {
            throw new AssertionError("cannot open file");
        }

        try {
            NBTParser nbtp = new NBTParser(filepath);
            this.tags = nbtp.getTags();
        } catch (ParserException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * REQUETES
     */

    public File getFile() {
        return file;
    }

    /*
     * METHODES
     */

    public void parseBlocks() {
        for (Tag t : tags) {
            if (t.getKey() == BLOCKS) {
                Object[] values = (Object[]) t.getValue();
            }
        }
    }

    public void saveToNBT(String filepath) throws ParserException {
        // Convertit le fichier .schem en NBT de structure bloc
        ArrayList<Tag> tags = this.convertSchematicsToNBT();
        // Enregistre le fichier
        try {
            new NBTParser(filepath, 'w', tags);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    /*
     * Outils
     */

    private ArrayList<Tag> convertSchematicsToNBT() throws ParserException {
        ArrayList<Tag> res = new ArrayList<>();
        ArrayList<Tag> size = new ArrayList<>();
        for (int i = 0; i < 3; ++i) { size.add(new TagInt()); }
        for (Tag t : this.tags) {
            switch (t.getKey()) {
                case "DataVersion":
                    TagInt dataVersion = new TagInt();
                    dataVersion.setKey("DataVersion");
                    dataVersion.setValue(t.getValue());
                    res.add(dataVersion);
                    break;
                case "Palette":
                    this.convertPalette(res, (TagCompound) t);
                    break;
                case "Length":
                    size.get(0).setValue(((Short) t.getValue()).intValue());
                    break;
                case "Height":
                    size.get(1).setValue(((Short) t.getValue()).intValue());
                    break;
                case "Width":
                    size.get(2).setValue(((Short) t.getValue()).intValue());
                    break;
            }
        }
        this.convertEntities(res);
        // size
        TagList sizeTag = new TagList();
        sizeTag.setKey("size");
        sizeTag.setValue(size);
        res.add(sizeTag);
        /*
        // size
        TagListExtended<Integer> tl = new TagListExtended<Integer>((byte)Tags.TAG_INT.ordinal(), 3);
        int width = 0;
        int length = 0;
        int height = 0;
        int toFind = 3;
        for (Tag t : this.tags) {
            if (toFind == 0) {
                break;
            }
            if (t.getKey().equals("Height")) {
                height = (Integer)t.getValue();
                --toFind;
            } else if (t.getKey().equals("Length")) {
                length = (Integer)t.getValue();
                --toFind;
            } else if (t.getKey().equals("Width")) {
                width = (Integer)t.getValue();
                --toFind;
            }
        }
        // pas sûr de l'ordre
        tl.add(length);
        tl.add(height);
        tl.add(width);
        res.add(tl);*/
        return res;
    }

    @SuppressWarnings("unchecked")
    private void convertPalette(ArrayList<Tag> res, TagCompound schemPalette) throws ParserException {
        TagList palette = new TagList();
        ArrayList<Tag> paletteVal = new ArrayList<>();
        // Transforme le dictionnaire schemPalette en TagList de TagCompound
        ((ArrayList<Tag>) schemPalette.getValue()).sort(
            Comparator.comparingInt(o -> ((Integer) o.getValue()))
        );
        for (Tag t : (ArrayList<Tag>) schemPalette.getValue()) {
            // Sépare la clé du NBT et ses propriétés
            String[] key_prop = t.getKey().split("\\[");

            TagCompound tagCompound = new TagCompound();

            ArrayList<Tag> compoundVal = new ArrayList<>();
            TagString compoundValName = new TagString();
            compoundValName.setKey("Name");
            compoundValName.setValue(key_prop[0]);
            compoundVal.add(compoundValName);

            if (key_prop.length > 1) {
                key_prop[1] = key_prop[1].substring(0, key_prop[1].length() - 1);
                TagCompound props = new TagCompound();
                ArrayList<Tag> propsVal = new ArrayList<>();
                for (String prop : key_prop[1].split(",")) {
                    String[] prop_name_val = prop.split("=");
                    TagString propTagString = new TagString();
                    propTagString.setKey(prop_name_val[0]);
                    propTagString.setValue(prop_name_val[1]);
                    propsVal.add(propTagString);
                }
                props.setKey("Properties");
                props.setValue(propsVal);
                compoundVal.add(props);
            }

            tagCompound.setValue(compoundVal);
            paletteVal.add(tagCompound);
        }
        // Ajoute la liste au résultat
        palette.setKey("palette");
        palette.setValue(paletteVal);
        res.add(palette);
    }

    private void convertEntities(ArrayList<Tag> res) {
        TagList entities = new TagList();
        res.add(entities);
    }

    private void convertBlocks(ArrayList<Tag> res, TagCompound schemBlocks) {

    }

}
