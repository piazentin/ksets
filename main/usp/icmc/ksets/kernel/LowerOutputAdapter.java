package main.usp.icmc.ksets.kernel;

public class LowerOutputAdapter implements HasOutput {

	private K2Layer origin;
	
	public LowerOutputAdapter(K2Layer object) {
		this.origin = object; 
	}
	
	@Override
	public double getOutput() {
		return origin.getLowerOutput();
	}

	@Override
	public double getOutput(int delay) {
		return origin.getLowerOutput(delay);
	}

}
