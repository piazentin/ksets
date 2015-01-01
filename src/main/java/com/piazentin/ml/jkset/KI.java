package com.piazentin.ml.jkset;

public class KI implements Kset {
	
	/**
	 * A basic KI is made of two connected KO
	 */
	public KO[] k = new KO[2];

	/**
	 * Create a KI with the defined connection weights
	 * @param w01 connection weight from the first KO (k[0]) to the second (k[1]) 
	 * @param w10 connection weight from the second KO (k[1]) to the first (k[0])
	 */
	public KI(double w01, double w10) {
		k[0] = new KO();
		k[1] = new KO();
		
		k[0].connect(k[1], w01);
		k[1].connect(k[0], w10);
	}
	
	/**
	 * Register a connection with the primary KO set of the KI set, given the origin node and the connection weight
	 * @param origin The origin node 
	 * @param weight The connection weight
	 */
	public Connection connect(HasOutput origin, double weight) {
		return k[0].connect(origin, weight);
	}
	
	/**
	 * Register a connection with the primary KO set of the KI set, given the origin node, the connection weight, and the connection delay
	 * @param origin origin node 
	 * @param weight connection weight
	 * @param delay connection delay
	 */
	public Connection connect(HasOutput origin, double weight, int delay) {
		return k[0].connect(origin, weight, delay);
	}
	
	/**
	 * Return the output at the current network time t.
	 * Output is from the first KO in the set (k[0])
	 */
	public double getOutput() {
		return k[0].getOutput();
	}
	
	public double getOutput(int i) {
		return k[0].getOutput(i);
	}

	public double[] getActivation() {
		return k[0].getActivation();
	}
	
	/**
	 * Solve the ODE for all underlying KO
	 */
	public void run() {
		for (int i = 0; i < k.length; i++) {
			k[i].run();
		}
	}
	
	public double[] getHistory() {
		return k[0].getHistory();
	}
	
	public double[] getHistory(int kIndex) {
		return k[kIndex].getHistory();
	}
	
	/**
	 * Set the external stimulus received by the network. The stimulus is set on the top KO unit (k[0])
	 * @param stimulus the stimulus to be set
	 */
	public void setExternalStimulus(double stimulus) {
		k[0].setExternalStimulus(stimulus);
	}
}
