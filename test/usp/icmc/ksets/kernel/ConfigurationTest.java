package test.usp.icmc.ksets.kernel;

import static org.junit.Assert.*;

import main.usp.icmc.ksets.kernel.Configuration;

import org.junit.Test;

// TODO properly test the class
public class ConfigurationTest {

	@Test
	public void testIncTime() {
		Configuration.time = 0;
		Configuration.historySize = 4;
		
		assertEquals(0, Configuration.getTime());
		Configuration.incTime();
		assertEquals(1, Configuration.getTime());
		Configuration.incTime();
		assertEquals(2, Configuration.getTime());
		Configuration.incTime();
		assertEquals(3, Configuration.getTime());
		Configuration.incTime();
		assertEquals(0, Configuration.getTime());
	}

	@Test
	public void testGetTimePlus() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTimeMinus() {
		Configuration.time = 2;
		Configuration.historySize = 4;
		
		assertEquals(2, Configuration.getTimeMinus(0));
		assertEquals(1, Configuration.getTimeMinus(1));
		assertEquals(0, Configuration.getTimeMinus(2));
		assertEquals(3, Configuration.getTimeMinus(3));
		assertEquals(2, Configuration.getTimeMinus(4));
		assertEquals(1, Configuration.getTimeMinus(5));
		assertEquals(0, Configuration.getTimeMinus(6));
		assertEquals(3, Configuration.getTimeMinus(7));
		assertEquals(2, Configuration.getTimeMinus(8));
	}

}
