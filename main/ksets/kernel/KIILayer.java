package main.ksets.kernel;

import java.io.Serializable;
import java.util.ArrayList;


public class KIILayer extends KLayer implements Serializable {

	private static final long serialVersionUID = 5708993646173660914L;

	private WLat wLatType;
	private Connection[][] inhibitoryLatConnections;
	private boolean trainInhibitory = false;
	private boolean doHomeostasis = false;
	public ArrayList<double[]> inhibitoryWeightsHistory;
	
	private static final double alpha = Config.alpha;
	
	public KIILayer(int size, double wee, double wei, double wie, double wii, double wLat_ee, double wLat_ii, WLat type) {
		super(size);
		this.wLatType = type;
		
		k = new KII[size];
		Connection[][] latConnections = new Connection[size][size];
		this.inhibitoryLatConnections = new Connection[size][size];
		
		for (int i = 0; i < size; ++i) {
			k[i] = new KII(wee, wei, wie, wii);
		}
		
		// Normalize the Weights in function of the layer size
		this.nLatConnections = size > 1 ? size - 1 : 1;//;
		if (size > 1) {
			wLat_ee = wLat_ee / nLatConnections;
			wLat_ii = wLat_ii / nLatConnections;
		}
		
		// Creating lateral connections between KII-sets in the layer
		for (int i = 0; i < size - 1; ++i) {
			for (int j = i+1; j < size; ++j) {
				double wLat = getLatWeight(wLat_ee);
				latConnections[i][j] = k[i].connect(k[j], wLat);
				latConnections[j][i] = k[j].connect(k[i], wLat);									
				inhibitoryLatConnections[i][j] = ((KII) k[i]).connectInhibitory(k[j], wLat_ii);
				inhibitoryLatConnections[j][i] = ((KII) k[j]).connectInhibitory(k[i], wLat_ii);
			}
		}
		
		super.setLateralConnections(latConnections);
		
		weightsHistory = new ArrayList<double[]>();
		weightsHistory.add(getWeights());
		
		inhibitoryWeightsHistory = new ArrayList<double[]>();
		inhibitoryWeightsHistory.add(getInhibitoryWeights());
	}
	
	public KIILayer(int size, double[] defaultW1, double[] defaultWLat1, WLat type) {
		this(size, defaultW1[0], defaultW1[1], defaultW1[2], defaultW1[3], defaultWLat1[0], defaultWLat1[1], type);
	}
	
	private double getLatWeight(double x) {
		switch (wLatType) {
		case USE_FIXED_WEIGHTS:
			return x;
		case USE_RANDOM_WEIGHTS:
			return Math.abs(Math.random() * x);
		}
		return x;
	}
	
	public double[] getLayerInhibitoryOutput() {
		return this.getLayerInhibitoryOutput(0);
	}
	
	public double[] getLayerInhibitoryOutput(int t) {
		double[] output = new double[this.k.length];
		for (int i = 0; i < this.k.length; ++i) {
			output[i] = ((KII) k[i]).getInhibitoryOutput(t);
		}
		
		return output;
	}
	
	/**
	 * Returns the average activation from all inhibitory nodes
	 */
	public double getInhibitoryOutput() {
		return this.getInhibitoryOutput(0);
	}
	
	/**
	 * Returns the average activation from all inhibitory nodes at time (now - delay)
	 */
	public double getInhibitoryOutput(int delay) {
		double sum = 0.0;
		
		for (int i = 0; i < this.k.length; ++i) {
			sum += ((KII) k[i]).getInhibitoryOutput(delay);
		}
		
		return sum / getSize();
	}

	public void connect(Layer origin, double weight, int delay, InterlayerMethod connectionMethod) {
		connect(origin, weight, delay, connectionMethod, ConnectionType.EXCITATORY);
	}
	
	public void connectInhibitory(Layer origin, double weight, int delay, InterlayerMethod connectionMethod) {
		connect(origin, weight, delay, connectionMethod, ConnectionType.INHIBITORY);
	}
	
	public void connect(Layer origin, double weight, int delay, InterlayerMethod connectionMethod, ConnectionType connectTo) {
		switch (connectionMethod) {
		case CONVERGE_DIVERGE:
			ConnectionType from = origin instanceof LowerOutputAdapter ? ConnectionType.INHIBITORY : ConnectionType.EXCITATORY;
			connectConvergeDiverge(origin, weight, delay, from, connectTo);
			break;
		case AVERAGE:
			connectAverage(origin, weight, delay, connectTo);
		}
	}
	
	private void connectAverage(HasOutput origin, double weight, int delay, ConnectionType toType) {
		for (int i = 0; i < this.k.length; ++i) {
			if (toType == ConnectionType.EXCITATORY) {
				k[i].connect(origin, weight / getSize(), delay);
			} else {
				((KII) k[i]).connectInhibitory(origin, weight / getSize(), delay);
			}
		}
	}
	
	private void connectConvergeDiverge(Layer layer, double weight, int delay, ConnectionType fromType, ConnectionType toType) {
		int nCon = Math.max(layer.getSize(), this.getSize());
		double wNorm = weight / nCon;
		double ratio = layer.getSize() / this.getSize();
		
		for (int i = 1; i <= nCon; i++) {
			int idxTo = -1;
			HasOutput output = null;
			
			if (ratio >= 1) {
				// cycling between output layer
				int inputIndex = ((int) Math.ceil(i / ratio)) - 1;
				output = getOrigin(layer, fromType, i - 1);
				idxTo = inputIndex;
			} else {
				// cycling between input layer
				int outputIndex = ((int) Math.ceil(i * ratio)) - 1;
				output = getOrigin(layer, fromType, outputIndex);
				idxTo = i - 1;
			}
			
			if (toType == ConnectionType.EXCITATORY) {
				this.k[idxTo].connect(output, wNorm, delay);
			} else {
				((KII) this.k[idxTo]).connectInhibitory(output, wNorm, delay);
			}
		}
	}
	
