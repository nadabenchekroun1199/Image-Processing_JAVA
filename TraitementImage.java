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

public abstract class TraitementImage {
  private byte[] px;
  private int[] px2;
  private String fileName;
  private String fileType;
  private String shortName;
  private String fileExtension;
  private String filePath;
  private int longueur;
  private int largeur;

  /*
  Ce constructeur permet de créer un objet TraitementImage, cet objet contient le nom du fichier, son type,
  sa largeur et longueur mais aussi le tableau correspondant aux pixels (tableau pour RGB ou Gray)
  C'est à partir de cet objet que nous effectuerons les opérations et créerons les nouveaux fichiers
  */
  public TraitementImage (String cheminFichier, String nomFichier, String extensionFichier){
    File input;
    this.shortName = nomFichier;
    this.fileExtension = extensionFichier;
    this.filePath = cheminFichier;
    //args = new String[]{"images\\shining-gs.png"};
    String[] args;
    args = new String[]{cheminFichier+nomFichier+extensionFichier}; // Faudra donc écrire le chemin relatif pour chaque image choisie

    // le fichier à traiter
    if(args.length >= 1){
        this.fileName = args[0];
    }
    // detection type d'image s'il s'agit d'une image couleur
    // ou une image en niveau de gris

    RenderedOp ropimage;
    ropimage = JAI.create("fileload", fileName);
    BufferedImage bi = ropimage.getAsBufferedImage();

    int IMG_WIDTH = ropimage.getWidth();
    int IMG_HEIGHT = ropimage.getHeight();

    // get  longueur and largeur de l'image
    this.largeur = IMG_WIDTH;
    this.longueur = IMG_HEIGHT;

    ColorModel colorModel = ropimage.getColorModel();
    if(colorModel.getColorSpace().getType() == ColorSpace.TYPE_RGB) {
        // si c'est le cas, on peut prendre chaque pixel RGB avec getRGB
        //getRGB() va transformer chaque pixel en aRGB pixel en int
        //chaque int est en 4byte

        this.px2 = bi.getRGB(0, 0, IMG_WIDTH, IMG_HEIGHT, null, 0,IMG_WIDTH);
        this.fileType="Color";
    } else if (bi.getType() == BufferedImage.TYPE_BYTE_GRAY && colorModel.getColorSpace().getType() == ColorSpace.TYPE_GRAY) {
        // we can't get directly the data from the BufferedImage
        // we need the Raster image, which containes de raw data from the image
        Raster r = ropimage.getData();
        // from the Raster object we retrieve first a DataBufferByte
        DataBufferByte db = (DataBufferByte)(r.getDataBuffer());
        // then, the real array of bytes
        this.px = db.getData();
        this.fileType="Gray";
    }

  }

  /*
  Les getters
  */

  public int getLargeur () {
    return largeur;
  }
  public int getLongueur () {
    return longueur;
  }

  public String getFileType () {
    return fileType;
  }

  public String getFileName () {
    return fileName;
  }

  public String getShortName () {
    return shortName;
  }

  public String getFilePath () {
    return filePath;
  }

  public String getFileExtension () {
    return fileExtension;
  }

  public int[] getPx2 () {
    return px2;
  }

  public byte[] getPx () {
    return px;
  }

  /*
  Les setters
  */

  public void setFileName( String nomFichier ) {
    this.fileName = nomFichier;
  }
  public void setShortName ( String nomCourt ) {
    this.shortName = nomCourt;
  }
  public void setFilePath (String nomChemin) {
    this.filePath = nomChemin;
  }
  public void setFileExtension (String nomExtension) {
    this.fileExtension = nomExtension;
  }
  public void setPx( byte[] pxP ) {
    this.px = pxP;
  }
  public void setPx2( int[] px2p ) {
    this.px2 = px2p;
  }
  public void setFileType( String typeFichier ) {
    this.fileType = typeFichier;
  }


  public void eclaircir() {}

  public void assombrir() {}

  public void analyse() {}

}
