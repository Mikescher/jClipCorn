package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;

@SuppressWarnings("nls")
public class TestCCDateTime extends ClipCornBaseTest {

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
}
