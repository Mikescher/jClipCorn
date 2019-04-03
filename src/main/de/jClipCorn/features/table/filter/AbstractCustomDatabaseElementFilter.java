package de.jClipCorn.features.table.filter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;

public abstract class AbstractCustomDatabaseElementFilter extends AbstractCustomFilter {

	public abstract boolean includes(CCDatabaseElement e);

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof CCDatabaseElement) 
			return includes((CCDatabaseElement)e);
		else if (e instanceof CCSeason) 
			return includes((CCSeason)e);
		else
			return false;
	}
	
	public boolean includes(CCSeason s) { 
		return false; 
	}
}
