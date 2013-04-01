package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class CompareElement {
	private final String title;
	private final CCMovieZyklus zyklus;
	
	private String cs_File_DB1 = null;
	private String cs_Cover_DB1 = null;
	
	private String cs_File_DB2 = null;
	private String cs_Cover_DB2 = null;
	
	public CompareElement(String title, String zyklus, int zid) {
		this.title = title;
		this.zyklus = new CCMovieZyklus(zyklus, zid);
	}

	public String getCS_File_DB1() {
		return cs_File_DB1;
	}

	public String getCS_Cover_DB1() {
		return cs_Cover_DB1;
	}

	public String getCS_File_DB2() {
		return cs_File_DB2;
	}

	public String getCS_Cover_DB2() {
		return cs_Cover_DB2;
	}

	public void setDB2(String cs_cover, String cs_file) {
		this.cs_Cover_DB2 = cs_cover;
		this.cs_File_DB2 = cs_file;
	}
	
	public void setDB1(String cs_cover, String cs_file) {
		this.cs_Cover_DB1 = cs_cover;
		this.cs_File_DB1 = cs_file;
	}

	public CCMovieZyklus getZyklus() {
		return zyklus;
	}

	public String getTitle() {
		return title;
	}
	
	public boolean isInDB1() {
		return cs_Cover_DB1 != null;
	}
	
	public boolean isInDB2() {
		return cs_Cover_DB2 != null;
	}
	
	public boolean isDifferentCover() {
		return isInDB1() && isInDB2() && cs_Cover_DB1.equals(cs_Cover_DB2);
	}
	
	public boolean isDifferentFiles() {
		return isInDB1() && isInDB2() && cs_File_DB1.equals(cs_File_DB2);
	}
}
