package de.jClipCorn.features.serialization.xmlimport.impl;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.util.exceptions.CCFormatException;

/**
 * V12 writes the viewed history as the JSON array of the database column, so it carries the comments.
 */
public class DatabaseXMLImportImpl_V12 extends DatabaseXMLImportImpl_V11 {
	@Override
	protected CCDateTimeList parseHistory(String v) throws CCFormatException {
		return CCDateTimeList.fromJSONArray(v);
	}
}
