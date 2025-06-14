package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;
import ar.edu.itba.cys.utils.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class BMPIO {

  public static final String FILE_EXTENSION = ".bmp";

  public static Pair<List<BMPHostImage>,List<Shadow>> getShadowForEachHostImage(String hostImagesDir, int k) {
    Path dirPath = Paths.get(hostImagesDir);
    List<Shadow> shadows = new ArrayList<>();
    List<BMPHostImage> hosts = new ArrayList<>();
    List<Path> files = getFiles(dirPath);
    int imagesRead = 0;
    for (Path file : files) {
      if (imagesRead == k){
        break;
      }
      BMPHostImage image = (BMPHostImage) readFromBMP(file, true);
      hosts.add(image);
      imagesRead++;
      Shadow shadow = image.getShadow(k);
      shadows.add(shadow);
    }
    shadows.sort(Comparator.comparingInt(Shadow::getIndex));
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
  public static int readShortLE(InputStream fis) throws IOException {
    int b1 = fis.read();
    int b2 = fis.read();

    if ((b1 | b2 ) < 0) {
      throw new IOException("Unexpected end of file when reading int.");
    }

    return ((b2 & 0xFF) << 8) | (b1 & 0xFF);
  }


  public static int readIntLE(FileInputStream fis) throws IOException {
    int b1 = fis.read();
    int b2 = fis.read();
    int b3 = fis.read();
    int b4 = fis.read();

    if ((b1 | b2 | b3 | b4) < 0) {
      throw new IOException("Unexpected end of file when reading int.");
    }

    return ((b4 & 0xFF) << 24) | ((b3 & 0xFF) << 16) | ((b2 & 0xFF) << 8) | (b1 & 0xFF);
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

      int size = ((header[17] & 0xFF) << 24) | ((header[16] & 0xFF) << 16) | ((header[15] & 0xFF) << 8) | (header[14] & 0xFF);
      int width = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) | ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
      int height = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) | ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);
      int planes = ((header[27] & 0xFF) << 8) | (header[26] & 0xFF);
      int bitsPerPixel = ((header[29] & 0xFF) << 8) | (header[28] & 0xFF);
      int compression = ((header[33] & 0xFF) << 24) | ((header[32] & 0xFF) << 16) | ((header[31] & 0xFF) << 8) | (header[30] & 0xFF);
      int xPixelsPerM = ((header[41] & 0xFF) << 24) | ((header[40] & 0xFF) << 16) | ((header[39] & 0xFF) << 8) | (header[38] & 0xFF);
      int yPixelsPerM = ((header[45] & 0xFF) << 24) | ((header[44] & 0xFF) << 16) | ((header[43] & 0xFF) << 8) | (header[42] & 0xFF);
      int colorsUsed= ((header[49] & 0xFF) << 24) | ((header[48] & 0xFF) << 16) | ((header[47] & 0xFF) << 8) | (header[46] & 0xFF);
      int importantColors = ((header[53] & 0xFF) << 24) | ((header[52] & 0xFF) << 16) | ((header[51] & 0xFF) << 8) | (header[50] & 0xFF);

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
      List<Integer> colorTable = new ArrayList<>(colorTableSize);
      for (int i = 0; i < colorTableSize; i++){
        colorTable.add(in.read());
      }

      long n = (dataOffset - 54 - colorTableSize);
      int secretImageWidth = width;
      int secretImageHeight = height;
      if (n > 0){
        secretImageWidth = readShortLE(in);
        secretImageHeight = readShortLE(in);
      }

      List<Integer> pixelData =  ImageParsing.getGrayscaleBMPImageList(file.toFile());
      BMPHeader bmpHeader = new BMPHeader(fileSize, reservedHigh, reservedLow, dataOffset, width, height,bitsPerPixel, compression, imageSize, xPixelsPerM, yPixelsPerM, colorsUsed, importantColors);
      BMPImage image;
      if (hasSecret){
        image = new BMPHostImage(bmpHeader, colorTable, pixelData, secretImageWidth, secretImageHeight);
      }else{
        image = new BMPImage(bmpHeader, colorTable, pixelData);
      }

      return image;

    } catch (IOException e) {
      throw new RuntimeException("Failed to read BMP: " + file.getFileName() + e.getMessage(), e);
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

  public static void writeToBMP(String filename, BMPHeader header, List<Integer> colorTable, List<Integer> pixels) {
    try (FileOutputStream fos = new FileOutputStream(filename)) {
      byte[] signature = new byte[] { 'B', 'M' };
      int fileSize = header.getFileSize();
      int reservedHigh = 0;
      int reservedLow = 0;
      int dataOffset = 1078;

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

      for (int i = 0; i < colorTable.size(); i++){
        fos.write(colorTable.get(i));
      }

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

  public static List<Integer> convertToBits(List<Integer> input) {
    List<Integer> bits = new ArrayList<>();
    for (int number : input) {
      for (int i = Byte.SIZE - 1; i >= 0; i--) {
        bits.add((number >> i) & 1);
      }
    }
    return bits;
  }

  public static List<Byte> convert(List<Integer> ints) {
    List<Byte> bytes = new ArrayList<>();
    for (int i : ints) {
      bytes.add((byte) (i & 0xFF));
    }
    return bytes;
  }


  public static List<Integer> convertFromBits(List<Integer> bits) {
    if (bits.size() % Byte.SIZE != 0) {
      throw new IllegalArgumentException("Bit list length must be a multiple of 32.");
    }

    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < bits.size(); i += Byte.SIZE) {
      int value = 0;
      for (int j = 0;j < Byte.SIZE; j++) {
        value = (value << 1) | bits.get(i + j);
      }
      result.add(value);
    }
    return result;
  }

  public static void writeShadowToBMPHostImage(BMPImage hostImage, String shadowFilename, Shadow shadow, int k) {
    try (FileOutputStream fos = new FileOutputStream(shadowFilename)) {
      BMPHeader header = hostImage.getHeader();
      List<Integer> colorTable = hostImage.getColorTable();
      byte[] signature = new byte[] { 'B', 'M' };
      int fileSize = header.getFileSize();
      int reservedHigh = header.getReservedH();
      int reservedLow = header.getReservedL();
      //adding bytes for secret image's width and height
      int dataOffset = header.getDataOffset() + 4;
      fos.write(signature);
      writeIntLE(fos, fileSize);
      writeShortLE(fos, shadow.getSeed());
      writeShortLE(fos, shadow.getIndex());
      writeIntLE(fos, dataOffset);

      int size = header.getSize();
      int width = header.getWidth();
      int height = header.getHeight();
      int planes = header.getPlanes();
      int bitsPerPixel = header.getBitsPerPixel();
      int compression = header.getCompression();
      int imageSize = header.getImageSize();
      int secretImageHeight = shadow.getSecretImageHeight();
      int secretImageWidth = shadow.getSecretImageWidth();
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

      for (int i = 0; i < colorTable.size(); i++){
        fos.write(colorTable.get(i));
      }
      writeShortLE(fos, secretImageWidth);
      writeShortLE(fos, secretImageHeight);

      List<Integer> hostPixels = hostImage.getPixels();
      List<Integer> shadowPixels = shadow.getBitPixels();

      List<Integer> hostPixelsBits = convertToBits(hostPixels);
      List<Integer> shadowPixelsBits = convertToBits(shadowPixels);

      for (int hostPixelsIndex= Byte.SIZE-1, shadowPixelIndex = 0; shadowPixelIndex < shadowPixelsBits.size(); hostPixelsIndex+= Byte.SIZE, shadowPixelIndex++){
        hostPixelsBits.set(hostPixelsIndex, shadowPixelsBits.get(shadowPixelIndex));
      }

      List<Integer> finalIntegerPixels = convertFromBits(hostPixelsBits);
      for (Integer pixel : finalIntegerPixels){
        fos.write(pixel);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
