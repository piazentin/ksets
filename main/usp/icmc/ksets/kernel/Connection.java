package main.usp.icmc.ksets.kernel;

public class Connection {
	private HasOutput origin;
	private double weight;
	private int delay = 0;
		
	public Connection(HasOutput origin, double weight) {
		this.origin = origin;
		this.weight = weight;
	}
	
	public Connection(HasOutput origin, double weight, int delay) {
		this.origin = origin;
		this.weight = weight;
		this.delay  = delay;
	}
	
	public HasOutput getOrigin() {
		return origin;
	}
	
	public void setOrigin(HasOutput origin) {
		this.origin = origin;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	public double getOutput() {
		return this.origin.getOutput(delay);
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
}
