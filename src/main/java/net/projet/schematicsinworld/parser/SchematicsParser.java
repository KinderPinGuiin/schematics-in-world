package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.*;
import net.projet.schematicsinworld.parser.utils.BlockData;
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
            System.out.println("Parsing file " + filepath);
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
    public void saveToNBT(String filepath) throws ParserException {
        ArrayList<Tag> struct = this.convertSchematicsToNBT();
        try {
            new NBTParser(filepath + "_" + 0 + "_" + 0 + ".nbt", 'w', struct);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    /*
     * Outils
     */


    @SuppressWarnings("unchecked")
    private ArrayList<Tag> convertSchematicsToNBT() throws ParserException {
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
                    convertPalette(palette, (TagCompound) t);
                    break;
                case "Length":
                    size.get(2).setValue(((Short) t.getValue()).intValue());
                    break;
                case "Height":
                    size.get(1).setValue(((Short) t.getValue()).intValue());
                    break;
                case "Width":
                    size.get(0).setValue(((Short) t.getValue()).intValue());
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

        ArrayList<BlockData> blockData = this.convertBlocks(blocks, blockEntities, size);

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
        sizeTag.setValue(size);
        structNbt.add(sizeTag);


        // --- Partie blocks ---

        // TagList d'id 'blocks' (la TagList principale contenant les blocks)
        TagList blocksTag = new TagList();
        blocksTag.setKey("blocks");

        // Future valeur de la TagList blocksTag
        ArrayList<TagCompound> blocksList = new ArrayList<>();

        // Parcours des BlockData de la structure
        for (BlockData bd : blockData) {
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

        return structNbt;
    }



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
        palette.setValue(paletteVal);
    }


    private void convertEntities(TagList entities) throws ParserException {
        entities.setKey("entities");
        entities.setValue(new ArrayList<Tag>());
    }


    @SuppressWarnings("unchecked")
    private ArrayList<BlockData> convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities,
                                                   ArrayList<Tag> size) {

        ArrayList<BlockData> blocksVal = new ArrayList<>();

        int i = 0;
        for (byte b : blockData) {
            int x = (i % ((int) size.get(0).getValue() * (int) size.get(2).getValue())) % (int) size.get(0).getValue();
            int y = i / ((int) size.get(0).getValue() * (int) size.get(2).getValue());
            int z = (i % ((int) size.get(0).getValue() * (int) size.get(2).getValue())) / (int) size.get(0).getValue();
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
            BlockData bd = new BlockData(x, y, z, b, isHere ? nbt : new HashMap<>());

            // Pour déterminer dans quelle sous-structure on ajoute ce bloc
            blocksVal.add(bd);
            ++i;
        }
        return blocksVal;
    }

    /*
    @SuppressWarnings("unchecked")
    private ArrayList<BlockData>[][] convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities,
                                                   ArrayList<Tag> size)
            throws ParserException {
        // Permet de connaître le nombre de chunks pris par la structure en X et en Z
        int nbStructX = (int) size.get(0).getValue() / MAX_SIZE;
        nbStructX += ((int) size.get(0).getValue()) % MAX_SIZE == 0 ? 0 : 1;
        int nbStructZ = (int) size.get(2).getValue() / MAX_SIZE;
        nbStructZ += ((int) size.get(2).getValue()) % MAX_SIZE == 0 ? 0 : 1;
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
            int x = (i % ((int) size.get(0).getValue() * (int) size.get(2).getValue())) % (int) size.get(0).getValue();
            int y = i / ((int) size.get(0).getValue() * (int) size.get(2).getValue());
            int z = (i % ((int) size.get(0).getValue() * (int) size.get(2).getValue())) / (int) size.get(0).getValue();
            int pX = (x % MAX_SIZE);
            int pZ = (z % MAX_SIZE);
            int structX = x / MAX_SIZE;
            int structZ = z / MAX_SIZE;
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
            // System.out.println("cpt : " + i + " / " + blockData.length);
            blocksVal[structX][structZ].add(bd);

            ++i;
        }
        return blocksVal;
    }*/
}
