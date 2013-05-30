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