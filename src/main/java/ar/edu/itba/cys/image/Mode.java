package ar.edu.itba.cys.image;

/**
 * The operation mode for a secret image.
 */
public enum Mode {
  /**
   * Operation mode that indicates that a secret image is going to be <strong>distributed</strong> in other images.
   */
  DISTRIBUTE {
    @Override
    public String toString() {
      return "DISTRIBUTE";
    }
  },
  /**
   * Operation mode that indicates than a secret image is going to be <strong>recovered</strong> from other images.
   */
  RECOVER {
    @Override
    public String toString() {
      return "RECOVER";
    }
  }
}
