package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;
import java.text.DecimalFormat;

import org.jdom2.Element;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.CCDate;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.PathFormatter;

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
	private CCDate lastViewed; // NOT SET = 1.1.1900
	
	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner = owner;
		this.localID = localID;
		
		filesize = new CCMovieSize();
		tags = new CCMovieTags();
		addDate = CCDate.getMinimumDate();
		lastViewed = CCDate.getMinimumDate();
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
		this.viewed = v;
		
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

	public void setLastViewed(CCDate date) {
		this.lastViewed = date;
		
		updateDB();
	}
	
	public void resetLastViewed() {
		this.lastViewed = CCDate.getMinimumDate();
		
		updateDB();
	}
	
	public void setLastViewed(Date sqldate) {
		this.lastViewed = CCDate.create(sqldate);
		
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
		lastViewed = CCDate.getMinimumDate();
		
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
		return PathFormatter.getAbsolute(getPart());
	}

	public CCMovieTags getTags() {
		return tags;
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
		
		setLastViewed(CCDate.getCurrentDate());
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

	@SuppressWarnings("nls")
	protected void setXMLAttributes(Element e, boolean fileHash) {
		e.setAttribute("localid", localID + "");
		e.setAttribute("title", title);
		e.setAttribute("viewed", viewed + "");
		e.setAttribute("adddate", addDate.getSimpleStringRepresentation());
		e.setAttribute("episodenumber", episodeNumber + "");
		e.setAttribute("filesize", filesize.getBytes() + "");
		e.setAttribute("format", format.asInt() + "");
		e.setAttribute("lastviewed", lastViewed.getSimpleStringRepresentation());
		e.setAttribute("length", length + "");
		e.setAttribute("part", part);
		e.setAttribute("quality", quality.asInt() + "");
		e.setAttribute("tags", tags.asShort() + "");
		
		if (fileHash) {
			e.setAttribute("filehash", getFastMD5());
		}
	}
	
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed) {
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
		
		if (e.getAttributeValue("lastviewed") != null) {
			setLastViewed(CCDate.parse(e.getAttributeValue("lastviewed"), "D.M.Y"));
		}
		
		if (e.getAttributeValue("length") != null)
			setLength(Integer.parseInt(e.getAttributeValue("length")));
		
		if (e.getAttributeValue("part") != null)
			setPart(e.getAttributeValue("part"));
		
		if (e.getAttributeValue("quality") != null)
			setQuality(Integer.parseInt(e.getAttributeValue("quality")));
		
		if (e.getAttributeValue("short") != null)
			setTags(Short.parseShort(e.getAttributeValue("short")));

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
	
	@SuppressWarnings("nls")
	public File getFileForCreatedFolderstructure(File parentfolder) {
		if (! parentfolder.isDirectory()) {
			return null; // meehp
		}
		
		DecimalFormat decFormattter = new DecimalFormat("00");
		
		CCSeason season = this.getSeason();
		CCSeries series = season.getSeries();
		
		String parent = parentfolder.getAbsolutePath();
		if (! parent.endsWith("\\")) {
			parent += "\\";
		}
		
		String seriesfoldername = series.getTitle();
		if (series.getLanguage() != CCMovieLanguage.GERMAN) {
			seriesfoldername += String.format(" [%s]", series.getLanguage().getShortString());
		}
		seriesfoldername = PathFormatter.fixStringToFilename(seriesfoldername);
		
		String seasonfoldername = season.getTitle();
		seasonfoldername = PathFormatter.fixStringToFilename(seasonfoldername);
		
		String filename = String.format("S%sE%s - %s", decFormattter.format(season.getSeasonNumber() + 1), decFormattter.format(this.getEpisode()), this.getTitle());
		filename += "." + this.getFormat().asString();
		filename = PathFormatter.fixStringToFilename(filename);
		
		String path = parent + seriesfoldername + "\\" + seasonfoldername + "\\" + filename;
		
		return new File(path);
	}
}
