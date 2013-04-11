package main.ksets.kernel;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KIII {

	private K2Layer[] k3;
	
	private ThreadPoolExecutor pool;
	
	public KIII(int size) {
		k3 = new K2Layer[3];
		
		k3[0] = new K2Layer(size, Config.defaultW1, Config.defaultWLat1);
		k3[1] = new K2Layer(size, Config.defaultW2, Config.defaultWLat2);
		k3[2] = new K2Layer(size, Config.defaultW3, Config.defaultWLat3);
		
		k3[0].connect(k3[1], 0.05, -1);
		k3[0].connectInhibitory(k3[1], 0.25, -1);
		
		k3[0].connectInhibitory(new LowerOutputAdapter(k3[2]), -0.05, -1);
		k3[1].connect(k3[0], 0.15, -1);
		
		k3[1].connectInhibitory(k3[2], 0.2, -1);
		k3[2].connect(k3[0], 0.6, -1);
		
		pool = new ThreadPoolExecutor(4, 10, 10, TimeUnit.NANOSECONDS, new PriorityBlockingQueue<Runnable>());	
	}
	
	public void setExternalStimulus(double[] stimulus) {
		k3[0].setExternalStimulus(stimulus);
	}
	
	public double[] getFullOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullOutput(int delay) {
		return k3[2].getLayerOutput(0);
	}
	
	public void solve() {
		k3[0].solve();
		k3[1].solve();
		k3[2].solve();
	}
	
	public void solveAsync() {
		pool.execute(k3[0]);
		pool.execute(k3[1]);
		pool.execute(k3[2]);
		
		while(pool.getActiveCount() > 0){}
	}
	
	public void train() {
		k3[2].train();
	}

	public void step(double[] stimulus, int times) {
		for (int i = 0; i < times; ++i) {
			solve();
			Config.incTime();
		}
	}

	public void stepAsync(double[] stimulus, int times) {
		for (int i = 0; i < times; ++i) {
			solveAsync();
			Config.incTime();
		}
	}
	
}
