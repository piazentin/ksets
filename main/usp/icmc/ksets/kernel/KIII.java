package main.usp.icmc.ksets.kernel;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KIII {

	private K2Layer[] k3;
	
	 private ThreadPoolExecutor pool;
	
	public KIII(int size) {
		k3 = new K2Layer[3];
		
		
		// TODO other, more biologically plausible KIII architectures can be made
		// TODO create external noise generator
		k3[0] = new K2Layer(size, Configuration.defaultW1, Configuration.defaultWLat1);
		k3[1] = new K2Layer(size, Configuration.defaultW2, Configuration.defaultWLat2);
		k3[2] = new K2Layer(size, Configuration.defaultW3, Configuration.defaultWLat3);
		 // Arquitetura de LiH_2003
		
		k3[0].registerConnection(k3[1], 0.05, 1);
		k3[0].registerLowerConnection(k3[1], 0.25, 1);
		
		k3[0].registerLowerConnection(new LowerOutputAdapter(k3[2]), -0.05, 1);
		k3[1].registerConnection(k3[0], 0.15, 1);
		
		k3[1].registerLowerConnection(k3[2], 0.2, 1);
		k3[2].registerConnection(k3[0], 0.6, 1);
		
		pool = new ThreadPoolExecutor(4, 10, 10, TimeUnit.NANOSECONDS, new PriorityBlockingQueue<Runnable>());
		
		
	}
	
	public void setExternalStimulus(double[] stimulus) {
		k3[0].setExternalStimulus(stimulus);
	}
	
	public double[] getFullOutput() {
		return this.getFullOutput(0);
	}
	
	public double[] getFullOutput(int delay) {
		return k3[2].getFullOutput(0);
	}
	
	public double[] solve() {
		k3[0].solve();
		k3[1].solve();
		return k3[2].solve();
	}
	
	public double[] solve2() {
		/*k3[0].solve();
		k3[1].solve();
		return k3[2].solve();*/
		pool.execute(k3[0]);
		pool.execute(k3[1]);
		pool.execute(k3[2]);
		
		while(pool.getActiveCount() > 0){}

		return k3[2].getFullOutput();
	}
	
	public void train() {
		k3[2].train();
	}

}
