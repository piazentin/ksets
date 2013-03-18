package main.usp.icmc.ksets.kernel;


public class K2Layer implements HasOutput, Runnable, Comparable<Object> {

	private KII[] k;
	private Connection[][] lateralConnections;
	private int id;
	
	private static final double alpha = Configuration.alpha;
	
	public K2Layer(int size) {
		this(size, 2, 1.5, -2.0, -1.0, 0.15, -0.1);
	}
	
	public K2Layer(int size, double wee, double wei, double wie, double wii, double wLat_ee, double wLat_ii) {
		k = new KII[size];
		id = Configuration.getNextId();
		lateralConnections = new Connection[size][size];
		
		for (int i = 0; i < size; ++i) {
			k[i] = new KII(wee, wei, wie, wii);
		}
		
		for (int i = 0; i < size - 1; ++i) {
			for (int j = 1; j < size; ++j) {
				if (i != j) {
					lateralConnections[i][j] = new Connection(k[j], wLat_ee);
					lateralConnections[j][i] = new Connection(k[i], wLat_ee);
					
					k[i].registerConnection(lateralConnections[i][j]);
					k[j].registerConnection(lateralConnections[j][i]);
					
					k[i].registerLowerConnection(k[j], wLat_ii);
					k[j].registerLowerConnection(k[i], wLat_ii);
				}
			}
		}
	}
	
	public K2Layer(int size, double[] defaultW1, double[] defaultWLat1) {
		this(size, defaultW1[0], defaultW1[1], defaultW1[2], defaultW1[3], defaultWLat1[0], defaultWLat1[1]);
	}

	public double[] getFullOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullOutput(int delay) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = this.k[i].getOutput(delay);
		}
		
		return output;
	}
	
	public double[] getFullLowerOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullLowerOutput(int delay) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = this.k[i].getLowerOutput(delay);
		}
		
		return output;
	}
	
	public double getOutput() {
		return this.getOutput(0);
	}
	
	public double getOutput(int delay) {
		// TODO can be optimized by saving a history of summed activations for the layer
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += this.k[i].getOutput(delay);
		}
		
		return sum;
	}
	
	public double getLowerOutput() {
		return this.getLowerOutput(0);
	}
	
	public double getLowerOutput(int delay) {
		// TODO can be optimized by saving a history of summed activations for the layer
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += this.k[i].getLowerOutput(delay);
		}
		
		return sum;
	}
	
	public void setExternalStimulus(double[] stimulus) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].setExternalStimulus(stimulus[i]);
		}
	}
	
	public void registerConnection(HasOutput origin, double weight, int delay) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].registerConnection(origin, weight, delay);
		}
	}
	
	public void registerConnection(HasOutput origin, double weight) {
		registerConnection(origin, weight, 0);
	}
	
	public void registerLowerConnection(HasOutput origin, double weight, int delay) {
		for (int i = 0; i < this.k.length; ++i) {
			k[i].registerLowerConnection(origin, weight, delay);
		}
	}

	public void registerLowerConnection(HasOutput origin, double weight) {
		registerLowerConnection(origin, weight, 0);
	}
	
	public double[] solve() {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; i++) {
			k[i].solve();
		}
		
		return output;
	}


	public void run(){
		solve();
	}
	
	public void train() {
		// TODO understand and implement k3_filt.m in java, in the while, use an simple average
		double meanStd = 0.0;
		double[] std = new double[lateralConnections.length]; 
		for (int i = 0; i < lateralConnections.length; ++i) {
			std[i] = stardardDeviation(lateralConnections[i], i);
			meanStd += std[i];
		}
		
		double deltaW = 0;
		
		for (int i = 0; i < lateralConnections.length - 1; ++i) {
			for (int j = 1; j < lateralConnections.length; ++j) {
				if (i != j) {
					deltaW = alpha * (std[i] - meanStd) * (std[j] - meanStd);
					if (deltaW > 0) {
						lateralConnections[i][j].setWeight(lateralConnections[i][j].getWeight() + deltaW);
					}
				}
			}
		}
		
	}
	
	private double stardardDeviation(Connection[] x, int index) {
		double sum = 0;
		double x_ = mean(x, index);
		
		for (int i = 0; i < x.length; ++i) {
			if (i != index) {
				sum += Math.pow(x[i].getOutput() - x_, 2);
			}
		}
		
		sum = Math.sqrt(sum/(x.length - 1));
		
		return sum;
	}
	
	private double mean(Connection[] x, int skip) {
		double sum = 0;
		
		for (int i = 0; i < x.length; ++i) {
			if (i != skip) {
				sum += x[i].getOutput();
			}
		}
		
		return sum/(x.length - 1);
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof K2Layer) 
			return (((K2Layer) o).id == this.id) ? 1 : 1;
		return -1;
	}
}
