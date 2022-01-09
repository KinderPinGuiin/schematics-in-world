package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.utils.BytesStream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

class NBTParser {

    /*
     * Attributs
     */

    private ArrayList<Tag> tags;
    private BytesStream buffer;

    /*
     * Constructeur
     */

    public NBTParser(String filepath) throws ParserException {
        // Initialisation des attributs
        this.tags = new ArrayList<Tag>();
        this.buffer = new BytesStream();
        // Décompression du fichier
        try {
            GZIPInputStream gis = new GZIPInputStream(
                new FileInputStream(filepath)
            );
        } catch (FileNotFoundException err) {
            throw new ParserException(
                "Une erreur est survenue lors de l'ouverture du fichier"
            );
        } catch (IOException e) {
            throw new ParserException(
                "Une erreur est survenue lors de la décompression du fichier"
            );
        }
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
