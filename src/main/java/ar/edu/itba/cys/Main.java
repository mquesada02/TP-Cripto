package ar.edu.itba.cys;

import ar.edu.itba.cys.image.Mode;
import ar.edu.itba.cys.image.ImageParsing;
import ar.edu.itba.cys.image.algorithm.ImageSharing;
import ar.edu.itba.cys.system.Properties;
import java.io.File;

public class Main {
    public static void main(String[] arg) {
        Properties properties = Properties.getProperties();
        Mode mode = properties.mode();
        int k = properties.k();
        int n = properties.n();
        File secretFile = properties.secret();
        String output = properties.directory();
        if (mode == Mode.DISTRIBUTE) {
            int[][] imageMatrix = ImageParsing.getXORGrayscaleBMPImage(secretFile);
            ImageSharing.encode(k, n, imageMatrix, output);
        } else {
            ImageSharing.decode(k, n, output, secretFile);
        }
    }
}
