package test.ksets.kernel;

import static org.junit.Assert.*;
import main.ksets.kernel.ODESolver;


import org.junit.Test;

public class KOSolverTest {

	@Test
	public void testSolve() {
		ODESolver kSolver = new ODESolver();
		
		double[] actuals = kSolver.solve(1, 1, 1);
		assertArrayEquals(new double[]{1.3962, 0.6106}, actuals, 4);
		
		actuals = kSolver.solve(0, 0, 0);
		assertArrayEquals(new double[]{0, 0}, actuals, 4);
		
		actuals = kSolver.solve(0, 1, 2);
		assertArrayEquals(new double[]{0.4302, 0.7361}, actuals, 4);
		
		actuals = kSolver.solve(-1, -1, -1);
		assertArrayEquals(new double[]{-1.3962, -0.6106}, actuals, 4);
		
		actuals = kSolver.solve(10, 100, 1000);
		assertArrayEquals(new double[]{66.4493, 123.1893}, actuals, 4);
		
		actuals = kSolver.solve(1/3, 1/3, 1/3);
		assertArrayEquals(new double[]{0.4654, 0.2035}, actuals, 4);
	}

}
