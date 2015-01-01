package com.piazentin.ml.jkset;

import java.io.Serializable;

public interface Layer extends HasOutput, Runnable, Serializable {
	int getSize();
	HasOutput getUnit(int index);
	double[] getLayerOutput();
	double[] getLayerOutput(int delay);
	void run();
	void setExternalStimulus(double[] stimulus);
	double[] getWeights();
	public double[][] getHistory() ;
}
