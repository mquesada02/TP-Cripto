package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;
import ar.edu.itba.cys.utils.Pair;
import ar.edu.itba.cys.utils.RandomGenerator;
import ar.edu.itba.cys.utils.Size;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class BMPIO {

  public static final String FILE_EXTENSION = ".bmp";

  public static Pair<List<BMPHostImage>,List<Shadow>> getShadowForEachHostImage(String hostImagesDir) {
    Path dirPath = Paths.get(hostImagesDir);
    List<Shadow> shadows = new ArrayList<>();
    List<BMPHostImage> hosts = new ArrayList<>();
    List<Path> files = getFiles(dirPath);
    for (Path file : files) {
      BMPHostImage image = (BMPHostImage) readFromBMP(file, true);
      hosts.add(image);
      Shadow shadow = image.getShadow();
      shadows.add(shadow);
    }
    return new Pair<>(hosts, shadows);
  }

  public static List<Path> getFiles(Path dir) {
    try (Stream<Path> files = Files.list(dir)) {
      return files.filter(file -> !Files.isDirectory(file) && file.getFileName().toString().endsWith(BMPIO.FILE_EXTENSION)).toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static long getFileAmount(String dir) {
    Path dirPath = Paths.get(dir);
    if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
      throw new IllegalArgumentException("The following path does not exist or isn't a directory: " + dirPath);
    }
    return getFiles(dirPath).size();
  }

  public static int readIntLE(FileInputStream fis) throws IOException {
    int b1 = fis.read();
    int b2 = fis.read();
    int b3 = fis.read();
    int b4 = fis.read();

    if ((b1 | b2 | b3 | b4) < 0) {
      throw new IOException("Unexpected end of file when reading int.");
    }

    return (b1 & 0xFF) | ((b2 & 0xFF) << 8) | ((b3 & 0xFF) << 16) | ((b4 & 0xFF) << 24);
  }

  public static BMPImage readFromBMP(Path file, Boolean hasSecret) {
    try (InputStream in = Files.newInputStream(file)) {
      byte[] header = new byte[54];
      if (in.read(header) != 54) {
        throw new IOException("Invalid BMP header size");
      }
      int fileSize = ((header[5] & 0xFF) << 24) | ((header[4] & 0xFF) << 16) | ((header[3] & 0xFF) << 8) | (header[2] & 0xFF);
      int reservedHigh = ((header[7] & 0xFF) << 8 | (header[6] & 0xFF));
      int reservedLow = ((header[9] & 0xFF) << 8 | (header[8] & 0xFF));
      int dataOffset = ((header[13] & 0xFF) << 24) | ((header[12] & 0xFF) << 16) | ((header[11] & 0xFF) << 8) | (header[10] & 0xFF);

      int width = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) | ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
      int height = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) | ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);
      int bitsPerPixel = ((header[29] & 0xFF) << 24) | ((header[28] & 0xFF) << 16) | ((header[27] & 0xFF) << 8) | (header[26] & 0xFF);
      int compression = ((header[33] & 0xFF) << 24) | ((header[32] & 0xFF) << 16) | ((header[31] & 0xFF) << 8) | (header[30] & 0xFF);
      int xPixelsPerM = ((header[41] & 0xFF) << 24) | ((header[40] & 0xFF) << 16) | ((header[39] & 0xFF) << 8) | (header[38] & 0xFF);
      int yPixelsPerM = ((header[45] & 0xFF) << 24) | ((header[44] & 0xFF) << 16) | ((header[43] & 0xFF) << 8) | (header[42] & 0xFF);
      int colorsUsed = ((header[45] & 0xFF) << 24) | ((header[44] & 0xFF) << 16) | ((header[43] & 0xFF) << 8) | (header[42] & 0xFF);
      int importantColors = ((header[49] & 0xFF) << 24) | ((header[48] & 0xFF) << 16) | ((header[47] & 0xFF) << 8) | (header[46] & 0xFF);

      int bitDepth = ((header[29] & 0xFF) << 8) | (header[28] & 0xFF);
      if (bitDepth != 8) {
        throw new IOException("Only 8-bit grayscale BMPs are supported.");
      }

      int rowSize = ((width + 3) / 4) * 4;
      int imageSize = rowSize * height;
      Map<Integer, Integer> numColorsMap = new HashMap<>();
      numColorsMap.putIfAbsent(1,1);
      numColorsMap.putIfAbsent(4,16);
      numColorsMap.putIfAbsent(8, 256);
      numColorsMap.putIfAbsent(16, 65536);
      numColorsMap.putIfAbsent(24, 16777216);
      if (!numColorsMap.containsKey(bitsPerPixel)){
       throw new IOException("Invalid numColors value in BMP File");
      }

      int numColors = numColorsMap.get(bitsPerPixel);
      int colorTableSize = numColors * 4;
      byte[] colorTable = new byte[colorTableSize];
      in.read(colorTable);

      long n = in.skip(dataOffset - 54 - colorTableSize);
      if (n < dataOffset - 54 - colorTableSize) {
        throw new IOException("Corrupted BMP file");
      }

      byte[] pixelData = new byte[imageSize];
      if (in.read(pixelData) != imageSize) {
        throw new IOException("Unexpected EOF while reading pixels");
      }
      BMPHeader bmpHeader = new BMPHeader(fileSize, reservedHigh, reservedLow, dataOffset, width, height,bitsPerPixel, compression, imageSize, xPixelsPerM, yPixelsPerM, colorsUsed, importantColors);
      BMPImage image;
      if (hasSecret){
        image = new BMPHostImage(bmpHeader, colorTable, pixelData);
      }else{
        image = new BMPImage(bmpHeader, colorTable, pixelData);
      }

      return image;

    } catch (IOException e) {
      throw new RuntimeException("Failed to read BMP: " + file.getFileName(), e);
    }
  }

  public static void writeIntLE(OutputStream os, int value) throws IOException {
    writeShortLE(os, value);
    os.write((value >> 16) & 0xFF);
    os.write((value >> 24) & 0xFF);
  }

  public static void writeShortLE(OutputStream os, int value) throws IOException {
    os.write(value & 0xFF);
    os.write((value >> 8) & 0xFF);
  }

  public static void writeToBMP(String filename, BMPHeader header, byte[] colorTable, List<Integer> pixels) {
    try (FileOutputStream fos = new FileOutputStream(filename)) {
      byte[] signature = new byte[] { 'B', 'M' };
      int fileSize = header.getFileSize();
      int reservedHigh = header.getReservedH();
      int reservedLow = header.getReservedL();
      int dataOffset = header.getDataOffset();
      fos.write(signature);
      writeIntLE(fos, fileSize);
      writeShortLE(fos, reservedHigh);
      writeShortLE(fos, reservedLow);
      writeIntLE(fos, dataOffset);

      int size = header.getSize();
      int width = header.getWidth();
      int height = header.getHeight();
      int planes = header.getPlanes();
      int bitsPerPixel = header.getBitsPerPixel();
      int compression = header.getCompression();
      int imageSize = header.getImageSize();
      int xPixelsPerM = header.getXPixelsPerM();
      int yPixelsPerM = header.getYPixelsPerM();
      int colorsUsed = header.getColorsUsed();
      int importantColors = header.getImportantColors();

      writeIntLE(fos, size);
      writeIntLE(fos, width);
      writeIntLE(fos, height);
      writeShortLE(fos, planes);
      writeShortLE(fos, bitsPerPixel);
      writeIntLE(fos, compression);
      writeIntLE(fos, imageSize);
      writeIntLE(fos,  xPixelsPerM);
      writeIntLE(fos, yPixelsPerM);
      writeIntLE(fos, colorsUsed);
      writeIntLE(fos, importantColors);

      fos.write(colorTable);
      int rowSize = (width + 3) & ~3;
      byte[] row = new byte[rowSize];
      int pixelIndex = 0;
      for (int y = height - 1; y >= 0; y--) {
        Arrays.fill(row, (byte) 0);
        for (int x = 0; x < width; x++) {
          row[x] = (byte) (pixels.get(pixelIndex++) & 0xFF);
        }
        fos.write(row);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void writeShadowToBMPHostImage(String hostFilename, String shadowFilename, Shadow shadow) {
    try (FileOutputStream fos = new FileOutputStream(shadowFilename)) {
      int rowSize = (shadow.getWidth()+ 3) & ~3;
      int imageSize = rowSize * shadow.getHeight();
      int paletteSize = 256 * 4;
      int fileSize = 14 + 40 + paletteSize + imageSize;
      int seed = RandomGenerator.getSeed();
      int shadowIndex = shadow.getIndex();
      int width = shadow.getWidth();
      int height = shadow.getHeight();

      byte[] header = new byte[] { 'B', 'M' };
      fos.write(header);
      writeIntLE(fos, fileSize);
      writeShortLE(fos, seed);
      writeShortLE(fos, shadowIndex);
      writeIntLE(fos, 14 + 40 + paletteSize);

      writeIntLE(fos, 40);
      writeIntLE(fos, width);
      writeIntLE(fos, height);
      writeShortLE(fos, 1);
      writeShortLE(fos, 8);
      writeIntLE(fos, 0);
      writeIntLE(fos, imageSize);
      writeIntLE(fos, 2835);
      writeIntLE(fos, 2835);
      writeIntLE(fos, 256);
      writeIntLE(fos, 0);

      for (int i = 0; i < 256; i++) {
        fos.write(i); fos.write(i); fos.write(i); fos.write(0);
      }

      int shadowPixelsIndex = 0;
      int hostPixelsIndex = 0;
      byte[] row = new byte[rowSize];
      BMPImage hostImage = readFromBMP(Path.of(hostFilename), true);
      byte[] originalPixels = hostImage.getPixels();
      List<Integer> shadowPixels = shadow.getPixels();

      for (int y = height - 1; y >= 0; y--) {
        Arrays.fill(row, (byte) 0);
        for (int x = 0; x < width; x++) {
          // replace with k
          if (x > 0 && x % 7 == 0){
            row[x] = (byte) (shadowPixels.get(shadowPixelsIndex++) & 0xFF);
            hostPixelsIndex++;
          }
          else{
            row[x] = originalPixels[hostPixelsIndex++];
          }
        }
        fos.write(row);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
