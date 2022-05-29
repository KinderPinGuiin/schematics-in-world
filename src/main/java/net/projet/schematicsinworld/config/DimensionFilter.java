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
        if (expr == null || expr.matches("\\s*")) {
            return;
        }
        dims = new ArrayList<>();
        analyseExpr(expr);
    }

    public boolean apply(Set<BiomeDictionary.Type> dimension) {
        boolean res = true;
        for (BiomeDictionary.Type dim : dimension) {
            System.out.println("During apply; dimension contained : " + dim.toString());
            if (dim.equals(BiomeDictionary.Type.OVERWORLD)) {
                res = dims.contains(dim.toString().toUpperCase());
            }
        }

        return res;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (String s : dims) {
            sb.append(s.toUpperCase() + ";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length());
        }
        System.out.println("result of tostring : " + new String(sb));
        return new String(sb);
    }

    private void analyseExpr(String expr) {
        String[] dimensions = expr.split(";");
        for (String d : dimensions) {
            if (d == null || d.length() == 0) {
                throw new AssertionError("Invalid dimension filter !");
            }
            System.out.println("Dimension readed : " + d);
            dims.add(d.toUpperCase());
        }
    }
}
