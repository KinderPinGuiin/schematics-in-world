package net.projet.schematicsinworld.parser;

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

    public final static String PALETTE = "Palette";
    public final static int MAX_SIZE = 100000;
    public final static String JOINT = "rollable";
    public final static String JIGSAW_ID = "minecraft:jigsaw";
    public final static String EMPTY_ID = "minecraft:empty";
    public final static String FINAL_STATE = "minecraft:obsidian";

    /*
     * ATTRIBUTS
     */

    // Les tags parsés dans le fichier passé dans le constructeur
    private ArrayList<Tag> tags;
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

    public SchematicsParser(String filepath) {
        if (filepath == null) {
            throw new AssertionError("filepath is null");
        }
        file = new File(filepath);
        if (!file.isFile() || !file.canRead()) {
            throw new AssertionError("cannot open file");
        }
        try {
            System.out.println("CHIBRAX TMAX");
            NBTParser nbtp = new NBTParser(filepath);
            System.out.println("CHIBRAX TMAX");
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
        ArrayList<Tag>[][] structs = this.convertSchematicsToNBT();
        for (int i = 0; i < structs.length; ++i) {
            for (int j = 0; j < structs[0].length; ++j) {
                System.out.println("coucou2 : " + i + ", " + j);
                System.out.println(filepath);
                System.out.println(file.getName());
                // Enregistre le fichier
                try {
                    new NBTParser(filepath + "_" + j + "_" + i + ".nbt", 'w', structs[i][j]);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
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
    private ArrayList<Tag>[][] convertSchematicsToNBT() throws ParserException {
        TagInt dataVersion = new TagInt();
        TagList palette = new TagList();
        int paletteLength = 0;
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
                    paletteLength = this.convertPalette(palette, (TagCompound) t);
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

        ArrayList<BlockData>[][] blockData = this.convertBlocks(blocks, blockEntities, size, palette, paletteLength);
        int nbX = blockData.length;
        int nbZ = blockData[0].length;
        ArrayList<Tag> dummy = new ArrayList<>();
        int[] nbStruct = {nbX, nbZ};
        ArrayList<Tag>[][] results = (ArrayList<Tag>[][]) Array.newInstance(dummy.getClass(), nbStruct);

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
                int x = (int) size.get(0).getValue();
                int z = (int) size.get(2).getValue();
                System.out.println(x + " et " + z);
                int sizeX = (i < nbX - 1 || x % MAX_SIZE == 0) ? MAX_SIZE : x % MAX_SIZE;
                curSize.get(2).setValue(sizeX);
                // Size en z
                int sizeZ = (j < nbZ - 1 || z % MAX_SIZE == 0) ? MAX_SIZE : z % MAX_SIZE;
                curSize.get(0).setValue(sizeZ);
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
                results[i][j] = structNbt;
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
    private int convertPalette(TagList palette, TagCompound schemPalette) throws ParserException {
        ArrayList<Tag> paletteVal = new ArrayList<>();
        // Transforme le dictionnaire schemPalette en TagList de TagCompound
        ((ArrayList<Tag>) schemPalette.getValue()).sort(
                Comparator.comparingInt(o -> ((Integer) o.getValue()))
        );
        int paletteLength = 0;
        for (Tag t : (ArrayList<Tag>) schemPalette.getValue()) {
            ++paletteLength;
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
        return paletteLength;
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

    private void addJigsawInBlocks(HashMap<String, Tag> nbt, int structX, int structZ, int structNextX, int structNextZ,
                                   boolean isTarget, String state) throws ParserException {
        String fileName = getFile().getName().substring(0, getFile().getName().length() - 6);
        String name;
        String pool;
        String target;
        if (!isTarget) {
            name = EMPTY_ID;
            pool = "siw:" + fileName + "/" + fileName + "_"
                    + structNextX + "_" + structNextZ + "_pool";
            target = "siw:" + fileName + "_" + structNextX + "_" + structNextZ;
        } else {
            name = "siw:" + fileName + "_" + structX + "_" + structZ;
            pool = EMPTY_ID;
            target = EMPTY_ID;
        }
        Tag tJoint = new TagString();
        tJoint.setKey("joint");
        tJoint.setValue(JOINT);
        nbt.put(tJoint.getKey(), tJoint);
        Tag tName = new TagString();
        tName.setKey("name");
        tName.setValue(name);
        nbt.put(tName.getKey(), tName);
        Tag tPool = new TagString();
        tPool.setKey("pool");
        tPool.setValue(pool);
        nbt.put(tPool.getKey(), tPool);
        Tag tFinalState = new TagString();
        tFinalState.setKey("final_state");
        tFinalState.setValue(state);
        nbt.put(tFinalState.getKey(), tFinalState);
        Tag tId = new TagString();
        tId.setKey("id");
        tId.setValue(JIGSAW_ID);
        nbt.put(tId.getKey(), tId);
        Tag tTarget = new TagString();
        tTarget.setKey("target");
        tTarget.setValue(target);
        nbt.put(tTarget.getKey(), tTarget);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<BlockData>[][] convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities,
                                                   ArrayList<Tag> size, TagList palette, int paletteLength)
            throws ParserException {
        // Permet de connaître le nombre de chunks pris par la structure en X et en Z
        int nbStructX = (int) size.get(2).getValue() / MAX_SIZE;
        nbStructX += ((int) size.get(2).getValue()) % MAX_SIZE == 0 ? 0 : 1;
        int nbStructZ = (int) size.get(0).getValue() / MAX_SIZE;
        nbStructZ += ((int) size.get(0).getValue()) % MAX_SIZE == 0 ? 0 : 1;
        ArrayList<BlockData> dummy = new ArrayList<>();
        int[] nbStruct = {nbStructX, nbStructZ};
        ArrayList<BlockData>[][] blocksVal = (ArrayList<BlockData>[][]) Array.newInstance(dummy.getClass(), nbStruct);

        for (int i = 0; i < nbStructX; ++i) {
            for (int j = 0; j < nbStructZ; ++j) {
                blocksVal[i][j] = new ArrayList<>();
            }
        }

        System.out.println("N_CHUNKS = " + nbStructX + ", " + nbStructZ);
        int i = 0;
        for (byte b : blockData) {
            int x = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) % (int) size.get(2).getValue();
            int y = i / ((int) size.get(2).getValue() * (int) size.get(0).getValue());
            int z = (i % ((int) size.get(2).getValue() * (int) size.get(0).getValue())) / (int) size.get(2).getValue();
            int pX = (x % MAX_SIZE);
            int pZ = (z % MAX_SIZE);
            int structX = x / MAX_SIZE;
            int structZ = z / MAX_SIZE;

            HashMap<String, Tag> nbt = new HashMap<>();
            boolean isHere = false;

            //Tests des coordonnées pour ajouter les jigsaw blocks aux bons endroits
            if (y == 0) {
                JigsawOrientations orientation = null;
                String final_state;
                // Pas dernière structure en X : on pointe sur la structure suivante en X
                if (structX < nbStructX - 1 && pX == (MAX_SIZE - 1) && pZ == 0) {
                    orientation = JigsawOrientations.EAST;
                    final_state = "minecraft:brick_stairs[facing=east,]";
                    addJigsawInBlocks(nbt, structX, structZ, (structX + 1), structZ, false, final_state);
                // Pas dernière structure en Z : on pointe sur la structure suivante en Z
                } else if (structX == 0 && structZ < nbStructZ - 1 && pZ == (MAX_SIZE - 1) && pX == 0) {
                    orientation = JigsawOrientations.SOUTH;
                    final_state = "minecraft:oak_stairs[facing=south,]";
                    addJigsawInBlocks(nbt, structX, structZ, structX, (structZ + 1), false, final_state);
                // Pas en 0,0
                } else if (structX != 0 || structZ != 0) {
                    // Coin en bas à gauche d'une structure
                    if (pX == 0 && pZ == 0) {
                        // Pas première structure en Z : on est la target d'une structure précédente en Z
                        if (structX == 0) {
                            orientation = JigsawOrientations.NORTH;
                            final_state = "minecraft:quartz_stairs[facing=north,]";

                        // Première structure en Z : target d'une structure précédente en X
                        } else {
                            orientation = JigsawOrientations.WEST;
                            final_state = "minecraft:stone_stairs[facing=west,]";
                        }
                        addJigsawInBlocks(nbt, structX, structZ, structX, structZ, true, final_state);
                    }
                }


                if (!nbt.isEmpty()) {
                    byte state = (byte) (orientation.ordinal() + paletteLength);
                    BlockData bd = new BlockData(pX, y, pZ, state, nbt);
                    /*System.out.println("SIZE : x = " + (int) size.get(0).getValue() + ", z = "  + (int) size.get(2).getValue());
                    System.out.println("struct coordonnées : " + structX + " " + structZ);
                    System.out.println("struct coordonnées max : " + nbStructX + " " + nbStructZ);
                    System.out.println("coordonnées bloc : " + x + " " + z);*/
                    blocksVal[structX][structZ].add(bd);
                }
            }

            for (TagCompound tc : blockEntities) {
                if (isHere) {
                    break;
                }
                nbt = new HashMap<>();
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
            BlockData bd = new BlockData(pX, y, pZ, b, isHere ? nbt : new HashMap<>());

            // Pour déterminer dans quelle sous-structure on ajoute ce bloc
//            System.out.println("cpt : " + i + " / " + blockData.length);
//            System.out.println("sx et sz : " + structX + " " + structZ);
            /*System.out.println("SIZE : x = " + (int) size.get(0).getValue() + ", z = "  + (int) size.get(2).getValue());
            System.out.println("struct coordonnées : " + structX + " " + structZ);
            System.out.println("struct coordonnées max : " + nbStructX + " " + nbStructZ);
            System.out.println("coordonnées bloc : " + x + " " + z);*/
            blocksVal[structX][structZ].add(bd);

            ++i;
        }
        return blocksVal;
    }
}
