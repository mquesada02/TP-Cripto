package ar.edu.itba.cys.image.algorithm;

import ar.edu.itba.cys.image.BMPIO;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.math.LagrangianInterpolation;
import ar.edu.itba.cys.math.ModularInterpolation;
import ar.edu.itba.cys.utils.RandomGenerator;

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
        return IntStream.rangeClosed(1, n).map(x -> ModularInterpolation.interpolate(x, coefficients)).boxed().collect(Collectors.toList()); // [f_j(1), ..., f_j(n)]
    }

    public static void encode(int k, int n, int[][] imageMatrix, String hostsDirectory) {
        List<Integer> image = ImageParsing.flatMatrixToList(imageMatrix);
        List<Shadow> shadowImages = new ArrayList<>();
        int seed = RandomGenerator.getSeed();
        int height = imageMatrix.length;
        int width = imageMatrix[0].length;

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
                Shadow shadow = new Shadow(j/k +1, seed, width/k, height/k);
                shadow.getPixels().add(shadowPixels.get(i));
                shadowImages.add(shadow);
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
        for (int i = 0; i < n; i++) {
            String hostFilename = fileSet.get(i);
            String shadowFilename = String.format("%sssd%s", hostFilename, BMPIO.FILE_EXTENSION);
            Shadow shadow = shadowImages.get(i);
            BMPIO.writeToBMP(hostFilename, shadowFilename, , width, height);
        }
    }

    public static void decode(int k, String directory, File outputFile) {
        List<Shadow> shadows = BMPIO.getPixels(directory);

        Shadow firstShadow = shadows.getFirst();
        int height = firstShadow.getHeight();
        int width = firstShadow.getWidth();
        int seed = firstShadow.getSeed();
        int finalIndex = 0;
        List<Integer> image = new ArrayList<>();

        for (int i = 0; i < shadows.size(); i++) {
            List<Integer> ys = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                int pixel = shadows.get(j).getPixels().get(i);
                ys.add(pixel);
            }
            List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
            image.addAll(coefficients);
        }

        Random rand = RandomGenerator.getRandom();
        List<Integer> pixels = new ArrayList<>(image.size());
        for (int i = 0; i < image.size(); i++) {
            pixels.add(rand.nextInt(256));
        }
        List<Integer> secretImage = new ArrayList<>();
        for (int i = 0; i < image.size(); i++) {
            secretImage.add(image.get(i) ^ pixels.get(i));
        }

        BMPIO.writeToBMP(outputFile.getPath(), secretImage, seed, finalIndex, width, height);
    }
}
