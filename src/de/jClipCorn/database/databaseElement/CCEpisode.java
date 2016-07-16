package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;

import org.jdom2.Element;

import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;

public class CCEpisode {
	private final CCSeason owner;
	private final int localID;
	
	private int episodeNumber;
	private String title;
	private boolean viewed;
	private CCMovieQuality quality;
	private int length;
	private CCMovieTags tags;
	private CCMovieFormat format;
	private CCMovieSize filesize;
	private String part;
	private CCDate addDate;
	private CCDateTimeList viewedHistory;
	
	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner = owner;
		this.localID = localID;
		
		filesize = new CCMovieSize();
		tags = new CCMovieTags();
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

			if (! viewed) {
				fullResetViewedHistory();
			}

			if (viewed && getTag(CCMovieTags.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				setTag(CCMovieTags.TAG_WATCH_LATER, false);
			}

			updateDB();
		}
	}
	
	public void addToViewedHistory(CCDateTime datetime) {
		this.viewedHistory = this.viewedHistory.add(datetime);
		
		updateDB();
	}
	
	public void fullResetViewedHistory() {
		this.viewedHistory = CCDateTimeList.createEmpty();
		
		updateDB();
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
	
	public void setTags(CCMovieTags stat) {
		tags = stat;
		
		updateDB();
	}
	
	public void setTags(short stat) {
		tags.parseFromShort(stat);
		
		updateDB();
	}
	
	public void switchTag(int c) {
		tags.switchTag(c);
		
		updateDB();
	}
	
	public void setTag(int c, boolean v) {
		tags.setTag(c, v);
		
		updateDB();
	}
	
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
		quality = CCMovieQuality.STREAM;
		length = 0;
		format = CCMovieFormat.MKV;
		filesize.setBytes(0);
		tags.clear();
		part = ""; //$NON-NLS-1$
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
		
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
		return viewed;
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
		return PathFormatter.fromCCPath(getPart());
	}

	public CCMovieTags getTags() {
		return tags;
	}

	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
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
		int prop = CCProperties.getInstance().PROP_SERIES_DISPLAYED_DATE.getValue();
		
		if (prop == 0) return getViewedHistoryLast();
		if (prop == 1) return getViewedHistoryFirst();

		return getViewedHistoryAverage();
	}

	public CCDate getAddDate() {
		return addDate;
	}

	public void play(boolean updateEpisodeState) {
		MoviePlayer.play(this);
		
		if (updateEpisodeState) {
			setViewed(true);
			
			addToViewedHistory(CCDateTime.getCurrentDateTime());
		}
	}
	
	/**
	 * @return the Number of the Episode (as it is in the Season-List) (NOT THE ID)
	 */
	public int getEpisodeNumber() {
		return getSeason().findEpisode(this);
	}
	
	public String getStringIdentifier() {
		return String.format("S%02dE%02d", getSeason().getSortedSeasonNumber() + 1, getEpisode()); //$NON-NLS-1$
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
		e.setAttribute("adddate", addDate.getSimpleStringRepresentation());
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
			setAddDate(CCDate.parse(e.getAttributeValue("adddate"), "D.M.Y"));
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
		
		if (e.getAttributeValue("history") != null) {
			setViewedHistory(CCDateTimeList.parse(e.getAttributeValue("history")));
		} else if (e.getAttributeValue("lastviewed") != null) {
			setViewedHistory(CCDateTimeList.create(CCDate.parse(e.getAttributeValue("lastviewed"), "D.M.Y"))); // backwards compatibility
		}
		
		if (e.getAttributeValue("length") != null)
			setLength(Integer.parseInt(e.getAttributeValue("length")));
		
		if (e.getAttributeValue("part") != null)
			setPart(e.getAttributeValue("part"));
		
		if (e.getAttributeValue("quality") != null)
			setQuality(Integer.parseInt(e.getAttributeValue("quality")));
		
		if (e.getAttributeValue("short") != null)
			setTags(Short.parseShort(e.getAttributeValue("short")));

		if (resetTags)
			setTags(new CCMovieTags());
		
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
		if (! parentfolder.isDirectory()) {
			return null; // meehp
		}

		String parent = PathFormatter.appendSeparator(parentfolder.getAbsolutePath());

		String path = parent + getRelativeFileForCreatedFolderstructure();
		
		return new File(path);
	}
	
	@SuppressWarnings("nls")
	private String getRelativeFileForCreatedFolderstructure() {		
		DecimalFormat decFormattter = new DecimalFormat("00");
		
		CCSeason season = this.getSeason();
		CCSeries series = season.getSeries();
		
		String seriesfoldername = series.getFolderNameForCreatedFolderStructure();
		String seasonfoldername = season.getFolderNameForCreatedFolderStructure();
		int seasonIndex = season.getIndexForCreatedFolderStructure();
		
		String filename = String.format("S%sE%s - %s", decFormattter.format(seasonIndex), decFormattter.format(this.getEpisode()), this.getTitle());
		filename += "." + this.getFormat().asString();
		filename = PathFormatter.fixStringToFilesystemname(filename);
		
		String path = PathFormatter.combine(seriesfoldername, seasonfoldername, filename);
		
		return path;
	}

	public ExtendedViewedState getExtendedViewedState() {
		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, getViewedHistory());
		else if (tags.getTag(CCMovieTags.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, getViewedHistory());
		else if (tags.getTag(CCMovieTags.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, getViewedHistory());
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, getViewedHistory());
	}

	public boolean checkFolderStructure() {
		if (! getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase())) {
			System.out.println(getAbsolutePart() + " <> " + getRelativeFileForCreatedFolderstructure()); //$NON-NLS-1$
		}
		
		return getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase());
	}
}
