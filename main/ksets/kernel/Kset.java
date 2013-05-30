package main.ksets.kernel;

public interface Kset extends HasOutput {
	Connection connect(HasOutput k, double w);
}
