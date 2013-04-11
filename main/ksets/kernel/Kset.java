package main.ksets.kernel;

public interface Kset extends HasOutput {
	void connect(HasOutput k, double w);
}
