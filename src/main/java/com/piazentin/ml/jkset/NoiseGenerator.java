package com.piazentin.ml.jkset;

import java.io.Serializable;
import java.util.Random;

public class NoiseGenerator implements Serializable {

	private static final long serialVersionUID = -629683690302798364L;
	private double mean;
	private double standardDeviation;
	private Random random = new Random();
	
	public NoiseGenerator(double mean, double standardDeviation) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}
	
	public double get() {
		return (random.nextGaussian() * standardDeviation) + mean;
	}
	
}
