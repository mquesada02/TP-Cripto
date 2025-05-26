package ar.edu.itba.cys.system;

import java.io.FileInputStream;
import java.io.IOException;

public class ImageParsing {

    private static int readLittleEndianInt(FileInputStream fis) throws IOException {
        int b1 = fis.read();
        int b2 = fis.read();
        int b3 = fis.read();
        int b4 = fis.read();

        if ((b1 | b2 | b3 | b4) < 0) {
            throw new IOException("Unexpected end of file when reading int.");
        }

        return (b1 & 0xFF) |
                ((b2 & 0xFF) << 8) |
                ((b3 & 0xFF) << 16) |
                ((b4 & 0xFF) << 24);
    }

    /**
     *
     * @param imagePath string path of the target image
     * @return int[] {imageHeight, imageWidth}
     */
    public static int[] getImageSizeForBMP(String imagePath){
        try (FileInputStream fileInputStream = new FileInputStream(imagePath)) {
            int imageWidth, imageHeight, counter = 0;
            while ((fileInputStream.read()) != -1) {
                if (counter == 17){
                    imageWidth = readLittleEndianInt(fileInputStream);
                    imageHeight = readLittleEndianInt(fileInputStream);
                    int[] result = {imageHeight, imageWidth};
                    fileInputStream.close();
                    return result;
                }else {
                    counter++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        //empty
        return new int[]{ };
    }

    /**
     *
     * @param imagePath string path of the target image
     * @param imageHeight int with height (getImageSizeForBMP[0])
     * @param imageWidth int with width (getImageSizeForBMP[1])
     * @return
     */
    public static int[][] getGrayscaleImageUsingBMP(String imagePath, int imageHeight, int imageWidth){
        try (FileInputStream fileInputStream = new FileInputStream(imagePath)) {
            int counter = 0;
            int[][] imageBitMap;
            while ((fileInputStream.read()) != -1) {
                if (counter == 53){
                    imageBitMap = new int[imageHeight][imageWidth];
                    for (int row = 0; row < imageHeight; row++){
                        for (int col = 0; col < imageWidth; col++){
                            imageBitMap[row][col] = fileInputStream.read();
                        }
                    }
                    fileInputStream.close();
                    return imageBitMap;
                }else {
                    counter++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        //empty
        return new int[][]{ };

    }
}
