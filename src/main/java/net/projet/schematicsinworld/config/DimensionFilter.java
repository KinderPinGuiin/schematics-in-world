package net.projet.schematicsinworld.config;

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
            System.out.println("During apply; dimension contained : " + dim.toString());
            if (dim.equals(BiomeDictionary.Type.OVERWORLD)) {
                res = dims.contains(dim.toString());
            } else if (dim.equals(BiomeDictionary.Type.NETHER)) {
                res = dims.contains(dim.toString());
            } else if (dim.equals(BiomeDictionary.Type.END)) {
                res = dims.contains(dim.toString());
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
}
