package main.usp.icmc.ksets.kernel;

// TODO doubles lacks precision. Consider using BigDecimal in future versions
public class KOSolver {
	
	private static double a = Configuration.a;
	private static double b = Configuration.b;
	private static double h = Configuration.h; // time step / time resolution for RungeKutta solver / dT

	/**
	 * Solving the ODE by Runge-Kutta's method. 
	 * Using the method from the original Roman Ilin implementation in matlab.
	 * @param x is the activation
	 * @param y is the derivative of x. Thus x' = y
	 * @param I is the right-hand side of the equation, the input.
	 * @return
	 */
	public static double[] solve(double x, double y, double I) {
		double k1 = F(y) * h;
		double l1 = G(x, y, I) * h;
		
		double k2 = F(y + l1/2) * h;
		double l2 = G(x + k1/2, y + l1/2, I) * h;
		
		double k3 = F(y + l2/2) * h;
		double l3 = G(x + k2/2, y + l2/2, I) * h;
		
		double k4 = F(y + l2) * h; // 0.0531 (Matlab) x 0.05009 (Java)
		double l4 = G(x + k3, y + l3, I) * h;
		
		x = x + (k1 + 2*k2 + 2*k3 + k4)/6;
		y = y + (l1 + 2*l2 + 2*l3 + l4)/6;
		
		return new double[]{x, y};
	}
	
	/**
	 * 
	 * @param B the derivative
	 * @return the right hand side of the equation x' = y
	 */
	private static double F(double y) {
		return y;
	}
	
	private static double G(double x, double y, double I) {
		return (-(a + b) * y) - (a * b * x) + (a * b * I); // Try make this more precise
	}
}
