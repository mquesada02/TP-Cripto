package ar.edu.itba.cys.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GaussianInterpolation {

  public static List<Integer> getCoefficients(List<Integer> ys) {
    int r = ys.size();
    int[][] matrix = new int[r][r];
    for (int i = 0; i < r; i++) {
      for (int j = 0; j < r; j++) {
        matrix[i][j] = (int) Math.pow(i + 1, j);
      }
    }
    return solve(matrix, ys);
  }

  private static List<Integer> solve(int[][] matrix, List<Integer> ys) {
    int r = ys.size();

    Integer[] rhs = ys.toArray(new Integer[0]);

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

    List<Integer> x = new ArrayList<>(Collections.nCopies(r, 0));
    for (int i = r - 1; i >= 0; i--) {
      int sum = rhs[i];
      for (int j = i + 1; j < r; j++) {
        sum -= matrix[i][j] * x.get(j);
      }
      x.set(i, sum * RemainderTable.getMultiplicativeInverse(matrix[i][i]));
    }

    return x;
  }
}
