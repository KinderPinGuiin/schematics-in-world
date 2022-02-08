package net.projet.schematicsinworld.config;


import net.projet.schematicsinworld.world.structures.SiwStructureProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Classe qui permet de gérer de manière globales les configurations.
 * Permet de charger les config.
 */
public abstract class ConfigHandler {

    public static final File CONFIG_DIRECTORY = new File("config" + File.separator + "siwstructures");

    public static List<SiwStructureProvider> getConfigurations(){

        // Si le sous-dossier n'a pas été créé
        if (!CONFIG_DIRECTORY.exists()) {
            CONFIG_DIRECTORY.mkdir();
        }


        List<String> structureNames = new LinkedList<String>();

        File[] fileList = new File("D:\\shematics_in_world2\\schematics-in-world-gen_experimental\\src\\main\\resources\\data\\siw\\structures").listFiles();
        for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".nbt")) {
                String r = StringUtils.removeEnd(file.getName(), ".nbt");
                structureNames.add(r);
            }
        }

        List<SiwStructureProvider> providerList = new LinkedList<SiwStructureProvider>();

        for(String str : structureNames) {
            File cfgFile = new File(CONFIG_DIRECTORY.getAbsolutePath() + File.separator + str + ".JSON");
            if (!cfgFile.exists()) {

                // Création du fichier de configuration par défaut
                try {
                    cfgFile.createNewFile();
                    BufferedWriter writer = new BufferedWriter(new FileWriter(cfgFile));
                    writer.write(new StructConfig(str).toString());
                    writer.close();
                } catch (IOException e) {
                    throw new AssertionError(e.getMessage());
                }
            }
            // On rajoute la structure
            if (cfgFile.canRead()) {
                providerList.add(new SiwStructureProvider(new StructConfig(cfgFile)));
            }
        }
        return providerList;
    }
}
