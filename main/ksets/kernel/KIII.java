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
	public K2Layer[] k3;
	private int inputSize;
	private double[] emptyArray; // Empty array used during the resting period
	private int outputLayer = 2;
	private OutputMethod outputMethod;
	
	private transient ThreadPoolExecutor pool;
	
	public static enum InterLayerConnectionType {
		AVERAGE,
		CONVERGE_DIVERGE
	}
	
	public static enum OutputMethod {
		STANDARD_DEVIATION,
		SHORT_TERM_POWER
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
		k3[1] = new K2Layer(1, Config.defaultW2, Config.defaultWLat2, K2Layer.WLat.USE_FIXED_WEIGHTS);
		k3[2] = new K2Layer(1, Config.defaultW3, Config.defaultWLat3, K2Layer.WLat.USE_FIXED_WEIGHTS);
		
		// Configure noise sources
		k3[0].injectNoise(0.0, 0.07);
		k3[1].injectNoise(0.2, 0.7);		
		
		// feedforward connection from layer 1 to layer 2
		k3[1].connect(k3[0], 0.3, -1, InterLayerConnectionType.CONVERGE_DIVERGE);
		// feedforward connection from layer 1 to layer 3
		k3[2].connect(k3[0], 0.5, -1, InterLayerConnectionType.CONVERGE_DIVERGE); 

		// excitatory feedback connection from layer 2 to layer 1
		k3[0].connect(k3[1], 0.5, -17, InterLayerConnectionType.AVERAGE);
		// excitatory-to-inhibitory feedback connection from layer 2 to layer 1
		k3[0].connectInhibitory(k3[1], 0.6, -25, InterLayerConnectionType.AVERAGE);
		// There is no feedforward connection from layer 2 to layer 3 in the original Matlab model
		// and in many literature diagrams
		// k3[2].connect(k3[1], 0, 0);
		
		// inhibitory-to-inhibitory feedback connection from layer 3 to layer 1
		k3[0].connectInhibitory(new LowerOutputAdapter(k3[2]), -0.5, -25, InterLayerConnectionType.AVERAGE);
		// excitatory-to-inhibitory feedback connection from layer 3 to layer 2
		k3[1].connectInhibitory(k3[2], 0.5, -25, InterLayerConnectionType.AVERAGE);
		
		this.outputMethod = OutputMethod.STANDARD_DEVIATION;
		this.setOutputLayer(0);
		
		pool = new ThreadPoolExecutor(4, 10, 10, TimeUnit.NANOSECONDS, new PriorityBlockingQueue<Runnable>());	
	}
	
	/**
	 * Initialize KIII-set with a perturbed input to move it from zero steady state and let it stabilize
	 */
	public void initialize() {
		double[] perturbed = new double[inputSize];
		
		for (int i = 0; i < inputSize; ++i) {
			perturbed[i] = Math.random() - 0.5;
		}
		
		this.step(perturbed, 1);
		this.step(emptyArray, Config.rest);
	}
	
	public double[][][] getHistory() {
		double[][][] history = new double[k3.length][][];
		
		for (int i = 0; i < k3.length; i++) {
			history[i] = k3[i].getHistory();
		}
		
		return history;
	}
	
	public int getOutputLayer() {
		return this.outputLayer;
	}
	
	public void setOutputLayer(int outputLayer) {
		if (outputLayer < 0 || outputLayer >= k3.length) {
			throw new RuntimeException("outputLayer must be a number between 0 and number of KIII layers - 1");
		}
		
		this.outputLayer = outputLayer;
	}
	
	public void setExternalStimulus(double[] stimulus) {
		k3[0].setExternalStimulus(stimulus);
	}
	
	public double[] getFullOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullOutput(int delay) {
		return k3[2].getLayerOutput(this.outputLayer);
	}
	
	public double[] getOutput() {
		switch (outputMethod) {
		case SHORT_TERM_POWER:
			return k3[this.outputLayer].getActivationPower();
		case STANDARD_DEVIATION:
		default:
			return k3[this.outputLayer].getActivationDeviation();
		}
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
			
			outputs[i] = this.getOutput();
			// Put the network to rest, to prepare it for the next stimulus
			this.step(emptyArray, Config.rest);
		}
		
		return outputs;
	}
	
	public double[][][] runAndGetActivation(ArrayList<double[]> data) {
		double[][][] outputs = new double[data.size()][][];
		
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), data.get(i).length);
			// Stimulate the network (equivalent to an sniff)
			this.step(stimulus, Config.active);
			// Calculate the output as the standard deviation of the activation history of each top KII node
			
			outputs[i] = k3[outputLayer].getActivation();
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
			outputs[i] = this.getOutput();			
			// Put the network to rest, and prepare for the next stimulus
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
