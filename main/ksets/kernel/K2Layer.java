package main.ksets.kernel;


public class K2Layer implements HasOutput, Runnable, Comparable<Object> {

	private KII[] k;
	private Connection[][] lateralConnections;
	private int id;
	
	private static final double alpha = Config.alpha;
	
	public K2Layer(int size, double wee, double wei, double wie, double wii, double wLat_ee, double wLat_ii) {
		k = new KII[size];
		id = Config.getNextId();
		lateralConnections = new Connection[size][size];
		
		for (int i = 0; i < size; ++i) {
			k[i] = new KII(wee, wei, wie, wii);
		}
		
		for (int i = 0; i < size - 1; ++i) {
			for (int j = 1; j < size; ++j) {
				if (i != j) {					
					k[i].connect(k[j], wLat_ee);
					k[j].connect(k[i], wLat_ee);
					
					k[i].connectInhibitory(k[j], wLat_ii);
					k[j].connectInhibitory(k[i], wLat_ii);
				}
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
	
	public double[] k3_filt (double[] x) {
		double[] b = new double[]{-0.000435, -0.000561, -0.000631, -0.000618, -0.000497, -0.000264, 0.000044, 0.000341, 0.000500, 0.000406, 0.000013, -0.000588, -0.001129, -0.001191, -0.000304, 0.001893, 0.005458, 0.009986, 0.014549, 0.017796, 0.018212, 0.014512, 0.006074, -0.006704, -0.022246, -0.037930, -0.050516, -0.056811, -0.054430, -0.042463, -0.021872, 0.004522, 0.032480, 0.057151, 0.074064, 0.080078, 0.074064, 0.057151, 0.032480, 0.004522, -0.021872, -0.042463, -0.054430, -0.056811, -0.050516, -0.037930, -0.022246, -0.006704, 0.006074, 0.014512, 0.018212, 0.017796, 0.014549, 0.009986, 0.005458, 0.001893, -0.000304, -0.001191, -0.001129, -0.000588, 0.000013, 0.000406, 0.000500, 0.000341, 0.000044, -0.000264, -0.000497, -0.000618, -0.000631, -0.000561, -0.000435};
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

	@Override
	public int compareTo(Object o) {
		if (o instanceof K2Layer) 
			return (((K2Layer) o).id == this.id) ? 0 : 1;
		return -1;
	}
}
