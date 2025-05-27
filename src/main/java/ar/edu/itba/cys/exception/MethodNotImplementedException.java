package ar.edu.itba.cys.exception;

public class MethodNotImplementedException extends RuntimeException {
  public MethodNotImplementedException() {
    super("This method is not implemented");
  }
}
