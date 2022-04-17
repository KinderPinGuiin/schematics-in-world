package net.projet.schematicsinworld;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.projet.schematicsinworld.parser.SchematicsParser;
import net.projet.schematicsinworld.world.structure.ModStructures;
import net.projet.schematicsinworld.parser.utils.ParserException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SchematicsInWorld.MOD_ID)
public class SchematicsInWorld {
    public static final String MOD_ID = "siw";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SchematicsInWorld() throws IOException {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Pour l'instant ça plante pas car le dossier Schematics dans le .minecraft est vide
        // si on met des .schem, c'est la hess
        List<String> paths = null;
        String rootPath = System.getProperty("user.home") + File.separator +
                "AppData" + File.separator + "Roaming" + File.separator +
                ".minecraft";

        // Dossier .minecraft
        File root = new File(rootPath);
        if (!root.exists()) {
            // gérer le problème car ça veut dire pas de dossier .minecraft
        }

        // Dossier.schem
        File schemDir = new File(rootPath + File.separator + "Schematics");
        if (!schemDir.exists()) {
            System.out.println("n'existe pas de base");
            schemDir.mkdir();
            // partir ? car rien à charger, ou on laisse le jeu se lancer jsp
        } else {
            System.out.println("existe de base");
        }
        System.out.println(schemDir.getAbsolutePath());

        // Pour chercher avec ctrl+f dans le terminal, ça va plus vite xd
        System.out.println("chibre");

        try {
            String path = schemDir.getAbsolutePath();
            paths = findFiles(Paths.get(path), "schem");
            paths.forEach(x -> System.out.println(x));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (paths != null) {
            SchematicsParser s;
            for (int i = 0; i < paths.size(); ++i) {
                s = new SchematicsParser(paths.get(i));
                try {
                    System.out.println("KAARISM");
                    System.out.println(paths.get(i).substring(0, paths.get(i).length() - 5) + "nbt");
                    s.saveToNBT(paths.get(i).substring(0, paths.get(i).length() - 5) + "nbt");
                } catch (ParserException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

        }

        /*
        SchematicsParser s = new SchematicsParser("C:\\Users\\utilisateur\\Desktop\\Minecraft Modding\\schematicsInWorld\\schem_tests\\maison.schem");
        try {
            s.saveToNBT("C:\\Users\\utilisateur\\Desktop\\Minecraft Modding\\schematicsInWorld\\schem_tests\\test.nbt");
        } catch (ParserException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        */

        ModStructures.register(modEventBus);

        modEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        modEventBus.addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        modEventBus.addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        modEventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private List<String> findFiles(Path path, String ext) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException("Path must be a directory !");
        }

        List<String> result;

        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(p -> !Files.isDirectory(p))
                    .map(p -> p.toString().toLowerCase())
                    .filter(f -> f.endsWith(ext))
                    .collect(Collectors.toList());
        }
        return result;
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        event.enqueueWork(() -> {
           ModStructures.setupStructures();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }
    }
}
