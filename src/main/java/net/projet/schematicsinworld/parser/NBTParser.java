package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.utils.StringStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

class NBTParser {

    /*
     * Attributs
     */

    private ArrayList<Tag> tags;
    private StringStream buffer;

    /*
     * Constructeur
     */

    public NBTParser(String filepath) throws IOException {
        // Initialisation des attributs
        this.tags = new ArrayList<Tag>();
        this.buffer = new StringStream();
        // Décompression du fichier
        GZIPInputStream gis = new GZIPInputStream(
            new FileInputStream(filepath)
        );

        // Lire le premier tag

        // ...
    }

    /*
     * Requêtes
     */

    public ArrayList<Tag> getTags() {
        return new ArrayList<Tag>(this.tags);
    }

    /*
     * Commandes
     */

    /*
     * Classe interne
     */

    /**
     * Exception lancée en cas d'erreur lors du parsing du fichier.
     */
    public class ParserException extends Exception {

        public ParserException(String msg) {
            super(msg);
        }

    }

}
