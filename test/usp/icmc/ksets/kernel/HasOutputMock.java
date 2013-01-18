package test.usp.icmc.ksets.kernel;

import main.usp.icmc.ksets.kernel.HasOutput;

public class HasOutputMock implements HasOutput {
	public double output;
	
	public HasOutputMock(int output) {
		this.output = output;
	}

	@Override
	public double getOutput() {
		return output;
	}

	@Override
	public double getOutput(int delay) {
		// TODO Implement delay
		return output;
	}

}
