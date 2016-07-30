package de.jClipCorn.util.parser;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.util.exceptions.XMLFormatException;

@SuppressWarnings("nls")
/**
 * https://turbine.apache.org/
 * 
 * https://db.apache.org/ddlutils/schema/
 * 
 * https://db.apache.org/ddlutils/
 * 
 * @author Mikescher
 *
 */
public class TurbineParser {
	
	private final String source;
	private Document doc;
	private GenericDatabase db;
	
	public TurbineParser(String xmlSource, GenericDatabase database) {
		source = xmlSource;
		db = database;
	}
	
	public void parse() throws JDOMException, IOException {
		doc = new SAXBuilder().build(new StringReader(source));
	}
	
	public void create() throws SQLException, XMLFormatException {
		Element db = doc.getRootElement();

		for (Element table : db.getChildren("table")) {
			CreateTable(table);
		}
	}
	
	private void CreateTable(Element tab) throws SQLException, XMLFormatException {
		String tabSQL = getSQLCreateTable(tab);
		
		db.executeSQLThrow(tabSQL);
	}
	
	private String getSQLCreateTable(Element tab) throws XMLFormatException {
		String tableName = tab.getAttributeValue("name");
		
		List<String> colSQL = new ArrayList<>();
		for (Element column : tab.getChildren("column")) {
			colSQL.add(getSQLCreateColumn(column));
		}
		for (Element fkey : tab.getChildren("foreign-key")) {
			colSQL.addAll(getSQLCreateForeignKey(fkey));
		}
		
		return "CREATE TABLE " + tableName + "(" + String.join(",", colSQL) + ")";
	}
	
	private String getSQLCreateColumn(Element col) throws XMLFormatException {
		String columnName = getXMLAttrStr(col, "name");
		String columnType = getXMLAttrStr(col, "type");
		int columnSize = getXMLAttrInt(col, "size", -1);
		boolean columnPrimary = getXMLAttrBool(col, "primaryKey", false);
		boolean columnRequired = getXMLAttrBool(col, "required", false);

		String tabSQL = columnName + " " + getDatatypeString(columnType, columnSize);
		
		if (columnPrimary) {
			tabSQL += " PRIMARY KEY";
		} else if (columnRequired) {
			tabSQL += " NOT NULL";
		}
		
		return tabSQL;
	}
	
	private List<String> getSQLCreateForeignKey(Element fkey) throws XMLFormatException {
		String foreignTableName = getXMLAttrStr(fkey, "foreignTable");
		
		List<String> result = new ArrayList<>();
		for (Element fref : fkey.getChildren("reference")) {
			String colLocal = getXMLAttrStr(fref, "local");
			String colForeign = getXMLAttrStr(fref, "foreign");
			
			String sql = "FOREIGN KEY(" + colLocal + ") REFERENCES " + foreignTableName + "(" + colForeign + ")";
			
			result.add(sql);
		}
		
		return result;
	}
	
	private int getXMLAttrInt(Element e, String attr, int def) throws XMLFormatException {
		String v = e.getAttributeValue(attr, "");
		if (v.isEmpty()) return def;
		
		try {
			return Integer.parseInt(v);
		} catch (NumberFormatException ex) {
			throw new XMLFormatException("Cannot parse integer: " + v);
		}
	}
	
	private String getXMLAttrStr(Element e, String attr) throws XMLFormatException {
		String s = e.getAttributeValue(attr, (String)null);
		
		if (s == null) throw new XMLFormatException("Attribute not found: " + attr);
		
		return s;
	}
	
	private boolean getXMLAttrBool(Element e, String attr, boolean def) {
		String v = e.getAttributeValue(attr, "");
		if (v.isEmpty()) return def;

		if (v.equalsIgnoreCase("true")) return true;
		if (v.equalsIgnoreCase("false")) return false;

		return Boolean.parseBoolean(attr);
	}
	
	private String getDatatypeString(String type, int size) {
		if (size < 0 ) {
			return type;
		} else {
			return type + "(" + size + ")";
		}
	}
}
