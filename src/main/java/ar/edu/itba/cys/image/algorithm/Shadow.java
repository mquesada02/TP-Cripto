package ar.edu.itba.cys.image.algorithm;

import java.util.ArrayList;
import java.util.List;

public class Shadow {
    private final int index;
    private final int seed;
    private final int secretImageHeight;
    private final int secretImageWidth;
    private List<Integer> bitPixels = new ArrayList<>();

    public Shadow(int index, int seed, int secretImageWidth, int secretImageHeight, List<Integer> bitPixels) {
        this.index = index;
        this.seed = seed;
        this.secretImageHeight = secretImageHeight;
        this.secretImageWidth = secretImageWidth;
        this.bitPixels = bitPixels;
    }

    public Shadow(int index, int seed, int secretImageWidth, int secretImageHeight) {
        this.index = index;
        this.seed = seed;
        this.secretImageHeight = secretImageHeight;
        this.secretImageWidth = secretImageWidth;
    }

    public List<Integer> getBitPixels() {
        return bitPixels;
    }

    public int getIndex() {
        return index;
    }

    public int getSecretImageHeight() {
        return secretImageHeight;
    }

    public int getSecretImageWidth() {
        return secretImageWidth;
    }

    public int getSeed() {
        return seed;
    }
}
