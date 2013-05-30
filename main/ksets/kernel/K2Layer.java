package main.ksets.kernel;


public class K2Layer implements HasOutput, Runnable, Comparable<Object> {

	private KII[] k;
	private Connection[][] latConnections;
	private int id;
	
	private static final double alpha = Config.alpha;
	
	public K2Layer(int size, double wee, double wei, double wie, double wii, double wLat_ee, double wLat_ii) {
		k = new KII[size];
		id = Config.getNextId();
		latConnections = new Connection[size][size];
		
		for (int i = 0; i < size; ++i) {
			k[i] = new KII(wee, wei, wie, wii);
		}
		
		for (int i = 0; i < size - 1; ++i) {
			for (int j = 1; j < size; ++j) {
				if (i == j) continue; 
				
				latConnections[i][j] = k[i].connect(k[j], wLat_ee);
				latConnections[j][i] = k[j].connect(k[i], wLat_ee);									
				k[i].connectInhibitory(k[j], wLat_ii);
				k[j].connectInhibitory(k[i], wLat_ii);
			}
		}
	}
	
	public K2Layer(int size, double[] defaultW1, double[] defaultWLat1) {
		this(size, defaultW1[0], defaultW1[1], defaultW1[2], defaultW1[3], defaultWLat1[0], defaultWLat1[1]);
	}

	public double[] getLayerOutput() {
		return this.getLayerOutput(0);
	}
	
	public double[] getLayerOutput(int t) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = this.k[i].getOutput(t);
		}
		
		return output;
	}
	
	public double[] getLayerInhibitoryOutput() {
		return this.getLayerInhibitoryOutput(0);
	}
	
	public double[] getLayerInhibitoryOutput(int t) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = this.k[i].getInhibitoryOutput(t);
		}
		
		return output;
	}
	
	public double getOutput() {
		return this.getOutput(0);
	}
	
	public double getOutput(int t) {
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += this.k[i].getOutput(t);
		}
		
		return sum;
	}
	
	public double getInhibitoryOutput() {
		return this.getInhibitoryOutput(0);
	}
	
	public double getInhibitoryOutput(int t) {
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += this.k[i].getInhibitoryOutput(t);
		}
		
		return sum;
	}
	
	public void setExternalStimulus(double[] stimulus) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].setExternalStimulus(stimulus[i]);
		}
	}
	
	public void connect(HasOutput origin, double weight, int delay) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].connect(origin, weight, delay);
		}
	}
	
	public void connect(HasOutput origin, double weight) {
		connect(origin, weight, 0);
	}
	
	public void connectInhibitory(HasOutput origin, double weight, int delay) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].connectInhibitory(origin, weight, delay);
		}
	}

	public void connectInhibitory(HasOutput origin, double weight) {
		connectInhibitory(origin, weight, 0);
	}
	
	public void solve() {
		for (int i = 0; i < this.k.length; i++) {
			k[i].solve();
		}
	}

	public void run(){
		solve();
	}
	
	public void train() {
		double meanStd = 0.0;
		double[] std = new double[latConnections.length];
		
		for (int i = 0; i < latConnections.length; ++i) {
			std[i] = stardardDeviation(getActivations(latConnections[i], i));
			meanStd += std[i];
		}
		
		meanStd = meanStd / std.length;	
		double deltaW = 0;
		
		for (int i = 0; i < latConnections.length - 1; ++i) {
			for (int j = 1; j < latConnections.length; ++j) {
				if (i == j) continue;
				
				deltaW = alpha * (std[i] - meanStd) * (std[j] - meanStd);
				if (deltaW > 0) {
					latConnections[i][j].setWeight(latConnections[i][j].getWeight() + deltaW);
				}	
			}
		}
	}
	
	private double[] getActivations(Connection[] connections, int skip) {
		double[] act = new double[connections.length - 1];
		
		for (int i = 0; i < connections.length; i++) {
			int j = i < skip ? i : i - 1;
			if (i != skip) {
				act[j] = connections[i].getOutput();
			}
		}
		
		return act;
	}
	
	private double stardardDeviation(double[] x) {
		double sum = 0;
		double x_ = mean(x);
		
		for (int i = 0; i < x.length; ++i) {
			sum += Math.pow(x[i] - x_, 2);
		}
		
		return Math.sqrt(sum/x.length);	
	}
	
	private double mean(double[] x) {
		double sum = 0;
		
		for (int i = 0; i < x.length; ++i) {
			sum += x[i];
		}
		
		return sum/x.length;
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof K2Layer) 
			return (((K2Layer) o).id == this.id) ? 0 : 1;
		return -1;
	}
}
