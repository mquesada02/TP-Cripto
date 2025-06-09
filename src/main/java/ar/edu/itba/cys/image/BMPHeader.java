package ar.edu.itba.cys.image;

import java.util.List;

public class BMPHeader {
    // Header
    //all of them are 4 bytes each except reserved which is stored as two separate variables
    private static final String SIGNATURE = "BM";
    private final int fileSize;
    // from 0006h
    // 2 bytes each
    private final int reservedH;
    private final int reservedL;
    // till 000Ah
    private int dataOffset;
    // Info Header
    private final int size = 40;
    private int width;
    private int height;
    // 2 bytes
    private final int planes = 1;
    //2 bytes
    private final int bitsPerPixel;
    private final int compression;
    private final int imageSize;
    private final int xPixelsPerM;
    private final int yPixelsPerM;
    private final int colorsUsed;
    private final int importantColors;

    public BMPHeader(int fileSize, int reservedH, int reservedL, int dataOffset, int width, int height, int bitsPerPixel, int compression, int imageSize, int xPixelsPerM, int YPixelsPerM, int colorsUsed, int importantColors) {
        this.fileSize = fileSize;
        this.reservedH = reservedH;
        this.reservedL = reservedL;
        this.dataOffset = dataOffset;
        this.width = width;
        this.height = height;
        this.bitsPerPixel = bitsPerPixel;
        this.compression = compression;
        this.imageSize = imageSize;
        this.xPixelsPerM = xPixelsPerM;
        this.yPixelsPerM = YPixelsPerM;
        this.colorsUsed = colorsUsed;
        this.importantColors = importantColors;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDataOffset(int newDataOffset){
        this.dataOffset = newDataOffset;
    }
    public int getFileSize() {
        return fileSize;
    }

    public int getReservedH() {
        return reservedH;
    }

    public int getReservedL() {
        return reservedL;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPlanes() {
        return planes;
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public int getCompression() {
        return compression;
    }

    public int getImageSize() {
        return imageSize;
    }

    public int getXPixelsPerM() {
        return xPixelsPerM;
    }

    public int getYPixelsPerM() {
        return yPixelsPerM;
    }

    public int getColorsUsed() {
        return colorsUsed;
    }

    public int getImportantColors() {
        return importantColors;
    }

    public List<Integer> getBasicHeaderWithoutSignature(){
        return List.of(fileSize, reservedH, reservedL, dataOffset);
    }

    public List<Integer> getInfoHeader(){
        return List.of(fileSize);
    }
}
