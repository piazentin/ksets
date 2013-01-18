package main.usp.icmc.ksets.kernel;

public interface HasOutput {
	
	/**
	 * Recovers the last activation of the network, without side effects
	 * @return The last activation from the KO
	 */
	double getOutput();
	
	/**
	 * Recovers the activation of the network at the time NOW - delay, without side effects
	 * @return The activation from the KO
	 */
	double getOutput(int delay);
}
