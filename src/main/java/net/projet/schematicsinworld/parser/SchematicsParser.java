package net.projet.schematicsinworld.parser;

import net.minecraftforge.common.util.Constants;
import net.projet.schematicsinworld.commons.BlockData;
import net.projet.schematicsinworld.commons.EntityData;
import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SchematicsParser {

    /*
     * CONSTANTES
     */

    public final static String BLOCKS = "BlockData";
    public final static String PALETTE = "Palette";

    /*
     * ATTRIBUTS
     */

    private ArrayList<BlockData> blocks;
    private ArrayList<EntityData> entities;
    private ArrayList<Tag> tags;

    // à voir
    private File file;

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

        blocks = new ArrayList<>();
        entities = new ArrayList<>();

        NBTParser nbtp = null;
        try {
            nbtp = new NBTParser(filepath);
        } catch (ParserException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        tags = nbtp.getTags();
    }

    /*
     * REQUETES
     */
    public ArrayList<BlockData> getBlocks() {
        return new ArrayList<>(blocks);
    }

    public ArrayList<EntityData> getEntities() {
        return new ArrayList<>(entities);
    }

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

}
