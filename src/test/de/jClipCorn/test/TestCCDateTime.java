package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.util.datetime.CCDateTime;

@SuppressWarnings("nls")
public class TestCCDateTime {

	@Test
	public void testFormat() {
		CCDateTime base = CCDateTime.create(26, 1, 2001, 19, 45, 1);
		
		assertEquals("19:45:01", base.getStringRepresentation("HH:mm:ss"));
		assertEquals("19:45", base.getStringRepresentation("HH:mm"));
		assertEquals("19:45:1", base.getStringRepresentation("H:m:s"));
		assertEquals("194501", base.getStringRepresentation("HHmmss"));
		assertEquals("001900450001", base.getStringRepresentation("HHHHmmmmssss"));
		assertEquals("07:45:01", base.getStringRepresentation("hh:mm:ss"));
		assertEquals("07:45:01 PM", base.getStringRepresentation("hh:mm:ss t"));

		assertEquals("26.01.2001", base.getStringRepresentation("dd.MM.yyyy"));
		assertEquals("26.01.01", base.getStringRepresentation("dd.MM.yy"));
		assertEquals("26.1.01", base.getStringRepresentation("d.M.yy"));
		assertEquals("2001-01-26", base.getStringRepresentation("yyyy-MM-dd"));

		assertEquals("2001-01-26 (19:45:01)->PM", base.getStringRepresentation("yyyy-MM-dd (HH:mm:ss)->t"));
	}
	
	@Test
	public void testParse() {
		assertEquals(CCDateTime.create(19,8,2020, 7,50,35), CCDateTime.parse("19,8,2020 07:50:35", "dd,MM,yyyy HH:mm:ss"));
		assertEquals(CCDateTime.create(19,8,2020, 0,0,0), CCDateTime.parse("19,8,2020", "dd,MM,yyyy"));
	}

}
