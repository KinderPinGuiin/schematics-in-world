package net.projet.schematicsinworld.world.structures;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.jigsaw.JigsawManager;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.common.BiomeDictionary;
import net.projet.schematicsinworld.SchematicsInWorld;
import net.projet.schematicsinworld.config.StructConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.util.Set;
import java.util.concurrent.CompletionException;

/**
 * This class' purpose is to provide all information relevant to generating its own generic structure.
 * This is done through a local class depending on each instance of this class.
 *
 * It must be Immutable.
 */
public class SiwStructureProvider {

    // ATTRIBUTS
    private final StructConfig config;

    // CONSTRUCTORS

    public SiwStructureProvider(StructConfig config) {
        this.config = (StructConfig) config.clone(); }

    // REQUETES

    /**
     * If this location is suitable for spawning the structure.
     * @return true if this location is suitable.
     */
    public boolean isLocationOk(Set<BiomeDictionary.Type> biome) {
        return config.isEnabled() && config.isSpawningBiome(biome);
    }
    
    // The name of the structure. Used for /locate, notably
    public String name() {
        return config.getName();
    }

    // Distance max entre structure.
    public int maxDist(){
        return config.getDistMaxSpawn();
    }

    // Distance min entre structure.
    public int minDist(){
        return config.getDistMinSpawn();
    }

    // The random seed associated with this provider.
    // Linked to the name.
    public int randseed() {
        return name().hashCode();
    }

    // The main method. It provides instances of its Structure.
    // You main consider it a form of "new Structure...".
    public Structure<NoFeatureConfig> provide(){
        return new SiwStructure();
    }

    // The Structure class template for the provider. All provided structures will extends this class.
    private class SiwStructure extends Structure<NoFeatureConfig> {

        public SiwStructure() {
            super(NoFeatureConfig.CODEC);
        }

        /*
            On génére la structure au moment de génération des structure de surface,
            AVANT les plantes et minerais.
        */
        @Override
        public GenerationStage.Decoration getDecorationStage() {
            return GenerationStage.Decoration.SURFACE_STRUCTURES;
        }

        @Override
        public boolean func_230363_a_(ChunkGenerator chunkGenerator, BiomeProvider biomeSource,
                                      long seed, SharedSeedRandom chunkRandom, int chunkX, int chunkZ,
                                      Biome biome, ChunkPos chunkPos, NoFeatureConfig featureConfig) {
            BlockPos centerOfChunk = new BlockPos(chunkX * 16, 0, chunkZ * 16);

            int landHeight = chunkGenerator.getHeight(centerOfChunk.getX(), centerOfChunk.getZ(),
                    Heightmap.Type.WORLD_SURFACE_WG);

            boolean res = true;
            res &= landHeight != 0;

            IBlockReader columnOfBlocks = chunkGenerator.func_230348_a_(centerOfChunk.getX(), centerOfChunk.getZ());
            BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.up(landHeight - 1));

            res &= (config.isSpawningInWater() || topBlock.getFluidState().isEmpty());

            return res;
        }

        @Override
        public IStartFactory<NoFeatureConfig> getStartFactory() {
            return Start::new;
        }

        public class Start extends StructureStart<NoFeatureConfig> {

            public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ,
                         MutableBoundingBox mutableBoundingBox,
                         int referenceIn, long seedIn) {
                super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
            }

            @Override // GeneratePieces
            public void func_230364_a_(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator,
                                       TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn,
                                       NoFeatureConfig config) {
                int x = (chunkX << 4) + 8;
                int z = (chunkZ << 4) + 8;

                BlockPos blockpos = new BlockPos(x, 0, z);
                try {
                    JigsawManager.func_242837_a(dynamicRegistryManager,
                            new VillageConfig(() -> dynamicRegistryManager.getRegistry(Registry.JIGSAW_POOL_KEY)
                                    .getOrDefault(new ResourceLocation(SchematicsInWorld.MOD_ID,
                                            name() + "/" + name() + "_pool")),
                                    10), AbstractVillagePiece::new, chunkGenerator, templateManagerIn,
                            blockpos, this.components, this.rand, false, true);
                } catch (NullPointerException e) {
                    //handle this shit
                }

                this.components.forEach(piece -> piece.offset(0, 1, 0));
                this.components.forEach(piece -> piece.getBoundingBox().minY -= 1);

                this.recalculateStructureSize();

                LogManager.getLogger().log(Level.DEBUG, name() +" at " +
                        this.components.get(0).getBoundingBox().minX + " " +
                        this.components.get(0).getBoundingBox().minY + " " +
                        this.components.get(0).getBoundingBox().minZ);
            }
        }
    }
}
