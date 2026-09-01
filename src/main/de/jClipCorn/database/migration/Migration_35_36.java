package de.jClipCorn.database.migration;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Splits the database into the shared ClipCornDB.db and the per-user ClipCornUserData.db
 * (already ATTACHed as `userdata` when this runs).
 *
 * Everything the publisher and the subscriber may legitimately disagree about - ratings, comments,
 * tags, viewed history, saved filters and the application settings - moves into the user file; the
 * shared file keeps the objective part and can then be replaced wholesale by a sync.
 */
@SuppressWarnings("nls")
public class Migration_35_36 extends DBMigration {

	private static final String DEF_VIEWED_HISTORY = CCDateTimeList.createEmpty().asJSONArray();
	private static final String DEF_TAGS           = CCTagList.EMPTY.asJSONArray();
	private static final int    DEF_SCORE          = CCUserScore.RATING_NO.asInt();
	private static final String DEF_SCORECOMMENT   = Str.Empty;

	public Migration_35_36(GenericDatabase db, FSPath databaseDirectory, String databaseName, boolean readonly) {
		super(db, databaseDirectory, databaseName, readonly);
	}

	@Override
	public String getFromVersion() {
		return "35";
	}

	@Override
	public String getToVersion() {
		return "36";
	}

	@Override
	protected boolean backupAndRestoreTrigger() {
		return true;
	}

	@Override
	protected List<UpgradeAction> run() throws Exception {
		CCLog.addInformation("[UPGRADE v35 -> v36] Split off the per-user database");

		createUserDataSchema();
		moveUserColumns();
		moveUserTables();
		writeUserDataInfo();

		return new ArrayList<>();
	}

	private void createUserDataSchema() throws SQLException {
		db.executeSQLThrow("CREATE TABLE userdata.MOVIES   ([ID] TEXT NOT NULL PRIMARY KEY, [VIEWED_HISTORY] TEXT NOT NULL, [TAGS] TEXT NOT NULL, [SCORE] TINYINT NOT NULL, [SCORECOMMENT] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.SERIES   ([ID] TEXT NOT NULL PRIMARY KEY,                                 [TAGS] TEXT NOT NULL, [SCORE] TINYINT NOT NULL, [SCORECOMMENT] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.SEASONS  ([ID] TEXT NOT NULL PRIMARY KEY,                                                       [SCORE] TINYINT NOT NULL, [SCORECOMMENT] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.EPISODES ([ID] TEXT NOT NULL PRIMARY KEY, [VIEWED_HISTORY] TEXT NOT NULL, [TAGS] TEXT NOT NULL, [SCORE] TINYINT NOT NULL, [SCORECOMMENT] TEXT NOT NULL)");

		db.executeSQLThrow("CREATE TABLE userdata.INFO ([IKEY] TEXT NOT NULL PRIMARY KEY, [IVALUE] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.[TEMP] ([IKEY] TEXT NOT NULL PRIMARY KEY, [IVALUE] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.HISTORY ([TABLE] TEXT NOT NULL, [ID] TEXT NOT NULL, [DATE] TEXT NOT NULL, [ACTION] TEXT NOT NULL, [FIELD] TEXT NOT NULL, [OLD] TEXT, [NEW] TEXT)");
		db.executeSQLThrow("CREATE TABLE userdata.FILTERS ([ID] INTEGER NOT NULL PRIMARY KEY, [SORT] INTEGER NOT NULL, [NAME] TEXT NOT NULL, [DEFINITION] TEXT NOT NULL)");
		db.executeSQLThrow("CREATE TABLE userdata.PROPERTIES ([PKEY] TEXT NOT NULL PRIMARY KEY, [PVALUE] TEXT NOT NULL, [LAST_CHANGED] TEXT NOT NULL)");
	}

	/** Only rows that actually carry a non-default value get a user-data row (see D12/§6.3). */
	private void moveUserColumns() throws SQLException {
		copyUserRows("MOVIES",   true,  true);
		copyUserRows("SERIES",   false, true);
		copyUserRows("SEASONS",  false, false);
		copyUserRows("EPISODES", true,  true);

		for (String tab : new String[] { "MOVIES", "SERIES", "SEASONS", "EPISODES" }) {
			for (String col : userColumns(tab.equals("MOVIES") || tab.equals("EPISODES"), !tab.equals("SEASONS"))) {
				db.executeSQLThrow("ALTER TABLE main." + tab + " DROP COLUMN [" + col + "]");
			}
		}
	}

