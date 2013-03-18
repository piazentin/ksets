package main.usp.icmc.ksets.kernel;

import java.util.LinkedList;
import java.util.List;

import static main.usp.icmc.ksets.kernel.Configuration.getTime;
import static main.usp.icmc.ksets.kernel.Configuration.getTimePlus;
import static main.usp.icmc.ksets.kernel.Configuration.getTimeMinus;

/**
 * Provides the implementation of a KO set
 * @author Denis Piazentin
 *
 */
public class KO implements Kset, Runnable {
	private double[] activation = new double[Configuration.historySize];
	private double derivative;
	
	private List<Connection> connections = new LinkedList<>();
	private double externalStimulus = 0.0;
	private KOSolver koSolver = new KOSolver();
	
	static final double q = Configuration.q;
	
	/**
	 * Register a connection within the KO, given the origin node and the connection weight
	 * @param origin The origin node 
	 * @param weight The connection weight
	 */
	public void registerConnection(HasOutput origin, double weight) {
		connections.add(new Connection(origin, weight));
	}
	
	/**
	 * Register a connection within the KO, given proper connection to be registered
	 * @param connection The connection to be registered
	 */
	public void registerConnection(Connection connection) {
		connections.add(connection);
	}
	
	/**
	 * Solve the ODE using the current values from the connections and the external stimulus.
	 * Recalculate the current activation, accessible from getOutput(), and its derivative.
	 * @return The resulting activation
	 */
	public double solve() {
		return solve(calculateRHS());
	}
	
	/**
	 * Same as solve(), but returns void
	 */
	public void run() {
		solve();
	}
	
	/**
	 * Solve the ODE using the current values from the connections and a given external input for the right hand side
	 * of the equation.
	 * Recalculate the current activation, accessible from getOutput(), and its derivative.
	 * @return The resulting activation
	 */
	public double solve(double input) {
		double[] result = koSolver.solve(activation[getTime()], derivative, input);
		activation[getTimePlus(1)] = result[0];
		derivative = result[1];
		return activation[getTimePlus(1)];
	}
	
	/**
	 * Recovers the last activation of the network, without side effects
	 * @return The last activation from the KO
	 */
	public double getOutput() {
		return activation[getTime()];
	}
	
	/**
	 * Recovers the activation of the network at the time NOW - delay, without side effects
	 * @return The activation from the KO
	 */
	public double getOutput(int delay) {
		return activation[getTimeMinus(1)];
	}
	
	/**
	 * Recovers the newest activation of the network, without side effects.
	 * Must be run after solving for the next activation and before advancing time
	 * @return The last activation from the KO
	 */
	public double getNewOutput() {
		return activation[getTimePlus(1)];
	}
	
	/**
	 * Allows setting the value of the external stimulus for solving the ODE
	 * The stimulus must have been previously calculated and contain any external 
	 * sources of stimulus like the network input and output from other layers
	 * @param stimulus The stimulus to be set
	 */
	public void setExternalStimulus(double stimulus) {
		this.externalStimulus = stimulus;
	}
	
	/**
	 * Calculate the Right Hand Side of the ODE.
	 * Calculate W * Q(X) + I
	 * @return Returns the calculated input, the right hand side of the ODE
	 */
	public double calculateRHS() {
		double input = 0.0;
		for (Connection c : connections) {
			input += c.getWeight() * sigmoid(c.getOutput());
		}
		input += externalStimulus;
		
		return input;
	}
	
	/**
	 * The asymmetric sigmoid Q.
	 * Q models the transformation between pulses and waves on neuron activations
	 * @param x
	 * @return The resulting double of the sigmoid or -1 if x < x0, where x0 is a constant.
	 */
	public double sigmoid(double x) {
		return q * (1 - Math.exp(-(Math.exp(x)-1)/q));
	}
}
