package de.jClipCorn.database.databaseElement;

import java.sql.Date;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieStatus;
import de.jClipCorn.database.databaseElement.columnTypes.CombinedMovieQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.PathFormatter;

public class CCEpisode {
	private final CCSeason owner;
	private final int localID;
	
	private int episodeNumber;
	private String title;
	private boolean isViewed;
	private CCMovieQuality quality;
	private int length;
	private CCMovieStatus status;
	private CCMovieFormat format;
	private CCMovieSize filesize;
	private String part;
	private CCDate addDate;
	private CCDate lastViewed; // NOT SET = 1.1.1900
	
	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner = owner;
		this.localID = localID;
		
		filesize = new CCMovieSize();
	}

	public void beginUpdating() {
		isUpdating = true;
	}
	
	public void endUpdating() {
		isUpdating = false;
		
		updateDB();
	}
	
	public void abortUpdating() {
		isUpdating = false;
	}
	
	private void updateDB() {
		if (! isUpdating) {
			getSeries().getMovieList().update(this);
		}
	}

	public void setEpisodeNumber(int en) {
		this.episodeNumber = en;
		
		updateDB();
	}

	public void setTitle(String t) {
		this.title = t;
		
		updateDB();
	}

	public void setViewed(boolean v) {
		this.isViewed = v;
		
		if (! v) {
			resetLastViewed();
		}
		
		updateDB();
	}
	
	public void setQuality(int quality) {
		this.quality = CCMovieQuality.find(quality);
		
		if (this.quality == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", quality)); //$NON-NLS-1$
		}
		
		updateDB();
	}
	
	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}
	
	public void setFormat(int format) {
		this.format = CCMovieFormat.find(format);
		
		updateDB();
	}
	
	public void setFormat(CCMovieFormat format) {
		this.format = format;
		
		updateDB();
	}
	
	public void setFilesize(long filesize) {
		this.filesize.setBytes(filesize);
		
		updateDB();
	}
	
	public void setPart(String path) {
		part = path;
		
		updateDB();
	}
	
	public void setAddDate(CCDate date) {
		this.addDate = date;
		
		updateDB();
	}
	
	public void setAddDate(Date sqldate) {
		this.addDate = new CCDate(sqldate);
		
		updateDB();
	}

	public void setLastViewed(CCDate date) {
		this.lastViewed = date;
		
		updateDB();
	}
	
	public void resetLastViewed() {
		this.lastViewed = CCDate.getNewMinimumDate();
		
		updateDB();
	}
	
	public void setLastViewed(Date sqldate) {
		this.lastViewed = new CCDate(sqldate);
		
		updateDB();
	}
	
	public void setStatus(CCMovieStatus stat) {
		status = stat;
		
		updateDB();
	}
	
	public void setStatus(int stat) {
		status = CCMovieStatus.find(stat);
		
		updateDB();
	}
	
	public CCSeason getSeason() {
		return owner;
	}
	
	public CCSeries getSeries() {
		return owner.getSeries();
	}

	public void setDefaultValues(boolean updateDB) {
		episodeNumber = 0;
		title = ""; //$NON-NLS-1$
		isViewed = false;
		quality = CCMovieQuality.STREAM;
		length = 0;
		format = CCMovieFormat.MKV;
		filesize.setBytes(0);
		status = CCMovieStatus.STATUS_OK;
		part = ""; //$NON-NLS-1$
		addDate = CCDate.getNewMinimumDate();
		lastViewed = CCDate.getNewMinimumDate();
		
		if (updateDB) {
			updateDB();
		}
	}

	public String getTitle() {
		return title;
	}

	public int getLocalID() {
		return localID;
	}

	public int getEpisode() {
		return episodeNumber;
	}

	public boolean isViewed() {
		return isViewed;
	}

	public CCMovieQuality getQuality() {
		return quality;
	}

	public int getLength() {
		return length;
	}

	public CCMovieFormat getFormat() {
		return format;
	}

	public CCMovieSize getFilesize() {
		return filesize;
	}

	public String getPart() {
		return part;
	}
	
	public String getAbsolutePart() {
		return PathFormatter.getAbsolute(getPart());
	}

	public CCMovieStatus getStatus() {
		return status;
	}

	public CCDate getLastViewed() {
		return lastViewed;
	}

	public CCDate getAddDate() {
		return addDate;
	}

	public void play() {
		MoviePlayer.play(this);
		
		setViewed(true);
		
		setLastViewed(new CCDate());
	}

	public CombinedMovieQuality getCombinedQuality() {
		return new CombinedMovieQuality(getQuality(), getStatus());
	}
	
	/**
	 * @return the Number of the Season (as it is in the Series-List) (NOT THE ID)
	 */
	public int getEpisodeNumber() {
		return getSeason().findEpisode(this);
	}
	
	public void delete() {
		getSeason().deleteEpisode(this);
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}
