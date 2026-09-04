package de.jClipCorn.test;

import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.exceptions.CCFormatException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestCCUUID extends ClipCornBaseTest {

	private static void assertParseFails(String v) {
		try {
			CCUUID.parse(v);
			fail("Expected UUIDFormatException for '" + v + "'");
		} catch (CCFormatException e) {
			// Good!
		}
	}

	private static void assertRFC9562Layout(CCUUID v) {
		var u = v.asUUID();
		assertEquals("version", 7, u.version());
		assertEquals("variant", 2, u.variant());
	}

	@Test
	public void testEmpty() {
		assertEquals("00000000-0000-0000-0000-000000000000", CCUUID.EMPTY.toString());
		assertTrue(CCUUID.EMPTY.isEmpty());
		assertFalse(CCUUID.generate().isEmpty());
	}

	@Test
	public void testParse() throws CCFormatException {
		assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", CCUUID.parse("6ba7b810-9dad-11d1-80b4-00c04fd430c8").toString());
		assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", CCUUID.parse("6BA7B810-9DAD-11D1-80B4-00C04FD430C8").toString());
		assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", CCUUID.parse("  6ba7b810-9dad-11d1-80b4-00c04fd430c8  ").toString());

		assertParseFails(null);
		assertParseFails("");
		assertParseFails("not-a-uuid");
		assertParseFails("1-1-1-1-1"); // UUID.fromString() accepts this short form - we must not

		assertEquals(CCUUID.EMPTY, CCUUID.parseOrEmpty("garbage"));
		assertEquals(CCUUID.EMPTY, CCUUID.parseOrEmpty(null));

		assertTrue(CCUUID.isValid("6ba7b810-9dad-11d1-80b4-00c04fd430c8"));
		assertFalse(CCUUID.isValid("6ba7b810-9dad-11d1-80b4-00c04fd430c"));
	}

	@Test
	public void testParseRejectsMalformed36CharValues() {
		// all 36 chars long, and UUID.fromString() maps every one of them onto a *different*, valid id
		for (String v : new String[]
		{
			"6ba7b810-9dad-11d1-80b4-+0c04fd430c8", // -> 6ba7b810-9dad-11d1-80b4-00c04fd430c8, a real id
			"0000000000000000000000000000-0-0-0-0", // -> the nil sentinel
			"6ba7b8-109dad-11d1-80b4-00c04fd430c8", // -> 006ba7b8-9dad-11d1-80b4-00c04fd430c8
			"+ba7b810-9dad-11d1-80b4-00c04fd430c8", // -> 0ba7b810-9dad-11d1-80b4-00c04fd430c8
		})
		{
			assertParseFails(v);
			assertFalse(v, CCUUID.isValid(v));
			assertEquals(v, CCUUID.EMPTY, CCUUID.parseOrEmpty(v));
		}
	}

	@Test
	public void testParseNormalizesUppercase() throws CCFormatException {
		// uppercase hex is a legal UUID representation - accepted, but normalized to the canonical form
		var v = "6BA7B810-9DAD-11D1-80B4-00C04FD430C8";

		assertTrue(CCUUID.isValid(v));
		assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", CCUUID.parse(v).toString());
		assertEquals(CCUUID.parse(v.toLowerCase()), CCUUID.parse(v));
	}

	@Test
	public void testRoundtrip() throws CCFormatException {
		for (int i = 0; i < 128; i++) {
			var a = CCUUID.generate();
			assertEquals(a, CCUUID.parse(a.toString()));
			assertEquals(a.hashCode(), CCUUID.parse(a.toString()).hashCode());

			// the raw SQL joins between the main and the userdata database compare id *strings*
			assertEquals(a.toString(), CCUUID.parse(a.toString()).toString());
		}
	}

	@Test
	public void testGenerateLayout() {
		for (int i = 0; i < 1024; i++) assertRFC9562Layout(CCUUID.generate());
	}

	@Test
	public void testGenerateMonotonic() {
		// generating in a tight loop stays within a single millisecond for most iterations,
		// which is exactly the case the counter-based monotonicity has to cover
		var prev = CCUUID.generate();
		for (int i = 0; i < 100_000; i++) {
			var cur = CCUUID.generate();
			assertTrue("not monotonic at " + i + ": " + prev + " -> " + cur, prev.compareTo(cur) < 0);
			prev = cur;
		}
	}

	@Test
	public void testGenerateUnique() {
		Set<CCUUID> seen = new HashSet<>();
		for (int i = 0; i < 100_000; i++) assertTrue(seen.add(CCUUID.generate()));
	}

	@Test
	public void testLexicographicOrderEqualsValueOrder() {
		List<CCUUID> gen = new ArrayList<>();
		for (int i = 0; i < 4096; i++) gen.add(CCUUID.generate());

		var byValue  = new ArrayList<>(gen);
		var byString = new ArrayList<>(gen);

		byValue.sort(CCUUID::compareTo);
		byString.sort((a, b) -> a.toString().compareTo(b.toString()));

		assertEquals(byValue, byString);
		assertEquals(gen, byValue); // ...and both equal the creation order
	}

	@Test
	public void testMigrationDeterminism() {
		// golden values - these must never change, or a re-migrated database gets different ids
		assertEquals("00dc6acf-ac00-7893-a24b-389f82896d60", CCUUID.deriveMigrationUUID("MOVIES", 0).toString());
		assertEquals("00dc6acf-ac01-763c-acbf-8659753e22cb", CCUUID.deriveMigrationUUID("MOVIES", 1).toString());
		assertEquals("00dc6acf-ac01-7312-9665-2e8d82572824", CCUUID.deriveMigrationUUID("SERIES", 1).toString());
		assertEquals("00dc6acf-ac01-76a4-8aa2-3b86f1ce283b", CCUUID.deriveMigrationUUID("COVERS", 1).toString());
		assertEquals("00dc6ad0-6733-7007-a523-22c8378b4ef1", CCUUID.deriveMigrationUUID("EPISODES", 47923).toString());
	}

	@Test
	public void testMigrationLayout() {
		for (int i = 0; i < 512; i++) {
			assertRFC9562Layout(CCUUID.deriveMigrationUUID("MOVIES", i));
			assertRFC9562Layout(CCUUID.deriveMigrationUUID("COVERS", i));
		}
	}

	@Test
	public void testMigrationPreservesInsertionOrder() {
		var prev = CCUUID.deriveMigrationUUID("MOVIES", 0);
		for (int i = 1; i < 50_000; i++) {
			var cur = CCUUID.deriveMigrationUUID("MOVIES", i);
			assertTrue(prev.compareTo(cur) < 0);
			assertTrue(prev.toString().compareTo(cur.toString()) < 0);
			prev = cur;
		}
	}

	@Test
	public void testMigrationTableSeparation() {
		for (int i = 0; i < 8192; i++) {
			// same id, different table -> different UUID (COVERS has its own id-counter that
			// overlaps numerically with the entity ids)
			assertNotEquals(CCUUID.deriveMigrationUUID("MOVIES", i), CCUUID.deriveMigrationUUID("COVERS", i));
		}
	}

	@Test
	public void testMigrationSortsBeforeRuntime() {
		var runtime = CCUUID.generate();
		for (int i = 0; i < 1024; i++) {
			assertTrue(CCUUID.deriveMigrationUUID("MOVIES", i).compareTo(runtime) < 0);
		}
		assertTrue(CCUUID.deriveMigrationUUID("MOVIES", Integer.MAX_VALUE).compareTo(runtime) < 0);
	}

	@Test
	public void testCompareIsUnsigned() throws CCFormatException {
		// java.util.UUID.compareTo() compares signed, which puts these two in the wrong order
		var lo = CCUUID.parse("00000000-0000-7000-8000-000000000000");
		var hi = CCUUID.parse("ffffffff-ffff-7fff-bfff-ffffffffffff");

		assertTrue(lo.compareTo(hi) < 0);
		assertTrue(hi.compareTo(lo) > 0);
		assertEquals(0, lo.compareTo(lo));
	}
}
