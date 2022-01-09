package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.utils.BytesStream;
import org.apache.commons.lang3.ArrayUtils;

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
        byte[] fileContent;
        try {
            // Parcours le fichier en le décompressant
            FileInputStream fis = new FileInputStream(filepath);
            GZIPInputStream gis = new GZIPInputStream(fis);
            ArrayList<Byte> bufferList = new ArrayList<Byte>();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                for (int i = 0; i < len; ++i) {
                    bufferList.add(buffer[i]);
                }
            }
            // Remplit un tableau contenant l'intégralité du fichier
            fileContent = new byte[bufferList.size()];
            int i = 0;
            for (Byte b : bufferList) {
                fileContent[i] = b;
                ++i;
            }
            gis.close();
            fis.close();
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
        for (int i = 0; i < fileContent.length; ++i) {
            System.out.println(fileContent[i] & 0xFF);
        }
        // Parser le reste du fichier
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
