package ar.edu.itba.cys.image;

public class BMPImage {
    private final BMPHeader header;
    private final byte[] colorTable;
    private byte[] pixels;

    public BMPImage(BMPHeader header, byte[] colorTable, byte[] pixels) {
        this.header = header;
        this.colorTable = colorTable;
        this.pixels = pixels;
    }

    public void setPixels(byte[] pixels) {
        this.pixels = pixels;
    }

    public BMPHeader getHeader() {
        return header;
    }

    public byte[] getColorTable() {
        return colorTable;
    }

    public byte[] getPixels() {
        return pixels;
    }
}
