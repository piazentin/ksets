package main.usp.icmc.ksets.kernel;

/**
 * Can be used to create a connection who gets the output from the lower, inhibitory, K sets
 * @author Denis
 *
 */
public class LowerOutputAdapter implements HasOutput {

	private K2Layer origin;
	
	/**
	 * Creates an adapter for the kset
	 * @param kset
	 */
	public LowerOutputAdapter(K2Layer kset) {
		this.origin = kset; 
	}
	
	
	@Override
	/**
	 * Get the lower output from the origin K set
	 * @return The lower output from the K set
	 */
	public double getOutput() {
		return origin.getLowerOutput();
	}

	@Override
	/**
	 * Get the delayed lower output from the origin K set
	 * @param delay The delay
	 * @return The lower output from the K set
	 */
	public double getOutput(int delay) {
		return origin.getLowerOutput(delay);
	}

}
