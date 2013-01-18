package main.usp.icmc.ksets.kernel;

//TODO insert commentaries
public class KI implements Kset {
	
	private KO[] k = new KO[2];

	public KI() {
		this(Math.random(), Math.random());
	}
	
	public KI(double w01, double w10) {
		k[0] = new KO();
		k[1] = new KO();
		
		k[0].registerConnection(k[1], w01);
		k[1].registerConnection(k[0], w10);
	}
	
	/**
	 * Register a connection with the primary KO set of the KI set, given the origin node and the connection weight
	 * @param origin The origin node 
	 * @param weight The connection weight
	 */
	public void registerConnection(HasOutput origin, double weight) {
		k[0].registerConnection(new Connection(origin, weight));
	}
	
	/**
	 * Register a connection with the primary KO set of the KI set, given proper connection to be registered
	 * @param connection The connection to be registered
	 */
	public void registerConnection(Connection connection) {
		k[0].registerConnection(connection);
	}
	
	@Override
	public double getOutput() {
		return k[0].getOutput();
	}
	
	public double getOutput(int delay) {
		return k[0].getOutput(delay);
	}

	
	public double solve() {
		for (int i = 0; i < k.length; i++) {
			k[i].solve();
		}
		
		return k[0].getNewOutput();
	}
	
	public void setExternalStimulus(double stimulus) {
		k[0].setExternalStimulus(stimulus);
	}
}
