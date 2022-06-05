package net.projet.schematicsinworld.config;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Un filtre de dimension, construit à partir d'une chaîne de caractères.
 * Comme cette classe est non-mutable, le clonage profond n'est pas nécessaire
 * si cette classe est un attribut.
 *
 * Les dimensions sont séparées par des points-virgule, permettant une gestion simple.
 */
public class DimensionFilter {

    private List<String> dims;

    public DimensionFilter(String expr) {
        dims = new ArrayList<>();
        if (expr == null || expr.matches("\\s*")) {
            return;
        }
        analyseExpr(expr);
    }

    public boolean apply(Set<BiomeDictionary.Type> dimension) {
        boolean res = true;
        for (BiomeDictionary.Type dim : dimension) {
            // Dimensions vanilla
            if (dim.toString().equalsIgnoreCase(BiomeDictionary.Type.OVERWORLD.toString()) ||
                    dim.toString().equalsIgnoreCase(BiomeDictionary.Type.NETHER.toString()) ||
                    dim.toString().equalsIgnoreCase(BiomeDictionary.Type.END.toString())) {
                res = dims.contains(dim.toString());
            // Biomes vanilla -> pas intéressé ici
            } else if (isVanillaExceptDims(dim)) {
                continue;
            // Dimension non-vanilla
            } else {
                res = caseInsensitiveContains(dim.toString(), dims);
            }
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : dims) {
            sb.append(s + ";");
        }

        int k = sb.lastIndexOf(";");
        if (k >= 0) {
            sb.deleteCharAt(k);
        }
        return sb.toString();
    }

    private void analyseExpr(String expr) {
        String[] dimensions = expr.split(";");
        for (String d : dimensions) {
            if (d == null || d.length() == 0) {
                throw new AssertionError("Invalid dimension filter !");
            }
            dims.add(d.toUpperCase().trim());
        }
    }

    /**
     * Returns true if the biome is vanilla, excepting dimensions (OVERWORLD, NETHER, END).
     * Tests are effectued by order of frequency to make the function execute faster.
     *
     * @param biome The biome we are testing.
     * @return      True if the biome is vanilla, false otherwise.
     */
    private boolean isVanillaExceptDims(BiomeDictionary.Type biome) {
        return biome.equals(BiomeDictionary.Type.FOREST) ||
                biome.equals(BiomeDictionary.Type.PLAINS) ||
                biome.equals(BiomeDictionary.Type.COLD) ||
                biome.equals(BiomeDictionary.Type.CONIFEROUS) ||
                biome.equals(BiomeDictionary.Type.HILLS) ||
                biome.equals(BiomeDictionary.Type.RARE) ||
                biome.equals(BiomeDictionary.Type.SWAMP) ||
                biome.equals(BiomeDictionary.Type.WET) ||
                biome.equals(BiomeDictionary.Type.BEACH) ||
                biome.equals(BiomeDictionary.Type.OCEAN) ||
                biome.equals(BiomeDictionary.Type.HOT) ||
                biome.equals(BiomeDictionary.Type.WATER) ||
                biome.equals(BiomeDictionary.Type.DRY) ||
                biome.equals(BiomeDictionary.Type.SPARSE) ||
                biome.equals(BiomeDictionary.Type.MOUNTAIN) ||
                biome.equals(BiomeDictionary.Type.SNOWY) ||
                biome.equals(BiomeDictionary.Type.MESA) ||
                biome.equals(BiomeDictionary.Type.SANDY) ||
                biome.equals(BiomeDictionary.Type.PLATEAU) ||
                biome.equals(BiomeDictionary.Type.DENSE) ||
                biome.equals(BiomeDictionary.Type.WASTELAND) ||
                biome.equals(BiomeDictionary.Type.VOID) ||
                biome.equals(BiomeDictionary.Type.SPOOKY) ||
                biome.equals(BiomeDictionary.Type.SAVANNA) ||
                biome.equals(BiomeDictionary.Type.JUNGLE) ||
                biome.equals(BiomeDictionary.Type.DEAD) ||
                biome.equals(BiomeDictionary.Type.LUSH) ||
                biome.equals(BiomeDictionary.Type.MAGICAL) ||
                biome.equals(BiomeDictionary.Type.MODIFIED) ||
                biome.equals(BiomeDictionary.Type.MUSHROOM) ||
                biome.equals(BiomeDictionary.Type.RIVER);
    }

    /**
     * Returns true if the dimension denoted by the string dim is present in the list of strings searchIn, case
     * insensitive, false otherwise.
     *
     * @param dim      The string searched in searchIn.
     * @param searchIn The list of strings we are searching dim in.
     * @return         True if dim was found in searchIn, case insensitive.
     */
    private boolean caseInsensitiveContains(String dim, List<String> searchIn) {
        for (String s : searchIn) {
            if (dim.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
