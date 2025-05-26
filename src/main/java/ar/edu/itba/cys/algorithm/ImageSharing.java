package ar.edu.itba.cys.algorithm;

import java.util.ArrayList;
import java.util.List;

public class ImageSharing {

    private final Integer k;
    private final Integer n;
    private final List<Integer> image;
    private final Integer length;
    private final Integer width;
    private final List<List<Integer>> shadowImages;

    public ImageSharing(Integer k, Integer n, List<Integer> image, Integer length, Integer width, List<List<Integer>> shadowImages) {
        this.k = k;
        this.n = n;
        this.image = image;
        this.length = length;
        this.width = width;
        this.shadowImages = shadowImages;
    }

    public void encode() {
        // asume image ya con el XOR, sino agregar

        for (int j = 0; j < image.size() - k; j += k) {
            List<Integer> sharingSection = image.subList(j, j + k);
            // n shadow pixels
            List<Integer> shadowPixels = image.subList(j, j + k); // TODO: cambiar por [f_j(1), ..., f_j(n)]
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
                shadowPixels.clear();
                shadowPixels = image.subList(j, j + k); // TODO: cambiar por [f_j(1), ..., f_j(n)]
            }
            // TODO: Assign pixels to the j-th pixel of the n shadow images
            for (int i = 0; i < shadowImages.size(); i++) {
                shadowImages.get(i).add(j, shadowPixels.get(i));
            }
        }
    }

    public int[][] decode() {
        int[] R = new int[image.size()]; // TODO: Random predefined image, store in constructor??
        List<Integer> Q = new ArrayList<>();
        for (int j = 0; j < image.size(); j++) {
            // Take the j-th pixel of each of the r shadow images
            List<Integer> jPixels = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                jPixels.add(shadowImages.get(i).get(j));
            }
            // jPixels = [f_j(1), ..., f_j(k)]
            // TODO: Obtener [a_0, ..., a_k-1] con el interpolador
            // f_j(x) = (a_0 + a_1 x + a_2 x^2 + ... + a_k-1 x^k-1) mod 257
            List<Integer> pixels = new ArrayList<>();
            // pixels = [a_0, ..., a_k-1]
            Q.addAll(pixels);
        }

        int[] secretImage = new int[image.size()];
        for (int i = 0; i < image.size(); i++) {
            secretImage[i] = R[i] ^ Q.get(i); // XOR
        }

        // Turn into length x width matrix
        int[][] resultImage = new int[length][width];

        // Para el caso secretImage.length == length * width
        for (int i = 0; i < secretImage.length; i++) {
            resultImage[i / width][i % width] = secretImage[i];
        }
        return resultImage;
    }
}
