package main.ksets.kernel;

import java.io.Serializable;

/**
 * Can be used to create a connection who gets the output from the lower, inhibitory, K sets
 * @author Denis
 *
 */
public class LowerOutputAdapter implements HasOutput, Serializable {

	private static final long serialVersionUID = -1241949392405962193L;
	private KIILayer origin;
	
	/**
	 * Creates an adapter for the kset
	 * @param kset
	 */
	public LowerOutputAdapter(KIILayer kset) {
		this.origin = kset; 
	}
	
	
	@Override
	/**
	 * Get the lower output from the origin K set
	 * @return The lower output from the K set
	 */
	public double getOutput() {
		return origin.getInhibitoryOutput();
	}

	@Override
	/**
	 * Get the delayed lower output from the origin K set
	 * @param delay The delay
	 * @return The lower output from the K set
	 */
	public double getOutput(int delay) {
		return origin.getInhibitoryOutput(delay);
	}

}
