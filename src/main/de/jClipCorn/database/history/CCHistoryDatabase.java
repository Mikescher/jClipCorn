package de.jClipCorn.database.history;

import de.jClipCorn.database.driver.CCDatabase;
import de.jClipCorn.database.migration.HistoryDatabaseMigrator;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.CCTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FileLockManager;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.sqlwrapper.CCSQLKVKey;
import de.jClipCorn.util.sqlwrapper.SQLBuilder;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static de.jClipCorn.database.driver.DatabaseStructure.*;

@SuppressWarnings("nls")
public class CCHistoryDatabase {

	private static final String JDBC_DRIVER = "org.sqlite.JDBC";
	private static final String JDBC_PROTOCOL = "jdbc:sqlite:";
	private static final String HISTORY_DB_FILENAME = "ClipCornHistory.db";

	private final FSPath databaseDirectory; // null for in-memory mode
	private final String databaseName;      // null for in-memory mode
	private final boolean readonly;
	private final boolean inMemory;

	private Connection connection;

	private PreparedStatement insertHistoryStmt;
	private PreparedStatement queryHistoryAllStmt;
	private PreparedStatement queryHistoryFilteredStmt;
	private PreparedStatement queryHistoryLimitedStmt;
	private PreparedStatement queryHistoryFilteredLimitedStmt;
	private PreparedStatement countHistoryStmt;
	private PreparedStatement readInfoStmt;
	private PreparedStatement writeInfoStmt;

	private CCHistoryDatabase(FSPath dbDir, String dbName, boolean readonly, boolean inMemory) {
		this.databaseDirectory = dbDir;
		this.databaseName = dbName;
		this.readonly = readonly;
		this.inMemory = inMemory;
	}

	public static CCHistoryDatabase createFileBased(FSPath dbDir, String dbName, boolean readonly) {
		return new CCHistoryDatabase(dbDir, dbName, readonly, false);
	}

	public static CCHistoryDatabase createInMemory() {
		return new CCHistoryDatabase(null, null, false, true);
	}

	public FSPath getHistoryDbFilePath() {
		if (inMemory) return null;
		return databaseDirectory.append(databaseName, HISTORY_DB_FILENAME);
	}

	/**
	 * Connect to (or create) the history database.
	 * Must be called after the main database is fully connected.
	 */
	public boolean tryconnect(CCDatabase mainDb) {
		try {
			if (inMemory) {
				openInMemoryConnection();
				createSchema();
				writeInitialInfo(mainDb);
			} else {
				FSPath dbFilePath = getHistoryDbFilePath();
				boolean creating = !dbFilePath.exists();

				if (creating) {
					dbFilePath.createFolders();
				}

				openFileConnection(dbFilePath, creating);

				if (creating) {
					createSchema();
					writeInitialInfo(mainDb);
					CCLog.addInformation("History DB created: " + dbFilePath);
				} else {
					new HistoryDatabaseMigrator(connection, readonly).tryUpgrade();
					validateDUUID(mainDb);
					syncInfoFromMainDb(mainDb);
				}
			}

			prepareStatements();

			return true;
		} catch (Exception e) {
			CCLog.addError("Failed to connect to history database", e);
			closeQuietly();
			return false;
		}
	}

	@SuppressWarnings("deprecation")
	private void openFileConnection(FSPath dbFilePath, boolean creating) throws Exception {
		try {
			Class.forName(JDBC_DRIVER).newInstance();
		} catch (Exception e) {
			CCLog.addError(e);
		}

		if (!creating) {
			if (FileLockManager.isLocked(dbFilePath)) {
				throw new Exception("History database file is locked: " + dbFilePath);
			}
		}

		if (!FileLockManager.tryLockFile(dbFilePath, true)) {
			throw new Exception("Cannot lock history database file: " + dbFilePath);
		}

		Properties cfg = new Properties();
		if (readonly) cfg.setProperty("open_mode", "1");

		connection = DriverManager.getConnection(JDBC_PROTOCOL + dbFilePath, cfg);
		connection.setAutoCommit(true);

		executeSQLDirect("PRAGMA recursive_triggers = true");

		if (!creating && !readonly) {
			executeSQLDirect("REPLACE INTO [TEMP] (IKEY,IVALUE) VALUES ('RAND','" + Double.toString(Math.random()).substring(2) + "'),('ACCESS','" + CCDateTime.getCurrentDateTime().toStringISO() + "')");
		}
	}

	@SuppressWarnings("deprecation")
	private void openInMemoryConnection() throws Exception {
		try {
			Class.forName(JDBC_DRIVER).newInstance();
		} catch (Exception e) {
			CCLog.addError(e);
		}

		connection = DriverManager.getConnection(JDBC_PROTOCOL + ":memory:");
		connection.setAutoCommit(true);

		executeSQLDirect("PRAGMA recursive_triggers = true");
	}

