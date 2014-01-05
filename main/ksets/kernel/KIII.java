package main.ksets.kernel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KIII implements Serializable {

	private static final long serialVersionUID = -1945033929994699028L;
	private K2Layer[] k3;
	private int inputSize;
	private double[] emptyArray; // Empty array used during the resting period
	
	private transient ThreadPoolExecutor pool;
	
	public static enum InterLayerConnectionType {
		AVERAGE,
		CONVERGE_DIVERGE
	}
	
	/**
	 * Create a new KIII with the default configurations from the old matlab implementation.
	 * @param size
	 */
	public KIII(int size) {
		this.inputSize = size;
		this.emptyArray = new double[inputSize];
		
		k3 = new K2Layer[3];
		
		k3[0] = new K2Layer(size, Config.defaultW1, Config.defaultWLat1, K2Layer.WLat.USE_FIXED_WEIGHTS);
		k3[1] = new K2Layer(size, Config.defaultW2, Config.defaultWLat2, K2Layer.WLat.USE_FIXED_WEIGHTS);
		k3[2] = new K2Layer(size, Config.defaultW3, Config.defaultWLat3, K2Layer.WLat.USE_FIXED_WEIGHTS);
		
		// feedforward connection from layer 1 to layer 2
		k3[1].connect(k3[0], 0.3 / k3[0].getSize(), -1, InterLayerConnectionType.CONVERGE_DIVERGE);
		// feedforward connection from layer 1 to layer 3
		k3[2].connect(k3[0], 0.5 / k3[0].getSize(), -1, InterLayerConnectionType.CONVERGE_DIVERGE); 
		
		// excitatory feedback connection from layer 2 to layer 1
		k3[0].connect(k3[1], 0.5 / k3[1].getSize(), -17, InterLayerConnectionType.AVERAGE);
		// excitatory-to-inhibitory feedback connection from layer 2 to layer 1
		k3[0].connectInhibitory(k3[1], 0.6 / k3[1].getSize(), -25, InterLayerConnectionType.AVERAGE);
		// There is no feedforward connection from layer 2 to layer 3 in the original Matlab model
		// and in many literature diagrams
		// k3[2].connect(k3[1], 1, -1);
		
		// inhibitory-to-inhibitory feedback connection from layer 3 to layer 1
		k3[0].connectInhibitory(new LowerOutputAdapter(k3[2]), -0.5 / k3[2].getSize(), 25, InterLayerConnectionType.AVERAGE);
		// excitatory-to-inhibitory feedback connection from layer 3 to layer 2
		k3[1].connectInhibitory(k3[2], 0.5, 25, InterLayerConnectionType.AVERAGE);
		
		pool = new ThreadPoolExecutor(4, 10, 10, TimeUnit.NANOSECONDS, new PriorityBlockingQueue<Runnable>());	
	}
	
	/**
	 * Initialize KIII-set with a perturbed input to move it from zero steady state and let it stabilize
	 */
	public double[][][] initialize() {
		double[] perturbed = new double[inputSize];
		
		for (int i = 0; i < inputSize; ++i) {
			perturbed[i] = Math.random() - 0.5;
		}
		perturbed = new double[]{1,0.33,-0.33,-1};
		this.step(perturbed, Config.active * 10);
		this.step(emptyArray, Config.rest * 10);
		
		double[][][] outputs = new double[3][][];	
		outputs[0] = k3[0].getActivation();
		outputs[1] = k3[1].getActivation();
		outputs[2] = k3[2].getActivation();
		return outputs;
	}
	
	public void setExternalStimulus(double[] stimulus) {
		k3[0].setExternalStimulus(stimulus);
	}
	
	public double[] getFullOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullOutput(int delay) {
		return k3[2].getLayerOutput(0);
	}
	
	public void solve() {
		k3[0].solve();
		k3[1].solve();
		k3[2].solve();
	}
	
	public void solveAsync() {
		pool.execute(k3[0]);
		pool.execute(k3[1]);
		pool.execute(k3[2]);
		
		while(pool.getActiveCount() > 0){}
	}

	public void step(double[] stimulus, int times) {
		setExternalStimulus(stimulus);
		for (int i = 0; i < times; ++i) {
			solve();
			Config.incTime();
		}
	}

	public void stepAsync(double[] stimulus, int times) {
		setExternalStimulus(stimulus);
		for (int i = 0; i < times; ++i) {
			solveAsync();
			Config.incTime();
		}
	}

	public void train(ArrayList<double[]> data) {
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), data.get(i).length);
			this.step(stimulus, Config.active);
			k3[2].train();
			this.step(emptyArray, Config.rest);
		}
	}
	
	public void trainAsync(ArrayList<double[]> data) {
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), data.get(i).length);
			this.stepAsync(stimulus, Config.active);
			k3[2].train();
			this.stepAsync(emptyArray, Config.rest);
		}
	}

	public double[][] run(ArrayList<double[]> data) {
		double[][] outputs = new double[data.size()][];
		
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), data.get(i).length);
			// Stimulate the network (equivalent to an sniff)
			this.step(stimulus, Config.active);
			// Calculate the output as the standard deviation of the activation history of each top KII node
			
			outputs[i] = k3[2].getActivationDeviation();
			// Put the network to rest, to prepare it for the next stimulus
			this.step(emptyArray, Config.rest);
		}
		
		return outputs;
	}
	
	public void runAsync(ArrayList<double[]> data) {
		double[][] outputs = new double[data.size()][];
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), data.get(i).length);			
			// Stimulate the network (equivalent to an sniff)
			this.stepAsync(stimulus, Config.active);
			// Calculate the output as the standard deviation of the activation history of each top KII node
			outputs[i] = k3[2].getActivationDeviation();			
			// Put the network to rest, to prepare it for the next stimulus
			this.stepAsync(emptyArray, Config.rest);
		}
	}
	
	
	/*
	 * Serialization Methods
	 */
	
	public void save(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}
	
	public static KIII load(String filename) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		KIII k3 = (KIII) ois.readObject();
		ois.close();
		return k3;
	}
	
	private void readObject(ObjectInputStream is) {
		try {
			is.defaultReadObject();
			pool = new ThreadPoolExecutor(4, 10, 10, TimeUnit.NANOSECONDS, new PriorityBlockingQueue<Runnable>());	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
