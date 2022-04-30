package net.projet.schematicsinworld.parser;

import jdk.nashorn.internal.ir.Block;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.projet.schematicsinworld.parser.tags.*;
import net.projet.schematicsinworld.parser.utils.BlockData;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

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

    // Convertit le fichier .schem en une liste de .nbt (de longueur > 1 si la taille de la
    // structure dépasse la taille autorisée)
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

    /*
     * Outils
     */

    @SuppressWarnings("unchecked")
    private ArrayList<ArrayList<Tag>> convertSchematicsToNBT() throws ParserException {
        ArrayList<ArrayList<Tag>> results = new ArrayList<>();
        ArrayList<Tag> res = new ArrayList<>();
        ArrayList<Tag> size = new ArrayList<>();
        for (int i = 0; i < 3; ++i) { size.add(new TagInt()); }
        byte[] blocks = new byte[] {};
        ArrayList<TagCompound> blockEntities = null;
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
                /*
                boolean northOn = false;
                boolean southOn = false;
                boolean eastOn = false;
                boolean westOn = false;

                 */

                for (String prop : key_prop[1].split(",")) {
                    String[] prop_name_val = prop.split("=");
                    TagString propTagString = new TagString();
                    // Fixing "facing" property (stairs, ..)
                    /*
                    if (prop_name_val[0].equals("facing")) {
                        if (prop_name_val[1].equals("north")) {
                            prop_name_val[1] = "west";
                        } else if (prop_name_val[1].equals("east")) {
                            prop_name_val[1] = "north";
                        } else if (prop_name_val[1].equals("south")) {
                            prop_name_val[1] = "east";
                        } else {
                            prop_name_val[1] = "south";
                        }


                    // Fixing "cardinal points" property for other block (like glass panes, fences, ..)
                    } else if (prop_name_val[0].equals("east")) {
                        if (prop_name_val[1].equals("true")) {
                            prop_name_val[1] = "false";
                            northOn = true;
                        }
                    } else if (prop_name_val[0].equals("south")) {
                        if (prop_name_val[1].equals("true")) {
                            prop_name_val[1] = "false";
                            eastOn = true;
                        }
                    } else if (prop_name_val[0].equals("north")) {
                        if (prop_name_val[1].equals("true")) {
                            prop_name_val[1] = "false";
                            westOn = true;
                        }
                    } else if (prop_name_val[0].equals("west")) {
                        if (prop_name_val[1].equals("true")) {
                            prop_name_val[1] = "false";
                            southOn = true;
                        }
                    // Doors
                    } else if (prop_name_val[0].equals("hinge")) {
                        prop_name_val[1] = (prop_name_val[1].equals("left") ? "right" : "left");
                    // Logs
                    } else if (prop_name_val[0].equals("axis")) {
                        if (prop_name_val[1].equals("x")) {
                            prop_name_val[1] = "z";
                        } else if (prop_name_val[1].equals("z")) {
                            prop_name_val[1] = "x";
                        }
                    // Rails
                    } else if (key_prop[0].endsWith("rail") && prop_name_val[0].equals("shape")) {
                        if (prop_name_val[1].equals("north_south")) {
                            prop_name_val[1] = "east_west";
                        } else if (prop_name_val[1].equals("east_west")) {
                            prop_name_val[1] = "north_south";
                        }

                    }

                        // Skulls
                    // todo gérer tous les cas de rails et skulls à faire
                    */
                    propTagString.setKey(prop_name_val[0]);
                    propTagString.setValue(prop_name_val[1]);
                    propsVal.add(propTagString);
                }

                // We reloop through propsVal to update values with our flags
                /*
                for (Tag ts : propsVal) {
                    if ((ts.getKey().equals("east") && eastOn) ||
                            (ts.getKey().equals("north") && northOn) ||
                            (ts.getKey().equals("south") && southOn) ||
                            (ts.getKey().equals("west") && westOn)
                    ) {
                        ts.setValue("true");
                    }
                }
                */


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

    private void convertEntities(ArrayList<Tag> res) throws ParserException {
        TagList entities = new TagList();
        entities.setKey("entities");
        entities.setValue(new ArrayList<Tag>());
        res.add(entities);
    }

    @SuppressWarnings("unchecked")
    private ArrayList<BlockData> convertBlocks(byte[] blockData, ArrayList<TagCompound> blockEntities, ArrayList<Tag> size) {
        TagList blocks = new TagList();
        ArrayList<BlockData> blocksVal = new ArrayList<>();
        int nextX = 0;
        int nextY = 0;
        int nextZ = (int) size.get(2).getValue() - 1;
        for (byte b : blockData) {
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

            nextZ = ((nextZ - 1) + (int) size.get(2).getValue()) % (int) size.get(2).getValue();
            if (nextZ + 1 == (int) size.get(2).getValue()) {
                nextX = (nextX + 1) % (int) size.get(0).getValue();
                if (nextX == 0) {
                    nextY = (nextY +  1)  % (int) size.get(1).getValue();
                }
            }
        }
        return blocksVal;
    }
}
