package net.projet.schematicsinworld.config;


import net.projet.schematicsinworld.SchematicsInWorld;
import net.projet.schematicsinworld.world.structures.SiwStructureProvider;
import net.projet.schematicsinworld.world.structures.generic.GenericStructurePool;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe qui permet de gérer de manière globales les configurations.
 * Permet de charger les config.
 */
public abstract class ConfigHandler {

    public static final File CONFIG_DIRECTORY = new File("config" + File.separator + "siwstructures");
    public static final List<String> STRUCTURE_NAMES = new LinkedList<String>();
    public static final List<String> STRUCTURE_FILES = new LinkedList<String>();

    public static List<SiwStructureProvider> getConfigurations(){

        // Si le sous-dossier n'a pas été créé
        if (!CONFIG_DIRECTORY.exists()) {
            CONFIG_DIRECTORY.mkdir();
        }

        String start = System.getProperty("user.dir");
        start += "/../src/main/resources/data/" + SchematicsInWorld.MOD_ID + "/structures";

        try (Stream<Path> stream = Files.walk(Paths.get(start), Integer.MAX_VALUE)) {
            List<String> collect = stream
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());
            for (String str : collect) {
                File file = new File(str);
                if (file.isFile() && file.getName().endsWith(".nbt")) {
                    String r = StringUtils.removeEnd(file.getName(), ".nbt");
                    STRUCTURE_FILES.add(r);
                    r = r.substring(0, r.length() - 2);
                    if (!STRUCTURE_NAMES.contains(r)) {
                        STRUCTURE_NAMES.add(r);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String str : STRUCTURE_FILES) {
            GenericStructurePool gsp = new GenericStructurePool(str);
        }

        List<SiwStructureProvider> providerList = new LinkedList<SiwStructureProvider>();

        for(String str : STRUCTURE_NAMES) {
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