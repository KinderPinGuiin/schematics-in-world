package net.projet.schematicsinworld.world.structures.generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.projet.schematicsinworld.SchematicsInWorld;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericStructurePool {

    private final transient String json;
    private final transient String structName;
    private final transient String subStructName;
    private final transient String subName;
    private final String name;
    private final String fallback = "minecraft:empty";
    private final Element[] elements;

    public GenericStructurePool(String n) {
        structName = n;
        subStructName = n.substring(0, n.length() - 2);
        subName = "siw:" + subStructName + "/" + structName;
        elements = new Element[1];
        elements[0] = new Element();
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        final Gson gson = builder.create();
        json = gson.toJson(this);

        String path = System.getProperty("user.dir");
        path += "/../src/main/resources/data/" + SchematicsInWorld.MOD_ID
                + "/worldgen/template_pool/" + subStructName + "/";

        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        path += structName + "_pool.json";
        name = "siw:" + subStructName + "/" + structName;

        Writer writer = null;
        try {
            File file = new File(path);

            if (file.exists()) {
                file.delete();
            }

            writer = new FileWriter(path);
            gson.toJson(this, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Element {
        private final int weight = 1;
        private final SubElement element = new SubElement();
    }

    private class SubElement {
        private final String location = subName;
        private final String processors = "minecraft:empty";
        private final String projection = "rigid";
        private final String element_type = "minecraft:single_pool_element";
    }

    public static void main(String[] args) {
        final String param = "rooms_0";
        GenericStructurePool test = new GenericStructurePool(param);
        final String param2 = "rooms_1";
        GenericStructurePool test2 = new GenericStructurePool(param2);
    }
}
