package de.jClipCorn.database.databaseElement;

import java.sql.Date;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.CCDate;
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
	private CCDate date;					// DATE
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
		date = new CCDate(1, 1, CCDate.YEAR_MIN);
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

	public CCDate getDate() {
		return date;
	}

	public void setDate(CCDate date) {
		this.date = date;
		
		updateDB();
	}
	
	public void setDate(Date sqldate) {
		this.date = new CCDate(sqldate);
		
		updateDB();
	}

	public CCMovieFormat getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = CCMovieFormat.find(format);
		
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
}
