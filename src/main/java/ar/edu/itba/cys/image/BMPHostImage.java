package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;

import java.util.ArrayList;
import java.util.List;

public class BMPHostImage extends BMPImage {
    private Shadow shadow;

    public BMPHostImage(BMPHeader header, List<Integer> colorTable, List<Integer> pixelsWithShadow) {
        super(header, colorTable, pixelsWithShadow);
    }

    public BMPHostImage(BMPHeader header, List<Integer> colorTable, List<Integer> pixels, Shadow secret) {
        super(header, colorTable, pixels);
        this.shadow = secret;
        int width = header.getWidth();
        int height = header.getHeight();
        List<Integer> pixelsWithShadow = setShadowInPixels(pixels, width, height, shadow);
        super.setPixels(pixelsWithShadow);
    }

    private List<Integer> setShadowInPixels(List<Integer> originalPixels, int width, int height, Shadow shadow){
        List<Integer> shadowPixels = shadow.getPixels();
        List<Integer> finalPixels = new ArrayList<>(height * width);
        int shadowPixelsIndex = 0;
        int hostPixelsIndex = 0;
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                // replace with k
                if (x > 0 && x % 7 == 0){
                    finalPixels.add(shadowPixels.get(shadowPixelsIndex++));
                }else{
                    finalPixels.add(originalPixels.get(hostPixelsIndex));
                }
                hostPixelsIndex++;
            }
        }
        return finalPixels;
    }

    public Shadow getShadow(){
        List<Integer> pixelData = this.getPixels();
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
                    int gray = pixelData.get(rowStart + x);
                    shadowPixels.add(gray);
                }
            }
        }
        return new Shadow(shadowIndex, seed, width, height, shadowPixels);
    }

}
