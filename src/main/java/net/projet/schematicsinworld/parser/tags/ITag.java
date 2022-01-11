package net.projet.schematicsinworld.parser.tags;

/**
 * Définit les différents types de données que l'on peut retrouver dans un
 * fichier NBT.
 */
public interface ITag {

    /**
     * Renvoie la clé correspondante au tag.
     *
     * @return La clé.
     */
    String getKey();

    /**
     * Renvoie la valeur correspondante au tag.
     *
     * @return La valeur.
     */
    Object getValue();

}
