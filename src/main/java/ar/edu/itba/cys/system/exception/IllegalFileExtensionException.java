package ar.edu.itba.cys.system.exception;

public class IllegalFileExtensionException extends RuntimeException {
  public IllegalFileExtensionException(String file) {
    super("The file " + file + " must have a valid file extension");
  }
}
