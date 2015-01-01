package com.piazentin.ml.jkset;

import java.io.Serializable;

/**
 * Implements the connection between two K sets.
 * @author Denis Piazentin
 *
 */
public class Connection implements Serializable {

	private static final long serialVersionUID = 5366548013955684357L;
	private HasOutput origin;
	private double weight;
	private int delay = 0;
	
	/**
	 * Create a connection with zero delay
	 * @param origin The origin of the connection
	 * @param weight Connection weight
	 */
	public Connection(HasOutput origin, double weight) {
		this.origin = origin;
		this.weight = weight;
	}
	
	/**
	 * Create a delayed connection
	 * @param origin The origin of the connection
	 * @param weight Connection weight
	 * @param delay Connection delay
	 */
	public Connection(HasOutput origin, double weight, int delay) {
		this.origin = origin;
		this.weight = weight;
		this.delay  = delay;
	}
	
	/**
	 * Gets the origin of the connection
	 * @return The origin of the connection
	 */
	public HasOutput getOrigin() {
		return origin;
	}
	
	/**
	 * Sets a new origin for the connection
	 * @param origin Origin to be set
	 */
	public void setOrigin(HasOutput origin) {
		this.origin = origin;
	}
	
	/**
	 * Gets the weight of the connection
	 * @return connection weight
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * Sets a new weight for the connection
	 * @param weight Weight to be set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	/**
	 * Gets the output from the connection.
	 * @return The output of the origin of the connection 
	 */
	public double getOutput() {
		return this.origin.getOutput(delay);
	}

	/**
	 * Gets the delay of the connection
	 * @return connection delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Sets the delay of the connection
	 * @return connection delay
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
