package ar.edu.itba.cys;

import ar.edu.itba.cys.image.Mode;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.image.algorithm.ImageSharing;
import ar.edu.itba.cys.system.Properties;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] arg) throws IOException {
        Properties properties = Properties.getProperties();
        Mode mode = properties.mode();
        int k = properties.k();
        File secretFile = properties.secret();
        String output = properties.directory();
        if (mode == Mode.DISTRIBUTE) {
            int seed = 43;
            int[][] imageMatrix = ImageParsing.getXORGrayscaleBMPImage(secretFile, seed);
            int n = properties.n();
            ImageSharing.encode(k, n, imageMatrix, output, seed);
        } else {
            ImageSharing.decode(k, output, secretFile);
        }
    }
}