	private HasOutput getOrigin(Layer layer, ConnectionType conType, int index) {
		HasOutput output;
		
		if (conType == ConnectionType.INHIBITORY) {
			output = ((KII) layer.getUnit(index)).getInhibitoryUnit();
		} else {
			output = layer.getUnit(index);
		}
	
		return output;
	}
	
	public void run() {
		for (int i = 0; i < this.k.length; i++) {
			k[i].run();
		}
	}
	
	public void switchInhibitoryTraining(boolean trainInhibitory) {
		this.trainInhibitory = trainInhibitory;
	}
	
	public void switchHomeostasis(boolean doHomeostasis) {
		this.doHomeostasis = doHomeostasis;
	}
	
	public void batchTrain(double[][] activations, double mean) {
		for (int h = 0; h < activations.length; ++h) {
			double[] std = activations[h];
			
			double homeostasis = 0;
			
			for (int i = 0; i < getSize(); ++i) {
				for (int j = 0; j < getSize(); ++j) {
					if (i == j) continue;
					
					double deltaW = 0;
					if ((std[i] > mean) && (std[j] > mean)) {
						deltaW = (alpha) * (std[i] - mean) * (std[j] - mean);
						homeostasis += deltaW;
					}
					
					latConnections[i][j].setWeight(latConnections[i][j].getWeight() + deltaW);
					latConnections[i][j].setWeight(latConnections[i][j].getWeight() * Config.habituation);
				}
			}
			
			if (doHomeostasis) {
				homeostasis = homeostasis / (getSize() * (getSize() - 1));
				for (int i = 0; i < getSize(); ++i) {
					for (int j = 0; j < getSize(); ++j) {
						if (i == j) continue;
						latConnections[i][j].setWeight(latConnections[i][j].getWeight() - homeostasis);
					}
				}
			}
		}
		
		// Save weights history
		weightsHistory.add(getWeights());
	}
	
	public void train() {
		double meanStd = 0.0;
		double[] std = new double[k.length];
		
		for (int i = 0; i < getSize(); ++i) {
			std[i] = stardardDeviation(k[i].getActivation());
			meanStd += std[i];
		}
		
		meanStd = meanStd / std.length;	
		double homeostasis = 0;
		
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				if (i == j) continue;
				
				double deltaW = 0;				
				if ((std[i] > meanStd) && (std[j] > meanStd)) {
					deltaW = (alpha / nLatConnections) * (std[i] - meanStd) * (std[j] - meanStd);
					homeostasis += deltaW;
				}
				
				latConnections[i][j].setWeight(latConnections[i][j].getWeight() + deltaW);
				latConnections[i][j].setWeight(latConnections[i][j].getWeight() * Config.habituation);
			}
		}
		
		if (doHomeostasis) {
			homeostasis = homeostasis / (getSize() * (getSize() - 1));
			for (int i = 0; i < getSize(); ++i) {
				for (int j = 0; j < getSize(); ++j) {
					if (i == j) continue;
					latConnections[i][j].setWeight(latConnections[i][j].getWeight() - homeostasis);
				}
			}
		}
		
		// Save weights history
		weightsHistory.add(getWeights());
		if (trainInhibitory) {
			trainInhibitory();
		}
	}
	
	public void rollbackWeights() {
		double[] weights = weightsHistory.get(weightsHistory.size() - 2);
		int index = 0;
		
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				if (i == j) continue;				
				latConnections[i][j].setWeight(weights[index++]);
			}
		}
	}
	
	public void trainInhibitory() {
		double meanStd = 0.0;
		double[] std = new double[k.length];
		
		for (int i = 0; i < getSize(); ++i) {
			std[i] = stardardDeviation(((KII) k[i]).getActivation(3));
			meanStd += std[i];
		}
		
		meanStd = meanStd / std.length;	
		double deltaW = 0;
		
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				if (i == j) continue;
				
				if (std[i] < meanStd && std[j] < meanStd) {
					deltaW = -1 * (alpha / nLatConnections) * (std[i] - meanStd) * (std[j] - meanStd);
				}
				
				inhibitoryLatConnections[i][j].setWeight(inhibitoryLatConnections[i][j].getWeight() + deltaW);
				inhibitoryLatConnections[i][j].setWeight(inhibitoryLatConnections[i][j].getWeight() * Config.habituation);
			}
		}
		// Save weights history
		inhibitoryWeightsHistory.add(getWeights());
	}
	
	public double[][] getInhibitoryHistory() {
		double[][] history = new double[getSize()][];
		
		for (int i = 0; i < getSize(); ++i) {
			history[i] = ((KII) k[i]).getHistory(3);
		}
		
		return history;
	}

	public double[] getInhibitoryWeights() {
		double[] weights = new double[getSize() * (getSize() - 1)];
		int index = 0;
		
		for (int i = 0; i < getSize(); ++i) {
			for (int j = 0; j < getSize(); ++j) {
				if (i == j) continue;				
				weights[index++] = inhibitoryLatConnections[i][j].getWeight();
			}
		}
		
		return weights;
	}
	
	public double[][] getInhibitoryWeightsHistory() {
		return Utils.toMatrix(inhibitoryWeightsHistory);
	}
}
