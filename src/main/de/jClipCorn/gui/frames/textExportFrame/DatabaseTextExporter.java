package de.jClipCorn.gui.frames.textExportFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;

public abstract class DatabaseTextExporter {
	protected CCMovieList movielist;
	
	protected boolean includeSeries;
	
	protected boolean addViewed;
	protected boolean addLanguage;
	protected boolean addFormat;
	protected boolean addQuality;
	protected boolean addYear;
	protected boolean addSize;
	
	protected TextExportOrder order;

	public String generate(CCMovieList _movielist, TextExportOrder _order, boolean _includeSeries, boolean _addLanguage, boolean _addYear, boolean _addFormat, boolean _addQuality, boolean _addSize, boolean _addViewed) {
		movielist = _movielist;
		
		includeSeries = _includeSeries;
		order = _order;
		addLanguage = _addLanguage;
		addYear = _addYear;
		addFormat = _addFormat;
		addQuality = _addQuality;
		addSize = _addSize;
		addViewed = _addViewed;
		
		return create();
	}
	
	@Override
	public String toString() {
		return getFormatName();
	}
	
	public List<CCDatabaseElement> getOrderedList() {
		List<CCDatabaseElement> result = new ArrayList<>();
		
		for (CCDatabaseElement elem : movielist.iterator()) {
			if (! includeSeries && elem.isSeries())
				continue;
			
			result.add(elem);
		}
		
		Comparator<CCDatabaseElement> cp;
		
		switch (order) {
		case TITLE:
			cp = new Comparator<CCDatabaseElement>() {
				@Override
				public int compare(CCDatabaseElement o1, CCDatabaseElement o2) {
					return o1.getTitle().compareToIgnoreCase(o2.getTitle());
				}
			};
			break;
		case TITLE_SMART:
			cp = new Comparator<CCDatabaseElement>() {
				@Override
				public int compare(CCDatabaseElement o1, CCDatabaseElement o2) {
					String to1;
					String to2;
					
					if (o1.isMovie())
						to1 = ((CCMovie)o1).getOrderableTitle();
					else if (o1.isSeries())
						to1 = o1.getTitle();
					else
						to1 = null;
					
					if (o2.isMovie())
						to2 = ((CCMovie)o2).getOrderableTitle();
					else if (o2.isSeries())
						to2 = o2.getTitle();
					else
						to2 = null;
					
					return to1.compareToIgnoreCase(to2);
				}
			};
			break;
		case ADD_DATE:
			cp = new Comparator<CCDatabaseElement>() {
				@Override
				public int compare(CCDatabaseElement o1, CCDatabaseElement o2) {
					return o1.getAddDate().compare(o2.getAddDate());
				}
			};
			break;
		case YEAR:
			cp = new Comparator<CCDatabaseElement>() {
				@Override
				public int compare(CCDatabaseElement o1, CCDatabaseElement o2) {
					return Integer.compare(o1.getFirstYear(), o2.getFirstYear());
				}
			};
			break;
		default:
			cp = null;
			break;
		}
		
		Collections.sort(result, cp);
		
		return result;
	}
	
	public String simpleEscape(String txt) {
		return txt.replace("\\", "\\\\").replace("\"", "\\\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	protected abstract String create();
	protected abstract String getFormatName();
	protected abstract String getFileExtension();
}