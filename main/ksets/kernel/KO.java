package main.ksets.kernel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static main.ksets.kernel.Config.getTime;

public class KO implements Kset, Runnable, Serializable {

	private static final long serialVersionUID = -1183011352569488440L;
	private double[] activation = new double[Config.historySize];
	private double derivative;
	
	private List<Connection> connections = new LinkedList<>();
	private double externalStimulus = 0.0;
	private K0Solver koSolver = new K0Solver();
	
	static final double q = Config.q;
	
	public KO() {
		activation[getTime()] = 0;//Math.random() / 100;
		derivative = 0;//Math.random() / 100;
	}
	
	/**
	 * Register a connection within the KO, given the origin node and the connection weight
	 * @param origin origin node 
	 * @param weight connection weight
	 */
	public Connection connect(HasOutput origin, double weight) {
		return connect(origin, weight, 0);
	}
	
	public Connection connect(HasOutput origin, double weight, int delay) {
		Connection con = new Connection(origin, weight, delay);
		connections.add(con);
		return con;
	}

	/**
	 * Solve the ODE
	 * @return activation
	 */
	public void solve() {
		solve(calculateRHS());
	}
	
	public void run() {
		solve();
	}
	
	/**
	 * Solve the ODE
	 */
	public void solve(double input) {
		double[] result = koSolver.solve(activation[getTime()], derivative, input);
		activation[getTime(1)] = result[0];
		derivative = result[1];
	}

	public double getOutput() {
		return activation[getTime()];
	}
	
	/**
	 * @return activation from the KO at the time t + i
	 */
	public double getOutput(int i) {
		return activation[getTime(i)];
	}
	
	public void setExternalStimulus(double stimulus) {
		this.externalStimulus = stimulus;
	}
	
	/**
	 * @return Returns the calculated Right Hand Side of the ODE W * Q(X) + I
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
	 * @param x
	 * @return The resulting double of the sigmoid or -1 if x < x_0, where x_0 is a constant.
	 */
	public double sigmoid(double x) {
		return q * (1 - Math.exp(-(Math.exp(x)-1)/q));
	}
	
	/**
	 * 
	 */
	public double[] getActivation() {
		int from = getTime(-Config.stableActivation);
		int to = getTime();
		
		if (to > from) {
			return Arrays.copyOfRange(activation, from, to);
		} else {
			double[] pt1 = Arrays.copyOfRange(activation, from, activation.length);
			double[] pt2 = Arrays.copyOfRange(activation, 0, to);
			double[] activations = new double[pt1.length + pt2.length];
			
			for (int i = 0; i < pt1.length; i++) {
				activations[i] = pt1[i];
			}
			
			for (int i = 0; i < pt2.length; i++) {
				activations[i + pt1.length] = pt2[i];
			}
			
			return activations;
		}
	}
}
