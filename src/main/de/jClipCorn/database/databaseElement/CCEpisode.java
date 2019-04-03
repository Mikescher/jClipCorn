package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;

import de.jClipCorn.database.databaseElement.columnTypes.*;
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
	
	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner = owner;
		this.localID = localID;
		
		filesize = CCFileSize.ZERO;
		tags = new CCTagList();
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
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
	
	public void setQuality(int quality) {
		this.quality = CCQuality.getWrapper().find(quality);
		
		if (this.quality == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", quality)); //$NON-NLS-1$
		}
		
		updateDB();
	}
	
	public void setQuality(CCQuality quality) {
		this.quality = quality;
		
		updateDB();
	}
	
	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}
	
	public void setFormat(int format) {
		this.format = CCFileFormat.getWrapper().find(format);
		
		updateDB();
	}
	
	public void setFormat(CCFileFormat format) {
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

	@SuppressWarnings("nls")
	protected void setXMLAttributes(Element e, boolean fileHash) {
		e.setAttribute("localid", localID + "");
		e.setAttribute("title", title);
		e.setAttribute("viewed", viewed + "");
		e.setAttribute("adddate", addDate.toStringSerialize());
		e.setAttribute("episodenumber", episodeNumber + "");
		e.setAttribute("filesize", filesize.getBytes() + "");
		e.setAttribute("format", format.asInt() + "");
		e.setAttribute("history", viewedHistory.toSerializationString());
		e.setAttribute("length", length + "");
		e.setAttribute("part", part);
		e.setAttribute("quality", quality.asInt() + "");
		e.setAttribute("tags", tags.asShort() + "");
		
		if (fileHash) {
			e.setAttribute("filehash", getFastMD5());
		}
	}
	
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetTags) throws CCFormatException {
		beginUpdating();
		
		if (e.getAttributeValue("title") != null)
			setTitle(e.getAttributeValue("title"));
		
		if (e.getAttributeValue("viewed") != null)
			setViewed(e.getAttributeValue("viewed").equals(true + ""));
		
		if (resetViewed)
			setViewed(false);
		
		if (e.getAttributeValue("adddate") != null) {
			setAddDate(CCDate.deserialize(e.getAttributeValue("adddate")));
		}
		
		if (resetAddDate) {
			setAddDate(CCDate.getCurrentDate());
		}
		
		if (e.getAttributeValue("episodenumber") != null)
			setEpisodeNumber(Integer.parseInt(e.getAttributeValue("episodenumber")));
		
		if (e.getAttributeValue("filesize") != null)
			setFilesize(Long.parseLong(e.getAttributeValue("filesize")));
		
		if (e.getAttributeValue("format") != null)
			setFormat(Integer.parseInt(e.getAttributeValue("format")));

		if (e.getAttributeValue("lastviewed") != null) { // backwards compatibility
			CCDate d = CCDate.deserialize(e.getAttributeValue("lastviewed"));
			if (!d.isMinimum())
				setViewedHistory(CCDateTimeList.create(d));
		}		
		
		if (e.getAttributeValue("history") != null)
			setViewedHistory(CCDateTimeList.parse(e.getAttributeValue("history")));
		
		if (e.getAttributeValue("length") != null)
			setLength(Integer.parseInt(e.getAttributeValue("length")));
		
		if (e.getAttributeValue("part") != null)
			setPart(e.getAttributeValue("part"));
		
		if (e.getAttributeValue("quality") != null)
			setQuality(Integer.parseInt(e.getAttributeValue("quality")));
		
		if (e.getAttributeValue("short") != null)
			setTags(Short.parseShort(e.getAttributeValue("short")));

		if (resetTags)
			setTags(new CCTagList());
		
		endUpdating();
	}

	@SuppressWarnings("nls")
	public Element generateXML(Element el, boolean fileHash) {
		Element epis = new Element("episode");
		
		setXMLAttributes(epis, fileHash);
		
		el.addContent(epis);
		
		return epis;
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
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, getViewedHistory());
		else if (viewed && tags.getTag(CCTagList.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, getViewedHistory());
		else if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, getViewedHistory());
		else if (tags.getTag(CCTagList.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, getViewedHistory());
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, getViewedHistory());
	}

	public boolean checkFolderStructure() {
		if (! getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase())) {
			CCLog.addDebug(getAbsolutePart() + " <> " + getRelativeFileForCreatedFolderstructure()); //$NON-NLS-1$
		}
		
		return getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase());
	}
}
