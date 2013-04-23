package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.PathFormatter;

public class CCMovie extends CCDatabaseElement {
	public final static int PARTCOUNT_MAX = 6; // 0 - 5
	public final static int NAME_LENGTH_MAX = 128;
	public final static int ZYKLUS_LENGTH_MAX = 128;
	public final static int FILENAME_LENGTH_MAX = 512;
	public final static int PART_LENGTH_MAX = 512;
	
	private boolean viewed;					// BIT
	private CCMovieZyklus zyklus;			// LEN = 128 + INTEGER
	private CCMovieQuality quality;			// TINYINT
	private int length;						// INTEGER - in minutes
	private CCDate addDate;					// DATE
	private CCMovieFormat format;			// TINYINT
	private	int year;						// SMALLINT
	private CCMovieSize filesize;			// BIGINT - signed (unfortunately)
	private CCMovieStatus status;			// TINYINT
	private String[] parts;					// [0..5] -> LEN = 512
	
	public CCMovie(CCMovieList ml, int id) {
		super(ml, CCMovieTyp.MOVIE, id, -1);
		parts = new String[PARTCOUNT_MAX];
		
		zyklus = new CCMovieZyklus();
		filesize = new CCMovieSize();
	}
	
	@Override
	public void setDefaultValues(boolean updateDB) {
		super.setDefaultValues(false);
		viewed = false;
		zyklus.reset();
		quality = CCMovieQuality.STREAM;
		length = 0;
		addDate = new CCDate(1, 1, CCDate.YEAR_MIN);
		format = CCMovieFormat.MKV;
		year = 1900;
		filesize.setBytes(0);
		status = CCMovieStatus.STATUS_OK;
		parts[0] = ""; //$NON-NLS-1$
		parts[1] = ""; //$NON-NLS-1$
		parts[2] = ""; //$NON-NLS-1$
		parts[3] = ""; //$NON-NLS-1$
		parts[4] = ""; //$NON-NLS-1$
		parts[5] = ""; //$NON-NLS-1$
		
		
		if (updateDB) {
			updateDB();
		}
	}
	
	@Override
	protected void updateDB() {
		if (! isUpdating) {
			movielist.update(this);
		}
	}
	
	//------------------------------------------------------------------------\
	//						   GETTER  /  SETTER					   		  |
	//------------------------------------------------------------------------/
	
