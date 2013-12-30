package main.ksets.kernel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
	
	static String testFile = "iris0.txt";
	
	public static void main(String[] args) throws Exception {
		//simulateKII();
		//k3sim();
		//layerSim();
		long t1 = System.currentTimeMillis();
		simulateK3();
		//simulateKII();
		System.out.println("time = " + (System.currentTimeMillis() - t1)/1000.0);
	}
	
	public static void simulateLayer() throws FileNotFoundException {
		ArrayList<double[]> data = Utils.readTable(testFile);
		int dataSize = data.get(0).length - 1;
		K2Layer layer = new K2Layer(dataSize, Config.defaultW1, Config.defaultWLat1);
		layer.setExternalStimulus(new double[]{1,1,1,1,1});
		layer.solve();
		layer.setExternalStimulus(new double[]{0,0,0,0,0});
		for (int i = 0; i < 1000; i++) {
			System.out.println(Arrays.toString(layer.getLayerOutput()));
			Config.incTime();
			layer.solve();
			layer.train();
		}
	}
	
	private static void simulateK3() throws IOException, Exception {
		ArrayList<double[]> data = Utils.readTable(testFile);
		int dataSize = data.get(0).length - 1;
		System.out.println(dataSize);
		KIII k3 = new KIII(dataSize);
		double[][] output = k3.initialize();
		//k3.train(data);
		
		//double[][] output = k3.run(data);
		Utils.saveMatrix(output, "output.txt");
		
		k3.save("k3.jk3");
		
		//KIII k3async = new KIII(dataSize);
		//k3async.initialize();
		//k3async.trainAsync(data);
		//k3async.runAsync(data);
	}

	public static void simulateKO() {
		KO ko = new KO();
		
		ko.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			ko.solve();
			System.out.print(" " + ko.getOutput(1));
		}
		
		ko.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			ko.solve();
			System.out.print(", " + ko.getOutput(1));
		}
	}
	
	public static void simulateKI() {
		KI ki = new KI(0.33, 0.33);
		
		ki.setExternalStimulus(1);
		for (int i = 0; i < 1; ++i) {
			ki.solve();
			System.out.print(" " + ki.getOutput(1));
			Config.incTime();
		}
		
		ki.setExternalStimulus(0);
		for (int i = 0; i < 100; ++i) {
			ki.solve();
			System.out.print(", " + ki.getOutput(1));
			Config.incTime();
		}
	}
	
	public static void simulateKII() {
		//KII k = new KII(1.8, 1.0, -2.0, -0.8);
		//KII k = new KII(1.6, 1.6, -1.5, -2.0);
		//KII k = new KII(1.6, 1.9, -0.2, -1.0);
		//KII k = new KII(2, 1.5, -2.0, -1.0);
		KII k = new KII(0.1, 1.8, -1.0, -1.8);
		
		k.setExternalStimulus(1);
		for (int i = 0; i < 1000; ++i) {
			k.solve();
			System.out.print(" " + k.getOutput(1));
			Config.incTime();
		}
		
		k.setExternalStimulus(0);
		for (int i = 0; i < 1000; ++i) {
			k.solve();
			System.out.print(" " + k.getOutput(1));
			Config.incTime();
		}
	}
}
