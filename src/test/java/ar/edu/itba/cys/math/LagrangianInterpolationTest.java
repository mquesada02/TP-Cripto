package ar.edu.itba.cys.math;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class LagrangianInterpolationTest {

  @Test
  public void interpolateIdentityFiveValues() {
    final int totalValues = 5;
    List<Integer> ys = IntStream.rangeClosed(1, totalValues).boxed().toList();
    List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
    final List<Integer> expectedCoefficients = new ArrayList<>(Collections.nCopies(totalValues, 0));
    expectedCoefficients.set(1, 1);
    assertEquals(expectedCoefficients, coefficients);
  }

  @Test
  public void interpolateIdentityModValues() {
    final int totalValues = RemainderTable.PRIME_MOD;
    List<Integer> ys = IntStream.rangeClosed(1, totalValues).boxed().toList();
    List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
    final List<Integer> expectedCoefficients = new ArrayList<>(Collections.nCopies(totalValues, 0));
    expectedCoefficients.set(1, 1);
    assertEquals(expectedCoefficients, coefficients);
  }

  @Test
  public void interpolateIdentityMoreThanModValues() {
    final int totalValues = RemainderTable.PRIME_MOD + 1;
    List<Integer> ys = IntStream.rangeClosed(1, totalValues).boxed().toList();
    List<Integer> coefficients = LagrangianInterpolation.getCoefficients(ys);
    final List<Integer> expectedCoefficients = new ArrayList<>(Collections.nCopies(totalValues, 1));
    expectedCoefficients.set(0, 0);
    assertEquals(expectedCoefficients, coefficients);
  }

}
