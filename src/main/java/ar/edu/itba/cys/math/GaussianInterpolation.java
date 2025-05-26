package ar.edu.itba.cys.math;

import java.util.function.Function;

public class GaussianInterpolation implements ModularInterpolation {
  private Function<Integer, Integer> interpolator;

  @Override
  public void initialize(int ...ys) {
    int r = ys.length;
    int[][] matrix = new int[r][r];
    for (int i = 0; i < r; i++) {
      for (int j = 0; j < r; j++) {
        matrix[i][j] = (int) Math.pow(i + 1, j);
      }
    }
    int[] coefficients = solve(matrix, ys);
    this.interpolator = x -> {
      int acum = 0;
      int xAcum = 1;
      for (int i = 0; i < r; i++) {
        acum += coefficients[i] * xAcum;
        xAcum *= x;
      }
      return acum;
    };
  }

  @Override
  public int interpolate(int x) {
    return interpolator.apply(x);
  }

  private int[] solve(int[][] matrix, int ...ys) {
    int r = ys.length;

    int[] rhs = ys.clone();

    for (int i = 0; i < r; i++) {
      int maxRow = i;
      for (int k = i + 1; k < r; k++) {
        if (Math.abs(matrix[k][i]) > Math.abs(matrix[maxRow][i])) {
          maxRow = k;
        }
      }

      int[] tempRow = matrix[i];
      matrix[i] = matrix[maxRow];
      matrix[maxRow] = tempRow;

      int temp = rhs[i];
      rhs[i] = rhs[maxRow];
      rhs[maxRow] = temp;

      for (int k = i + 1; k < r; k++) {
        int factor = matrix[k][i] * RemainderTable.getMultiplicativeInverse(matrix[i][i]);
        for (int j = i; j < r; j++) {
          matrix[k][j] -= factor * matrix[i][j];
        }
        rhs[k] -= factor * rhs[i];
      }
    }

    int[] x = new int[r];
    for (int i = r - 1; i >= 0; i--) {
      int sum = rhs[i];
      for (int j = i + 1; j < r; j++) {
        sum -= matrix[i][j] * x[j];
      }
      x[i] = (sum * RemainderTable.getMultiplicativeInverse(matrix[i][i])) % RemainderTable.PRIME_MOD;
    }

    return x;
  }
}