	private void createSchema() throws Exception {
		var stmtList = new ArrayList<de.jClipCorn.util.sqlwrapper.CCSQLStatement>();
		for (var tab : new de.jClipCorn.util.sqlwrapper.CCSQLTableDef[]{TAB_HISTORY, TAB_INFO, TAB_TEMP}) {
			var sql = SQLBuilder.createSchema(tab).build((s) -> connection.prepareStatement(s), stmtList);
			sql.execute();
			sql.tryClose();
		}
		for (var s : stmtList) s.tryClose();
	}

	private void writeInitialInfo(CCDatabase mainDb) throws SQLException {
		writeInfoDirect(INFOKEY_DBVERSION, HistoryDatabaseMigrator.HISTORYDB_VERSION);
		writeInfoDirect(INFOKEY_DATE, CCDate.getCurrentDate().toStringSQL());
		writeInfoDirect(INFOKEY_TIME, CCTime.getCurrentTime().toStringSQL());
		writeInfoDirect(INFOKEY_USERNAME, ApplicationHelper.getCurrentUsername());
		writeInfoDirect(INFOKEY_DUUID, mainDb.getInformation_DUUID());
		writeInfoDirect(INFOKEY_HISTORY, mainDb.readInformationFromDB(INFOKEY_HISTORY, "0"));
		writeInfoDirect(INFOKEY_VERSION_MAINDB, mainDb.getInformation_DBVersion());
	}

	private void validateDUUID(CCDatabase mainDb) {
		String mainDuuid = mainDb.getInformation_DUUID();
		String histDuuid = readInfoDirect(INFOKEY_DUUID);
		if (histDuuid != null && !mainDuuid.equals(histDuuid)) {
			CCLog.addError("History DB DUUID mismatch! Main=" + mainDuuid + " History=" + histDuuid);
		}
	}

	private void syncInfoFromMainDb(CCDatabase mainDb) {
		if (readonly) return;
		try {
			writeInfoDirect(INFOKEY_HISTORY, mainDb.readInformationFromDB(INFOKEY_HISTORY, "0"));
			writeInfoDirect(INFOKEY_VERSION_MAINDB, mainDb.getInformation_DBVersion());
		} catch (SQLException e) {
			CCLog.addError("Failed to sync info from main DB to history DB", e);
		}
	}

	private void prepareStatements() throws SQLException {
		insertHistoryStmt = connection.prepareStatement(
				"INSERT INTO [HISTORY] ([TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW]) VALUES (?, ?, ?, ?, ?, ?, ?)");

		queryHistoryAllStmt = connection.prepareStatement(
				"SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] ORDER BY [DATE] DESC");

		queryHistoryFilteredStmt = connection.prepareStatement(
				"SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] WHERE [ID] = ? ORDER BY [DATE] ASC");

		queryHistoryLimitedStmt = connection.prepareStatement(
				"SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] WHERE [DATE] > ? ORDER BY [DATE] DESC");

		queryHistoryFilteredLimitedStmt = connection.prepareStatement(
				"SELECT [TABLE], [ID], [DATE], [ACTION], [FIELD], [OLD], [NEW] FROM [HISTORY] WHERE ([ID] = ?) AND ([DATE] > ?) ORDER BY [DATE] DESC");

		countHistoryStmt = connection.prepareStatement(
				"SELECT COUNT(*) FROM [HISTORY]");

		readInfoStmt = connection.prepareStatement(
				"SELECT [IVALUE] FROM [INFO] WHERE [IKEY] = ?");

		writeInfoStmt = connection.prepareStatement(
				"INSERT OR REPLACE INTO [INFO] ([IKEY], [IVALUE]) VALUES (?, ?)");
	}

	// ==================== Transaction support ====================

	public void beginTransaction() throws SQLException {
		connection.setAutoCommit(false);
	}

	public void commitTransaction() throws SQLException {
		connection.commit();
		connection.setAutoCommit(true);
	}

	public void rollbackTransaction() {
		try {
			connection.rollback();
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			CCLog.addError("Failed to rollback history DB transaction", e);
		}
	}

	// ==================== History insert ====================

	public void insertHistoryRow(String table, String id, String date, String action, String field, Object oldVal, Object newVal) throws SQLException {
		insertHistoryStmt.setString(1, table);
		insertHistoryStmt.setString(2, id);
		insertHistoryStmt.setString(3, date);
		insertHistoryStmt.setString(4, action);
		insertHistoryStmt.setString(5, field);
		insertHistoryStmt.setObject(6, oldVal);
		insertHistoryStmt.setObject(7, newVal);
		insertHistoryStmt.executeUpdate();
	}

	// ==================== History queries ====================

