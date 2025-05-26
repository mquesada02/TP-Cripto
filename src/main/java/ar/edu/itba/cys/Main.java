package ar.edu.itba.cys;

import ar.edu.itba.cys.system.ImageParsing;
import ar.edu.itba.cys.system.Properties;
import java.io.File;

public class Main {
    public static void main(String[] arg) {
        Properties properties = Properties.getProperties();
        File secretFile= properties.secret();
        int[] imageSize = ImageParsing.getImageSizeForBMP(secretFile.getPath());
        int[][] secretImageBitMap = ImageParsing.getGrayscaleImageUsingBMP(secretFile.getPath(), imageSize[0], imageSize[1]);
    }
}
