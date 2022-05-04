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
 *   Represente une configuration de structure.
 *   Les champs doivent être simple, pour pouvoir être écrit dans un
 *   fichier de configuration ou cloné de surface.
 *   Le fichier de configuration equivalent est un
 *   JsonObject unique, avec des champs nommés pour chaque variable.
 *
 * @Inv :
 *      getDistMaxSpawn() >= getDistMinSpawn()
 *      getName() != null && getName() != ""
 */
public class StructConfig implements Cloneable {

    // ATTRIBUTS
    private final String struct_name;
    private int distMaxSpawn = 32;
    private int distMinSpawn = 8;
    private boolean isEnabled = true;
    private boolean isBiomeFilterBlackList = true;
    private BiomeFilter biomeFilter = new BiomeFilter("");

    public static final String JSON_INDENTATION = "      ";

    // CONSTRUCTEURS
    // par défaut. Possède des valeurs de base.
    public StructConfig(String name){
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
            if (json.get("distMaxSpawn") != null) {
                distMaxSpawn = json.get("distMaxSpawn").getAsInt();
            }
            if (json.get("distMinSpawn") != null) {
                distMinSpawn = json.get("distMinSpawn").getAsInt();
            }
            if (json.get("isEnabled") != null) {
                isEnabled = json.get("isEnabled").getAsBoolean();
            }
            if (json.get("isBiomeFilterBlackList") != null) {
                isBiomeFilterBlackList = json.get("isBiomeFilterBlackList").getAsBoolean();
            }
            if (json.get("biomeFilter") != null) {
                setBiomeFilter(json.get("biomeFilter").getAsString());
            }

            // Error checking
            if (distMaxSpawn < distMinSpawn) {
                throw new IncoherentConfigurationError("distMaxSpawn is lower than distMinSpawn");
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

    public int getDistMaxSpawn() {
        return distMaxSpawn;
    }

    public int getDistMinSpawn() {
        return distMinSpawn;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean isSpawningBiome(Set<BiomeDictionary.Type> biome){
        if(isBiomeFilterBlackList){
            return !biomeFilter.apply(biome);
        }
        return biomeFilter.apply(biome);
    }

    // COMMANDES

    public void setDistMaxSpawn(int distMaxSpawn) {
        if(distMaxSpawn < getDistMinSpawn()) {
            throw new AssertionError("Value is lower than distMinSpawn!");
        }
        this.distMaxSpawn = distMaxSpawn;
    }

    public void setDistMinSpawn(int distMinSpawn) {
        if(getDistMaxSpawn() < distMinSpawn) {
            throw new AssertionError("Value is higher than distMaxSpawn!");
        }
        this.distMinSpawn = distMinSpawn;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setBiomeFilterBlackList(boolean biomeFilterBlackList){
        isBiomeFilterBlackList = biomeFilterBlackList;
    }

    public void setBiomeFilter(String filterString){
        try {
            biomeFilter = new BiomeFilter(filterString);
        } catch (AssertionError e){
            throw new IncoherentConfigurationError(e.getMessage());
        } catch (Error e) {
            // This should not happen. contact mod author if it does.
            throw new IncoherentConfigurationError("Unknown BiomeFilter creation error");
        }
    }
    /**
     *
     * @return Une chaine JSON qui represente cette configuration.
     * N'inclut pas le nom de la structure.
     * Possède des commentaires, donc illisible sans "Lenient Parsing".
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{\n");

        builder.append(attributToJson(
                "The maximum distance between two of these structures in chunks",
                "distMaxSpawn", distMaxSpawn));

        builder.append(attributToJson(
                "The minimum distance between two of these structures in chunks",
                "distMinSpawn", distMinSpawn));

        builder.append(attributToJson(
                "If this structure is to spawn naturally in the world",
                "isEnabled", isEnabled));

        builder.append(stringToComment(
                "Wether or not the biome filter is a blacklist or a whitelist."));
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
        return JSON_INDENTATION + "#" + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": " + String.valueOf(n) + ",\n\n";
    }

    private String attributToJson(String comment, String attrname, boolean value) {
        return JSON_INDENTATION + "#" + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": " + String.valueOf(value) + ",\n\n";
    }

    private String attributToJson(String comment, String attrname, String value){
        return JSON_INDENTATION + "#" + comment + "\n"
                + JSON_INDENTATION + "\"" + attrname + "\": \"" + value + "\",\n\n";
    }

    private String stringToComment(String comment){
        return JSON_INDENTATION + "#" + comment + "\n";
    }
}

