package ar.edu.itba.cys.system.exception;

public class FileNotFoundException extends RuntimeException {
  public FileNotFoundException(String fileName) {
    super("The file " + fileName + " does not exist");
  }
}
