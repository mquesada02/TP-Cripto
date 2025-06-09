package ar.edu.itba.cys.image;

import ar.edu.itba.cys.utils.Size;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ImageParsing {

  /**
   * Obtains the <code>width</code> and <code>height</code> of a {@value BMPIO#FILE_EXTENSION} image
   *
   * @param secret file for decoding
   * @return the {@link ar.edu.itba.cys.utils.Size} of the image
   */
  public static Size getImageSizeForBMP(File secret) {
    int width = 0, height = 0;
    try (FileInputStream fileInputStream = new FileInputStream(secret)) {
      int counter = 0;
      while ((fileInputStream.read()) != -1) {
        if (counter == 17) {
          width = BMPIO.readIntLE(fileInputStream);
          height = BMPIO.readIntLE(fileInputStream);
          fileInputStream.close();
          return new Size(width, height);
        } else {
          counter++;
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
      throw new RuntimeException(e);
    }
    return new Size(width, height);
  }

  /**
   * Convert a matrix into a list, appending each row sequentially
   * @param matrix any int array of arrays
   * @return a flattened {@link List} of {@link Integer}
   */
  public static List<Integer> flatMatrixToList(int[][] matrix) {
    return Arrays.stream(matrix).flatMapToInt(Arrays::stream).boxed().collect(Collectors.toList());
  }

  /**
   * Returns the grayscale {@value BMPIO#FILE_EXTENSION} image as a {@link List} of {@link Integer}
   *
   * @param secret file to decode
   * @return a {@link List} of {@link Integer} containing the bytes from the grayscale image
   */
  public static List<Integer> getGrayscaleBMPImageList(File secret) {
    int[][] matrix = getGrayscaleBMPImage(secret);
    return flatMatrixToList(matrix);
  }

  /**
   * Generates a permutation matrix from XORing the original <code>secret</code> image matrix with a matrix of random ints
   * @param secret file to decode
   * @return a <code>int[][]</code> matrix containing the bytes from the grayscale image XOR'd with random numbers
   */
  public static int[][] getXORGrayscaleBMPImage(File secret, int seed) {
    Random rand = new Random();
    rand.setSeed(seed);
    int[][] secretImage = getGrayscaleBMPImage(secret);
    for (int i = 0; i < secretImage.length; i++) {
      for (int j = 0; j < secretImage[i].length; j++) {
        int r = rand.nextInt(256);
        secretImage[i][j] ^= r;
      }
    }
    return secretImage;
  }

  /**
   * Returns the grayscale {@value BMPIO#FILE_EXTENSION} image as a matrix
   *
   * @param secret file to decode
   * @return a matrix containing the bytes of the grayscale image read from the <code>secret</code> parameter
   */
  public static int[][] getGrayscaleBMPImage(File secret) {
    Size imageSize = getImageSizeForBMP(secret);
    int width = imageSize.getWidth(), height = imageSize.getHeight();
    int[][] imageBitMap = new int[height][width];

    try (FileInputStream fileInputStream = new FileInputStream(secret)) {
      byte[] header = new byte[54];
      if (fileInputStream.read(header) != 54) {
        throw new IOException("Invalid BMP header length");
      }

      int dataOffset = ((header[13] & 0xFF) << 24) | ((header[12] & 0xFF) << 16) |
              ((header[11] & 0xFF) << 8) | (header[10] & 0xFF);

      fileInputStream.skip(dataOffset - 54);

      int rowPadding = (4 - (width % 4)) % 4;

      for (int row = 0; row < height; row++) {
        for (int col = 0; col < width; col++) {
          int pixel = fileInputStream.read();
          if (pixel == -1) throw new IOException("Unexpected end of file");
          imageBitMap[row][col] = pixel;
        }
        // Skip padding bytes
        fileInputStream.skip(rowPadding);
      }

      return imageBitMap;

    } catch (IOException e) {
      System.err.println("Error reading secret: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }


}
