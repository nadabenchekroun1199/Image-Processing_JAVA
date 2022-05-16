package mam3.ipa.projet;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.media.jai.RasterFactory;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TraitementImageNiveauGris extends TraitementImage {

  // Variable que l'on utilise uniquement pour le traitement local, c'est la "taille" de la matrice de convolution
  private int size2;

  /**
    * Constructeur de la classe TraitementImageCouleur
    * à partir du constructeur de la super classe TraitementImage
    */
  public TraitementImageNiveauGris ( String cheminFichier, String nomFichier, String extensionFichier ){
    super( cheminFichier, nomFichier, extensionFichier );
  }
  /**
    * Méthode pour eclaircir une image en grayscale
    */
  @Override
  public void eclaircir() {
    byte[] pxp = this.getPx();
    for (int i = 0; i < this.getLargeur()*this.getLongueur(); i++) {
      // on modifie chaque valeur du pixel
      int in = pxp[i];
      in = in & 0xFF;
      double in2 = Math.sqrt( (double) in);
      in = (int) in2;
      int out = normalized( in , 16);
      // on remplace chaque pixel par son eclaircee
      pxp[i] = (byte) out;
    }
    this.setShortName(this.getShortName()+"-eclairee");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );
    // write image
    SampleModel sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,this.getLargeur(),this.getLongueur(),1);
    BufferedImage image = new BufferedImage(this.getLargeur(), this.getLongueur(), BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(sm, new DataBufferByte(pxp, pxp.length), new Point()));
    // Créer la nouvelle image eclairee-nomFichier.png
    JAI.create("filestore",image,this.getFileName(),"PNG");
    this.setPx(pxp);

  }


  /**
    * Méthode pour assombrir une image en grayscale
    */
  @Override
  public void assombrir() {
    byte[] pxp = this.getPx();
    for (int i = 0; i < this.getLargeur()*this.getLongueur(); i++) {
      // on modifie chaque valeur du pixel
      int in = pxp[i];
      in = in & 0xFF;
      int out = normalized(in * in, 65025);
      // on remplace chaque pixel par son assombri
      pxp[i] = (byte) out;
    }

    this.setShortName(this.getShortName()+"-assombrie");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );

    // write image
    SampleModel sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,this.getLargeur(),this.getLongueur(),1);
    BufferedImage image = new BufferedImage(this.getLargeur(), this.getLongueur(), BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(sm, new DataBufferByte(pxp, pxp.length), new Point()));
    // Créer la nouvelle image assombrie-nomFichier.png
    JAI.create("filestore",image,this.getFileName(),"PNG");
    this.setPx(pxp);

  }

  /**
    * Méthode pour analyser une image en grayscale
    */
  @Override
  public void analyse() {

    int[][] tableauExport = new int[256][2];
    /*
    int[x][] représente la valeur de 0-255
    int[][x] représente la nombre d'occurence de cette valeure
    */
    byte[] pxp = this.getPx();

    for (int j = 0; j < 255; j++) {
      int nombreOccurence = 0;
      for (int i = 0;  i < this.getLargeur()*this.getLongueur(); i++) {
        if(pxp[i] == j){
          nombreOccurence += 1;
        }
      }
      tableauExport[j][0] = j+1;
      tableauExport[j][1] = nombreOccurence;
    }
    // Création du fichier csv à partir des valeurs de l'array tableauExport
    try {
      FileWriter myWriter = new FileWriter(this.getFilePath()+this.getShortName()+"-h.txt");
      for (int i = 0; i < 255; i++) {
        myWriter.write(tableauExport[i][0]+","+tableauExport[i][1]+"\n");
      }
      myWriter.close();
    } catch ( IOException e) {
      System.out.println("Une erreure est survenue");
      e.printStackTrace();
    }

  }

  /**
    * Méthode pour effectuer un traitement local grâce à une matrice de convolution
    * @param matrixShortName
    */

  public void traitementLocal ( String matrixShortName) {
    String matrixName = this.getFilePath()+matrixShortName;
    String line ="";
    String[] line2;
    int[][] matriceConv = new int[9][9];
    int size2;
    // On ouvre le fichier csv correspondant et on extrait les lignes dans un array de 2 dimensions
    try {
      BufferedReader br = new BufferedReader( new FileReader(matrixName) );
      int i = 0;
      while( (line = br.readLine()) != null) {
        line2 =  line.split(",");
        int size = line2.length;
        this.size2 = size;
        for (int j=0; j<size; j++){
          matriceConv[i][j] = Integer.parseInt( line2[j] );
        }
        i++;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] pxp = this.getPx();
    int x = 0;
    int y = -1;
    int largeur = this.getLargeur();
    int longueur = this.getLongueur();

    // On parcoure l'array type byte correspondant aux pixels de l'images,
    // on calcule ensuite le x et y en fonction de celà
    // Puis on met ensuite en place les cas particuliers e le cas général

    for (int i=0; i< largeur*longueur; i++){
      if( i%this.getLargeur() == 0){
        y += 1;
        x = 0;
      }
      else {
        x +=1;
      }
      // Cas particuliers
      if ( x == 0 && y == 0){
        int passerelle =  pxp[i]*matriceConv[1][1] + pxp[i+1]*matriceConv[1][2] + pxp[i+largeur]*matriceConv[2][1] + pxp[i+largeur+1]*matriceConv[2][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( x == largeur-1 && y == 0){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[2*largeur-1]*matriceConv[2][1] + pxp[2*largeur-2]*matriceConv[2][0];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( x == largeur-1 && y == longueur-1) {
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur-1]*matriceConv[0][0];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( x == largeur-1 && y == 0 ){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i+1]*matriceConv[1][2] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur+1]*matriceConv[0][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( x == 0  && y != 0 && y != longueur-1  ){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i+1]*matriceConv[1][2] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur+1]*matriceConv[0][2] +  pxp[i+largeur]*matriceConv[2][1] + pxp[i+largeur+1]*matriceConv[2][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( x == largeur-1 && ( y != 0 || y !=longueur-1 ) ){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[i+largeur]*matriceConv[2][1] + pxp[i+largeur-1]*matriceConv[2][0] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur-1]*matriceConv[0][0];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( y == 0 && ( x != 0 || x!= longueur-1 ) ){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[i+1]*matriceConv[1][2] + pxp[i+largeur]*matriceConv[2][1] + pxp[i+largeur-1]*matriceConv[2][0] + pxp[i+largeur+1]*matriceConv[2][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      else if ( y == longueur-1 && ( x != 0 || x!= longueur-1 ) ){
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[i+1]*matriceConv[1][2] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur-1]*matriceConv[0][0] + pxp[i-largeur+1]*matriceConv[0][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }
      // Cas général
      else {
        int passerelle = pxp[i]*matriceConv[1][1] + pxp[i-1]*matriceConv[1][0] + pxp[i+1]*matriceConv[1][2] + pxp[i-largeur]*matriceConv[0][1] + pxp[i-largeur-1]*matriceConv[0][0] + pxp[i-largeur+1]*matriceConv[0][2] + pxp[i+largeur]*matriceConv[2][1] + pxp[i+largeur-1]*matriceConv[2][0] + pxp[i+largeur+1]*matriceConv[2][2];
        passerelle = normalized(passerelle, 2295);
        pxp[i] = (byte) passerelle;
      }

    }
    // On change le nom du fichier avant de créer la nouvelle image
    this.setShortName(this.getShortName()+"-traiteeLocalement");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );
    // write image
    SampleModel sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,this.getLargeur(),this.getLongueur(),1);
    BufferedImage image = new BufferedImage(this.getLargeur(), this.getLongueur(), BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(sm, new DataBufferByte(pxp, pxp.length), new Point()));
    // Créer la nouvelle image eclairee-nomFichier.png
    JAI.create("filestore",image,this.getFileName(),"PNG");
    this.setPx(pxp);
  }

  /**
     * Methode pour normaliser le résultat sur
     * l'échelle [0:255]
     * @param in
     * @param maxValue
     * @return
     */
    private int normalized(int in, int maxValue) {
        //System.out.println("in = " + in);
        double scale = (double) 255 / maxValue;
        //System.out.println("scale = " + scale);
        int out = (int) (in * scale);
        //System.out.println(" --> out = " + out);
        return out;
    }

}
