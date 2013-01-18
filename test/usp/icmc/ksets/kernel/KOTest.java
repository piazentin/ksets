package test.usp.icmc.ksets.kernel;

import static org.junit.Assert.*;

import main.usp.icmc.ksets.kernel.Configuration;
import main.usp.icmc.ksets.kernel.KO;

import org.junit.Test;

public class KOTest {

	@Test
	public void testSolveDouble() {
		KO ko = new KO();
		assertEquals(0.0170, ko.solve(1), 0.001);
		Configuration.incTime();
		assertEquals(0.0586, ko.solve(1), 0.001);
		Configuration.incTime();
		assertEquals(0.1142, ko.solve(1), 0.001);
		Configuration.incTime();
		assertEquals(0.1769, ko.solve(1), 0.001);
		Configuration.incTime();
		assertEquals(0.2419, ko.solve(1), 0.001);
		Configuration.incTime();
		assertEquals(0.2895, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.3101, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.3132, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.3053, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.2907, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.2725, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.2525, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.2321, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.2120, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1927, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1747, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1578, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1424, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1282, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1153, ko.solve(0), 0.001);
		Configuration.incTime();
		assertEquals(0.1027, ko.solve(0), 0.01);
		Configuration.incTime();
		assertEquals(0.0931, ko.solve(0), 0.01);
		Configuration.incTime();
		assertEquals(0.0835, ko.solve(0), 0.01);
		Configuration.incTime();
		assertEquals(0.0750, ko.solve(0), 0.01);
		Configuration.incTime();
		assertEquals(0.0672, ko.solve(0), 0.01);
		Configuration.incTime();
		assertEquals(0.0603, ko.solve(0), 0.01);
	}
	
	@Test
	public void testSigmoid() {
		KO ko = new KO();
		assertEquals(-1.1040,ko.sigmoid(-6),0.0001);
		assertEquals(-1.0988,ko.sigmoid(-5),0.0001);
		assertEquals(-1.0847,ko.sigmoid(-4),0.0001);
		assertEquals(-1.0465,ko.sigmoid(-3),0.0001);
		assertEquals(-0.9439,ko.sigmoid(-2),0.0001);
		assertEquals(-0.6738,ko.sigmoid(-1),0.0001);
		assertEquals(0.0000,ko.sigmoid(0),0.0001);
		assertEquals(1.4541,ko.sigmoid(1),0.0001);
		assertEquals(3.6068,ko.sigmoid(2),0.0001);
		assertEquals(4.8900,ko.sigmoid(3),0.0001);
		assertEquals(4.9999,ko.sigmoid(4),0.0001);
		assertEquals(5.0000,ko.sigmoid(5),0.0001);
		assertEquals(5.0000,ko.sigmoid(6),0.0001);
	}
	
	@Test
	public void testCalculateInputWithoutConnections() {
		KO ko = new KO();
		ko.setExternalStimulus(0);
		assertEquals(0, ko.calculateRHS(), 0.0001);
		
		ko.setExternalStimulus(1);
		assertEquals(1, ko.calculateRHS(), 0.0001);
		
		ko.setExternalStimulus(-1);
		assertEquals(-1, ko.calculateRHS(), 0.0001);
	}
	
	@Test
	public void testCalculateInputWithConnections() {
		KO ko = new KO();
		
		HasOutputMock o1 = new HasOutputMock(0);
		HasOutputMock o2 = new HasOutputMock(1);
		HasOutputMock o3 = new HasOutputMock(-1);
		
		ko.setExternalStimulus(0);
		ko.registerConnection(o1, 1);
		assertEquals(0, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(1);
		assertEquals(1, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(-1);
		assertEquals(-1, ko.calculateRHS(), 0.0001);
		
		ko.setExternalStimulus(0);
		ko.registerConnection(o2, 1);
		assertEquals(1.4541, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(1);
		assertEquals(2.4541, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(-1);
		assertEquals(0.4541, ko.calculateRHS(), 0.0001);
		
		ko.setExternalStimulus(0);
		ko.registerConnection(o3, 1);
		assertEquals(0.7803, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(1);
		assertEquals(1.7803, ko.calculateRHS(), 0.0001);
		ko.setExternalStimulus(-1);
		assertEquals(-0.2197, ko.calculateRHS(), 0.0001);
	}

}
