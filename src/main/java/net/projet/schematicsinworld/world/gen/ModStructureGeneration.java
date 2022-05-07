package net.projet.schematicsinworld.world.gen;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.projet.schematicsinworld.world.structure.ModStructures;


import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModStructureGeneration {
    public static void generateStructures(final BiomeLoadingEvent event) {
        RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, event.getName());
        Set<BiomeDictionary.Type> types = BiomeDictionary.getTypes(key);
        for(int i = 0; i < ModStructures.SIW_STRUCTURES_LIST.size(); i++) {
            int finalI = i;

            List<Supplier<StructureFeature<?, ?>>> structures = event.getGeneration().getStructures();
            if (ModStructures.providerList.get(finalI).isLocationOk(types)) {
                structures.add(() -> ModStructures.SIW_STRUCTURES_LIST.get(finalI).get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
            }
        }
    }
}
