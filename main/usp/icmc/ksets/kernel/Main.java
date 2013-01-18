package main.usp.icmc.ksets.kernel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		//simulateKII();
		long t1 = System.currentTimeMillis();
		simulateK3();
		System.out.println("time = " + (System.currentTimeMillis() - t1)/1000.0);
	}

	private static void simulateK3() throws FileNotFoundException {
		ArrayList<double[]> data = readTable("C:\\iris.txt");
		int dataSize = data.get(0).length - 1;
		KIII k3 = new KIII(dataSize);
		double[] emptyArray = new double[dataSize];
		double[] perturbed = new double[dataSize];
		System.out.println("data size = " + dataSize + "    |    examples = " + data.size());
		for (int i = 0; i < dataSize; ++i) {
			perturbed[i] = Math.random() - 0.5;
		}
		
		int active = 50;
		int inactive = 300;
		
		
		k3Step(k3, perturbed, 1);
		k3Step(k3, emptyArray, 499);
		
		for (int i = 0; i < data.size(); ++i) {
			double[] stimulus = Arrays.copyOf(data.get(i), dataSize);
			k3Step(k3, stimulus, active);
			k3Step(k3, emptyArray, inactive);
		}
		
	}

	private static void k3Step(KIII k3, double[] stimulus, int times) {
		k3.setExternalStimulus(stimulus);
		for (int i = 0; i < times; ++i) {
			k3.solve();
			Configuration.incTime();
		}
	}
	
	private static ArrayList<double[]> readTable(String filename) throws FileNotFoundException {
		File f = new File(filename);
		ArrayList<double[]> data = new ArrayList<>();
		
		Scanner fr = new Scanner(f);
		while(fr.hasNextLine()) {
			String line = fr.nextLine();
			Scanner lr = new Scanner(line);
			ArrayList<Double> dl = new ArrayList<>();

			while (lr.hasNextDouble())
				dl.add(lr.nextDouble());
			
			data.add(toArray(dl));
			lr.close();
		}
		fr.close();
		
		return data;
	}

	private static double[] toArray(ArrayList<Double> dl) {
		double[] dd = new double[dl.size()];
		
		for (int i = 0; i < dl.size(); ++i) {
			dd[i] = dl.get(i);
		}
		
		return dd;
	}

	public static void simulateKO() {
		KO ko = new KO();
		
		ko.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			System.out.print(" " + ko.solve());
		}
		
		ko.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			System.out.print(" " + ko.solve());
		}
	}
	
	public static void simulateKI() {
		KI ki = new KI(0.33, 0.33);
		
		ki.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			System.out.print(" " + ki.solve());
			Configuration.incTime();
		}
		
		ki.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			System.out.print(" " + ki.solve());
			Configuration.incTime();
		}
	}
	
	public static void simulateKII() {
		//KII k = new KII(1.8, 1.0, -2.0, -0.8);
		//KII k = new KII(1.6, 1.6, -1.5, -2.0);
		//KII k = new KII(1.6, 1.9, -0.2, -1.0);
		//KII k = new KII(2, 1.5, -2.0, -1.0);
		KII k = new KII(0.1, 1.8, -1.0, -1.8);
		
		k.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			System.out.print(" " + k.solve());
			Configuration.incTime();
		}
		
		k.setExternalStimulus(0);
		for (int i = 0; i < 1200; ++i) {
			System.out.print(" " + k.solve());
			Configuration.incTime();
		}
	}
}
