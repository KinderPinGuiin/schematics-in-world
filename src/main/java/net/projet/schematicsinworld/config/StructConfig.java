package net.projet.schematicsinworld.config;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Set;

/**
 *   Représente une configuration de structure.
 *   Les champs doivent être simple, pour pouvoir être écrit dans un
 *   fichier de configuration ou cloné de surface.
 *   Le fichier de configuration équivalent est un
 *   JsonObject unique, avec des champs nommés pour chaque variable.
 *
 * @Inv :
 *      getavgDistSpawn() >= getminDistSpawn()
 *      getName() != null && getName() != ""
 */
public class StructConfig implements Cloneable {

    // ATTRIBUTS
    private final String struct_name;
    private int avgDistSpawn = 32;
    private int minDistSpawn = 8;
    private int structureHigh = 0;
    private boolean isEnabled = true;
    private boolean isSpawningInWater = false;
    private boolean isBiomeFilterBlackList = true;
    private BiomeFilter biomeFilter = new BiomeFilter("");
    private DimensionFilter dimensionFilter = new DimensionFilter("OVERWORLD");


    public static final String JSON_INDENTATION = "      ";

    // CONSTRUCTEURS
    // par défaut. Possède des valeurs de base.
    public StructConfig(String name) {
        struct_name = name;
    }

