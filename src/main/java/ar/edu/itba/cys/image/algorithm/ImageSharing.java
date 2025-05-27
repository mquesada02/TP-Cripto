package ar.edu.itba.cys.image.algorithm;

import ar.edu.itba.cys.image.BMPIO;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.math.LagrangianInterpolation;
import ar.edu.itba.cys.math.ModularInterpolation;
import ar.edu.itba.cys.utils.Pair;
import ar.edu.itba.cys.utils.Size;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImageSharing {

    private static List<Integer> getShadowPixels(int n, List<Integer> coefficients) {
        return IntStream.rangeClosed(1, n).map(x -> ModularInterpolation.interpolate(x, coefficients)).boxed().collect(Collectors.toList()); // [f_j(1), ..., f_j(n)]
    }

    public static void encode(int k, int n, int[][] imageMatrix, String outputDirectory) {
        List<Integer> image = ImageParsing.flatMatrixToList(imageMatrix);
        List<List<Integer>> shadowImages = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            shadowImages.add(new ArrayList<>(image.size() - k));
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
            for (int i = 0; i < n; i++) {
                shadowImages.get(i).add(shadowPixels.get(i)); // it appends to the current position
            }
        }

        int width = imageMatrix.length;
        int height = imageMatrix[0].length;

        for (int i = 0; i < n; i++) {
            String filename = String.format("%s%sshadow_%d%s", outputDirectory, File.pathSeparator, (i + 1), BMPIO.FILE_EXTENSION);
            BMPIO.writeToBMP(filename, shadowImages.get(i), width, height);
        }
    }

    public static void decode(int k, int n, String directory, File outputFile) {
        List<List<Integer>> shadows = BMPIO.getPixels(directory);

        List<Integer> image = new ArrayList<>();

        for (int i = 0; i < shadows.getFirst().size(); i++) {
            List<Integer> ys = new ArrayList<>(k);
            for (int j = 0; j < k; j++) {
                int pixel = shadows.get(j).get(i);
                ys.add(pixel);
            }
            List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
            image.addAll(coefficients);
        }

        Path xorFile = Path.of(String.format("%s%sXOR%s", directory, File.pathSeparator, BMPIO.FILE_EXTENSION));
        Pair<Size, List<Integer>> pair = BMPIO.readFromBMP(xorFile);
        Size size = pair.getFirst();
        List<Integer> pixels = pair.getSecond();
        List<Integer> secretImage = new ArrayList<>();
        for (int i = 0; i < image.size(); i++) {
            secretImage.add(image.get(i) ^ pixels.get(i));
        }

        BMPIO.writeToBMP(outputFile.getPath(), secretImage, size.getWidth(), size.getHeight());
    }
}
