package de.jClipCorn.test;

import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.history.CCHistoryTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.RefParam;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.stream.CCStreams;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestDatabaseHistoryTrigger extends ClipCornBaseTest {

	// Regression guard: every table that gets auto-history triggers must be representable as a CCHistoryTable value.
	// Otherwise CCDatabaseHistory.query(...) throws EnumValueNotFoundException while reading the DB-History
	// (this happened for the PROPERTIES table, which is a settings/meta table and must not be tracked).
	@Test
	public void testAllTriggeredTablesAreKnownHistoryTables() {
		final String addPrefix = "JCCTRIGGER_AUTOHISTORY_ADD_";

		List<Tuple3<String, String, String>> triggers = CCDatabaseHistory.createTriggerStatements();

		int checked = 0;
		for (Tuple3<String, String, String> trigger : triggers) {
			if (!trigger.Item1.startsWith(addPrefix)) continue;
			// the name is JCCTRIGGER_AUTOHISTORY_ADD_<SCHEMA>_<TABLE>
			String tableName = trigger.Item1.substring(addPrefix.length()).split("_", 2)[1];
			assertNotNull("auto-history trigger references table '" + tableName + "' that has no CCHistoryTable value", CCHistoryTable.getWrapper().findByTextOrNull(tableName));
			checked++;
		}

		assertTrue("expected at least one auto-history ADD trigger", checked > 0);
	}

	private static String triggerCode(String name) {
		var t = CCStreams.iterate(CCDatabaseHistory.createTriggerStatements()).singleOrNull(p -> Str.equals(p.Item1, name));
		assertNotNull("no trigger named " + name, t);
		return t.Item3;
	}

	// The user-data row is created with the first user-side edit and dropped again when everything is back
	// at its default - the element itself was neither added to nor removed from the collection.
	@Test
	public void testSparseUserRowChangesAreLoggedAsUpdate() {
		for (String tab : new String[] { "MOVIES", "SERIES", "SEASONS", "EPISODES" }) {
			for (String action : new String[] { "ADD", "REM" }) {
				String code = triggerCode("JCCTRIGGER_AUTOHISTORY_" + action + "_USERDATA_" + tab);

				assertTrue(code, code.contains("'UPDATE'"));
				assertFalse(code, code.contains("'ADD'"));
				assertFalse(code, code.contains("'DELETE'"));
			}
		}

		assertTrue(triggerCode("JCCTRIGGER_AUTOHISTORY_ADD_MAIN_MOVIES").contains("'ADD'"));
		assertTrue(triggerCode("JCCTRIGGER_AUTOHISTORY_REM_MAIN_MOVIES").contains("'DELETE'"));

		// only the entity rows are sparse - a new INFO key really is a new row
		assertTrue(triggerCode("JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_INFO").contains("'ADD'"));
	}

	// A database created by an older version carries that version's triggers - they have to be
	// detected and replaced on connect, in both files.
	@Test
	public void testOutdatedTriggerIsDetectedAndReplaced() throws Exception {
		var ml = createEmptyDB();
		var history = ml.getHistory();
		var db = ml.getDatabaseForUnitTests();

		history.enableTrigger();

		var referror = new RefParam<String>();
		assertTrue(referror.Value, history.testTrigger(true, referror));

		db.deleteTrigger("JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_MOVIES", false);
		db.createTrigger("CREATE TRIGGER userdata.[JCCTRIGGER_AUTOHISTORY_ADD_USERDATA_MOVIES] AFTER INSERT ON [MOVIES] BEGIN SELECT 1; END");
		assertFalse(history.testTrigger(true, referror));

		history.enableTrigger();
		assertTrue(referror.Value, history.testTrigger(true, referror));
	}
}
