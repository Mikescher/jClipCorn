package de.jClipCorn.gui.guiComponents.tableSorter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;

public class TableIntelliTitleComparator extends TableTitleComparator {
	@Override
	public int compare(CCDatabaseElement ma, CCDatabaseElement mb) {
		String titleA = ma.getTitle();
		String titleB = mb.getTitle();
		
		if (ma.isMovie() && mb.isMovie() && ((CCMovie)ma).getZyklus().isSet() && ((CCMovie)mb).getZyklus().isSet()) {
			if (((CCMovie)ma).getZyklus().getTitle().equals(((CCMovie)mb).getZyklus().getTitle())) { //In the same Zyklus
				return Integer.compare(((CCMovie)ma).getZyklus().getNumber(), ((CCMovie)mb).getZyklus().getNumber());
			}
		}
		
		if (ma.isMovie()) {
			if (((CCMovie)ma).getZyklus().isSet()) {
				titleA = ((CCMovie)ma).getZyklus().getTitle();
			}
		}
		
		if (mb.isMovie()) {
			if (((CCMovie)mb).getZyklus().isSet()) {
				titleB = ((CCMovie)mb).getZyklus().getTitle();
			}
		}
		
		return titleA.compareToIgnoreCase(titleB);
	}
}