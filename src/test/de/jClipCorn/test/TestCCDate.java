package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;

@SuppressWarnings("nls")
public class TestCCDate {

	@Test
	public void testAddSubDate() {
		CCDate tdate = CCDate.getMinimumDate();
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900);
		
		tdate = tdate.getAddDay(1);
		assertTrue(tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 1900);
		
		tdate = tdate.getSubDay(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900);
		
		tdate = tdate.getAddMonth(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 2 && tdate.getYear() == 1900);
		
		tdate = tdate.getSubMonth(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900);
		
		tdate = tdate.getAddYear(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901);
		
		tdate = tdate.getSubYear(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1900);
		
		tdate = tdate.getAddDay(365);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 1901);
		
		tdate = tdate.getAddDay(150);
		assertTrue(tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1901);
		assertEquals(5, tdate.getWeekdayInt());
		
		tdate = tdate.getAddDay(1096);
		assertTrue(tdate.getDay() == 31 && tdate.getMonth() == 5 && tdate.getYear() == 1904);
		assertTrue(tdate.isLeapYear());
		
		tdate = tdate.getAdd(215, 12, 94);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2000);
		
		tdate = tdate.getSetDay(2);
		assertTrue(tdate.getDay() == 2 && tdate.getMonth() == 1 && tdate.getYear() == 2000);
		assertEquals(7, tdate.getWeekdayInt());
		
		tdate = tdate.getAddDay(1);
		assertTrue(tdate.getDay() == 3 && tdate.getMonth() == 1 && tdate.getYear() == 2000);
		assertEquals(1, tdate.getWeekdayInt());
		assertTrue(tdate.isLeapYear());
		
		tdate = tdate.getAddMonth(12);
		assertTrue(tdate.getDay() == 3 && tdate.getMonth() == 1 && tdate.getYear() == 2001);
		assertFalse(tdate.isLeapYear());
	}

	@Test
	public void testFormat() {
		assertEquals("26.06.2001", CCDate.create(26,6,2001).getStringRepresentation("dd.MM.yyyy"));
		assertEquals("26.06.01", CCDate.create(26,6,2001).getStringRepresentation("dd.MM.yy"));
		assertEquals("26.6.01", CCDate.create(26,6,2001).getStringRepresentation("d.M.yy"));
		assertEquals("2001-06-26", CCDate.create(26,6,2001).getStringRepresentation("yyyy-MM-dd"));
	}
	
	@Test
	public void testParse() throws CCFormatException {
		assertTrue(CCDate.testparse("26-06-2001", "dd-MM-yyyy"));
		assertEquals(CCDate.create(26,6,2001), CCDate.parse("26-06-2001", "dd-MM-yyyy"));
		
		assertTrue(CCDate.testparse("26-06-01", "dd-MM-yy"));
		assertEquals(CCDate.create(26,6,2001), CCDate.parse("26-06-01", "dd-MM-yy"));

		assertFalse(CCDate.testparse("26-06-01-01", "dd-MM-yy"));
		assertFalse(CCDate.testparse("26-06-", "dd-MM-yy"));
		assertFalse(CCDate.testparse("26-06-01", "dd-MM"));
	}
}
