package net.projet.schematicsinworld.parser;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.projet.schematicsinworld.parser.tags.*;
import net.projet.schematicsinworld.parser.utils.BlockData;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.File;
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
    public final static String DATAVERSION = "DataVersion";
    public final static String LENGTH = "Length";
    public final static String HEIGHT = "Height";
    public final static String WIDTH = "Width";
    public final static String BLOCKDATA = "BlockData";
    public final static String BLOCKENTITIES = "BlockEntities";
    private static final String MISSING_BLOCK = "structure_void";

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
            throw new AssertionError("The file path provided is null");
        }
        file = new File(filepath);
        if (!file.isFile() || !file.canRead()) {
            throw new AssertionError("Cannot open file : " + filepath);
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
     * METHODES
     */

    // Convertit un fichier .schem en un fichier .nbt
    public void saveToNBT(String filepath) throws ParserException {
        ArrayList<Tag> struct = this.convertSchematicsToNBT();
        try {
            new NBTParser(filepath + ".nbt", NBTParser.WRITE, struct);
        } catch (ParserException e) {
            e.printStackTrace();
        }
    }

    /*
     * Outils
     */

    /**
     * Lit un fichier .schem et en extrait ses tags sous forme d'une liste dynamique.
     *
     * @return La liste des tags du fichier .schem chargé
     * @throws ParserException
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Tag> convertSchematicsToNBT() throws ParserException {
        TagInt dataVersion = new TagInt();
        TagList palette = new TagList();
        TagList entities = new TagList();
        ArrayList<TagCompound> blockEntities = null;
        ArrayList<Tag> size = new ArrayList<>();

        for (int i = 0; i < 3; ++i) {
            size.add(new TagInt());
        }
        byte[] blocks = new byte[]{};

        for (Tag t : this.tags) {
            switch (t.getKey()) {
                case DATAVERSION:
                    dataVersion.setKey(DATAVERSION);
                    dataVersion.setValue(t.getValue());
                    break;
                case PALETTE:
                    convertPalette(palette, (TagCompound) t);
                    break;
                case LENGTH:
                    size.get(2).setValue(((Short) t.getValue()).intValue());
                    break;
                case HEIGHT:
                    size.get(1).setValue(((Short) t.getValue()).intValue());
                    break;
                case WIDTH:
                    size.get(0).setValue(((Short) t.getValue()).intValue());
                    break;
                case BLOCKDATA:
                    blocks = (byte[]) t.getValue();
                    break;
                case BLOCKENTITIES:
                    blockEntities = (ArrayList<TagCompound>) t.getValue();
                    break;
            }
        }
        // Entitées
        this.convertEntities(entities);

        // Blocs
        ArrayList<BlockData> blockData = this.convertBlocks(blocks, blockEntities, size);

        // Liste résultat
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
            for (Integer coord : bd.getCoords()) {
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


    /**
     * Convertit l'élément "Palette" du fichier .schem en une TagList contenant les mêmes valeurs.
     *
     * @param palette      la TagList où sera stockée le résultat de la conversion
     * @param schemPalette l'élément "Palette" à convertir
     * @throws ParserException
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

            // Testing if the block exist and if he doesn't, we replace it by
            // the block "missing_texture_block"
            ResourceLocation r = new ResourceLocation(key_prop[0]);
            ResourceLocation r2 = new ResourceLocation("minecraft:air");
            Block b = ForgeRegistries.BLOCKS.getValue(r);
            String s = b.toString();
            Block b2 = ForgeRegistries.BLOCKS.getValue(r2);
            String s2 = b2.toString();
            if (s.equals(s2)) {
                if ((!key_prop[0].equals("minecraft:air"))) {
                    key_prop[0] = MISSING_BLOCK;
                }
            }
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

    /**
     * Convertit l'élément "Entities" du fichier .schem en une TagList (vide car
     * nous nous intéressons uniquement aux structures).
     *
     * @param entities la TagList où sera stockée le résultat de la conversion
     * @throws ParserException
     */
    private void convertEntities(TagList entities) throws ParserException {
        entities.setKey("entities");
        entities.setValue(new ArrayList<Tag>());
    }

    /**
     * Convertit l'élément "BlockData" du fichier .schem en une TagList contenant les mêmes valeurs.
     *
     * @param blockData     Tableau d'octets contenant les états (= indice du bloc correspondant dans la palette)
     *                      des blocs du fichier .schem
     * @param blockEntities Liste de BlockEntities : il s'agit des blocs "spéciaux" qui ont des propriétés en plus
     *                      par rapport aux blocs classiques
     * @param size          Liste de 3 Tag représentant la taille de la structure (x, y, z)
     * @throws ParserException
     */
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
                    // On vérifie si la position du bloc est bien celle que l'on traite
                    if (t.getKey().equals("Pos")) {
                        int[] tagPos = (int[]) t.getValue();
                        if (tagPos[0] == x && tagPos[1] == y && tagPos[2] == z) {
                            isHere = true;
                            continue;
                        }
                        break;
                    }

                    // On remplace car dans un fichier NBT la clé est id et pas Id
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
}
