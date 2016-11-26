package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;

public class CompareElement {
	private final String title;
	private final CCMovieZyklus zyklus;
	private final int language;
	
	
	private int csLIDDB1 = -1;
	private String csFileDB1 = null;
	private String csCoverDB1 = null;
	private String filenameDB1 = null;
	
	private int csLIDDB2 = -1;
	private String csFileDB2 = null;
	private String csCoverDB2 = null;
	private String filenameDB2 = null;
	
	public CompareElement(String title, String zyklus, int zid, int lid) {
		this.title = title;
		this.zyklus = new CCMovieZyklus(zyklus, zid);
		this.language = lid;
	}
	
	public int getCSLIDDB1() {
		return csLIDDB1;
	}

	public String getCSFileDB1() {
		return csFileDB1;
	}

	public String getCSCoverDB1() {
		return csCoverDB1;
	}
	
	public String getPathDB1() {
		return filenameDB1;
	}
	
	public int getCSLIDDB2() {
		return csLIDDB2;
	}
	
	public String getCSFileDB2() {
		return csFileDB2;
	}

	public String getCSCoverDB2() {
		return csCoverDB2;
	}
	
	public String getPathDB2() {
		return filenameDB2;
	}
	
	public void setDB1(String csCover, String csFile, String path, int lid) {
		this.csLIDDB1 = lid;
		this.csCoverDB1 = csCover;
		this.csFileDB1 = csFile;
		this.filenameDB1 = path;
	}

	public void setDB2(String csCover, String csFile, String path, int lid) {
		this.csLIDDB2 = lid;
		this.csCoverDB2 = csCover;
		this.csFileDB2 = csFile;
		this.filenameDB2 = path;
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
		return csCoverDB1 != null;
	}
	
	public boolean isInDB2() {
		return csCoverDB2 != null;
	}
	
	public boolean isDifferentCover() {
		return isInDB1() && isInDB2() && !csCoverDB1.equals(csCoverDB2);
	}
	
	public boolean isDifferentFiles() {
		return isInDB1() && isInDB2() && !csFileDB1.equals(csFileDB2);
	}
	
	@Override
	public String toString() {
		if (language != CCDBLanguage.GERMAN.asInt()) {
			return getCompleteTitle() + "[" + CCDBLanguage.getWrapper().find(language).asString() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			return getCompleteTitle();
		}
	}
}
