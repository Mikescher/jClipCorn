package de.jClipCorn.test;

import de.jClipCorn.database.history.CCDatabaseHistory;
import de.jClipCorn.database.history.CCHistoryTable;
import de.jClipCorn.util.datatypes.Tuple;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public class TestDatabaseHistoryTrigger extends ClipCornBaseTest {

	// Regression guard: every table that gets auto-history triggers must be representable as a CCHistoryTable value.
	// Otherwise CCDatabaseHistory.query(...) throws EnumValueNotFoundException while reading the DB-History
	// (this happened for the PROPERTIES table, which is a settings/meta table and must not be tracked).
	@Test
	public void testAllTriggeredTablesAreKnownHistoryTables() {
		final String addPrefix = "JCCTRIGGER_AUTOHISTORY_ADD_";

		List<Tuple<String, String>> triggers = CCDatabaseHistory.createTriggerStatements();

		int checked = 0;
		for (Tuple<String, String> trigger : triggers) {
			if (!trigger.Item1.startsWith(addPrefix)) continue;
			String tableName = trigger.Item1.substring(addPrefix.length());
			assertNotNull("auto-history trigger references table '" + tableName + "' that has no CCHistoryTable value", CCHistoryTable.getWrapper().findByTextOrNull(tableName));
			checked++;
		}

		assertTrue("expected at least one auto-history ADD trigger", checked > 0);
	}
}
