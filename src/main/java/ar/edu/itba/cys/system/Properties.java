package ar.edu.itba.cys.system;

import ar.edu.itba.cys.image.BMPIO;
import ar.edu.itba.cys.image.Mode;
import ar.edu.itba.cys.exception.FileNotFoundException;
import ar.edu.itba.cys.exception.IllegalFileExtensionException;
import ar.edu.itba.cys.exception.IllegalOptionForSelectedModeException;
import ar.edu.itba.cys.exception.MissingRequiredParameterException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A record class that contains the provided {@link System} parameters.
 *
 * @param mode      The operation mode for a secret image (e.g., DISTRIBUTE or RECOVER),
 *                  represented by the {@link Mode} enum.
 * @param secret    The input/output image {@link File}, depending on if the <code>mode</code> parameter is {@link Mode#DISTRIBUTE}/{@link Mode#RECOVER} respectively.
 * @param k         The minimum amount of shadows required to recover the secret on a <code>(k,n)</code> schema.
 * @param n         The total number of parts to divide the secret into (only valid if the <code>mode</code> parameter is {@link Mode#DISTRIBUTE}). Defaults to the amount of {@value BMPIO#FILE_EXTENSION} images on the selected <code>directory</code> parameter.
 * @param directory The path's string to the working directory containing the shares or where they will be saved. Defaults to the current directory.
 */
public record Properties(Mode mode, File secret, int k, int n, String directory) {

  public static Properties getProperties() {

    // Required parameters
    String d = System.getProperty("d");
    String r = System.getProperty("r");

    if ((d == null && r == null) || (d != null && r != null)) {
      throw new MissingRequiredParameterException("d", "r");
    }

    Mode mode;
    if (d != null) {
      mode = Mode.DISTRIBUTE;
    } else {
      mode = Mode.RECOVER;
    }

    String secret = System.getProperty("secret");
    if (secret == null) {
      throw new MissingRequiredParameterException("secret");
    }

    if (mode == Mode.DISTRIBUTE) {
      if (!secret.endsWith(BMPIO.FILE_EXTENSION)) {
        throw new IllegalFileExtensionException(secret);
      }
      if (!Files.exists(Paths.get(secret))) {
        throw new FileNotFoundException(secret);
      }
    }

    File secretFile = new File(secret);

    String k = System.getProperty("k");
    if (k == null) {
      throw new MissingRequiredParameterException("k");
    }

    int kValue = Integer.parseInt(k);

    if (kValue < 2 || kValue > 10) {
      throw new IllegalArgumentException("The parameter 'k' must be between 2 and 10");
    }

    // Optional parameters
    String n = System.getProperty("n");
    if (n != null && mode != Mode.DISTRIBUTE) {
      throw new IllegalOptionForSelectedModeException("n", mode);
    }

    String dir = System.getProperty("dir", ".");
    int nValue;
    if (n != null) {
      nValue = Integer.parseInt(n);
    } else {
      nValue = (int) BMPIO.getFileAmount(dir);
    }

    if (nValue < 2) {
      throw new IllegalArgumentException("The parameter 'n' must be greater or equal than 2");
    }

    return new Properties(mode, secretFile, kValue, nValue, dir);
  }

}
