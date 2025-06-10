package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BMPHostImage extends BMPImage {
    private final int secretImageWidth;
    private final int secretImageHeight;

    public BMPHostImage(BMPHeader header, List<Integer> colorTable, List<Integer> pixelsWithShadow, int secretImageWidth, int secretImageHeight) {
        super(header, colorTable, pixelsWithShadow);
        this.secretImageHeight = secretImageHeight;
        this.secretImageWidth = secretImageWidth;
    }

    public Shadow getShadow(int k){
        List<Integer> pixelData = this.getPixels();
        List<Integer> pixelByteData = BMPIO.convertToBits(pixelData);
        BMPHeader header = this.getHeader();

        int seed = header.getReservedH();
        int shadowIndex = header.getReservedL();
        int height = header.getHeight();
        int width = header.getWidth();
        int shadowPixelLimit = (int) Math.ceil((double) secretImageHeight * secretImageWidth/k) * Byte.SIZE;
        List<Integer> shadowPixels = new ArrayList<>(shadowPixelLimit);
        for (int x = Byte.SIZE-1; x < width * height * Byte.SIZE; x+=Byte.SIZE) {
            int grayBit = pixelByteData.get(x);
            shadowPixels.add(grayBit);
            if (shadowPixels.size() == shadowPixelLimit){
                break;
            }
        }
        return new Shadow(shadowIndex, seed, secretImageWidth, secretImageHeight, shadowPixels);
    }

}
