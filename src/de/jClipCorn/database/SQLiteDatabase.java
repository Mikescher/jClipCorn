package de.jClipCorn.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileAlreadyExistsException;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.exceptions.FileLockedException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.FileLockManager;
import de.jClipCorn.util.helper.TextFileUtils;

@SuppressWarnings("nls")
public class SQLiteDatabase extends GenericDatabase {

	private final static String DRIVER = "org.sqlite.JDBC";
	private final static String PROTOCOL = "jdbc:sqlite:";

	public String getDatabaseFilePath(String dbPath) {
		return PathFormatter.combine(dbPath, dbPath + ".db");
	}
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(dbFilePath);
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			establishDBConnection(dbPath);
			
			createTablesFromXML(TextFileUtils.readUTF8TextFile(xmlPath));
			
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}

	@Override
	public boolean createNewDatabasefromResourceXML(String xmlResPath, String dbPath) {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(dbFilePath);
			if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
			
			PathFormatter.createFolders(getDatabaseFilePath(dbPath));
			
			if (! FileLockManager.tryLockFile(dbFilePath, true)) {
				throw new Exception("Cannot lock databasefile");
			}
			
			connection = DriverManager.getConnection(PROTOCOL + dbFilePath);
			connection.setAutoCommit(true);
			
			createTablesFromXML(TextFileUtils.readTextResource(xmlResPath, getClass()));
		} catch (Exception e) {
			lastError = e;
			return false;
		}
		return true;
	}
	
	@Override
	public boolean databaseExists(String dbPath) {
		return new File(getDatabaseFilePath(dbPath)).exists();
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
		boolean unlockresult;
		try {
			unlockresult = FileLockManager.unlockFile(getDatabaseFilePath(dbPath));
			
			if (! unlockresult) {
				CCLog.addWarning("Cannot unlock database file");
			}
		} catch (IOException e) {
			CCLog.addWarning("Cannot unlock database file", e);
		}
		
        if(connection != null) {
            connection.close();
        }
	}
	
	@Override
	public void establishDBConnection(String dbPath) throws Exception {
		String dbFilePath = getDatabaseFilePath(dbPath);
		
		try {
			Class.forName(DRIVER).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			CCLog.addUndefinied(Thread.currentThread(), e);
		}
		
		if (!databaseExists(dbPath)) throw new FileNotFoundException(dbFilePath);
		if (FileLockManager.isLocked(dbFilePath)) throw new FileLockedException(dbFilePath);
		
		if (!FileLockManager.tryLockFile(dbFilePath, true)) {
			throw new Exception("Cannot lock databasefile");
		}
		
		connection = DriverManager.getConnection(PROTOCOL + dbFilePath);
		
		connection.setAutoCommit(true);
		
		// Test if newly created
		executeSQLThrow("SELECT * FROM " + CCDatabase.TAB_INFO + " LIMIT 1");
		
		// Test if writeable
		executeSQLThrow("REPLACE INTO " + CCDatabase.TAB_INFO + " (" + CCDatabase.TAB_INFO_COLUMN_KEY + ", " + CCDatabase.TAB_INFO_COLUMN_VALUE + ") VALUES ('" + CCDatabase.INFOKEY_RAND + "', '" + Math.random() + "')");
	}

	private void createTablesFromXML(String xmlSource) throws JDOMException, IOException, SQLException {
		Document doc = new SAXBuilder().build(new StringReader(xmlSource));
		
		Element db = doc.getRootElement();
		
		for (Element table : db.getChildren("table")) {
			String tableName = table.getAttributeValue("name");
			
			String tabSQL = "CREATE TABLE " + tableName + "(";
			
			boolean first = true;
			for (Element column : table.getChildren("column")) {
				String columnName = column.getAttributeValue("name");
				String columnType = column.getAttributeValue("type");
				int columnSize = Integer.parseInt(column.getAttributeValue("size", "-1"));
				boolean columnPrimary = column.getAttributeValue("primaryKey", "false").equalsIgnoreCase("true");
				boolean columnRequired = column.getAttributeValue("required", "false").equalsIgnoreCase("true");
				
				if (! first) {
					tabSQL += ", ";
				} else {
					first = false;
				}

				tabSQL += columnName + " " + getDatatype(columnType, columnSize);
				
				if (columnPrimary) {
					tabSQL += " PRIMARY KEY";
				} else if (columnRequired) {
					tabSQL += " NOT NULL";
				}
			}

			tabSQL += ")";
			
			executeSQLThrow(tabSQL);
		}
	}
	
	private String getDatatype(String type, int size) {
		if (size < 0 ) {
			return type;
		} else {
			return type + "(" + size + ")";
		}
	}

	@Override
	public boolean supportsDateType() {
		return false;
	}

	@Override
	public String GetDBTypeName() {
		return LocaleBundle.getString("CCProperties.DatabaseDriver.Opt0");
	}
}