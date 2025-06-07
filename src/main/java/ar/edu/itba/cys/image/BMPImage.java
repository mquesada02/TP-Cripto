package ar.edu.itba.cys.image;

import java.util.List;

public class BMPImage {
    private final BMPHeader header;
    private final List<Integer> colorTable;
    private List<Integer> pixels;

    public BMPImage(BMPHeader header, List<Integer> colorTable, List<Integer> pixels) {
        this.header = header;
        this.colorTable = colorTable;
        this.pixels = pixels;
    }

    public List<Integer> getColorTable() {
        return colorTable;
    }

    public List<Integer> getPixels() {
        return pixels;
    }

    public void setPixels(List<Integer> pixels) {
        this.pixels = pixels;
    }

    public BMPHeader getHeader() {
        return header;
    }


}
