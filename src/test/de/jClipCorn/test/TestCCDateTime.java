package de.jClipCorn.test;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestCCDateTime extends ClipCornBaseTest {

	private static void assertCCDateTimeEquals(CCDateTime expected, CCDateTime actual)
	{
		assertEquals(expected.getSQLStringRepresentation(), actual.getSQLStringRepresentation());
	}

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
	public void testParse() throws CCFormatException {
		assertEquals(CCDateTime.create(19,8,2020, 7,50,35), CCDateTime.parse("19,8,2020 07:50:35", "dd,MM,yyyy HH:mm:ss"));
		assertEquals(CCDateTime.create(19,8,2020, 99,99,99), CCDateTime.parse("19,8,2020", "dd,MM,yyyy"));
		assertTrue(CCDateTime.parse("19,8,2020", "dd,MM,yyyy").time.isUnspecifiedTime());

		assertTrue(CCDateTime.createFromSQL("UNSPECIFIED").isUnspecifiedDateTime());
		assertTrue(CCDateTime.createFromSQL("2016-09-07").time.isUnspecifiedTime());
		assertEquals(CCDateTime.create(7,9,2016, 12,13,14), CCDateTime.createFromSQL("2016-09-07 12:13:14"));
	}
	
	@Test
	public void testOrder() {
		assertEquals(00, CCDateTime.create(19,8,2020, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		
		assertEquals(-1, CCDateTime.create(19,8,2020, 7,50,34).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(19,8,2020, 7,50,36).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(-1, CCDateTime.create(19,8,2020, 7,49,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(19,8,2020, 7,51,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(-1, CCDateTime.create(19,8,2020, 6,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(19,8,2020, 8,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(-1, CCDateTime.create(19,8,2019, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(19,8,2021, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(-1, CCDateTime.create(19,7,2020, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(19,9,2020, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(-1, CCDateTime.create(18,8,2020, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));
		assertEquals(+1, CCDateTime.create(21,8,2020, 7,50,35).compareTo(CCDateTime.create(19,8,2020, 7,50,35)));

		assertEquals(00, CCDateTime.create(19,8,2020, 7,50,35).compareTo(CCDateTime.createDateOnly(19,8,2020)));
		assertEquals(-1, CCDateTime.create(10,8,2020, 7,50,35).compareTo(CCDateTime.createDateOnly(19,8,2020)));
		assertEquals(+1, CCDateTime.create(30,8,2020, 7,50,35).compareTo(CCDateTime.createDateOnly(19,8,2020)));
	}

	@Test
	public void testUnspecified() {
		assertEquals(+1, CCDateTime.create(19,8,2020, 7,50,35).compareTo(CCDateTime.getUnspecified()));
		assertEquals(-1, CCDateTime.getUnspecified().compareTo(CCDateTime.createDateOnly(19,8,2020)));
		
		assertTrue(00 == CCDateTime.getUnspecified().compareTo(CCDateTime.getUnspecified()));
		
		assertFalse(CCDateTime.getUnspecified().isEqual(CCDateTime.getUnspecified()));
		assertFalse(CCDateTime.create(19,8,2020, 7,50,35).isEqual(CCDateTime.getUnspecified()));
		assertFalse(CCDateTime.getUnspecified().isEqual(CCDateTime.createDateOnly(19,8,2020)));
	}

	@Test
	public void testEquals() {
		assertFalse(CCDateTime.create(19,8,2020, 7,50,35).isEqual(CCDateTime.getUnspecified()));
		assertFalse(CCDateTime.create(19,8,2020, 7,50,35).isExactEqual(CCDateTime.getUnspecified()));
		assertTrue(CCDateTime.getUnspecified().isExactEqual(CCDateTime.getUnspecified()));
		assertTrue(CCDateTime.create(19,8,2020, 7,50,35).isExactEqual(CCDateTime.create(19,8,2020, 7,50,35)));

		assertTrue(CCDateTime.create(19,8,2020, 7,50,35).isEqual(CCDateTime.create(CCDate.create(19,8,2020))));
		assertFalse(CCDateTime.create(19,8,2020, 7,50,35).isExactEqual(CCDateTime.create(CCDate.create(19,8,2020))));
		assertTrue(CCDateTime.create(CCDate.create(19,8,2020)).isExactEqual(CCDateTime.create(CCDate.create(19,8,2020))));
		assertTrue(CCDateTime.create(19,8,2020, 7,50,35).isExactEqual(CCDateTime.create(19,8,2020, 7,50,35)));
	}

	@Test
	public void testFiletime() {
		long a       = 1406325816000L;
		CCDateTime b = CCDateTime.createFromFileTimestamp(a, GMT_2);
		long c       = b.toFileTimestamp(GMT_2);
		CCDateTime d = CCDateTime.createFromFileTimestamp(c, GMT_2);

		assertEquals(a, c);
		assertTrue(b.isEqual(d));
		assertTrue(d.isEqual(b));

		long x = CCDateTime.create(18, 8, 2018, 19, 49, 30).toFileTimestamp(GMT_2);
		assertEquals(1534614570000L, x);
	}

	@Test
	public void testAddSubTime() {
		CCDateTime base = CCDateTime.create(3, 2, 2000, 13, 30, 15);

		assertEquals(base.time, base.getAddHour(24).time);
		assertEquals(base.time, base.getAddMinute(24*60).time);
		assertEquals(base.time, base.getAddSecond(24*60*60).time);
		assertNotEquals(base.date, base.getAddHour(24).date);
		assertNotEquals(base.date, base.getAddMinute(24*60).date);
		assertNotEquals(base.date, base.getAddSecond(24*60*60).date);

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

		assertEquals(13,   base.getSubHour(24).getHours());
		assertEquals(30,   base.getSubHour(24).getMinutes());
		assertEquals(15,   base.getSubHour(24).getSeconds());
		assertEquals(2,    base.getSubHour(24).getDay());
		assertEquals(2,    base.getSubHour(24).getMonth());
		assertEquals(2000, base.getSubHour(24).getYear());

		assertEquals(13,   base.getAddHour(24).getHours());
		assertEquals(30,   base.getAddHour(24).getMinutes());
		assertEquals(15,   base.getAddHour(24).getSeconds());
		assertEquals(4,    base.getAddHour(24).getDay());
		assertEquals(2,    base.getAddHour(24).getMonth());
		assertEquals(2000, base.getAddHour(24).getYear());
	}

	@Test
	public void testAddSubTime2() {
		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 23, 59, 0), CCDateTime.create(1, 2, 2021, 0,  0, 0).getAdd(0, 0, 0, 0,  0, -60));
		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 23, 59, 0), CCDateTime.create(1, 2, 2021, 0,  0, 0).getAdd(0, 0, 0, 0, -1,   0));

		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 23, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(0, 0, 0,  0, -60, 0));
		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 23, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(0, 0, 0, -1,   0, 0));

		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 0, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(0, 0,  0, -24, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(31, 1, 2021, 0, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(-1, 0, 0,   0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(-31, 0,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 2, 2021, 0, 0, 0).getAdd(0,  -1,  0, 0, 0, 0));


		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 23, 59, 0), CCDateTime.create(1, 1, 2021, 0,  0, 0).getAdd(0, 0, 0, 0,  0, -60));
		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 23, 59, 0), CCDateTime.create(1, 1, 2021, 0,  0, 0).getAdd(0, 0, 0, 0, -1,   0));

		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 23, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0, 0, 0,  0, -60, 0));
		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 23, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0, 0, 0, -1,   0, 0));

		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0, 0,  0, -24, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(31, 12, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(-1, 0, 0,   0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 12, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(-31, 0,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 12, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0,  -1,  0, 0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0, -12,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2020, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0,   0, -1, 0, 0, 0));



		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0,  0, 0), CCDateTime.create(31, 1, 2021, 23, 59, 0).getAdd(0, 0, 0, 0,  0, +60));
		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0,  0, 0), CCDateTime.create(31, 1, 2021, 23, 59, 0).getAdd(0, 0, 0, 0, +1,   0));

		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(31, 1, 2021, 23, 0, 0).getAdd(0, 0, 0,  0, +60, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(31, 1, 2021, 23, 0, 0).getAdd(0, 0, 0, +1,   0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(31, 1, 2021, 0, 0, 0).getAdd(0, 0,  0, +24, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(31, 1, 2021, 0, 0, 0).getAdd(+1, 0, 0,   0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(+31, 0,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 2, 2021, 0, 0, 0), CCDateTime.create(1, 1, 2021, 0, 0, 0).getAdd(0,  +1,  0, 0, 0, 0));


		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0,  0, 0), CCDateTime.create(31, 12, 2020, 23, 59, 0).getAdd(0, 0, 0, 0,  0, +60));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0,  0, 0), CCDateTime.create(31, 12, 2020, 23, 59, 0).getAdd(0, 0, 0, 0, +1,   0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(31, 12, 2020, 23, 0, 0).getAdd(0, 0, 0,  0, +60, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(31, 12, 2020, 23, 0, 0).getAdd(0, 0, 0, +1,   0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(31, 12, 2020, 0, 0, 0).getAdd(0, 0,  0, +24, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(31, 12, 2020, 0, 0, 0).getAdd(+1, 0, 0,   0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 12, 2020, 0, 0, 0).getAdd(+31, 0,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 12, 2020, 0, 0, 0).getAdd(0,  +1,  0, 0, 0, 0));

		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 1, 2020, 0, 0, 0).getAdd(0, +12,  0, 0, 0, 0));
		assertCCDateTimeEquals(CCDateTime.create(1, 1, 2021, 0, 0, 0), CCDateTime.create(1, 1, 2020, 0, 0, 0).getAdd(0,   0, +1, 0, 0, 0));
	}

	@Test
	public void testAddCalcDate() {
		CCDateTime tdate = CCDateTime.create(1, 1, 1900, 20, 15, 22);
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

		tdate = tdate.getAdd(215, 12, 94, 0, 0, 0);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2000);
		tdate = tdate.getAdd(0, 0, 0, 1, 30, 0);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2000);
		assertTrue(tdate.getHours() == 21 && tdate.getMinutes() == 45 && tdate.getSeconds() == 22);

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
	public void testAddCalcDate2() {
		CCDate tdate = CCDate.create(31, 12, 2018);
		assertTrue(tdate.getDay() == 31 && tdate.getMonth() == 12 && tdate.getYear() == 2018);

		tdate = tdate.getAddDay(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 1 && tdate.getYear() == 2019);

		tdate = tdate.getSubDay(1);
		assertTrue(tdate.getDay() == 31 && tdate.getMonth() == 12 && tdate.getYear() == 2018);

		tdate = tdate.getAddMonth(1);
		assertTrue(tdate.getDay() == 31 && tdate.getMonth() == 1 && tdate.getYear() == 2019);

		tdate = tdate.getAddDay(1);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 2 && tdate.getYear() == 2019);

		tdate = tdate.getSubMonth(2);
		assertTrue(tdate.getDay() == 1 && tdate.getMonth() == 12 && tdate.getYear() == 2018);
	}

}
