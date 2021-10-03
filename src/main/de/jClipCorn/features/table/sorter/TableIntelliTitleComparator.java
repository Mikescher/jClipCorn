package de.jClipCorn.features.table.sorter;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;

public class TableIntelliTitleComparator extends TableTitleComparator {
	@Override
	public int compare(CCDatabaseElement ma, CCDatabaseElement mb) {
		String titleA = ma.getTitle();
		String titleB = mb.getTitle();
		
		if (ma.isMovie() && mb.isMovie() && ma.asMovie().getZyklus().isSet() && mb.asMovie().getZyklus().isSet()) {
			if (ma.asMovie().getZyklus().getTitle().equals(mb.asMovie().getZyklus().getTitle())) { //In the same Zyklus
				return Integer.compare(ma.asMovie().getZyklus().getNumber(), mb.asMovie().getZyklus().getNumber());
			}
		}
		
		if (ma.isMovie()) {
			if (ma.asMovie().getZyklus().isSet()) {
				titleA = ma.asMovie().getZyklus().getTitle();
			}
		}
		
		if (mb.isMovie()) {
			if (mb.asMovie().getZyklus().isSet()) {
				titleB = mb.asMovie().getZyklus().getTitle();
			}
		}
		
		return titleA.compareToIgnoreCase(titleB);
	}
}