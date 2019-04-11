package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.exceptions.EnumFormatException;
import org.jdom2.Element;

import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;

public class CCEpisode implements ICCPlayableElement, ICCDatabaseStructureElement {
	private final CCSeason owner;
	private final int localID;
	
	private int episodeNumber;
	private String title;
	private boolean viewed;
	private CCQuality quality;
	private int length;
	private CCTagList tags;
	private CCFileFormat format;
	private CCFileSize filesize;
	private String part;
	private CCDate addDate;
	private CCDateTimeList viewedHistory;
	private CCDBLanguageList language;
	
	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner = owner;
		this.localID = localID;
		
		filesize = CCFileSize.ZERO;
		tags = new CCTagList();
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
		language = CCDBLanguageList.EMPTY;
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

	public void setViewed(boolean viewed) {
		if (viewed ^ this.viewed) {
			this.viewed = viewed;

			if (! viewed) fullResetViewedHistory();

			updateDB();
		}
	}
	
	public void addToViewedHistory(CCDateTime datetime) {
		this.viewedHistory = this.viewedHistory.add(datetime);
		
		if (getTag(CCTagList.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
			setTag(CCTagList.TAG_WATCH_LATER, false);
		}

		updateDB();
	}
	
	public void fullResetViewedHistory() {
		this.viewedHistory = CCDateTimeList.createEmpty();
		
		updateDB();
	}

	public void setQualitySafe(int quality) {
		this.quality = CCQuality.getWrapper().findOrNull(quality);

		if (this.quality == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", quality)); //$NON-NLS-1$
			this.quality = CCQuality.STREAM;
		}

		updateDB();
	}

	public void setQuality(int quality) throws EnumFormatException {
		this.quality = CCQuality.getWrapper().findOrException(quality);

		updateDB();
	}

	public void setQuality(CCQuality quality) {
		if (quality == null) {CCLog.addUndefinied("Prevented setting CCEpisode.Quality to NULL"); return; } //$NON-NLS-1$

		this.quality = quality;

		updateDB();
	}

	@Override
	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}

	public void setFormatSafe(int format) {
		this.format = CCFileFormat.getWrapper().findOrNull(format);

		if (this.format == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", format)); //$NON-NLS-1$
			this.format = CCFileFormat.MKV;
		}

		updateDB();
	}

	public void setFormat(int format) throws EnumFormatException {
		this.format = CCFileFormat.getWrapper().findOrException(format);

		updateDB();
	}

	public void setFormat(CCFileFormat format) {
		if (format == null) {CCLog.addUndefinied("Prevented setting CCEpisode.Format to NULL"); return; } //$NON-NLS-1$

		this.format = format;
		updateDB();
	}

	public void setFilesize(long filesize) {
		this.filesize = new CCFileSize(filesize);
		
		updateDB();
	}
	
	public void setPart(String path) {
		part = path;
		
		updateDB();
	}
	
	public void setAddDate(CCDate date) {
		if (date == null) {CCLog.addUndefinied("Prevented setting CCEpisode.AddDate to NULL"); return; } //$NON-NLS-1$

		this.addDate = date;
		
		updateDB();
	}
	
	public void setAddDate(Date sqldate) {
		this.addDate = CCDate.create(sqldate);
		
		updateDB();
	}

	public void setViewedHistory(CCDateTimeList datelist) {
		this.viewedHistory = datelist;
		
		updateDB();
	}

	public void setViewedHistory(String datelist) throws CCFormatException {
		this.viewedHistory = CCDateTimeList.parse(datelist);
		
		updateDB();
	}
	
	public void setTags(CCTagList stat) {
		tags = stat;
		
		updateDB();
	}
	
	public void setTags(short stat) {
		tags.parseFromShort(stat);
		
		updateDB();
	}

	public void switchTag(CCSingleTag t) {
		tags.switchTag(t);

		updateDB();
	}
	
	public void switchTag(int c) {
		tags.switchTag(c);
		
		updateDB();
	}

	public void setTag(CCSingleTag t, boolean v) {
		tags.setTag(t, v);

		updateDB();
	}
	
	public void setTag(int c, boolean v) {
		tags.setTag(c, v);
		
		updateDB();
	}

	@Override
	public boolean getTag(CCSingleTag t) {
		return tags.getTag(t);
	}