    public class IncoherentConfigurationError extends Error {
        private final String message;
        public IncoherentConfigurationError(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    /**
     * Constructeur à partir d'un fichier de configuration JSON.
     * Si le fichier n'est pas un .JSON, ou si le fichier ne peut pas
     * être ouvert, une AssertionError sera renvoyée.
     *
     * Si les valeurs sont incohérentes (et non pas absentes), une
     * IncoherentConfigurationError sera renvoyée.
     **/
    public StructConfig(File cfgFile) {

        if (!cfgFile.getName().endsWith(".JSON") || !cfgFile.canRead())
            throw new AssertionError("File unsupported");
        struct_name = StringUtils.removeEnd(cfgFile.getName(), ".JSON");
        JsonObject json;
        try {
            JsonReader reader;
            reader = new JsonReader(new FileReader(cfgFile));
            reader.setLenient(true);
            JsonElement jelem = new JsonParser().parse(reader);
            reader.close();

            json = jelem.getAsJsonObject();

            // On lit l'objet que nous avons obtenu
            if (json.get("avgDistSpawn") != null) {
                avgDistSpawn = json.get("avgDistSpawn").getAsInt();
            }
            if (json.get("minDistSpawn") != null) {
                minDistSpawn = json.get("minDistSpawn").getAsInt();
            }
            if (json.get("isEnabled") != null) {
                isEnabled = json.get("isEnabled").getAsBoolean();
            }
            if (json.get("isSpawningInWater") != null) {
                isSpawningInWater = json.get("isSpawningInWater").getAsBoolean();
            }

            if (json.get("structureHigh") != null) {
                structureHigh = json.get("structureHigh").getAsInt();
            }

            if (json.get("dimensionFilter") != null) {
                setDimensionFilter(json.get("dimensionFilter").getAsString());
            }

            if (json.get("isBiomeFilterBlackList") != null) {
                isBiomeFilterBlackList = json.get("isBiomeFilterBlackList").getAsBoolean();
            }

            if (json.get("biomeFilter") != null) {
                setBiomeFilter(json.get("biomeFilter").getAsString());
            }

            // Error checking

            if (avgDistSpawn < minDistSpawn) {
                throw new IncoherentConfigurationError("avgDistSpawn is lower than minDistSpawn");
            }

            if (structureHigh < 0) {
                throw new IncoherentConfigurationError("Structure high is lower than 0 !");
            }

        } catch (FileNotFoundException e) {
            System.out.println("This will never happen");
        } catch (IOException e) {
            System.out.println("could not close?");
        }
    }

    // REQUETES

    public String getName() {
        return struct_name;
    }

    public int getAvgDistSpawn() {
        return avgDistSpawn;
    }

    public int getMinDistSpawn() {
        return minDistSpawn;
    }

    public boolean isEnabled() { return isEnabled; }

    public boolean isSpawningInWater() { return isSpawningInWater; }

    public int getStructureHigh() {
        return structureHigh;
    }

    public boolean isSpawningBiome(Set<BiomeDictionary.Type> biome) {
        if(isBiomeFilterBlackList){
            return !biomeFilter.apply(biome);
        }
        return biomeFilter.apply(biome);
    }


    public boolean isSpawningDimension(Set<BiomeDictionary.Type> dimension) {
        return dimensionFilter.apply(dimension);
    }


    // COMMANDES

    public void setAvgDistSpawn(int avgDistSpawn) {
        if(avgDistSpawn < getMinDistSpawn()) {
            throw new AssertionError("Value is lower than minDistSpawn!");
        }
        this.avgDistSpawn = avgDistSpawn;
    }

    public void setMinDistSpawn(int minDistSpawn) {
        if(getAvgDistSpawn() < minDistSpawn) {
            throw new AssertionError("Value is higher than avgDistSpawn!");
        }
        this.minDistSpawn = minDistSpawn;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setSpawningInWater(boolean isSpawningInWater) { this.isSpawningInWater = isSpawningInWater; }
    public void setBiomeFilterBlackList(boolean biomeFilterBlackList){
        isBiomeFilterBlackList = biomeFilterBlackList;
    }


    public void setBiomeFilter(String filterString) {
        try {
            biomeFilter = new BiomeFilter(filterString);
        } catch (AssertionError e){
            throw new IncoherentConfigurationError(e.getMessage());
        } catch (Error e) {
            // This should not happen. contact mod author if it does.
            throw new IncoherentConfigurationError("Unknown BiomeFilter creation error");
        }
    }


    public void setDimensionFilter(String filterString) {
        try {
            dimensionFilter = new DimensionFilter(filterString);
        } catch(Error e) {
            // This should not happen. contact mod author if it does.
            throw new IncoherentConfigurationError("Unknown DimensionFilter creation error");
        }
    }

    /**
     *
     * @return Une chaine JSON qui représente cette configuration.
     * N'inclut pas le nom de la structure.
     * Possède des commentaires, donc illisible sans "Lenient Parsing".
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");

        // Max distance between two of these structures
        builder.append(attributToJson(
                "The average distance between two of these structures in chunks",
                "avgDistSpawn", avgDistSpawn));

        // Min distance between two of these structures
        builder.append(attributToJson(
                "The minimum distance between two of these structures in chunks",
                "minDistSpawn", minDistSpawn));

        // Generating structure ?
        builder.append(attributToJson(
                "If this structure is to spawn naturally in the world",
                "isEnabled", isEnabled));

        // Structure generation in water
        builder.append(attributToJson(
                "If this structure is to spawn in water",
                "isSpawningInWater", isSpawningInWater));

        // Structure high generation
        builder.append(attributToJson(
                "How many blocks above the floor the structure generates",
                "structureHigh", structureHigh));


        // Dimension configuration
        builder.append(stringToComment(
                "The dimensions this structure should generate in : Minecraft default dimensions are OVERWORLD, NETHER, and END."));
        builder.append(stringToComment(
                "Separate the wanted dimensions by placing semicolons."));
        builder.append(attributToJson(
                "example: \"OVERWORLD; END;\" will make the structure generate in Overworld and in the End",
                "dimensionFilter",
                dimensionFilter.toString()));



        // Biome configuration
        builder.append(stringToComment(
                "Whether or not the biome filter is a blacklist or a whitelist."));
        builder.append(attributToJson(
                "If true, only biomes who DO NOT pass the biome filter will spawn the structure. Otherwise, the opposite is true.",
                "isBiomeFilterBlackList",
                isBiomeFilterBlackList));

        builder.append(stringToComment(
                "The biome filter for spawning. it is represented by a string with Forge biome types, like PLAIN or VOID."));
        builder.append(attributToJson(
                "example: \"PLAIN&(water !cOLD)\" will work on biome with PLAIN and either they have water or they are not COLD.",
                "biomeFilter",
                biomeFilter.toString()));

        // On enleve le ',' qui est de trop
        builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");
        return builder.toString();
    }
    @Override
    public Object clone() {
        StructConfig clone = null;
        try {
            clone = (StructConfig) super.clone();
        } catch(CloneNotSupportedException e) {
            // N'arrivera pas
        }
        return clone;
    }

    private String attributToJson(String comment, String attrname, int n) {
        return JSON_INDENTATION + "# " + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": " + String.valueOf(n) + ",\n\n";
    }

    private String attributToJson(String comment, String attrname, boolean value) {
        return JSON_INDENTATION + "# " + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": " + String.valueOf(value) + ",\n\n";
    }

    private String attributToJson(String comment, String attrname, String value){
        return JSON_INDENTATION + "# " + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": \"" + value + "\",\n\n";
    }

    private String stringToComment(String comment) {
        return JSON_INDENTATION + "# " + comment + "\n";
    }
}

