package ar.edu.itba.cys.image;

import ar.edu.itba.cys.image.algorithm.Shadow;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class BMPHostImage extends BMPImage {
    private Shadow shadow;
    private int secretImageWidth;
    private int secretImageHeight;

    public BMPHostImage(BMPHeader header, List<Integer> colorTable, List<Integer> pixelsWithShadow, int secretImageWidth, int secretImageHeight) {
        super(header, colorTable, pixelsWithShadow);
        this.secretImageHeight = secretImageHeight;
        this.secretImageWidth = secretImageWidth;
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
        List<Integer> shadowPixels = shadow.getBitPixels();
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

    public int binaryToInteger(byte[] numbers) {
        int result = 0;
        for(int i=numbers.length - 1; i>=0; i--)
            if(numbers[i]== 1)
                result += Math.pow(2, (numbers.length-i - 1));
        return result;
    }
    public Shadow getShadow(int k){
        List<Integer> pixelData = this.getPixels();
        List<Integer> pixelByteData = BMPIO.convertToBits(pixelData);
        BMPHeader header = this.getHeader();

        int seed = header.getReservedH();
        int shadowIndex = header.getReservedL();
        int height = header.getHeight();
        int width = header.getWidth();
        int rowSize = ((width + 3) / 4) * 4;
        int shadowPixelLimit = secretImageHeight * secretImageWidth  * (int) Math.ceil((double) Byte.SIZE/ k);
        List<Integer> shadowPixels = new ArrayList<>(shadowPixelLimit);
        for (int x = Byte.SIZE-1; x < width * height * Byte.SIZE; x+=Byte.SIZE) {
            //int rowStart = y * rowSize * Byte.SIZE;
            //for (int x = 0; x < width * Byte.SIZE; x++) {
                    int grayBit = pixelByteData.get(x);
                    shadowPixels.add(grayBit);
                    if (shadowPixels.size() == shadowPixelLimit){
                        break;
                    }
            //}
        }
        return new Shadow(shadowIndex, seed, secretImageWidth, secretImageHeight, shadowPixels);
    }

}
