package net.projet.schematicsinworld.config;

import net.minecraftforge.common.BiomeDictionary;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Un filtre de biome, construit a partir d'une chaine de charactere.
 * Comme cette classe est non-mutable, le clonage profond n'est pas nécessaire
 * si cette classe est un attribut.
 *
 * La grammaire utilisé est :
 *      S' -> S$
 *      S  -> ES | E    // il s'agit du OU
 *      E  -> F&E | F   // il s'agit du ET
 *      F  -> !D | D    // negation.
 *      D  -> (S) | ID  // ou on a une expression dans des parentheses, ou bien un type.
 */
public class BiomeFilter {

    public static final boolean DEFAULT_BOOL = false;

    private List<String> args;
    private Filter filter;
    private static final Pattern isJustChars = Pattern.compile("[a-zA-Z]+");

    public BiomeFilter(String expr){
        if (expr == null || expr.matches("\\s*")){
            return;
        }
        analyseExpr(expr);
        filter = new OrFilter();
    }

    /**
     * Renvoit true si le filtre accepte le biome définit par ces types BiomeDictionnary.
     */
    public boolean apply(Set<BiomeDictionary.Type> biome){
        if (filter == null){
            return DEFAULT_BOOL;
        }
        return filter.apply(biome);
    }

    /**
     *
     * @return Une chaine qui puisse etre mise dans un JSON pour reobtenir retourner ce filtre.
     */
    @Override
    public String toString(){
        if (filter == null) {
            return "";
        }
        return filter.toString();
    }

    // OUTILS

    private void analyseExpr(String expr) {
        String[] noSpace = expr.split(" ");
        args = new LinkedList<String>();
        for (String str : noSpace) {
            StringTokenizer tokenizer = new StringTokenizer(str, "&()!", true);
            while(tokenizer.hasMoreTokens()){
                args.add(tokenizer.nextToken());
            }
        }
    }

    private interface Filter {
        public boolean apply(Set<BiomeDictionary.Type> biome);
    }

    private class OrFilter implements Filter {
        private AndFilter firstFilter;
        private OrFilter secondFilter;

        public OrFilter() {
            firstFilter = new AndFilter();
            if (args.size() > 0 && !args.get(0).equals(")")){
                secondFilter = new OrFilter();
            }
        }

        public boolean apply(Set<BiomeDictionary.Type> biome) {
            if (secondFilter == null) {
                return firstFilter.apply(biome);
            }
            return firstFilter.apply(biome) || secondFilter.apply(biome);
        }

        @Override
        public String toString(){
            if (secondFilter == null){
                return firstFilter.toString();
            }
            return firstFilter.toString() +" "+ secondFilter.toString();
        }
    }

    private class AndFilter implements Filter {
        private NegFilter firstFilter;
        private AndFilter secondFilter;

        public AndFilter() {
            firstFilter = new NegFilter();
            if (args.size() > 0 && args.get(0).equals("&")) {
                args.remove(0);
                secondFilter = new AndFilter();
            }
        }

        public boolean apply(Set<BiomeDictionary.Type> biome) {
            if (secondFilter == null) {
                return firstFilter.apply(biome);
            }
            return firstFilter.apply(biome) && secondFilter.apply(biome);
        }

        @Override
        public String toString(){
            if (secondFilter == null){
                return firstFilter.toString();
            }
            return firstFilter.toString() + "&" + secondFilter.toString();
        }
    }

    private class NegFilter implements Filter {
        private boolean negate = false;
        private Filter filter;

        public NegFilter(){
            if (args.get(0).equals("!")) {
                negate = true;
                args.remove(0);
            }
            if (args.get(0).equals("(")){
                args.remove(0);
                filter = new OrFilter();
                if (!args.get(0).equals(")")){
                    throw new AssertionError("Close your parentheses");
                }
                args.remove(0);
            } else {
                filter = new TypeFilter();
            }
        }

        public boolean apply(Set<BiomeDictionary.Type> biome) {
            if (negate) {
                return !filter.apply(biome);
            }
            return filter.apply(biome);
        }

        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            if (negate) str.append('!');
            if (filter instanceof TypeFilter){
                str.append(filter.toString());
            } else {
                str.append("(" + filter.toString() + ")");
            }
            return str.toString();
        }
    }

    private class TypeFilter implements Filter {
        private BiomeDictionary.Type type;

        public TypeFilter() {
            Matcher m = isJustChars.matcher(args.get(0));
            if (!m.matches()) {
                throw new AssertionError( args.get(0) +" is not a valid type");
            }
            type = BiomeDictionary.Type.getType(args.get(0));
            args.remove(0);
        }

        @Override
        public boolean apply(Set<BiomeDictionary.Type> biome) {
            return biome.contains(type);
        }

        @Override
        public String toString(){
            return type.toString();
        }
    }


}