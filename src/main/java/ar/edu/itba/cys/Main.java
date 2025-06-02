package ar.edu.itba.cys;

import ar.edu.itba.cys.image.Mode;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.image.algorithm.ImageSharing;
import ar.edu.itba.cys.system.Properties;
import ar.edu.itba.cys.utils.RandomGenerator;

import java.io.File;
import java.util.Random;

public class Main {
    public static void main(String[] arg) {
        Properties properties = Properties.getProperties();
        Mode mode = properties.mode();
        int k = properties.k();
        int seed = RandomGenerator.URIS_FAVORITE_NUMBER;
        RandomGenerator.setSeed(seed);
        File secretFile = properties.secret();
        String output = properties.directory();
        if (mode == Mode.DISTRIBUTE) {
            int[][] imageMatrix = ImageParsing.getXORGrayscaleBMPImage(secretFile);
            int n = properties.n();
            ImageSharing.encode(k, n, imageMatrix, output);
        } else {
            ImageSharing.decode(k, output, secretFile);
        }
    }
}
