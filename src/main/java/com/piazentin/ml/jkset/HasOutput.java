package com.piazentin.ml.jkset;

public interface HasOutput {
	
	/**
	 * Recovers the last activation of the network, without side effects
	 */
	double getOutput();
	
	/**
	 * Recovers the activation of the network at the time NOW - delay, without side effects
	 */
	double getOutput(int delay);
}
