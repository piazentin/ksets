package com.piazentin.ml.jkset;

import com.piazentin.ml.jkset.HasOutput;

public class HasOutputMock implements HasOutput {
	public double output;
	
	public HasOutputMock(int output) {
		this.output = output;
	}

	public double getOutput() {
		return output;
	}

	public double getOutput(int delay) {
		return output;
	}

}
