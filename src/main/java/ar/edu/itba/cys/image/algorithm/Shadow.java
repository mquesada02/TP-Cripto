package ar.edu.itba.cys.image.algorithm;

import java.util.ArrayList;
import java.util.List;

public class Shadow {
    private final int index;
    private final int seed;
    private final int height;
    private final int width;
    private List<Integer> pixels = new ArrayList<>();

    public Shadow(int index, int seed, int width, int height, List<Integer> pixels) {
        this.index = index;
        this.seed = seed;
        this.height = height;
        this.width = width;
        this.pixels = pixels;
    }

    public Shadow(int index, int seed, int width, int height) {
        this.index = index;
        this.seed = seed;
        this.height = height;
        this.width = width;
    }

    public List<Integer> getPixels() {
        return pixels;
    }

    public int getIndex() {
        return index;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSeed() {
        return seed;
    }
}
