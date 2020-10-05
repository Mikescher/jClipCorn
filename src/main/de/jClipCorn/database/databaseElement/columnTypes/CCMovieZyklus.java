package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.RomanNumberFormatter;

public class CCMovieZyklus
{
	public static final CCMovieZyklus EMPTY = new CCMovieZyklus();

	private final String title;
	private final int zyklusNmbr;
	
	public CCMovieZyklus()
	{
		this.title = Str.Empty;
		this.zyklusNmbr = -1;
	}
	
	public CCMovieZyklus(String zyklus, int id)
	{
		this.title = zyklus;
		this.zyklusNmbr = id;
	}

	public CCMovieZyklus getWithTitle(String zname) {
		return new CCMovieZyklus(zname, zyklusNmbr);
	}

	public CCMovieZyklus getWithNumber(int zid) {
		return new CCMovieZyklus(title, zid);
	}

	public boolean isSet() {
		return ! isEmpty();
	}
	
	public boolean isEmpty() {
		return title.isEmpty() || zyklusNmbr < 0;
	}

	public boolean hasNumber() {
		return zyklusNmbr > 0;
	}

	public String getTitle() {
		return title;
	}

	public int getNumber() {
		return zyklusNmbr;
	}

	public String getFormatted() {
		if (isSet()) {
			if (hasNumber()) {
				return title + " " + RomanNumberFormatter.decToRom(zyklusNmbr); //$NON-NLS-1$
			} else {
				return title;
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public String getOrderableFormatted() {
		if (isSet()) {
			if (hasNumber()) {
				return title + " " + String.format("%05d", zyklusNmbr); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				return title;
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	
	public String getHTMLFormatted() {
		if (isSet()) {
			if (hasNumber()) {
				return "<html><a href=\"\">" + title + "</a> " + RomanNumberFormatter.decToRom(zyklusNmbr) + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				return "<html><a href=\"\">" + title + "</a></html>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}
	
	public String getSimpleHTMLFormatted() {
		if (isSet()) {
			if (hasNumber()) {
				return "<html><u>" + title + "</u> " + RomanNumberFormatter.decToRom(zyklusNmbr) + "</html>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				return "<html><u>" + title + "</u></html>"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	public String getDecimalFormatted() {
		if (isSet()) {
			if (hasNumber()) {
				return title + " " + zyklusNmbr; //$NON-NLS-1$
			} else {
				return title;
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

	public String getRoman() {
		return RomanNumberFormatter.decToRom(zyklusNmbr);
	}
}
