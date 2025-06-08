package ar.edu.itba.cys.image.algorithm;

import ar.edu.itba.cys.image.BMPHeader;
import ar.edu.itba.cys.image.BMPHostImage;
import ar.edu.itba.cys.image.BMPIO;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.math.LagrangianInterpolation;
import ar.edu.itba.cys.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageSharing {

    private static List<Integer> getShadowPixels(int n, List<Integer> coefficients) {
        return IntStream.rangeClosed(1, n).map(x -> LagrangianInterpolation.interpolate(x, coefficients)).boxed().collect(Collectors.toList()); // [f_j(1), ..., f_j(n)]
    }

    public static void encode(int k, int n, int[][] imageMatrix, String hostsDirectory, int seed) throws IOException {
        List<Integer> image = ImageParsing.flatMatrixToList(imageMatrix);
        List<Shadow> shadowImages = new ArrayList<>();
        int height = imageMatrix.length;
        int width = imageMatrix[0].length;
        for (int i = 0; i < n; i++) {
            Shadow shadow = new Shadow( i+1, seed, width, height);
            shadowImages.add(shadow);
        }

        for (int j = 0; j <= image.size() - k; j += k) {
            List<Integer> sharingSection = image.subList(j, j + k); // [a_0, a_1, ..., a_(r-1)]
            List<Integer> shadowPixels = getShadowPixels(n, sharingSection);
            while (shadowPixels.contains(256)) {
                // First non-zero pixel of sharingSection decreased by one
                for (int i = 0; i < sharingSection.size(); i++) {
                    int val = sharingSection.get(i);
                    if (val != 0) {
                        sharingSection.set(i, val - 1);
                        break;
                    }
                }
                // Generate n shadow pixels
                List<Integer> mutableSharingSection = new ArrayList<>(sharingSection);
                shadowPixels = getShadowPixels(n, mutableSharingSection);
            }
            //shadow default is for k=8
            for (int i = 0; i < n; i++) {
                Shadow shadow = shadowImages.get(i);
                shadow.getBitPixels().add(shadowPixels.get(i));
            }
        }

        List<String> fileSet = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(hostsDirectory))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    String fileName = path.getFileName().toString();
                    int i = fileName.lastIndexOf('.');

                    if (i > 0) {
                        String name = fileName.substring(0,i);
                        String extension = fileName.substring(i);
                        if (!extension.equals(BMPIO.FILE_EXTENSION)){
                            continue;
                        }
                        fileSet.add(name);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Path ssdPath = Paths.get("ssd");
        Files.createDirectory(ssdPath);
        for (int i = 0; i < n; i++) {
            String hostname = fileSet.get(i);
            String hostFilename = String.format("%s/%s%s", hostsDirectory,hostname, BMPIO.FILE_EXTENSION);
            String shadowFilename = String.format("ssd/%sssd%s", hostname, BMPIO.FILE_EXTENSION);
            Shadow shadow = shadowImages.get(i);
            BMPIO.writeShadowToBMPHostImage(hostFilename, shadowFilename, shadow);
        }
    }

    public static int binaryToInteger(List<Integer> numbers) {
        int result = 0;
        for(int i=numbers.size() - 1; i>=0; i--)
            if(numbers.get(i)== 1)
                result += (int) Math.pow(2, (numbers.size()-i - 1));
        return result;
    }


    public static void decode(int k, String directory, File outputFile) {
        Pair<List<BMPHostImage>,List<Shadow>> pairHostShadow = BMPIO.getShadowForEachHostImage(directory);
        List<BMPHostImage> hosts = pairHostShadow.getFirst();
        List<Shadow> shadows = pairHostShadow.getSecond();

        BMPHostImage firstHostImage = hosts.getFirst();
        BMPHeader hostHeader = firstHostImage.getHeader();
        Shadow firstShadow = shadows.getFirst();
        int seed = firstShadow.getSeed();
        int shadowSize = firstShadow.getBitPixels().size();
        List<Integer> colorTable = firstHostImage.getColorTable();

        List<Integer> image = new ArrayList<>();

        for (int i = 0; i < shadowSize; i+=8){
            List<Pair<Integer, Integer>> ys = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                Shadow shadow = shadows.get(j);
                int index = shadow.getIndex();
                List<Integer> subBits = shadow.getBitPixels().subList(i, i+8);
                int byteValue = binaryToInteger(subBits);
                ys.add(Pair.of(index, byteValue));
            }
            List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
            image.addAll(coefficients);
        }
        Random rand = new Random();
        rand.setSeed(seed);
        List<Integer> secretImage = new ArrayList<>();
      for (Integer pixel : image) {
        secretImage.add(pixel ^ rand.nextInt(256));
      }
        BMPIO.writeToBMP(outputFile.getPath(), hostHeader, colorTable, secretImage);
    }
}
