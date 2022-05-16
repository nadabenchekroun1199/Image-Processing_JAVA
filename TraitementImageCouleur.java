package mam3.ipa.projet;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.Point;
import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.media.jai.RasterFactory;
import java.lang.Math;

public class TraitementImageCouleur extends TraitementImage {

    /**
      * Constructeur de la classe TraitementImageCouleur
      */

  public TraitementImageCouleur ( String cheminFichier, String nomFichier, String extensionFichier ){
    super( cheminFichier, nomFichier, extensionFichier );
  }

  /**
    * Méthode pour convertir une image en couleur (RGB) en une image en grayscale
    */
  public void conversionGreyscale(){
    //conversion en grayscale
    int[] px2p = this.getPx2();
    byte[] pxp = new byte[this.getLongueur()*this.getLargeur()];

    for (int i = 0; i < this.getLargeur()*this.getLongueur(); i++){

      //on modifie chaque valeur du pixel
      int p = px2p[i];

      int a = (p>>24)&0xff;
      int r = (p>>16)&0xff;
      int g = (p>>8)&0xff;
      int b = p&0xff;
      // moyenne des 3 canaux
      int avg = (r+g+b)/3;
      // on remplace chaque rgb par la moyenne calculée
      pxp[i] = (byte) avg;
    }
    this.setPx(pxp);

    this.setShortName(this.getShortName()+"-grayscale");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );
    this.setFileType("Gray");
    // write image
    SampleModel sm = RasterFactory.createBandedSampleModel(DataBuffer.TYPE_BYTE,this.getLargeur(),this.getLongueur(),1);
    BufferedImage image = new BufferedImage(this.getLargeur(), this.getLongueur(), BufferedImage.TYPE_BYTE_GRAY);
    image.setData(Raster.createRaster(sm, new DataBufferByte(pxp, pxp.length), new Point()));
    // Créer la nouvelle image gris-nomFichier.png
    JAI.create("filestore",image,this.getFileName(),"PNG");
  }

  /**
    * Méthode pour éclaircir une image en grayscale
    */
  @Override
  public void eclaircir() {
    int[] px2p = this.getPx2();
    // On parcoure l'array px2p qui représente les pixels de l'images
    for (int i = 0; i < this.getLargeur()*this.getLongueur(); i++) {
      int p = px2p[i];
      // On capture chaque RGB
      // puis on modifie chacune de ces valeurs
      int r = (p>>16)&0xff;
      int outR = normalized( (int) Math.sqrt( (double) r) , 16);
      //g
      int g = (p>>8)&0xff;
      int outG = normalized( (int) Math.sqrt( (double) g) , 16);
      // b
      int b = p&0xff;
      int outB = normalized( (int) Math.sqrt( (double) b), 16);

      Color color = new Color( outR, outG, outB );
      px2p[i] = color.getRGB();

    }
    this.setShortName(this.getShortName()+"-eclairee");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );
    // write image
    // generation de l'image resultante
    DataBufferInt dataBuffer = new DataBufferInt(px2p, px2p.length);
    ColorModel colorModel = new DirectColorModel(32,0xFF0000,0xFF00,0xFF,0xFF000000);
    WritableRaster raster = Raster.createPackedRaster(dataBuffer, this.getLargeur(), this.getLongueur(),this.getLargeur(),((DirectColorModel) colorModel).getMasks(), null);
    BufferedImage image = new BufferedImage(colorModel, raster,colorModel.isAlphaPremultiplied(), null);
    JAI.create("filestore",image,this.getFileName(),"PNG");
    this.setPx2(px2p);

  }

  /**
    * Méthode pour assombrir une image en grayscale
    */

  @Override
  public void assombrir() {
    int[] px2p = this.getPx2();
    // On parcoure l'array px2p qui représente les pixels de l'images
    for (int i = 0; i < this.getLargeur()*this.getLongueur(); i++) {
      int p = px2p[i];
      // On capture chaque RGB
      // puis on modifie chacune de ces valeurs
      int r = (p>>16)&0xff;
      int outR = normalized( r*r , 65025);
      //g
      int g = (p>>8)&0xff;
      int outG = normalized( g*g , 65025);
      // b
      int b = p&0xff;
      int outB = normalized( b*b, 65025);

      Color color = new Color( outR, outG, outB );
      px2p[i] = color.getRGB();
    }
    this.setShortName(this.getShortName()+"-assombrie");
    this.setFileName( this.getFilePath()+this.getShortName()+this.getFileExtension() );
    // write image
    // generation de l'image resultante
    DataBufferInt dataBuffer = new DataBufferInt(px2p, px2p.length);
    ColorModel colorModel = new DirectColorModel(32,0xFF0000,0xFF00,0xFF,0xFF000000);
    WritableRaster raster = Raster.createPackedRaster(dataBuffer, this.getLargeur(), this.getLongueur(),this.getLargeur(),((DirectColorModel) colorModel).getMasks(), null);
    BufferedImage image = new BufferedImage(colorModel, raster,colorModel.isAlphaPremultiplied(), null);
    JAI.create("filestore",image,this.getFileName(),"PNG");
    this.setPx2(px2p);

  }

  /**
    * Méthode pour analyser une image en grayscale
    */
  @Override
  public void analyse() {

    int[][] tableauExport = new int[256][4];
    /*
    int[x][] représente la valeur de 0-255
    int[][x] représente la nombre d'occurence de cette valeure pour chaque R,G et B
    */
    int[] px2p = this.getPx2();

    // On parcoure l'array px2p qui représente les pixels de l'images
    // et on compte le nombre d'occurence de chacune des valeurs entre 0 et 255
    // pour chaque valeur de R, G et B
    for (int j = 0; j < 255; j++) {
      int nombreOccurenceR = 0;
      int nombreOccurenceG = 0;
      int nombreOccurenceB = 0;
      for (int i = 0;  i < this.getLargeur()*this.getLongueur(); i++) {
        int p = px2p[i];
        if( ((p>>16)&0xff) == j){
          nombreOccurenceR += 1;
        }
        if ( ((p>>8)&0xff) == j) {
          nombreOccurenceG += 1;
        }
        if ( (p&0xff) == j){
          nombreOccurenceB += 1;
        }

      }
      tableauExport[j][0] = j+1;
      tableauExport[j][1] = nombreOccurenceR;
      tableauExport[j][2] = nombreOccurenceG;
      tableauExport[j][3] = nombreOccurenceB;
    }

    // Création du fichier csv à partir des valeurs de l'array tableauExport
    try {
      FileWriter myWriter = new FileWriter(this.getFilePath()+this.getShortName()+"-h.txt");
      for (int i = 0; i < 255; i++) {
        myWriter.write(tableauExport[i][0]+","+tableauExport[i][1]+","+tableauExport[i][2]+","+tableauExport[i][3]+"\n");
      }
      myWriter.close();
    } catch ( IOException e) {
      System.out.println("Une erreure est survenue");
      e.printStackTrace();
    }

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
