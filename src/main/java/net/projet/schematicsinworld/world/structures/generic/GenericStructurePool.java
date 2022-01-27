package net.projet.schematicsinworld.world.structures.generic;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.projet.schematicsinworld.SchematicsInWorld;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class GenericStructurePool {

    private final transient String structName;
    private final transient String json;
    private final String name;
    private final String fallback = "minecraft:empty";
    private final Element[] elements;

    public GenericStructurePool(String s) {
        structName = s;
        name = "siw:" + structName;
        elements = new Element[1];
        elements[0] = new Element();
        final GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        final Gson gson = builder.create();
        json = gson.toJson(this);

        String path = System.getProperty("user.dir") + "/src/main/resources/data/"
                + SchematicsInWorld.MOD_ID + "/worldgen/template_pool/generic/"
                + structName + "_pool.json";

        Writer writer = null;
        try {
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
        private final String location = "siw:generic/" + name;
        private final String processors = "minecraft:empty";
        private final String projection = "rigid";
        private final String element_type = "minecraft:single_pool_element";
    }

    public static void main(String[] args) {
        final String param = "brick";
        GenericStructurePool test = new GenericStructurePool(param);
        System.out.println(test.json);
    }
}
