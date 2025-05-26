package ar.edu.itba.cys.system.exception;

import ar.edu.itba.cys.image.Mode;

public class IllegalOptionForSelectedModeException extends RuntimeException {
  public IllegalOptionForSelectedModeException(String option, Mode mode) {
    super("The option '" + option + "' is not supported for mode '" + mode + "'");
  }
}
