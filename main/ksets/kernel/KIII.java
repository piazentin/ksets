package main.ksets.kernel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class KIII implements Serializable {

	private static final long serialVersionUID = -1945033929994699028L;
	public KIILayer[] k3;
	private int inputSize;
	private double[] emptyArray; // Empty array used during the resting period
	private int outputLayer;
	private boolean becameUnstable = false;

	/**
	 * Create a new KIII with the default configurations from the old matlab implementation.
	 * @param size
	 */
	public KIII(int size) {
		this.inputSize = size;
		this.emptyArray = new double[inputSize];
		
		k3 = new KIILayer[3];
		
		k3[0] = new KIILayer(size, Config.defaultW1, Config.defaultWLat1, KIILayer.WLat.USE_FIXED_WEIGHTS);
		k3[1] = new KIILayer(size, Config.defaultW2, Config.defaultWLat2, KIILayer.WLat.USE_FIXED_WEIGHTS);
		k3[2] = new KIILayer(size, Config.defaultW3, Config.defaultWLat3, KIILayer.WLat.USE_FIXED_WEIGHTS);
		
		k3[0].setLearningRate(Config.alpha);
		k3[1].setLearningRate(Config.alpha);
		k3[2].setLearningRate(Config.alpha);
		
		// Feedforward connection from layer 1 to layer 2
		k3[1].connect(k3[0], 0.15, -1, InterlayerMethod.CONVERGE_DIVERGE);
		// Feedforward connection from layer 1 to layer 3
		k3[2].connect(k3[0], 0.6, -1, InterlayerMethod.CONVERGE_DIVERGE); 

		// Excitatory feedback connection from layer 2 to layer 1
		k3[0].connect(k3[1], 0.05, -17, InterlayerMethod.AVERAGE);
		// Excitatory-to-inhibitory feedback connection from layer 2 to layer 1
		k3[0].connectInhibitory(k3[1], 0.25, -25, InterlayerMethod.AVERAGE);

		// There is no connection from layer 2 to layer 3
		
		// Inhibitory-to-inhibitory feedback connection from layer 3 to layer 1
		k3[0].connectInhibitory(new LowerOutputAdapter(k3[2]), -0.05, -25, InterlayerMethod.AVERAGE);
		// Excitatory-to-inhibitory feedback connection from layer 3 to layer 2
		k3[1].connectInhibitory(k3[2], 0.2, -25, InterlayerMethod.AVERAGE);
		
		this.setOutputLayer(0);
	}
	
	/**
	 * Initialize KIII-set with a perturbed input to move it from zero steady state and let it stabilize
	 */
	public void initialize() {
		double[] perturbed = new double[inputSize];
		
		for (int i = 0; i < inputSize; ++i) {
			perturbed[i] = 1; //Math.random() - 0.5;
		}
		
		this.step(perturbed, 1);
		this.step(emptyArray, Config.rest*4);
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
	
	public KIILayer getLayer(int i) {
		return k3[i];
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
		return k3[outputLayer].getLayerOutput(this.outputLayer);
	}
	
	public double[] getOutput() {
		return k3[this.outputLayer].getActivationStd();
	}
	
	public double[] getWeights(int layer) {
		return k3[layer].getWeights();
	}

	public void step(double[] stimulus, int times) {
		setExternalStimulus(stimulus);
		for (int i = 0; i < times; ++i) {
			k3[0].run();
			k3[1].run();
			k3[2].run();
			Config.incTime();
		}
	}

	public void train(double[][] data) {
		for (int i = 0; i < data.length; ++i) {
			double[] stimulus = Arrays.copyOf(data[i], data[i].length);
			this.step(stimulus, Config.active);
			k3[outputLayer].train();
			this.step(emptyArray, Config.rest);
			if (k3[2].getActivationMean()[0] > 2) {
				System.err.println("Instability detected in KIII. Last weight changes undone.");
				k3[outputLayer].rollbackWeights();
				this.becameUnstable = true;
				return;
			}
		}
	}
	
	public boolean hasBecameUnstable() {
		return this.becameUnstable;
	}

	public double[][] run(double[][] data) {
		double[][] outputs = new double[data.length][];
		
		for (int i = 0; i < data.length; ++i) {
			double[] stimulus = Arrays.copyOf(data[i], data[i].length);
			// Stimulate the network (equivalent to a sniff)
			this.step(stimulus, Config.active);
			// Calculate the output as the standard deviation of the activation history of each top KII node
			
			outputs[i] = this.getOutput();
			// Put the network to rest, to prepare it for the next stimulus
			this.step(emptyArray, Config.rest);
		}
		
		return outputs;
	}
	
	public double[][][] runAndGetRawActivation(double[][] data) {
		double[][][] outputs = new double[data.length][][];
		
		for (int i = 0; i < data.length; ++i) {
			double[] stimulus = Arrays.copyOf(data[i], data[i].length);
			// Stimulate the network (equivalent to an sniff)
			this.step(stimulus, Config.active);
			// Calculate the output as the standard deviation of the activation history of each top KII node
			
			outputs[i] = k3[outputLayer].getActivation();
			// Put the network to rest, to prepare it for the next stimulus
			this.step(emptyArray, Config.rest);
		}
		
		return outputs;
	}
	
	/*
	 * Serialization Methods
	 */	
	public KIII copy() {
		KIII k3 = null;
		
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.close();
			
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		    k3 = (KIII) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return k3;
	}
	
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
