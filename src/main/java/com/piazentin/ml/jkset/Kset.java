package com.piazentin.ml.jkset;

public interface Kset extends HasOutput {
	Connection connect(HasOutput k, double w);
	Connection connect(HasOutput k, double w, int delay);
	void setExternalStimulus(double stimulus);
	void run();
	double[] getActivation();
	double[] getHistory();
}
