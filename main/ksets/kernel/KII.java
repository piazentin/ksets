package main.ksets.kernel;

import java.io.Serializable;

public class KII implements Kset, Runnable, Serializable {
	
	private static final long serialVersionUID = -1646746114752724L;
	public KO[] k = new KO[4];
	
	/**
	 * Create a KII set with random connection weights
	 */
	public KII() {
		this(Math.random(), Math.random(),Math.random(),Math.random());
	}
	
	/**
	 * Create a KII set with the defined connection weights
	 * @param wee KOe to KOe connection weight
	 * @param wei KOe to KOi connection weight
	 * @param wie KOi to KOe connection weight
	 * @param wii KOi to KOi connection weight
	 */
	public KII(double wee, double wei, double wie, double wii) {
		for (int i = 0; i < k.length; ++i) {
			k[i] = new KO();
		}
		 
		k[0].connect(k[1], wee);
		k[1].connect(k[0], wee);
		
		k[0].connect(k[2], wie);
		k[2].connect(k[0], wei);
		
		k[0].connect(k[3], wie);
		k[3].connect(k[0], wei);
		
		k[1].connect(k[3], wie);
		k[3].connect(k[1], wei);
		
		k[2].connect(k[3], wii);
		k[3].connect(k[2], wii);
	}
	
	/**
	 * Return the output at the current network time t.
	 * Output is from the first KO in the set (k[0])
	 */
	public double getOutput() {
		return k[0].getOutput();
	}
	
	/**
	 * Return the output of the network in the time t + i.
	 * Output is from the first KO in the set (k[0])
	 */
	public double getOutput(int i) {
		return k[0].getOutput(i);
	}
	
	/**
	 * Return the output at the current network time t.
	 * Output is from the lower, inhibitory, KO in the set (k[3])
	 */
	public double getInhibitoryOutput() {
		return k[3].getOutput();
	}
	
	/**
	 * Return the output of the network in the time t + i.
	 * Output is from the lower, inhibitory, KO in the set (k[3])
	 */
	public double getInhibitoryOutput(int i) {
		return k[3].getOutput(i);
	}
	
	/**
	 * Set the external (excitatory) stimulus received by the network. 
	 * The stimulus is set on the top KO unit (k[0])
	 * @param stimulus the stimulus to be set
	 */
	public void setExternalStimulus(double stimulus) {
		k[0].setExternalStimulus(stimulus);
	}
	
	/**
	 * Set the external (inhibitory) stimulus received by the network. 
	 * The stimulus is set on the lower KO unit (k[3])
	 * @param stimulus the stimulus to be set
	 */
	public void setInhibitoryStimulus(double stimulus) {
		k[3].setExternalStimulus(stimulus);
	}
	
	/**
	 * Register a connection with the primary KO set of the KIi set, given the origin node and the connection weight
	 * @param origin The origin node 
	 * @param weight The connection weight
	 */
	public Connection connect(HasOutput origin, double weight) {
		return k[0].connect(origin, weight);
	}
	
	/**
	 * Register a connection with the primary KO set of the KIi set, given the origin node, the connection weight, and the connection delay
	 * @param origin origin node 
	 * @param weight connection weight
	 * @param delay connection delay
	 */
	public Connection connect(HasOutput origin, double weight, int delay) {
		return k[0].connect(origin, weight, delay);
	}
	
	/**
	 * @param origin origin node 
	 * @param weight connection weight
	 */
	public Connection connectInhibitory(HasOutput origin, double weight) {
		return k[3].connect(origin, weight);
	}
	
	/**
	 * Register a connection with the lower KO set (k[3]) of the KII set, given the origin node, the connection weight, and the connection delay
	 * @param origin origin node 
	 * @param weight connection weight
	 * @param delay connection delay
	 */
	public Connection connectInhibitory(HasOutput origin, double weight, int delay) {
		return k[3].connect(origin, weight, delay);
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
	 * Gets the history of activation from KII-set, from its top KO.
	 * @return a double array with the activation history
	 */
	public double[] getActivation() {
		return k[0].getActivation();
	}
	
	public double[] getActivation(int index) {
		return k[index].getActivation();
	}
	
	public HasOutput getExcitatoryUnit() {
		return this.k[0];
	}
	
	public HasOutput getInhibitoryUnit() {
		return this.k[3];
	}
}
