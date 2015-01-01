package com.piazentin.ml.jkset;

import java.io.Serializable;

/**
 * Can be used to create a connection who gets the output from the lower, inhibitory, K sets
 * @author Denis Piazentin
 *
 */
public class LowerOutputAdapter implements Layer, Serializable {

	private static final long serialVersionUID = -1241949392405962193L;
	private KIILayer origin;
	
	/**
	 * Creates an adapter for the kset
	 * @param kset
	 */
	public LowerOutputAdapter(KIILayer kset) {
		this.origin = kset; 
	}
	
	
	/**
	 * Get the lower output from the origin K set
	 * @return The lower output from the K set
	 */
	public double getOutput() {
		return origin.getInhibitoryOutput();
	}


	/**
	 * Get the delayed lower output from the origin K set
	 * @param delay The delay
	 * @return The inhibitory output from the K set
	 */
	public double getOutput(int delay) {
		return origin.getInhibitoryOutput(delay);
	}

	
	public void run() {
		origin.run();
	}


	public int getSize() {
		return origin.getSize();
	}


	public HasOutput getUnit(int index) {
		return origin.getUnit(index);
	}

	/**
	 * get the layer INHIBITORY output
	 */
	public double[] getLayerOutput() {
		return origin.getLayerInhibitoryOutput();
	}
	

	/**
	 * get the layer INHIBITORY output with a delay
	 */
	public double[] getLayerOutput(int delay) {
		return origin.getLayerInhibitoryOutput(delay);
	}
	
	
	/**
	 * set external stimulus in the EXCITATORY layer
	 */
	public void setExternalStimulus(double[] stimulus) {
		origin.setExternalStimulus(stimulus);
	}


	public double[] getWeights() {
		return origin.getInhibitoryWeights();
	}
	
	public double[][] getHistory() {
		return origin.getInhibitoryHistory();
	}
}
