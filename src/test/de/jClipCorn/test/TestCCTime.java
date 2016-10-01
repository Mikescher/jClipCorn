package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.exceptions.CCFormatException;

@SuppressWarnings("nls")
public class TestCCTime {

	@Test
	public void testAddSubTime() {
		CCLog.setUnitTestMode();
		
		CCTime base = CCTime.create(13, 30, 15);

		assertEquals(base, base.getAddHour(24));
		assertEquals(base, base.getAddMinute(24*60));
		assertEquals(base, base.getAddSecond(24*60*60));
		
		assertEquals(14, base.getAddHour(1).getHours());
		assertEquals(30, base.getAddHour(1).getMinutes());
		assertEquals(15, base.getAddHour(1).getSeconds());

		assertEquals(12, base.getSubHour(1).getHours());
		assertEquals(30, base.getSubHour(1).getMinutes());
		assertEquals(15, base.getSubHour(1).getSeconds());

		assertEquals(14, base.getAddMinute(40).getHours());
		assertEquals(10, base.getAddMinute(40).getMinutes());
		assertEquals(15, base.getAddMinute(40).getSeconds());

		assertEquals(12, base.getSubMinute(40).getHours());
		assertEquals(50, base.getSubMinute(40).getMinutes());
		assertEquals(15, base.getSubMinute(40).getSeconds());

		assertEquals(13, base.getAddSecond(55).getHours());
		assertEquals(31, base.getAddSecond(55).getMinutes());
		assertEquals(10, base.getAddSecond(55).getSeconds());

		assertEquals(13, base.getSubSecond(55).getHours());
		assertEquals(29, base.getSubSecond(55).getMinutes());
		assertEquals(20, base.getSubSecond(55).getSeconds());

		assertEquals(14, base.getAddSecond(60*60).getHours());
		assertEquals(30, base.getAddSecond(60*60).getMinutes());
		assertEquals(15, base.getAddSecond(60*60).getSeconds());
	}

	@Test
	public void testFormat() {
		CCLog.setUnitTestMode();
		
		CCTime base = CCTime.create(19, 45, 1);
		
		assertEquals("19:45:01", base.getStringRepresentation("HH:mm:ss"));
		assertEquals("19:45", base.getStringRepresentation("HH:mm"));
		assertEquals("19:45:1", base.getStringRepresentation("H:m:s"));
		assertEquals("194501", base.getStringRepresentation("HHmmss"));
		assertEquals("001900450001", base.getStringRepresentation("HHHHmmmmssss"));
		assertEquals("07:45:01", base.getStringRepresentation("hh:mm:ss"));
		assertEquals("07:45:01 PM", base.getStringRepresentation("hh:mm:ss t"));
	}
	
	@Test
	public void testParse() throws CCFormatException {
		CCLog.setUnitTestMode();
		
		assertTrue(CCTime.testparse("20:01:55", "HH:mm:ss"));
		assertEquals(CCTime.create(20,1,55), CCTime.parse("20:01:55", "HH:mm:ss"));

		assertTrue(CCTime.testparse("20:01", "HH:mm"));
		assertEquals(CCTime.create(20,1,0), CCTime.parse("20:01", "HH:mm"));
	}
}
