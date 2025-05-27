package ar.edu.itba.cys.image;

import ar.edu.itba.cys.utils.Pair;
import ar.edu.itba.cys.utils.Size;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class BMPIO {

  public static final String FILE_EXTENSION = ".bmp";

  public static List<List<Integer>> getPixels(String dir) {
    Path dirPath = Paths.get(dir);
    List<List<Integer>> pixelsByImage = new ArrayList<>();
    List<Path> files = getFiles(dirPath);
    for (Path file : files) {
      Pair<Size, List<Integer>> pair = readFromBMP(file);
      List<Integer> pixels = pair.getSecond();
      pixelsByImage.add(pixels);
    }
    return pixelsByImage;
  }

  public static List<Path> getFiles(Path dir) {
    return filterFiles(dir).toList();
  }

  public static Stream<Path> filterFiles(Path dir) {
    try (Stream<Path> files = Files.list(dir)) {
      return files.filter(file -> !Files.isDirectory(file) && file.getFileName().toString().endsWith(BMPIO.FILE_EXTENSION));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static long getFileAmount(String dir) {
    Path dirPath = Paths.get(dir);
    if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
      throw new IllegalArgumentException("The following path does not exist or isn't a directory: " + dirPath);
    }
    return filterFiles(dirPath).count();
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

  public static Pair<Size, List<Integer>> readFromBMP(Path file) {
    try (InputStream in = Files.newInputStream(file)) {
      byte[] header = new byte[54];
      if (in.read(header) != 54) {
        throw new IOException("Invalid BMP header size");
      }

      int dataOffset = ((header[13] & 0xFF) << 24) | ((header[12] & 0xFF) << 16) | ((header[11] & 0xFF) << 8) | (header[10] & 0xFF);

      int width = ((header[21] & 0xFF) << 24) | ((header[20] & 0xFF) << 16) | ((header[19] & 0xFF) << 8) | (header[18] & 0xFF);
      int height = ((header[25] & 0xFF) << 24) | ((header[24] & 0xFF) << 16) | ((header[23] & 0xFF) << 8) | (header[22] & 0xFF);

      int bitDepth = ((header[29] & 0xFF) << 8) | (header[28] & 0xFF);
      if (bitDepth != 8) {
        throw new IOException("Only 8-bit grayscale BMPs are supported.");
      }

      int rowSize = ((width + 3) / 4) * 4;
      int imageSize = rowSize * height;

      long n = in.skip(dataOffset - 54);
      if (n < dataOffset - 54) {
        throw new IOException("Corrupted BMP file");
      }

      byte[] pixelData = new byte[imageSize];
      if (in.read(pixelData) != imageSize) {
        throw new IOException("Unexpected EOF while reading pixels");
      }

      List<Integer> pixels = new ArrayList<>(width * height);
      for (int y = height - 1; y >= 0; y--) {
        int rowStart = y * rowSize;
        for (int x = 0; x < width; x++) {
          int gray = pixelData[rowStart + x] & 0xFF;
          pixels.add(gray);
        }
      }

      Size size = new Size(width, height);

      return Pair.of(size, pixels);
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

  public static void writeToBMP(String filename, List<Integer> pixels, int width, int height) {
    try (FileOutputStream fos = new FileOutputStream(filename)) {
      int rowSize = (width + 3) & ~3;
      int imageSize = rowSize * height;
      int paletteSize = 256 * 4;
      int fileSize = 14 + 40 + paletteSize + imageSize;

      byte[] header = new byte[] { 'B', 'M' };
      fos.write(header);
      writeIntLE(fos, fileSize);
      writeShortLE(fos, 0);
      writeShortLE(fos, 0);
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

      int idx = 0;
      byte[] row = new byte[rowSize];

      for (int y = height - 1; y >= 0; y--) {
        Arrays.fill(row, (byte) 0);
        for (int x = 0; x < width; x++) {
          if (idx < pixels.size()) {
            row[x] = (byte) (pixels.get(idx++) & 0xFF);
          }
        }
        fos.write(row);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
