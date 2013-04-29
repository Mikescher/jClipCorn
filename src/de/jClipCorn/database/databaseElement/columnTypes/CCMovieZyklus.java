package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.RomanNumberFormatter;

public class CCMovieZyklus {
	private String title;
	private int zyklusNmbr;
	
	public CCMovieZyklus() {
		this.title = ""; //$NON-NLS-1$
		this.zyklusNmbr = -1;
	}
	
	public CCMovieZyklus(String zyklus, int id) {
		this.title = zyklus;
		this.zyklusNmbr = id;
	}

	public void setTitle(String nwzyklus) { // ONLY CALL FROM CCMOVIELIST !!!
		this.title = nwzyklus;
	}

	public void setNumber(int zid) { // ONLY CALL FROM CCMOVIELIST !!!
		this.zyklusNmbr = zid;
	}

	public boolean isSet() {
		return ! (title.isEmpty() || zyklusNmbr < 0);
	}

	public String getTitle() {
		return title;
	}

	public int getNumber() {
		return zyklusNmbr;
	}

	public String getFormatted() {
		if (isSet()) {
			if (zyklusNmbr != 0) {
				return title + " " + RomanNumberFormatter.decToRom(zyklusNmbr); //$NON-NLS-1$
			} else {
				return title;
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	
	public String getHTMLFormatted() {
		if (isSet()) {
			if (zyklusNmbr != 0) {
				return "<html><a href=\"\">" + title + "</a> " + RomanNumberFormatter.decToRom(zyklusNmbr) + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				return "<html><a href=\"\">" + title + "</a></html>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public static int compare(CCMovieZyklus o1, CCMovieZyklus o2) {
		int rv = o1.getTitle().compareToIgnoreCase(o2.getTitle());
		
		if (rv == 0) {
			rv = Integer.compare(o1.getNumber(), o2.getNumber());
		}
		
		return rv;
	}

	public void reset() {
		setNumber(-1);
		setTitle(""); //$NON-NLS-1$
	}
	
	@Override
	public boolean equals(Object z) {
		if (z instanceof CCMovieZyklus) {
			return ((CCMovieZyklus)z).getNumber() == getNumber() && ((CCMovieZyklus)z).getTitle().equals(getTitle());
		} else {
			return false;
		}
	}
	
	@Override 
	public int hashCode() {
		return title.hashCode() * (zyklusNmbr + 2);
	}
}
