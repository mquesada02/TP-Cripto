package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;

import java.util.ArrayList;
import java.util.List;

public class BMPHostImage extends BMPImage {
    private Shadow shadow;

    public BMPHostImage(BMPHeader header, byte[] colorTable, byte[] pixelsWithShadow) {
        super(header, colorTable, pixelsWithShadow);
    }

    public BMPHostImage(BMPHeader header, byte[] colorTable, byte[] pixels, Shadow secret) {
        super(header, colorTable, pixels);
        this.shadow = secret;
        int width = header.getWidth();
        int height = header.getHeight();
        byte[] pixelsWithShadow = setShadowInPixels(pixels, width, height, shadow);
        super.setPixels(pixelsWithShadow);
    }

    private byte[] setShadowInPixels(byte[] originalPixels, int width, int height, Shadow shadow){
        List<Integer> shadowPixels = shadow.getPixels();
        byte[] finalPixels = new byte[height * width];
        int shadowPixelsIndex = 0;
        int hostPixelsIndex = 0;
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                // replace with k
                if (x > 0 && x % 7 == 0){
                    finalPixels[hostPixelsIndex] = (byte) (shadowPixels.get(shadowPixelsIndex++) & 0xFF);
                }else{
                    finalPixels[hostPixelsIndex] = originalPixels[hostPixelsIndex];
                }
                hostPixelsIndex++;
            }
        }
        return finalPixels;
    }

    public Shadow getShadow(){
        byte[] pixelData = this.getPixels();
        BMPHeader header = this.getHeader();

        int seed = header.getReservedH();
        int shadowIndex = header.getReservedL();
        int height = header.getHeight();
        int width = header.getWidth();
        int rowSize = ((width + 3) / 4) * 4;
        List<Integer> shadowPixels = new ArrayList<>(width * height);
        for (int y = height - 1; y >= 0; y--) {
            int rowStart = y * rowSize;
            for (int x = 0; x < width; x++) {
                if (x> 0 && x % 7 == 0) {
                    int gray = pixelData[rowStart + x] & 0xFF;
                    shadowPixels.add(gray);
                }
            }
        }
        return new Shadow(shadowIndex, seed, width, height, shadowPixels);
    }

}
