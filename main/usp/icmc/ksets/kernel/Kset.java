package main.usp.icmc.ksets.kernel;

public interface Kset extends HasOutput {
	void registerConnection(Connection c);
	void registerConnection(HasOutput k, double w);
}
