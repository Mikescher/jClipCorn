package de.jClipCorn.util.parser;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.util.sqlwrapper.CCSQLColDef;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.sqlwrapper.CCSQLType;
import de.jClipCorn.util.sqlwrapper.SQLBuilderHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.jClipCorn.database.driver.GenericDatabase;
import de.jClipCorn.util.exceptions.XMLFormatException;

/*
 * https://turbine.apache.org/
 * 
 * https://db.apache.org/ddlutils/schema/
 * 
 * https://db.apache.org/ddlutils/
 * 
 * @author Mikescher
 */
@SuppressWarnings("nls")
public class TurbineParser {
	
	private final String source;
	private Document doc;

	public TurbineParser(String xmlSource) {
		source = xmlSource;
	}
	
	public void parse() throws JDOMException, IOException {
		doc = new SAXBuilder().build(new StringReader(source));
	}
	
	public void create(GenericDatabase db) throws SQLException, XMLFormatException {
		Element xdb = doc.getRootElement();

		for (Element table : xdb.getChildren("table")) {
			CreateTable(table, db);
		}
	}
	
	private void CreateTable(Element tab, GenericDatabase db) throws SQLException, XMLFormatException {
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
		
		return "CREATE TABLE " + SQLBuilderHelper.sqlEscape(tableName) + "(" + String.join(",", colSQL) + ")";
	}
	
	private String getSQLCreateColumn(Element col) throws XMLFormatException {
		String columnName = getXMLAttrStr(col, "name");
		String columnType = getXMLAttrStr(col, "type");
		int columnSize = getXMLAttrInt(col, "size", -1);
		boolean columnPrimary = getXMLAttrBool(col, "primaryKey", false);
		boolean columnRequired = getXMLAttrBool(col, "required", false);

		String tabSQL = SQLBuilderHelper.sqlEscape(columnName) + " " + getDatatypeString(columnType, columnSize);
		
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
			
			String sql = "FOREIGN KEY(" + SQLBuilderHelper.sqlEscape(colLocal) + ") REFERENCES " + SQLBuilderHelper.sqlEscape(foreignTableName) + "(" + SQLBuilderHelper.sqlEscape(colForeign) + ")";
			
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

	public List<CCSQLTableDef> convertToTableDefinition() throws XMLFormatException {
		List<CCSQLTableDef> r = new ArrayList<>();

		Element xdb = doc.getRootElement();
		for (Element xtab : xdb.getChildren("table")) {
			String tableName = xtab.getAttributeValue("name");

			List<CCSQLColDef> primary = new ArrayList<>();
			List<CCSQLColDef> columns = new ArrayList<>();

			for (Element xcol : xtab.getChildren("column")) {
				String columnName      = getXMLAttrStr(xcol, "name");
				String columnType      = getXMLAttrStr(xcol, "type");
				//int columnSize       = getXMLAttrInt(xcol, "size", -1);
				boolean columnPrimary  = getXMLAttrBool(xcol, "primaryKey", false);
				boolean columnRequired = getXMLAttrBool(xcol, "required", false);

				CCSQLColDef col = new CCSQLColDef(columnName, convertType(columnType), columnRequired);

				if (columnPrimary) primary.add(col);
				columns.add(col);
			}

			if (primary.size()>1) throw new XMLFormatException("Too many primary columns");
			
			r.add(new CCSQLTableDef(tableName, primary.size()==0 ? null : primary.get(0), columns));
		}

		return r;
	}

	private CCSQLType convertType(String turbineType) throws XMLFormatException {
		turbineType = turbineType.toUpperCase();

		if ("VARCHAR".equals(turbineType))  return CCSQLType.VARCHAR;
		if ("BIT".equals(turbineType))      return CCSQLType.BIT;
		if ("INTEGER".equals(turbineType))  return CCSQLType.INTEGER;
		if ("BIGINT".equals(turbineType))   return CCSQLType.BIGINT;
		if ("DATE".equals(turbineType))     return CCSQLType.DATE;
		if ("TINYINT".equals(turbineType))  return CCSQLType.TINYINT;
		if ("SMALLINT".equals(turbineType)) return CCSQLType.SMALLINT;
		if ("REAL".equals(turbineType))     return CCSQLType.REAL;
		if ("BLOB".equals(turbineType))     return CCSQLType.BLOB;

		throw new XMLFormatException("Unknown type: " + turbineType);
	}
}
