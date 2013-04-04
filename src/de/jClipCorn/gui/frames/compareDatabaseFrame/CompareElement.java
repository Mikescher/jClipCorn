package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class CompareElement {
	private final String title;
	private final CCMovieZyklus zyklus;
	private final int language;
	
	
	private String cs_File_DB1 = null;
	private String cs_Cover_DB1 = null;
	private String filename_DB1 = null;
	
	private String cs_File_DB2 = null;
	private String cs_Cover_DB2 = null;
	private String filename_DB2 = null;
	
	public CompareElement(String title, String zyklus, int zid, int lid) {
		this.title = title;
		this.zyklus = new CCMovieZyklus(zyklus, zid);
		this.language = lid;
	}

	public String getCS_File_DB1() {
		return cs_File_DB1;
	}

	public String getCS_Cover_DB1() {
		return cs_Cover_DB1;
	}
	
	public String getPath_DB1() {
		return filename_DB1;
	}
	
	public String getCS_File_DB2() {
		return cs_File_DB2;
	}

	public String getCS_Cover_DB2() {
		return cs_Cover_DB2;
	}
	
	public String getPath_DB2() {
		return filename_DB2;
	}
	
	public void setDB1(String cs_cover, String cs_file, String path) {
		this.cs_Cover_DB1 = cs_cover;
		this.cs_File_DB1 = cs_file;
		this.filename_DB1 = path;
	}

	public void setDB2(String cs_cover, String cs_file, String path) {
		this.cs_Cover_DB2 = cs_cover;
		this.cs_File_DB2 = cs_file;
		this.filename_DB2 = path;
	}

	public CCMovieZyklus getZyklus() {
		return zyklus;
	}
	
	public int getLanguage() {
		return language;
	}

	public String getTitle() {
		return title;
	}
	
	public String getCompleteTitle() {
		if (zyklus.isSet()) {
			return zyklus.getFormatted() + ' ' + '-' + ' ' + getTitle();
		} else {
			return getTitle();
		}
	}
	
	public boolean isInDB1() {
		return cs_Cover_DB1 != null;
	}
	
	public boolean isInDB2() {
		return cs_Cover_DB2 != null;
	}
	
	public boolean isDifferentCover() {
		return isInDB1() && isInDB2() && !cs_Cover_DB1.equals(cs_Cover_DB2);
	}
	
	public boolean isDifferentFiles() {
		return isInDB1() && isInDB2() && !cs_File_DB1.equals(cs_File_DB2);
	}
	
	@Override
	public String toString() {
		if (language != CCMovieLanguage.GERMAN.asInt()) {
			return getCompleteTitle() + "[" + CCMovieLanguage.find(language).asString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return getCompleteTitle();
		}
	}
}
