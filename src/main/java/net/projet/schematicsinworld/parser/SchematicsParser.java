package net.projet.schematicsinworld.parser;

import jdk.nashorn.internal.ir.Block;
import net.projet.schematicsinworld.parser.tags.*;
import net.projet.schematicsinworld.parser.utils.BlockData;
import net.projet.schematicsinworld.parser.utils.JigsawOrientations;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

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
    public final static int MAX_SIZE = 32;
    public final static String BLOCKS = "BlockData";
    public final static String PALETTE = "Palette";
    public final static String JOINT = "rollable";
    public final static String JIGSAW_ID = "minecraft:jigsaw";
    public final static String EMPTY_ID = "minecraft:empty";

    /*
     * ATTRIBUTS
     */

    // Inutiles si jme trompe pas ces attributs ?

    // Les tags parsés dans le fichier passé dans le constructeur
    private ArrayList<Tag> tags = null;
    // Le fichier schematic source
    private final File file;

    /*
     * CONSTRUCTEURS
     */

    /*
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
     */

    private void addJigsawsInPalette(ArrayList<Tag> paletteVal) throws ParserException {
        JigsawOrientations[] Orientations = JigsawOrientations.values();
        TagString compoundValName = new TagString();
        compoundValName.setKey("Name");
        compoundValName.setValue("minecraft:jigsaw");
        for(JigsawOrientations orientation : Orientations) {
            TagCompound tagCompound = new TagCompound();
            ArrayList<Tag> compoundVal = new ArrayList<>();
            compoundVal.add(compoundValName);
            TagCompound props = new TagCompound();
            ArrayList<Tag> propsVal = new ArrayList<>();
            TagString propTagString = new TagString();
            propTagString.setKey("orientation");
            propTagString.setValue(orientation.getId());
            propsVal.add(propTagString);
            props.setKey("Properties");
            props.setValue(propsVal);
            compoundVal.add(props);
            tagCompound.setValue(compoundVal);
            paletteVal.add(tagCompound);
        }
    }

    private void addJigsawInBlocks(BlockData blockData,
                                   JigsawOrientations orientation,
                                   int nElemPalette,
                                   int x, int y, int z,
                                   String name, String pool, String target) {
        int state = orientation.ordinal() + nElemPalette;

    }

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

    // Inutile ?
    public File getFile() {
        return file;
    }

    /*
     * METHODES
     */

    // Inutile ?
    public void parseBlocks() {
        for (Tag t : tags) {
            if (t.getKey() == BLOCKS) {
                Object[] values = (Object[]) t.getValue();
            }
        }
    }

    // Convertit le fichier .schem en une liste de .nbt (de longueur > 1 si la taille de la
    // structure dépasse la taille autorisée)

    /*
    public void saveToNBT(String filepath) throws ParserException {
        // Convertit le fichier .schem en NBT de structure bloc
        ArrayList<ArrayList<Tag>> tags = this.convertSchematicsToNBT();
        // Enregistre le fichier
        try {
            new NBTParser(filepath, 'w', tags.get(0));
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }
     */

    public void saveToNBT(String filepath) throws ParserException {
        ArrayList<ArrayList<Tag>> tags = this.convertSchematicsToNBT();
        for (int i = 0; i < tags.size(); ++i) {
            // Enregistre le fichier
            try {
                new NBTParser(filepath + "_" + i + ".nbt", 'w', tags.get(i));
            } catch (ParserException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Outils
     */

    /*
    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<Tag>> convertSchematicsToNBT() throws ParserException {
        ArrayList<ArrayList<Tag>> results = new ArrayList<>();
        ArrayList<Tag> res = new ArrayList<>();
        ArrayList<Tag> size = new ArrayList<>();
        for (int i = 0; i < 3; ++i) { size.add(new TagInt()); }
        byte[] blocks = new byte[] {};
        ArrayList<TagCompound> blockEntities = null;
        for (Tag t : this.tags) {
            switch* (t.getKey()) {
                case "DataVersion":
                    TagInt dataVersion = new TagInt();
                    dataVersion.setKey("DataVersion");
                    dataVersion.setValue(t.getValue());
                    res.add(dataVersion);
                    break;
                case PALETTE:
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
                case "BlockData":
                    blocks = (byte[]) t.getValue();
                    break;
                case "BlockEntities":
                    blockEntities = (ArrayList<TagCompound>) t.getValue();
                    break;
            }
        }
        // entities
        this.convertEntities(res);

        // blocks
        ArrayList<BlockData> blockData = this.convertBlocks(blocks, blockEntities, size);
        TagList blocksTag = new TagList();
        blocksTag.setKey("blocks");
        ArrayList<TagCompound> blocksList = new ArrayList<>();
        for (BlockData bd : blockData) {
            TagCompound tc = new TagCompound();
            ArrayList<Tag> tcList = new ArrayList<>();
            // state
            TagInt state = new TagInt();
            state.setKey("state");
            state.setValue(bd.getState());
            tcList.add(state);
            // pos
            TagList tl = new TagList();
            tl.setKey("pos");
            ArrayList<TagInt> coords = new ArrayList<>();
            for(Integer i : bd.getCoords()) {
                TagInt ti = new TagInt();
                ti.setValue(i);
                coords.add(ti);
            }
            tl.setValue(coords);
            tcList.add(tl);
            // nbt
            if (bd.getNbt().size() > 0) {
                TagCompound nbt = new TagCompound();
                ArrayList<Tag> nbtValues = new ArrayList<>();
                nbt.setKey("nbt");
                for (String key : bd.getNbt().keySet()) {
                    nbtValues.add(bd.getNbt().get(key));
                }
                nbt.setValue(nbtValues);
                tcList.add(nbt);
            }
            // fin
            tc.setValue(tcList);
            blocksList.add(tc);
        }
        blocksTag.setValue(blocksList);
        res.add(blocksTag);

        // size
        TagList sizeTag = new TagList();
        sizeTag.setKey("size");
        sizeTag.setValue(size);
        res.add(sizeTag);

        results.add(res);

        return results;
    }
     */

    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<Tag>> convertSchematicsToNBT() throws ParserException {
        ArrayList<ArrayList<Tag>> results = new ArrayList<>();
        TagInt dataVersion = new TagInt();
        TagList palette = new TagList();
        TagList entities = new TagList();
        ArrayList<Tag> size = new ArrayList<>();

        for (int i = 0; i < 3; ++i) { size.add(new TagInt()); }
        byte[] blocks = new byte[] {};
        ArrayList<TagCompound> blockEntities = null;
        for (Tag t : this.tags) {
            switch (t.getKey()) {
                case "DataVersion":
                    dataVersion.setKey("DataVersion");
                    dataVersion.setValue(t.getValue());
                    break;
                case PALETTE:
                    this.convertPalette(palette, (TagCompound) t);
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
                case "BlockData":
                    blocks = (byte[]) t.getValue();
                    break;
                case "BlockEntities":
                    blockEntities = (ArrayList<TagCompound>) t.getValue();
                    break;
            }
        }
        // entities
        this.convertEntities(entities);

        // blocks
        ArrayList<BlockData>[][] blockData = this.convertBlocks(blocks, blockEntities, size);
        int nbX = blockData.length;
        int nbZ = blockData[0].length;

        // Parcours de toutes les sous-structures
        for (int i = 0; i < nbX; ++i) {
            for (int j = 0; j < nbZ; ++j) {
                ArrayList<Tag> structNbt = new ArrayList<>();
                // --- Partie "commune" ---

                // DataVersion tag
                structNbt.add(dataVersion);
                // Palette tag
                structNbt.add(palette);
                // Entities tag
                structNbt.add(entities);

                // Size tag
                TagList sizeTag = new TagList();
                sizeTag.setKey("size");
                // Size modulée
                ArrayList<TagInt> curSize = new ArrayList<>();
                for (int k = 0; k < 3; ++k) { curSize.add(new TagInt()); }
                // Size en x
                int sizeX = i < nbX - 1 ? MAX_SIZE : (int) size.get(0).getValue() % MAX_SIZE;
                curSize.get(0).setValue(sizeX);
                // Size en z
                int sizeZ = j < nbZ - 1 ? MAX_SIZE : (int) size.get(2).getValue() % MAX_SIZE;
                curSize.get(2).setValue(sizeZ);
                curSize.get(1).setValue((int) size.get(1).getValue());
                sizeTag.setValue(curSize);
                structNbt.add(sizeTag);

                // --- Partie blocks ---

                // TagList d'id 'blocks' (la TagList principale contenant les blocks)
                TagList blocksTag = new TagList();
                blocksTag.setKey("blocks");

                // Future valeur de la TagList blocksTag
                ArrayList<TagCompound> blocksList = new ArrayList<>();

                // Parcours des BlockData du ArrayList contenu dans la sous-structure étudiée
                for (BlockData bd : blockData[i][j]) {
                    // TagCompound contenant les données d'un bloc de blockData
                    TagCompound tc = new TagCompound();

                    // ArrayList de Tag contenant les tags propres au bloc (valeur de tc)
                    ArrayList<Tag> tcList = new ArrayList<>();

                    // State tag
                    TagInt state = new TagInt();
                    state.setKey("state");
                    state.setValue(bd.getState());
                    tcList.add(state);

                    // Pos tag
                    TagList tl = new TagList();
                    tl.setKey("pos");
                    ArrayList<TagInt> coords = new ArrayList<>();
                    for(Integer coord : bd.getCoords()) {
                        TagInt ti = new TagInt();
                        ti.setValue(coord);
                        coords.add(ti);
                    }
                    tl.setValue(coords);
                    tcList.add(tl);

                    // Nbt tag (si existant)
                    if (bd.getNbt().size() > 0) {
                        TagCompound nbt = new TagCompound();
                        ArrayList<Tag> nbtValues = new ArrayList<>();
                        nbt.setKey("nbt");
                        for (String key : bd.getNbt().keySet()) {
                            nbtValues.add(bd.getNbt().get(key));
                        }
                        nbt.setValue(nbtValues);
                        tcList.add(nbt);
                    }

                    // On définit la valeur de tc ici
                    tc.setValue(tcList);
                    // On ajoute ce TagCompound à la liste
                    blocksList.add(tc);
                }

                // On définit cette liste de TagCompound comme valeur de blocksTag
                blocksTag.setValue(blocksList);

                structNbt.add(blocksTag);
                results.add(structNbt);
            }
        }
        return results;
    }

    /*
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
     */

    @SuppressWarnings("unchecked")
    private void convertPalette(TagList palette, TagCompound schemPalette) throws ParserException {
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

        palette.setKey("palette");
        addJigsawsInPalette(paletteVal);
        palette.setValue(paletteVal);
    }

    /*
    private void convertEntities(ArrayList<Tag> res) throws ParserException {
        TagList entities = new TagList();
        entities.setKey("entities");
        entities.setValue(new ArrayList<Tag>());
        res.add(entities);
    }
     */

    private void convertEntities(TagList entities) throws ParserException {
        entities.setKey("entities");
        entities.setValue(new ArrayList<Tag>());
    }

    /*
    @SuppressWarnings("unchecked")
    private ArrayList<BlockData> convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities, ArrayList<Tag> size) {
        ArrayList<BlockData> blocksVal = new ArrayList<>();
        int i = 0;
        for (byte b : blockData) {
            int nextX = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) % (int) size.get(2).getValue();
            int nextY = i / ((int) size.get(2).getValue() * (int) size.get(0).getValue());
            int nextZ = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) / (int) size.get(2).getValue();
            HashMap<String, Tag> nbt = new HashMap<>();
            boolean isHere = false;
            for (TagCompound tc : blockEntities) {
                if (isHere) {
                    break;
                }
                nbt = new HashMap<>();
                for (Tag t : (ArrayList<Tag>) tc.getValue()) {
                    if (t.getKey().equals("Pos")) {
                        int[] tagPos = (int[]) t.getValue();
                        if (tagPos[0] == nextX && tagPos[1] == nextY && tagPos[2] == nextZ) {
                            isHere = true;
                            continue;
                        }
                        break;
                    }
                    if (t.getKey().equals("Id")) {
                        t.setKey("id");
                        nbt.put("id", t);
                    } else {
                        nbt.put(t.getKey(), t);
                    }
                }
            }
            BlockData bd = new BlockData(nextX, nextY, nextZ, b, isHere ? nbt : new HashMap<>());
            blocksVal.add(bd);
            ++i;
        }
        return blocksVal;
    }
     */

    @SuppressWarnings("unchecked")
    private ArrayList<BlockData>[][] convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities, ArrayList<Tag> size) {
        // Permet de connaître le nombre de chunks pris par la structure en X et en Z
        int nbStructX = (int) size.get(0).getValue() / MAX_SIZE;
        nbStructX += ((int) size.get(0).getValue() % MAX_SIZE) == 0 ? 0 : 1;
        int nbStructZ = (int) size.get(2).getValue() / MAX_SIZE;
        nbStructZ += ((int) size.get(2).getValue() % MAX_SIZE) == 0 ? 0 : 1;
        ArrayList<BlockData> dummy = new ArrayList<>();
        int[] nbStruct = {nbStructX, nbStructZ};
        ArrayList<BlockData>[][] blocksVal = (ArrayList<BlockData>[][]) Array.newInstance(dummy.getClass(), nbStruct);

        for (int i = 0; i < nbStructX; ++i) {
            for (int j = 0; j < nbStructZ; ++j) {
                blocksVal[i][j] = new ArrayList<>();
            }
        }

        int i = 0;
        for (byte b : blockData) {
            int x = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) % (int) size.get(2).getValue();
            int y = i / ((int) size.get(2).getValue() * (int) size.get(0).getValue());
            int z = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) / (int) size.get(2).getValue();
            int structX = x / MAX_SIZE;
            int structZ = z / MAX_SIZE;

            HashMap<String, Tag> nbt = new HashMap<>();
            boolean isHere = false;
            for (TagCompound tc : blockEntities) {
                if (isHere) {
                    break;
                }
                nbt = new HashMap<>();
                if (structX < nbStructX - 1 && (x % MAX_SIZE) == (MAX_SIZE - 1)) {
                    JigsawOrientations orientation = JigsawOrientations.EAST;
                    addJigsawInBlocks(nbt, orientation, palette.getLen(),
                            (x % MAX_SIZE), y, (z % MAX_SIZE));
                } else if (structZ < nbStructZ - 1 && (z % MAX_SIZE) == (MAX_SIZE - 1)) {
                    JigsawOrientations orientation = JigsawOrientations.EAST;
                    addJigsawInBlocks(nbt, orientation, palette.getLen(),
                            (x % MAX_SIZE), y, (z % MAX_SIZE));
                } else if (structX != 0 || structZ != 0) {
                    JigsawOrientations orientation = JigsawOrientations.EAST;
                    addJigsawInBlocks(nbt, orientation, palette.getLen(),
                            (x % MAX_SIZE), y, (z % MAX_SIZE));
                }
                for (Tag t : (ArrayList<Tag>) tc.getValue()) {
                    if (t.getKey().equals("Pos")) {
                        int[] tagPos = (int[]) t.getValue();
                        if (tagPos[0] == x && tagPos[1] == y && tagPos[2] == z) {
                            isHere = true;
                            continue;
                        }
                        break;
                    }
                    if (t.getKey().equals("Id")) {
                        t.setKey("id");
                        nbt.put("id", t);
                    } else {
                        nbt.put(t.getKey(), t);
                    }
                }
            }
            BlockData bd = new BlockData(x % MAX_SIZE, y, z % MAX_SIZE, b, isHere ? nbt : new HashMap<>());

            // Pour déterminer dans quelle sous-structure on ajoute ce bloc
            System.out.println("cpt : " + i + " / " + blockData.length);
            System.out.println("sx et sz : " + structX + " " + structZ);
            blocksVal[structZ][structX].add(bd);

            ++i;
        }
        return blocksVal;
    }
}
