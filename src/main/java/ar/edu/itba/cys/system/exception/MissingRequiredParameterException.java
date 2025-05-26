package ar.edu.itba.cys.system.exception;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MissingRequiredParameterException extends RuntimeException {

  public MissingRequiredParameterException(String ...parameterNames) {
    super("Any of the following parameters are missing: " + Arrays.stream(parameterNames).map(e -> "'" + e + "'").collect(Collectors.joining(", ")));
  }
}
