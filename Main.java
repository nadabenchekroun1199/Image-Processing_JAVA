import mam3.ipa.projet.TraitementImage;
import mam3.ipa.projet.TraitementImageNiveauGris;
import mam3.ipa.projet.TraitementImageCouleur;
import mam3.ipa.projet.InterfaceUtilisateur;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

/**
 * @file Test.java
 * @author Benmouloud Adam
 * @author Benchekroun Nada
 * @date 11/01/2021
 * @title Classe Test
 * @brief Classe Test pour compiler le projet
 */

public class Main {
    //main method, l'entrée du programme
    public static void main(String[] args) {

        InterfaceUtilisateur interfUtilisateur = new InterfaceUtilisateur();

        String fileName = null;

        if(args == null || args.length == 0) {
            interfUtilisateur.lancerMenu();
        } else if(args.length >= 1){
            fileName = args[0];
            RenderedOp ropimage;
            ropimage = JAI.create("fileload", fileName);
            interfUtilisateur.estCouleur(ropimage);
        }

    }

}