	private static List<String> userColumns(boolean withViewedHistory, boolean withTags) {
		List<String> cols = new ArrayList<>();
		if (withViewedHistory) cols.add("VIEWED_HISTORY");
		if (withTags)          cols.add("TAGS");
		cols.add("SCORE");
		cols.add("SCORECOMMENT");
		return cols;
	}

	private void copyUserRows(String table, boolean withViewedHistory, boolean withTags) throws SQLException {
		List<String> cols = userColumns(withViewedHistory, withTags);

		// the v35 columns are nullable while the v36 ones are not, so NULL has to become the default
		StringBuilder names  = new StringBuilder("[ID]");
		StringBuilder select = new StringBuilder("[ID]");
		StringBuilder where  = new StringBuilder();
		for (String c : cols) {
			names.append(", [").append(c).append("]");
			select.append(", COALESCE([").append(c).append("], ?)");
			if (where.length() > 0) where.append(" OR ");
			where.append("COALESCE([").append(c).append("], ?) <> ?");
		}

		String sql = "INSERT INTO userdata." + table + " (" + names + ") SELECT " + select + " FROM main." + table + " WHERE " + where;

		try (PreparedStatement ps = db.createPreparedStatement(sql)) {
			var defaults = new ArrayList<Object>();
			if (withViewedHistory) defaults.add(DEF_VIEWED_HISTORY);
			if (withTags)          defaults.add(DEF_TAGS);
			defaults.add(DEF_SCORE);
			defaults.add(DEF_SCORECOMMENT);

			int i = 1;
			for (Object d : defaults) setParam(ps, i++, d);             // SELECT
			for (Object d : defaults) { setParam(ps, i++, d); setParam(ps, i++, d); } // WHERE

			CCLog.addInformation("[UPGRADE v35 -> v36] Moved " + ps.executeUpdate() + " user-data rows of " + table);
		}
	}

	private static void setParam(PreparedStatement ps, int idx, Object value) throws SQLException {
		if (value instanceof Integer v) ps.setInt(idx, v);
		else                            ps.setString(idx, (String) value);
	}

	private void moveUserTables() throws SQLException {
		db.executeSQLThrow("INSERT INTO userdata.FILTERS    SELECT * FROM main.FILTERS");
		db.executeSQLThrow("INSERT INTO userdata.PROPERTIES SELECT * FROM main.PROPERTIES");

		db.executeSQLThrow("DROP TABLE main.FILTERS");
		db.executeSQLThrow("DROP TABLE main.PROPERTIES");
	}

	private void writeUserDataInfo() throws SQLException {
		String duuid   = db.querySingleStringSQLThrow("SELECT IVALUE FROM main.INFO WHERE IKEY='DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER'", 0);
		if (duuid == null) {
			duuid = UUID.randomUUID().toString();
			db.executeSQLThrow("INSERT OR REPLACE INTO main.INFO ([IKEY],[IVALUE]) VALUES ('DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER','" + duuid + "')");
		}
		String history = db.querySingleStringSQL("SELECT IVALUE FROM main.INFO WHERE IKEY='HISTORY_ENABLED'", 0);

		writeInfo("VERSION_DB",                              Main.USERDATA_DBVERSION);
		writeInfo("CREATION_DATE",                           CCDate.getCurrentDate().toStringSQL());
		writeInfo("CREATION_TIME",                           CCTime.getCurrentTime().toStringSQL());
		writeInfo("CREATION_USERNAME",                       ApplicationHelper.getCurrentUsername());
		writeInfo("DATABASE_UNIVERSALLY_UNIQUE_IDENTIFIER",  UUID.randomUUID().toString());
		writeInfo("HISTORY_ENABLED",                         (history == null) ? "0" : history);
		writeInfo("MAINDB_DUUID",                            duuid);
		writeInfo("VERSION_MAINDB",                          getToVersion());

		db.executeSQLThrow("DELETE FROM main.INFO WHERE IKEY='HISTORY_ENABLED'");
	}

	private void writeInfo(String key, String value) throws SQLException {
		try (PreparedStatement ps = db.createPreparedStatement("INSERT OR REPLACE INTO userdata.INFO ([IKEY], [IVALUE]) VALUES (?, ?)")) {
			ps.setString(1, key);
			ps.setString(2, value);
			ps.executeUpdate();
		}
	}
}
