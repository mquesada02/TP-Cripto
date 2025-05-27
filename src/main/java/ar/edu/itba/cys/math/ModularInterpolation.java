package ar.edu.itba.cys.math;

import ar.edu.itba.cys.exception.MethodNotImplementedException;

import java.util.List;

public interface ModularInterpolation {

  static List<Integer> getCoefficients(List<Integer> ys) {
    throw new MethodNotImplementedException();
  }

  static int interpolate(int x, List<Integer> coefficients) {
    int acum = 0;
    int xAcum = 1;
    for (int coefficient : coefficients) {
      acum += coefficient * xAcum;
      xAcum *= x;
    }
    return acum % RemainderTable.PRIME_MOD;
  }

}