	public String getCompleteTitle() {
		if (zyklus.isSet()) {
			return zyklus.getFormatted() + ' ' + '-' + ' ' + getTitle();
		} else {
			return getTitle();
		}
	}

	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		if (viewed ^ this.viewed) {
			this.viewed = viewed;
			
			updateDB();
		}
	}

	public CCMovieZyklus getZyklus() {
		return zyklus;
	}

	public void setZyklusTitle(String zyklus) {
		this.zyklus.setTitle(zyklus);
		
		updateDB();
	}
	
	public void setZyklusID(int zid) {
		this.zyklus.setNumber(zid);
		
		updateDB();
	}

	public CCMovieQuality getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = CCMovieQuality.find(quality);
		
		if (this.quality == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", quality)); //$NON-NLS-1$
		}
		
		updateDB();
	}
	
	public void setQuality(CCMovieQuality quality) {
		this.quality = quality;
		
		updateDB();
	}

	public int getPartcount() {
		int pc = 0;
		
		for (int i = 0; i < PARTCOUNT_MAX; i++) {
			pc += parts[i].isEmpty()?0:1; // nett
		}
		
		return pc;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}

	public CCDate getAddDate() {
		return addDate;
	}

	public void setAddDate(CCDate date) {
		this.addDate = date;
		
		updateDB();
	}
	
	public void setAddDate(Date sqldate) {
		this.addDate = new CCDate(sqldate);
		
		updateDB();
	}

	public CCMovieFormat getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = CCMovieFormat.find(format);
		
		updateDB();
	}
	
	public void setFormat(CCMovieFormat format) {
		this.format = format;
		
		updateDB();
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
		
		updateDB();
	}
	
	public CCMovieSize getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize.setBytes(filesize);
		
		updateDB();
	}
	
	public void setFilesize(CCMovieSize filesize) {
		setFilesize(filesize.getBytes());
	}
	
	public CCMovieStatus getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		setStatus(CCMovieStatus.find(status));
	}
	
	public void setStatus(CCMovieStatus status) {
		this.status = status;
		
		updateDB();
	}

	public String getPart(int idx) {
		return parts[idx];
	}
	
	public String getAbsolutePart(int idx) {
		return PathFormatter.getAbsolute(getPart(idx));
	}
	
	public void setPart(int idx, String path) {
		parts[idx] = path;
		
		updateDB();
	}
	
	public void resetPart(int idx) {
		setPart(idx, ""); //$NON-NLS-1$
	}

	public boolean hasHoleInParts() {
		boolean ret = false;
		
		for (int i = 1; i < PARTCOUNT_MAX; i++) {
			ret |= parts[i-1].isEmpty() && ! parts[i].isEmpty();
		}
		
		return ret;
	}
	
	public void delete() {
		movielist.remove(this);
	}
	
	public void play() {
		MoviePlayer.play(this);
		
		setViewed(true);
	}

	public CombinedMovieQuality getCombinedQuality() {
		return new CombinedMovieQuality(getQuality(), getStatus());
	}

	public CCMovieList getMovieList() {
		return movielist;
	}
	
	@SuppressWarnings("nls")
	@Override
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash) {
		super.setXMLAttributes(e, fileHash, coverHash);
		
		e.setAttribute("adddate", addDate.getSimpleStringRepresentation() + "");
		e.setAttribute("filesize", filesize.getBytes() + "");
		e.setAttribute("format", format.asInt() + "");
		e.setAttribute("length", length  + "");
		
		for (int i = 0; i < parts.length; i++) {
			e.setAttribute("part_"+i, parts[i]);
		}
		
		e.setAttribute("quality", quality.asInt() + "");
		e.setAttribute("status", status.asInt() + "");
		e.setAttribute("viewed", viewed + "");
		e.setAttribute("year", year + "");
		e.setAttribute("zyklus", zyklus.getTitle());
		e.setAttribute("zyklusnumber", zyklus.getNumber() + "");
		
		if (fileHash) {
			e.setAttribute("filehash", getFastMD5());
		}
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
	}
	
	@Override
	@SuppressWarnings("nls")
	public void parseFromXML(Element e) {
		beginUpdating();
		
		super.parseFromXML(e);
		
		if (e.getAttributeValue("adddate") != null) {
			CCDate d = new CCDate();
			d.parse(e.getAttributeValue("adddate"), "D.M.Y");
			setAddDate(d);
		}
		
		if (e.getAttributeValue("filesize") != null)
			setFilesize(Long.parseLong(e.getAttributeValue("filesize")));
		
		if (e.getAttributeValue("format") != null)
			setFormat(Integer.parseInt(e.getAttributeValue("format")));
		
		if (e.getAttributeValue("length") != null)
			setLength(Integer.parseInt(e.getAttributeValue("length")));
		
		for (int i = 0; i < PARTCOUNT_MAX; i++) {
			if (e.getAttributeValue("part_"+i) != null)
				setPart(i, e.getAttributeValue("part_"+i));
		}
		
		if (e.getAttributeValue("quality") != null)
			setQuality(Integer.parseInt(e.getAttributeValue("quality")));
		
		if (e.getAttributeValue("status") != null)
			setStatus(Integer.parseInt(e.getAttributeValue("status")));
		
		if (e.getAttributeValue("viewed") != null)
			setViewed(e.getAttributeValue("viewed").equals(true + ""));
		
		if (e.getAttributeValue("year") != null)
			setYear(Integer.parseInt(e.getAttributeValue("year")));
		
		if (e.getAttributeValue("zyklus") != null)
			setZyklusTitle(e.getAttributeValue("zyklus"));
		
		if (e.getAttributeValue("zyklusnumber") != null)
			setZyklusID(Integer.parseInt(e.getAttributeValue("zyklusnumber")));
		
		endUpdating();
	}
	
	public String getFastMD5() {
		File[] f = new File[getPartcount()];
		for (int i = 0; i < getPartcount(); i++) {
			f[i] = new File(getAbsolutePart(i));
		}
		return LargeMD5Calculator.getMD5(f);
	}
	
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}
	
	@Override
	public String toString() {
		return getCompleteTitle();
	}
	
	@SuppressWarnings("nls")
	public String generateFilename(int part) { //Test if database has no errors
		String filename = "";
		
		if (getZyklus().isSet()) {
			filename += getZyklus().getFormatted() + " - ";
			
			filename += getTitle().replace(": ", " - ");
		} else {
			filename += getTitle().replace(": ", " ");
		}
				
		if (getLanguage() != CCMovieLanguage.GERMAN) {
			filename += " [" + getLanguage().asString() + "]";
		}
		
		if (getPartcount() > 1) {
			filename += " (Part " + (part+1) + ")";
		}
		
		filename += "." + getFormat().asString();
		
		filename = PathFormatter.fixStringToFilename(filename);
		
		return filename;
	}
}
