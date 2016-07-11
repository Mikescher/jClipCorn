package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.exceptions.OnlineRefFormatException;

public class CCOnlineReference {
	public final CCOnlineRefType type;
	public final String id;
	
	public CCOnlineReference(CCOnlineRefType type, String id) {
		this.type = type;
		this.id = id;
	}

	public CCOnlineReference() {
		this(CCOnlineRefType.NONE, ""); //$NON-NLS-1$
	}

	public String toSerializationString() {
		if (type == CCOnlineRefType.NONE) return ""; //$NON-NLS-1$
		return type.getIdentifier() + ":" + id; //$NON-NLS-1$
	}

	public static CCOnlineReference parse(String data) throws OnlineRefFormatException {
		if (data.isEmpty()) return new CCOnlineReference();
		
		int idx = data.indexOf(':');

		String strtype = data.substring(0, idx);
		String strid = data.substring(idx+1);
		
		CCOnlineRefType type = CCOnlineRefType.parse(strtype);
		
		return new CCOnlineReference(type, strid);
	}
}
