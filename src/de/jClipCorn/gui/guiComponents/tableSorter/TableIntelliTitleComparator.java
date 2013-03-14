package de.jClipCorn.gui.guiComponents.tableSorter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;

public class TableIntelliTitleComparator extends TableTitleComparator {
	@Override
	public int compare(CCDatabaseElement arg0, CCDatabaseElement arg1) {
		String a = arg0.getTitle();
		String b = arg1.getTitle();
		
		if (arg0.isMovie()) {
			if (((CCMovie)arg0).getZyklus().isSet()) {
				a = ((CCMovie)arg0).getZyklus().getTitle() + ((CCMovie)arg0).getZyklus().getNumber();
			}
		}
		
		if (arg1.isMovie()) {
			if (((CCMovie)arg1).getZyklus().isSet()) {
				b = ((CCMovie)arg1).getZyklus().getTitle() + ((CCMovie)arg1).getZyklus().getNumber();
			}
		}
		
		return a.compareToIgnoreCase(b);
	}
}