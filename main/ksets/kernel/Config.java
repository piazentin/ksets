package main.ksets.kernel;

public class Config {
	
	public static int historySize = 100;
	public static int time = 0;
	
	public static final double q = 5;
	
	public static final double a = 0.22;
	public static final double b = 0.72;
	public static final double h = 0.5;
	
	public static final double[] defaultW1 = {1.8, 1.0, -2.0, -0.8};
	public static final double[] defaultW2 = {1.6, 1.6, -1.5, -2.0};
	public static final double[] defaultW3 = {1.6, 1.9, -0.2, -1.0};
	
	public static final double[] defaultWLat1 = {0.15, -0.1};
	public static final double[] defaultWLat2 = {0.2,  -0.2};
	public static final double[] defaultWLat3 = {0.15, -0.1};
	
	public static final double alpha = 0.0125;
	
	
	public static final double[] filtB = new double[]{-0.000435, -0.000561, -0.000631, -0.000618, -0.000497, 
		-0.000264, 0.000044, 0.000341, 0.000500, 0.000406, 0.000013, -0.000588, 
		-0.001129, -0.001191, -0.000304, 0.001893, 0.005458, 0.009986, 0.014549, 
		0.017796, 0.018212, 0.014512, 0.006074, -0.006704, -0.022246, -0.037930, 
		-0.050516, -0.056811, -0.054430, -0.042463, -0.021872, 0.004522, 0.032480, 
		0.057151, 0.074064, 0.080078, 0.074064, 0.057151, 0.032480, 0.004522, 
		-0.021872, -0.042463, -0.054430, -0.056811, -0.050516, -0.037930, -0.022246, 
		-0.006704, 0.006074, 0.014512, 0.018212, 0.017796, 0.014549, 0.009986, 
		0.005458, 0.001893, -0.000304, -0.001191, -0.001129, -0.000588, 0.000013, 
		0.000406, 0.000500, 0.000341, 0.000044, -0.000264, -0.000497, -0.000618, 
		-0.000631, -0.000561, -0.000435}; 
	
	public static int getTime() {
		return time;
	}
	
	public static int incTime() {
		time = (++time) % historySize;
		return time;
	}
	
	public static int getTime(int i) {
		int timeplus = (time + i) % historySize;
		return (timeplus >= 0) ? timeplus : (historySize + timeplus) ;
	}

	private static int id = 0;
	public static int getNextId() {
		return id++;
	}
	
}
