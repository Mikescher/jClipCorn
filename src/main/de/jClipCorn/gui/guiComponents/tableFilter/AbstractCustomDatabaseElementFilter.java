package de.jClipCorn.gui.guiComponents.tableFilter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;

public abstract class AbstractCustomDatabaseElementFilter extends AbstractCustomFilter {

	public abstract boolean includes(CCDatabaseElement e);

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof CCDatabaseElement) 
			return includes((CCDatabaseElement)e);
		else
			return false;
	}
}
