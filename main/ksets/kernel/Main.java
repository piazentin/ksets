package main.ksets.kernel;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	static String testFile = "irisn.txt";
	
	public static void main(String[] args) throws Exception {
		long t1 = System.currentTimeMillis();
		//simulateK3();
		System.out.println("time = " + (System.currentTimeMillis() - t1)/1000.0);
	}
	
	public static void simulateLayer() throws Exception {
		ArrayList<double[]> data = Utils.readTable(testFile);
		int dataSize = data.get(0).length - 1;
		KIILayer layer = new KIILayer(dataSize, Config.defaultW1, Config.defaultWLat1, KIILayer.WLat.USE_FIXED_WEIGHTS);
		layer.setExternalStimulus(new double[]{1,1,1,1,1});
		layer.run();
		layer.setExternalStimulus(new double[]{0,0,0,0,0});
		for (int i = 0; i < 1000; i++) {
			System.out.println(Arrays.toString(layer.getLayerOutput()));
			Config.incTime();
			layer.run();
			layer.train();
		}
	}
	
	public static double[][][] simulateK3() throws Exception {
		ArrayList<double[]> data = Utils.readTable(testFile);
		int dataSize = data.get(0).length - 1;
		KIII k3 = new KIII(dataSize);
		k3.initialize();
		double[][][] output = k3.getHistory();

		double[][][] outputRun = k3.runAndGetActivation(Utils.toMatrix(data));
		Utils.saveMatrix(output[0], "output0.txt");
		Utils.saveMatrix(output[1], "output1.txt");
		Utils.saveMatrix(output[2], "output2.txt");

		return outputRun;
	}

	public static void simulateKO() {
		KO ko = new KO();
		
		ko.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			ko.run();
			System.out.print(" " + ko.getOutput(1));
		}
		
		ko.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			ko.run();
			System.out.print(", " + ko.getOutput(1));
		}
	}
	
	public static void simulateKI() {
		KI ki = new KI(0.33, 0.33);
		
		ki.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			ki.run();
			System.out.print(" " + ki.getOutput(1));
			Config.incTime();
		}
		
		ki.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			ki.run();
			System.out.print(", " + ki.getOutput(1));
			Config.incTime();
		}
	}
	
	public static void simulateKII() {
		KII k = new KII(1.8, 1.0, -2.0, -0.8);
		//KII k = new KII(1.6, 1.6, -1.5, -2.0);
		//KII k = new KII(1.6, 1.9, -0.2, -1.0);
		//KII k = new KII(2, 1.5, -2.0, -1.0);
		//KII k = new KII(0.1, 1.8, -1.0, -1.8);
		
		k.setExternalStimulus(1);
		for (int i = 0; i < 100; ++i) {
			k.run();
			System.out.print(" " + k.getOutput(1));
			Config.incTime();
		}
		
		k.setExternalStimulus(0);
		for (int i = 0; i < 1000; ++i) {
			k.run();
			System.out.print(" " + k.getOutput(1));
			Config.incTime();
		}
	}
}