	@Override
	public boolean getTag(int c) {
		return tags.getTag(c);
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(CCDBLanguageList language) {
		this.language = language;

		if (this.language == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", language)); //$NON-NLS-1$
			this.language = CCDBLanguageList.EMPTY;
		}

		updateDB();
	}

	public void setLanguage(long dbdata) {
		this.language = CCDBLanguageList.fromBitmask(dbdata);

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
		viewed = false;
		quality = CCQuality.STREAM;
		length = 0;
		format = CCFileFormat.MKV;
		filesize = CCFileSize.ZERO;
		tags.clear();
		part = ""; //$NON-NLS-1$
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
		language = CCDBLanguageList.EMPTY;
		
		if (updateDB) {
			updateDB();
		}
	}

	@Override
	public String getTitle() {
		return title;
	}

	public int getLocalID() {
		return localID;
	}

	public int getEpisodeNumber() {
		return episodeNumber;
	}

	public int getGlobalEpisodeNumber() {
		int idx = 0;
		
		for (CCSeason season : getSeries().getSeasonsSorted()) {
			for (CCEpisode episode : season.getEpisodeList()) {
				idx++;
				
				if (episode == this) return idx;
			}
		}

		return -999;
	}

	@Override
	public boolean isViewed() {
		return viewed;
	}

	@Override
	public CCQuality getQuality() {
		return quality;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public CCFileFormat getFormat() {
		return format;
	}

	@Override
	public CCFileSize getFilesize() {
		return filesize;
	}

	public String getPart() {
		return part;
	}
	
	public String getAbsolutePart() {
		return PathFormatter.fromCCPath(getPart());
	}

	@Override
	public List<String> getParts() {
		return Arrays.asList(part);
	}

	@Override
	public CCTagList getTags() {
		return tags;
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
	}

	public CCDateTime getViewedHistoryLastDateTime() {
		return viewedHistory.getLastOrInvalid();
	}

	public CCDate getViewedHistoryLast() {
		return viewedHistory.getLastDateOrInvalid();
	}

	public CCDate getViewedHistoryFirst() {
		return viewedHistory.getFirstDateOrInvalid();
	}

	public CCDate getViewedHistoryAverage() {
		return viewedHistory.getAverageDateOrInvalid();
	}
	
	public CCDate getDisplayDate() {
		switch (CCProperties.getInstance().PROP_SERIES_DISPLAYED_DATE.getValue()) {
		case LAST_VIEWED:
			return getViewedHistoryLast();
		case FIRST_VIEWED:
			return getViewedHistoryFirst();
		case AVERAGE:
			return getViewedHistoryAverage();
		default:
			return null;
		}
	}

	@Override
	public CCDate getAddDate() {
		return addDate;
	}

	@Override
	public void play(boolean updateViewedAndHistory) {
		MoviePlayer.play(this);
		
		if (updateViewedAndHistory) {
			setViewed(true);
			
			addToViewedHistory(CCDateTime.getCurrentDateTime());
		}
	}
	
	/**
	 * @return the Number of the Episode (as it is in the Season-List) (NOT THE ID)
	 */
	public int getEpisodeIndexInSeason() {
		return getSeason().findEpisode(this);
	}
	
	public String getStringIdentifier() {
		return String.format("S%02dE%02d", getSeason().getSortedSeasonNumber() + 1, getEpisodeNumber()); //$NON-NLS-1$
	}
	
	public void delete() {
		getSeason().deleteEpisode(this);
	}
	
	@Override
	public String toString() {
		return getTitle();
	}

	public String getFastMD5() {
		File[] f = new File[1];
		f[0] = new File(getAbsolutePart());
		
		return LargeMD5Calculator.getMD5(f);
	}
	
	public File getFileForCreatedFolderstructure(File parentfolder) {
		return getSeason().getFileForCreatedFolderstructure(parentfolder, getTitle(), getEpisodeNumber(), getFormat());
	}
	
	public String getRelativeFileForCreatedFolderstructure() {
		return getSeason().getRelativeFileForCreatedFolderstructure(getTitle(), getEpisodeNumber(), getFormat());
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (!viewed && tags.getTag(CCTagList.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, getViewedHistory(), null);
		else if (viewed && tags.getTag(CCTagList.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, getViewedHistory(), null);
		else if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, getViewedHistory(), null);
		else if (tags.getTag(CCTagList.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, getViewedHistory(), null);
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, getViewedHistory(), null);
	}

	public boolean checkFolderStructure() {
		if (! getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase())) {
			CCLog.addDebug(getAbsolutePart() + " <> " + getRelativeFileForCreatedFolderstructure()); //$NON-NLS-1$
		}
		
		return getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase());
	}
}
