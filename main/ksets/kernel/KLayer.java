package main.ksets.kernel;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class KLayer implements Layer, Runnable, Comparable<Object>, Serializable {

	private static final long serialVersionUID = 3531454372987398253L;
	
	public static enum WLat { 
		USE_FIXED_WEIGHTS, 
		USE_RANDOM_WEIGHTS 
	};
	
	protected Kset[] k;
	private int size;
	private int id;
	private boolean injectNoise;
	private NoiseGenerator noise;
	protected Connection[][] latConnections;
	protected ArrayList<double[]> weightsHistory;
	protected int nLatConnections;
	protected double learningRate;
	
	public KLayer(int size) {
		id = Config.getNextId();
		this.size = size;
	}
	
	public void run(){
		for (int i = 0; i < this.k.length; i++) {
			k[i].run();
		};
	}
	
	public void setExternalStimulus(double[] stimulus) {		
		for (int i = 0; i < this.k.length; ++i) {
			stimulus[i] += this.injectNoise ? noise.get() : 0; 
			k[i].setExternalStimulus(stimulus[i]);
		}
	}
	
	protected void setLateralConnections(Connection[][] latConnections) {
		this.latConnections = latConnections;
	}
	
	public void injectNoise(double mean, double standardDeviation) {
		this.injectNoise = true;
		this.noise = new NoiseGenerator(mean, standardDeviation);
	}
	
	public double[] getLayerOutput() {
		return this.getLayerOutput(0);
	}
	
	public double[] getLayerOutput(int delay) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = this.k[i].getOutput(delay);
		}
		
		return output;
	}
	
	/**
	 * Returns the average activation from all excitatory nodes
	 */
	public double getOutput() {
		return this.getOutput(0);
	}
	
	/**
	 * Returns the average activation from all excitatory nodes at time (now - delay)
	 */
	public double getOutput(int delay) {
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += this.k[i].getOutput(delay);
		}
		
		return sum / this.size;
	}
	
	public double[][] getHistory() {
		double[][] history = new double[getSize()][];
		
		for (int i = 0; i < getSize(); ++i) {
			history[i] = k[i].getHistory();
		}
		
		return history;
	}
	
	public double[][] getActivation() {
		double[][] activation = new double[getSize()][];
		
		for (int i = 0; i < getSize(); ++i) {
			activation[i] = k[i].getActivation();
		}
		
		return activation;
	}
	
	public double[] getActivationDeviation() {
		double[] std = new double[k.length];
		
		for (int i = 0; i < getSize(); ++i) {
			std[i] = stardardDeviation(k[i].getActivation());
		}
		
		return std;
	}
	
	public double[] getActivationMean() {
		double[] mean = new double[k.length];
		
		for (int i = 0; i < getSize(); ++i) {
			mean[i] = mean(k[i].getActivation());
		}
		
		return mean;
	}
	
	public double[] getActivationPower() {
		double[] pow = new double[k.length];
		
		for (int i = 0; i < getSize(); ++i) {
			pow[i] = powerSum(k[i].getActivation());
		}
		
		return pow;
	}
	
	private double powerSum(double[] x) {
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum = sum + Math.pow(x[i], 2);
		}

		return Math.sqrt(sum);
	}
	
	protected double stardardDeviation(double[] x) {
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
	
	public int getSize() {
		return this.size;
	}
	
	public Kset getUnit(int index) {
		return this.k[index];
	}
	
	public double[] getWeights() {
		double[] weights = new double[getSize() * (getSize() - 1)];
		int index = 0;
		
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				if (i == j) continue;				
				weights[index++] = latConnections[i][j].getWeight();
			}
		}
		
		return weights;
	}
	
	public double[][] getWeightsHistory() {
		return Utils.toMatrix(weightsHistory);
	}
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof KLayer) 
			return (((KLayer) o).getId() == this.getId()) ? 0 : 1;
		return -1;
	}
	
	public int getId() {
		return id;
	}
}
