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

import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.TextFileUtils;

@SuppressWarnings("nls")
public class SQLiteDatabase extends GenericDatabase {
	
	private final static String PROTOCOL = "jdbc:sqlite:";
	private final static String DB_FILENAME = "database.db";
	
	@Override
	public boolean createNewDatabase(String xmlPath, String dbPath) {
		try {
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(PathFormatter.combine(dbPath, DB_FILENAME));
			
			PathFormatter.createFolders(PathFormatter.combine(dbPath, DB_FILENAME));
			
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
		try {
			if (databaseExists(dbPath)) throw new FileAlreadyExistsException(PathFormatter.combine(dbPath, DB_FILENAME));
			
			PathFormatter.createFolders(PathFormatter.combine(dbPath, DB_FILENAME));
			
			connection = DriverManager.getConnection(PROTOCOL + PathFormatter.combine(dbPath, DB_FILENAME));
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
		return new File(PathFormatter.combine(dbPath, DB_FILENAME)).exists();
	}
	
	@Override
	public void closeDBConnection(String dbPath, boolean cleanshutdown) throws SQLException {
        if(connection != null) {
            connection.close();
        }
	}
	
	@Override
	public void establishDBConnection(String dbPath) throws Exception {
		if (!databaseExists(dbPath)) throw new FileNotFoundException(PathFormatter.combine(dbPath, DB_FILENAME));
		
		connection = DriverManager.getConnection(PROTOCOL + PathFormatter.combine(dbPath, DB_FILENAME));
		
		connection.setAutoCommit(true);
		
		executeSQLThrow("SELECT * FROM " + CCDatabase.TAB_INFO + " LIMIT 1");
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
}