	public List<String[]> queryHistory(CCDateTime start, de.jClipCorn.util.datatypes.Opt<Integer> limit, String idfilter) {
		try {
			ResultSet rs;
			if (idfilter != null) {
				if (start != null) {
					queryHistoryFilteredLimitedStmt.clearParameters();
					queryHistoryFilteredLimitedStmt.setString(1, idfilter);
					queryHistoryFilteredLimitedStmt.setString(2, start.toUTC(java.util.TimeZone.getDefault()).toStringSQL());
					rs = queryHistoryFilteredLimitedStmt.executeQuery();
				} else {
					queryHistoryFilteredStmt.clearParameters();
					queryHistoryFilteredStmt.setString(1, idfilter);
					rs = queryHistoryFilteredStmt.executeQuery();
				}
			} else {
				if (start != null) {
					queryHistoryLimitedStmt.clearParameters();
					queryHistoryLimitedStmt.setString(1, start.toUTC(java.util.TimeZone.getDefault()).toStringSQL());
					rs = queryHistoryLimitedStmt.executeQuery();
				} else {
					queryHistoryAllStmt.clearParameters();
					rs = queryHistoryAllStmt.executeQuery();
				}
			}

			List<String[]> result = new ArrayList<>();
			while (rs.next()) {
				String[] arr = new String[7];
				arr[0] = rs.getString(1); // TABLE
				arr[1] = rs.getString(2); // ID
				arr[2] = rs.getString(3); // DATE
				arr[3] = rs.getString(4); // ACTION
				arr[4] = rs.getString(5); // FIELD
				arr[5] = rs.getString(6); // OLD
				arr[6] = rs.getString(7); // NEW
				result.add(arr);

				if (limit.isPresent() && result.size() >= limit.get()) break;
			}
			rs.close();
			return result;

		} catch (SQLException e) {
			CCLog.addError(e);
			return new ArrayList<>();
		}
	}

	public int getHistoryCount() {
		try {
			ResultSet rs = countHistoryStmt.executeQuery();
			int count = 0;
			if (rs.next()) count = rs.getInt(1);
			rs.close();
			return count;
		} catch (SQLException e) {
			CCLog.addError(e);
			return 0;
		}
	}

	// ==================== Info read/write ====================

	public String readInfo(CCSQLKVKey key) {
		return readInfo(key, null);
	}

	public String readInfo(CCSQLKVKey key, String defaultValue) {
		try {
			readInfoStmt.clearParameters();
			readInfoStmt.setString(1, key.Key);
			ResultSet rs = readInfoStmt.executeQuery();
			String value = rs.next() ? rs.getString(1) : defaultValue;
			rs.close();
			return value;
		} catch (SQLException e) {
			CCLog.addError(e);
			return defaultValue;
		}
	}

	public void writeInfo(CCSQLKVKey key, String value) {
		try {
			writeInfoStmt.clearParameters();
			writeInfoStmt.setString(1, key.Key);
			writeInfoStmt.setString(2, value);
			writeInfoStmt.executeUpdate();
		} catch (SQLException e) {
			CCLog.addError(e);
		}
	}

	// ==================== Internal helpers ====================

	private void writeInfoDirect(CCSQLKVKey key, String value) throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement("INSERT OR REPLACE INTO [INFO] ([IKEY], [IVALUE]) VALUES (?, ?)")) {
			ps.setString(1, key.Key);
			ps.setString(2, value);
			ps.executeUpdate();
		}
	}

	private String readInfoDirect(CCSQLKVKey key) {
		try (PreparedStatement ps = connection.prepareStatement("SELECT [IVALUE] FROM [INFO] WHERE [IKEY] = ?")) {
			ps.setString(1, key.Key);
			ResultSet rs = ps.executeQuery();
			String value = rs.next() ? rs.getString(1) : null;
			rs.close();
			return value;
		} catch (SQLException e) {
			CCLog.addError(e);
			return null;
		}
	}

	private void executeSQLDirect(String sql) throws SQLException {
		try (Statement s = connection.createStatement()) {
			s.execute(sql);
		}
	}

	public boolean isConnected() {
		return connection != null;
	}

	// ==================== Shutdown ====================

	public void disconnect() {
		try {
			if (insertHistoryStmt != null) insertHistoryStmt.close();
			if (queryHistoryAllStmt != null) queryHistoryAllStmt.close();
			if (queryHistoryFilteredStmt != null) queryHistoryFilteredStmt.close();
			if (queryHistoryLimitedStmt != null) queryHistoryLimitedStmt.close();
			if (queryHistoryFilteredLimitedStmt != null) queryHistoryFilteredLimitedStmt.close();
			if (countHistoryStmt != null) countHistoryStmt.close();
			if (readInfoStmt != null) readInfoStmt.close();
			if (writeInfoStmt != null) writeInfoStmt.close();
		} catch (SQLException e) {
			CCLog.addError(e);
		}

		try {
			if (connection != null) connection.close();
			connection = null;
		} catch (SQLException e) {
			CCLog.addError(e);
		}

		if (!inMemory) {
			try {
				FileLockManager.unlockFile(getHistoryDbFilePath());
			} catch (IOException e) {
				CCLog.addError(e);
			}
			CCLog.addInformation("History DB disconnected: " + getHistoryDbFilePath());
		}
	}

	private void closeQuietly() {
		try {
			if (connection != null) connection.close();
			connection = null;
		} catch (SQLException ignored) { /* quiet */ }

		if (!inMemory) {
			try {
				FileLockManager.unlockFile(getHistoryDbFilePath());
			} catch (IOException ignored) { /* quiet */ }
		}
	}
}
