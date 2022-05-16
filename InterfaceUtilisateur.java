package mam3.ipa.projet;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.util.Scanner;
import mam3.ipa.projet.TraitementImage;
import mam3.ipa.projet.TraitementImageNiveauGris;
import mam3.ipa.projet.TraitementImageCouleur;


/**
 * @file TraitementImageNiveauGris.java
 * @author Benmouloud Adam
 * @author Benchekroun Nada
 * @date 11/01/2021
 * @title Classe TraitementImageNiveauGris
 * @brief Classe qui gère le traitement et analyse des images en gris( Grayscale)
 */

/**
 * Classe pour interagir avec l'utilisateur pour demander,
 * par exemple, le type de traitement à effectuer
 * sur une image
 */

public class InterfaceUtilisateur {

    /**
     * menu pour interaction avec l'utilisateur
     */
    public void lancerMenu() {
        String fileName, filePath, fileExtension, fileFullName;
        String commande = "";
        Scanner sc;


        sc = new Scanner(System.in);
        System.out.println("Entrez le nom du fichier à traiter (sans extension) : ");
        System.out.print("> ");

        fileName = sc.nextLine();

        System.out.println("Entrez l'extension de ce fichier à traiter (exemple : .png) : ");
        System.out.print("> ");

        fileExtension = sc.nextLine();

        System.out.println("Entrez le chemin pour accéder au fichier à traiter (format .../dossier/) : ");
        System.out.print("> ");

        filePath = sc.nextLine();
        //fileName = "the-legend.png";
        // fileName = "shining.png";
        //fileName = "shining-gs.png";

        //fileName = "cap.png";

        // filePath = "./images/" + fileName;

        // RenderedOp ropimage;
        // ropimage = JAI.create("fileload", filePath);
        //
        // boolean isColored = estCouleur(ropimage);

        // TraitementImage traitementPics = new TraitementImage ( filePath, fileName, ".png");

        while(!commande.equalsIgnoreCase("sortir") &&  !commande.equalsIgnoreCase("s")) {
            // TraitementImage traitementPics = new TraitementImage ( filePath, fileName, ".png");

            fileFullName = filePath + fileName+ fileExtension;
            RenderedOp ropimage;
            ropimage = JAI.create("fileload", fileFullName);
            boolean isColored = estCouleur(ropimage);

            afficherMenu(isColored);
            System.out.print("> ");
            commande = sc.nextLine();

            switch (commande) {
                case "Grayscale":
                case "g":
                    TraitementImageCouleur traitementColorGrayscale = new TraitementImageCouleur (filePath, fileName, fileExtension);
                    traitementColorGrayscale.conversionGreyscale();
                    fileName = traitementColorGrayscale.getShortName();
                    break;
                case "Assombr":
                case "asm":
                    if ( isColored == true ) {
                      TraitementImageCouleur traitementColorAssombr = new TraitementImageCouleur (filePath, fileName, fileExtension);
                      traitementColorAssombr.assombrir();
                      fileName = traitementColorAssombr.getShortName();
                    }
                    else {
                      TraitementImageNiveauGris traitementGrayAssombr = new TraitementImageNiveauGris (filePath, fileName, fileExtension);
                      traitementGrayAssombr.assombrir();
                      fileName = traitementGrayAssombr.getShortName();
                    }
                    break;
                case "Ecl":
                case "ec":
                    if (isColored == true ) {
                        TraitementImageCouleur traitementColorEcl = new TraitementImageCouleur (filePath, fileName, fileExtension);
                        traitementColorEcl.eclaircir();
                        fileName = traitementColorEcl.getShortName();
                    }
                    else {
                        TraitementImageNiveauGris traitementGrayEcl = new TraitementImageNiveauGris (filePath, fileName, fileExtension);
                        traitementGrayEcl.eclaircir();
                        fileName = traitementGrayEcl.getShortName();
                    }
                    break;
                case "AnalyseExp":
                case "ana":
                    if (isColored == true ) {
                        TraitementImageCouleur traitementColorEcl = new TraitementImageCouleur (filePath, fileName, fileExtension);
                        traitementColorEcl.analyse();
                        fileName = traitementColorEcl.getShortName();
                    }
                    else {
                        TraitementImageNiveauGris traitementGrayEcl = new TraitementImageNiveauGris (filePath, fileName, fileExtension);
                        traitementGrayEcl.analyse();
                        fileName = traitementGrayEcl.getShortName();
                    }
                    break;
                case "TraitementLocal":
                case "trt" :
                    if (isColored != true ){
                      TraitementImageNiveauGris traitementGrayLocal= new TraitementImageNiveauGris(filePath, fileName, fileExtension);
                      traitementGrayLocal.traitementLocal("matriceConvex.csv");
                      fileName = traitementGrayLocal.getShortName();
                    }
                    break;
                default:
                    break;
            }
        }


        }

