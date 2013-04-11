package test.ksets.kernel;

import static org.junit.Assert.*;
import main.ksets.kernel.Config;


import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void testIncTime() {
		Config.time = 0;
		Config.historySize = 4;
		
		assertEquals(0, Config.getTime());
		Config.incTime();
		assertEquals(1, Config.getTime());
		Config.incTime();
		assertEquals(2, Config.getTime());
		Config.incTime();
		assertEquals(3, Config.getTime());
		Config.incTime();
		assertEquals(0, Config.getTime());
	}

	@Test
	public void testGetTime() {
		Config.time = 2;
		Config.historySize = 4;
		
		assertEquals(1, Config.getTime(-5));
		assertEquals(2, Config.getTime(-4));
		assertEquals(3, Config.getTime(-3));
		assertEquals(0, Config.getTime(-2));
		assertEquals(1, Config.getTime(-1));
		assertEquals(2, Config.getTime(0));
		assertEquals(3, Config.getTime(1));
		assertEquals(0, Config.getTime(2));
		assertEquals(1, Config.getTime(3));
		assertEquals(2, Config.getTime(4));
		assertEquals(3, Config.getTime(5));	
	}
}
