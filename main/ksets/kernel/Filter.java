package main.ksets.kernel;

public class Filter {

	public double[] k3_filt (double[] x) {
		double[] b = Config.filtB;
		double[] a = new double[b.length];
		a[0] = 1;
		
		return filter(b, a, x);
	}
	
	public double[] filter(double[] b, double[] a, double[] x) {
		
		double[] y = new double[x.length];
		int ord = y.length > b.length ? b.length-1 : y.length-1;
		int np  = y.length -1;
		
		y[0] = b[0] * x[0];
		for (int i = 1; i < ord+1; i++) {
			y[i] = 0.0;
	        for (int j = 0; j < i+1; j++)
	        	y[i] += b[j] * x[i-j];
	        for (int j = 0; j < i; j++)
	        	y[i] -= a[j+1] * y[i-j-1];
		}
		for (int i = ord+1; i < np+1; i++) {
			y[i] = 0.0;
			for (int j = 0; j < ord+1; j++)
				y[i] += b[j] * x[i-j];
			for (int j = 0; j < ord; j++)
				y[i] -= a[j+1] * y[i-j-1];
		}
		
		return y;		
	}
	
}
