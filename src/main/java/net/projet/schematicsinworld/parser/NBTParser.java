package net.projet.schematicsinworld.parser;

import net.projet.schematicsinworld.parser.tags.Tag;
import net.projet.schematicsinworld.parser.tags.TagCompound;
import net.projet.schematicsinworld.parser.tags.Tags;
import net.projet.schematicsinworld.parser.utils.BytesStream;
import net.projet.schematicsinworld.parser.utils.ParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

class NBTParser extends TagCompound {

    /*
     * Attributs
     */

    // Flux d'octets qui sera lu lors du parsing.
    private BytesStream buffer;

    /*
     * Constructeur
     */

    public NBTParser(String filepath) throws ParserException {
        // Initialisation des attributs
        this.buffer = new BytesStream();
        // Décompresse le fichier et stock ses données dans buffer.
        this.extractFile(filepath);
        // Parse le fichier
        this.parseBuffer();
    }

    /*
     * Requêtes
     */

    public ArrayList<Tag> getTags() {
        return new ArrayList<Tag>((ArrayList<Tag>) this.getValue());
    }

    /*
     * Commandes
     */

    private void extractFile(String filepath) throws ParserException {
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
        // Stocke les octets lu dans le flux d'octets
        this.buffer.setBytes(fileContent);
    }

    protected void parseBuffer() throws ParserException {
        // Lit le premier octet
        if (this.buffer.read(1)[0] != Tags.TAG_COMPOUND.ordinal()) {
            // Si celui-ci est différent de TAG_COMPOUND on renvoie une
            // exception
            throw new ParserException("Le fichier NBT est invalide");
        }
        // Parsing du reste du fichier
        super.parseBuffer(this.buffer);
    }

}
