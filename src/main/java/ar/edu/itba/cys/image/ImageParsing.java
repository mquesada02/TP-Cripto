package ar.edu.itba.cys.image;

import ar.edu.itba.cys.utils.RandomGenerator;
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
   * Generates a permutation vector from XORing the original <code>secret</code> image list with a list of random ints
   * @param secret file to decode
   * @return a {@link List} of {@link Integer} containing the bytes from the grayscale image XOR'd with random numbers
   */
  public static List<Integer> getXORGrayscaleBMPImageList(File secret) {
    Random rand = RandomGenerator.getRandom();
    List<Integer> secretImage = getGrayscaleBMPImageList(secret);
    List<Integer> randomNumbers = rand.ints(secretImage.size()).boxed().toList();
    for (int i = 0; i < secretImage.size(); i++) {
        secretImage.set(i, secretImage.get(i) ^ randomNumbers.get(i));
    }
    return secretImage;
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
  public static int[][] getXORGrayscaleBMPImage(File secret) {
    Random rand = RandomGenerator.getRandom();
    int[][] secretImage = getGrayscaleBMPImage(secret);
    for (int i = 0; i < secretImage.length; i++) {
      for (int j = 0; j < secretImage[i].length; j++) {
        secretImage[i][j] ^= rand.nextInt(256);
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
      int counter = 0;
      while ((fileInputStream.read()) != -1) {
        if (counter == 53) {
          for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
              imageBitMap[row][col] = fileInputStream.read();
            }
          }
          fileInputStream.close();
          return imageBitMap;
        } else {
          counter++;
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading secret: " + e.getMessage());
      throw new RuntimeException(e);
    }
    return imageBitMap;
  }
}
