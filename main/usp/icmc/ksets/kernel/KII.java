package main.usp.icmc.ksets.kernel;

// TODO insert comments
public class KII implements Kset {
	
	private KO[] k = new KO[4];
	
	public KII() {
		this(Math.random(), Math.random(),Math.random(),Math.random());
	}
	
	public KII(double wee, double wei, double wie, double wii) {
		for (int i = 0; i < k.length; ++i) {
			k[i] = new KO();
		}
		 
		k[0].registerConnection(k[1], wee);
		k[1].registerConnection(k[0], wee);
		
		k[0].registerConnection(k[2], wie);
		k[2].registerConnection(k[0], wei);
		
		k[0].registerConnection(k[3], wie);
		k[3].registerConnection(k[0], wei);
		
		k[1].registerConnection(k[3], wie);
		k[3].registerConnection(k[1], wei);
		
		k[2].registerConnection(k[3], wii);
		k[3].registerConnection(k[2], wii);
	}
	
	public double getOutput() {
		return k[0].getOutput();
	}
	
	public double getOutput(int delay) {
		return k[0].getOutput(delay);
	}
	
	public double getLowerOutput() {
		return k[3].getOutput();
	}
	
	public double getLowerOutput(int delay) {
		return k[3].getOutput(delay);
	}
	
	public void setExternalStimulus(double stimulus) {
		k[0].setExternalStimulus(stimulus);
	}
	
	public void setInhibitoryStimulus(double stimulus) {
		k[3].setExternalStimulus(stimulus);
	}
	
	public void registerConnection(Connection connection) {
		k[0].registerConnection(connection);
	}
	
	public void registerConnection(HasOutput origin, double weight) {
		k[0].registerConnection(new Connection(origin, weight));
	}
	
	public void registerConnection(HasOutput origin, double weight, int delay) {
		k[0].registerConnection(new Connection(origin, weight, delay));
	}
	
	public void registerLowerConnection(Connection connection) {
		k[3].registerConnection(connection);
	}
	
	public void registerLowerConnection(HasOutput origin, double weight) {
		k[3].registerConnection(new Connection(origin, weight));
	}
	
	public void registerLowerConnection(HasOutput origin, double weight, int delay) {
		k[3].registerConnection(new Connection(origin, weight, delay));
	}
	
	public double solve() {
		for (int i = 0; i < k.length; i++) {
			k[i].solve();
		}
		
		return k[0].getNewOutput();
	}
}
