package Skin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    static double[][][] likelihoodSkin = new double[256][256][256];
    static double[][][] likelihoodNonSkin = new double[256][256][256];


    public static void train() throws IOException {

        ReadFiles rf = new ReadFiles();
        File Mask[] = rf.getFiles("C:\\Users\\Asus\\Downloads\\images\\train\\Mask");
        File NonMask[] = rf.getFiles("C:\\Users\\Asus\\Downloads\\images\\train\\Non_Mask");
        int[][][]skinCount = new int [256][256][256];
        int[][][]nonSkinCount = new int [256][256][256];
        double skin=0,nonSkin=0;


        for(int i = 0 ; i < 500; i++){
            BufferedImage imgM= ImageIO.read(Mask[i]);
            BufferedImage imgM2 = new BufferedImage( imgM.getWidth(), imgM.getHeight(), BufferedImage.TYPE_INT_RGB );
            imgM2.getGraphics().drawImage( imgM, 0, 0, null );

            BufferedImage imgNm = ImageIO.read(NonMask[i]);
            BufferedImage imgNm2 = new BufferedImage( imgNm.getWidth(), imgNm.getHeight(), BufferedImage.TYPE_INT_RGB );
            imgNm2.getGraphics().drawImage( imgNm, 0, 0, null );


            int[] data = ( (DataBufferInt) imgM2.getRaster().getDataBuffer() ).getData();
            int[] data_Nm = ( (DataBufferInt) imgNm2.getRaster().getDataBuffer() ).getData();
            for ( int j = 0 ; j < data.length ; j++ ) {
                Color c = new Color(data[j]);
                Color cn = new Color(data_Nm[j]);

                if (c.getRed() > 250 && c.getGreen() > 250 && c.getBlue() > 250) {
                    nonSkinCount[c.getRed()][c.getGreen()][c.getBlue()]++;
                    nonSkin++;
                } else {
                    skinCount[cn.getRed()][c.getGreen()][c.getBlue()]++;
                    skin++;
                }
            }

            for(int j=0;j<256;j++) {
                for (int k = 0; k < 256; k++) {
                    for (int l = 0; l < 256; l++) {
                        if(skin!=0)
                            likelihoodSkin[j][k][l] = skinCount[j][k][l] / skin;
                        if(nonSkin!=0)
                            likelihoodNonSkin[j][k][l] = nonSkinCount[j][k][l] / nonSkin;
                    }
                }
            }

        }

    }

    public static void test() throws IOException {
        ReadFiles rf = new ReadFiles();
        File input[] = rf.getFiles("C:\\Users\\Asus\\Downloads\\images\\test\\inputImg");

        for(int i=0;i<input.length;i++){
            BufferedImage SampleImage = ImageIO.read(input[i]);
            BufferedImage outputImage = new BufferedImage(SampleImage.getWidth(), SampleImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

            int width = SampleImage.getWidth();
            int height = SampleImage.getHeight();
            int[] imageInPixels = SampleImage.getRGB(0, 0, width, height, null, 0, width);
            int[] imageOutPixels = new int[imageInPixels.length];

            for (int j = 0; j < imageInPixels.length; j++) {
                int alpha = (imageInPixels[j] & 0xFF000000) >> 24;
                int red = (imageInPixels[j] & 0x00FF0000) >> 16;
                int green = (imageInPixels[j] & 0x0000FF00) >> 8;
                int blue = (imageInPixels[j] & 0x000000FF) >> 0;

                if (  likelihoodSkin[red][green][blue] > 0.01*likelihoodNonSkin[red][green][blue] ){
                    red = 255;
                    green = 255;
                    blue = 255;

                } else {

                    red =10 ;
                    green = 10;
                    blue =10;
                }

                imageOutPixels[j] = (alpha & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF);

            }
            outputImage.setRGB(0, 0, width, height, imageOutPixels, 0, width);

            String sp = input[i].getName().split("\\.")[0];
            String path = "C:\\Users\\Asus\\Downloads\\images\\test\\outputImg\\out_"+sp+".png";
            File outputFile = new File(path);
            ImageIO.write(outputImage, "png", outputFile);
        }


    }

    public static void main(String[] args) throws IOException {
        train();
        test();

    }
}
