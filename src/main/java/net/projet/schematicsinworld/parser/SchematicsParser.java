package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.tags.TagList;
import net.projet.schematicsinworld.parser.tags.TagListExtended;
import net.projet.schematicsinworld.parser.tags.Tags;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
import java.util.ArrayList;

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

    public void saveToNBT(String filepath) {
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

    private ArrayList<Tag> convertSchematicsToNBT() {
        ArrayList<Tag> res = new ArrayList<>();
        // DataVersion
        for (Tag t : this.tags) {
            if (t.getKey().equals("DataVersion")) {
                res.add(t);
                break;
            }
        }

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
        res.add(tl);
        return res;
    }


}
