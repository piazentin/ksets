package main.ksets.kernel;

public class Config {
	
	public static int historySize = 10000;
	public static int time = 0;
	
	// Duration in <solve> cicles of the "active" and "resting" periods of the network
	public static int active = 600;
	public static int rest	 = 400;
	
	public static double alpha = 0.005;
	
	// Period of the activation that must be used for computing the training correlation
	// Must be smaller than active and historySize
	public static int stableActivation = (int) Math.floor(active / 2);
	
	public static final double q = 5;
	
	public static final double a = 0.22;
	public static final double b = 0.72;
	public static final double h = 0.5; // timestep for runge-kutta
	
	public static final double[] defaultW1 = {1.8, 1.0, -2.0, -0.8};
	public static final double[] defaultW2 = {1.6, 1.6, -1.5, -2.0};
	public static final double[] defaultW3 = {1.6, 1.9, -0.2, -1.0};
	
	public static final double[] defaultWLat1 = {0.15, -0.1};
	public static final double[] defaultWLat2 = {0.2,  -0.2};
	public static final double[] defaultWLat3 = {0.15, -0.1};	
	
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
