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
//    public static final String SIW_DIR = ".." + File.separator + "src" + File.separator + "main" + File.separator
//            + "resources" + File.separator + "data" + File.separator + MOD_ID;
    public static final String SIW_DIR = "mods" + File.separator
            + "resources" + File.separator + "data" + File.separator + MOD_ID;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SchematicsInWorld() {
        // Register the setup method for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Dossier Schematics (contenant les .schem)
        File schemDir = new File("Schematics");
        if (!schemDir.exists()) {
            LOGGER.info("Schematics folder not found : creating now.\n");
            schemDir.mkdirs();
        } else {
            LOGGER.info("Schematics folder found.\n");
        }

        // On cherche les fichiers .schem
        List<String> paths = null;
        try {
            String path = schemDir.getAbsolutePath();
            paths = findFiles(Paths.get(path), "schem");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convertit chaque fichier .schem trouvé dans le dossier Schematics

        if (paths != null) {
            // Dossier racine des fichiers NBT
            String dest = SIW_DIR + File.separator + "structures";

            // Ne devrait jamais arriver
            File destFolder = new File(dest);
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            SchematicsParser s;
            for (int i = 0; i < paths.size(); ++i) {
                s = new SchematicsParser(paths.get(i));
                try {
                    // Nom de la structure sans l'extension .schem
                    String name = paths.get(i).substring(paths.get(i).lastIndexOf(File.separator) + 1, paths.get(i).length() - 6);

                    // Dossier de la structure au format NBT
                    File nbtDir = new File(dest + File.separator + name);
                    if (!nbtDir.exists()) {
                        nbtDir.mkdirs();
                    }
                    s.saveToNBT(dest + File.separator + name + File.separator + name);
                } catch (ParserException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

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

    /**
     * Recherche les fichiers ayant l'extension "ext" dans le dossier de chemin "path".
     * Retourne la liste contenant les chemins d'accès de ces fichiers.
     *
     * @param path         Chemin du dossier où l'on cherche les fichiers
     * @param ext          Extension requise pour les fichiers à ajouter au résultat
     * @return             Liste des chemins d'accès aux fichiers ayant l'extension ext
     * @throws IOException
     */
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