    public void afficherMenu(Boolean isCouleur) {
        System.out.println("--------  # Menu:      -------------------------------------------------------------------\n" +
                "Tapez une des commandes suivantes pour exécuter un traitement ou une analyse sur l'image,\n" +
                "tapez 'sortir' pour quitter l'application.\n");

        if(isCouleur == true) {
          System.out.println("AnalyseExp (ou ana) :         pour faire une analyse de l'exposition de l'image,\n");
            System.out.println("Grayscale (ou g)  :         pour exécuter une transformation en échelle de gris de l'image,\n");
            System.out.println("Assombr (ou asm)  :         pour exécuter un traitement d'assombrissement sur l'image,\n");
            System.out.println("Ecl (ou ec)       :         pour exécuter un traitement d'eclairage sur l'image,\n");
        }
        else {
           System.out.println("TraitementLocal (ou trt) :         pour exécuter un traitement d'eclairage sur l'image,\n");
           System.out.println("AnalyseExp (ou ana)      :         pour faire une analyse de l'exposition de l'image,\n");
           System.out.println("Assombr (ou asm)         :         pour exécuter un traitement d'assombrissement sur l'image,\n");
           System.out.println("Ecl (ou ec)              :         pour exécuter un traitement d'eclairage sur l'image,\n");
        }

        System.out.println("Sortir(ou S):    pour quitter.\n");
        System.out.println("------------------------------------------------------------------------------------------\n");

    }

    /**
     * methode pour detecter type d'image s'il s'agit d'une image couleur
     * ou une image en niveau de gris
     * @param ropimage
     */
    public boolean estCouleur(RenderedOp ropimage) {
        boolean isColored = false;

        BufferedImage bi = ropimage.getAsBufferedImage();

        int IMG_WIDTH = ropimage.getWidth();
        int IMG_HEIGHT = ropimage.getHeight();

        ColorModel colorModel = ropimage.getColorModel();
        if(colorModel.getColorSpace().getType() == ColorSpace.TYPE_RGB) {
            // if so, we can get every RGB pixel now with getRGB()
            // getRGB() will always format every pixel as aRGB in an int
            // an int is written in 4 bytes in Java!
            // int[] px2 = bi.getRGB(0, 0, IMG_WIDTH, IMG_HEIGHT, null, 0,IMG_WIDTH);
            // System.out.println("l'image est couleur");
            isColored = true;
        } else if (bi.getType() == BufferedImage.TYPE_BYTE_GRAY && colorModel.getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
            // we can't get directly the data from the BufferedImage
            // we need the Raster image, which containes de raw data from the image

            // Raster r = ropimage.getData();

            // from the Raster object we retrieve first a DataBufferByte
            // DataBufferByte db = (DataBufferByte)(r.getDataBuffer());
            // then, the real array of bytes

            // byte[] px = db.getData();
            // System.out.println("l'image est en niveau de gris");
        }
        return isColored;
    }

}
