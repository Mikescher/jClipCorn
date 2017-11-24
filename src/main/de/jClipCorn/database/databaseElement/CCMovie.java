package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.sql.Date;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieZyklus;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
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

public class CCMovie extends CCDatabaseElement implements ICCPlayableElement, ICCDatedElement {
	public final static int PARTCOUNT_MAX = 6; // 0 - 5
	public final static int NAME_LENGTH_MAX = 128;
	public final static int ZYKLUS_LENGTH_MAX = 128;
	public final static int FILENAME_LENGTH_MAX = 512;
	public final static int PART_LENGTH_MAX = 512;
	
	private boolean viewed;					// BIT
	private CCMovieZyklus zyklus;			// LEN = 128 + INTEGER
	private CCQuality quality;			// TINYINT
	private int length;						// INTEGER - in minutes
	private CCDate addDate;					// DATE
	private CCFileFormat format;			// TINYINT
	private	int year;						// SMALLINT
	private CCFileSize filesize;			// BIGINT - signed (unfortunately)
	private CCTagList tags;				// SMALLINT
	private String[] parts;					// [0..5] -> LEN = 512
	private CCDateTimeList viewedHistory;   // VARCHAR
	
	public CCMovie(CCMovieList ml, int id) {
		super(ml, CCDBElementTyp.MOVIE, id, -1);
		parts = new String[PARTCOUNT_MAX];
		
		zyklus = new CCMovieZyklus();
		filesize = CCFileSize.ZERO;
		tags = new CCTagList();
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
	}
	
	@Override
	public void setDefaultValues(boolean updateDB) {
		super.setDefaultValues(false);
		viewed = false;
		zyklus.reset();
		quality = CCQuality.STREAM;
		length = 0;
		addDate = CCDate.getMinimumDate();
		format = CCFileFormat.MKV;
		year = 1900;
		filesize = CCFileSize.ZERO;
		tags.clear();
		parts[0] = ""; //$NON-NLS-1$
		parts[1] = ""; //$NON-NLS-1$
		parts[2] = ""; //$NON-NLS-1$
		parts[3] = ""; //$NON-NLS-1$
		parts[4] = ""; //$NON-NLS-1$
		parts[5] = ""; //$NON-NLS-1$
		viewedHistory = CCDateTimeList.createEmpty();
		
		
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
	
	public String getOrderableTitle() {
		if (zyklus.isSet()) {
			return zyklus.getOrderableFormatted() + ' ' + '-' + ' ' + getTitle();
		} else {
			return getTitle();
		}
	}

	@Override
	public String getFullDisplayTitle() {
		return getCompleteTitle();
	}

	@Override
	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		if (viewed ^ this.viewed) {
			this.viewed = viewed;
			
			updateDB();
			
			if (!viewed) {
				String.format("Clear ViewedHistory of %s ( %s )", getFullDisplayTitle(), viewedHistory.toSerializationString()); //$NON-NLS-1$
				
				fullResetViewedHistory();
			}
			
			if (viewed && getTag(CCTagList.TAG_WATCH_NEVER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHNEVER.getValue()) {
				setTag(CCTagList.TAG_WATCH_NEVER, false);
			}
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

	@Override
	public CCQuality getQuality() {
		return quality;
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

	public int getPartcount() {
		int pc = 0;
		
		for (int i = 0; i < PARTCOUNT_MAX; i++) {
			pc += parts[i].isEmpty()?0:1; // nett
		}
		
		return pc;
	}

	@Override
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}

	@Override
	public CCDate getAddDate() {
		return addDate;
	}

	public void setAddDate(CCDate date) {
		this.addDate = date;
		
		updateDB();
	}
	
	public void setAddDate(Date sqldate) {
		this.addDate = CCDate.create(sqldate);
		
		updateDB();
	}

	@Override
	public CCFileFormat getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = CCFileFormat.getWrapper().find(format);
		
		updateDB();
	}
	
	public void setFormat(CCFileFormat format) {
		this.format = format;
		
		updateDB();
	}

	@Override
	public int getYear() {
		return year;
	}
	
	@Override
	public int getFirstYear() {
		return getYear();
	}

	public void setYear(int year) {
		this.year = year;
		
		updateDB();
	}
	
	@Override
	public CCFileSize getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = new CCFileSize(filesize);
		
		updateDB();
	}
	
	public void setFilesize(CCFileSize filesize) {
		setFilesize(filesize.getBytes());
	}
	
	@Override
	public CCTagList getTags() {
		return tags;
	}
	
	public void setTags(short ptags) {
		tags.parseFromShort(ptags);
	}
	
	public void setTags(CCTagList ptags) {
		this.tags = ptags;
		
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
	
	@Override
	public boolean getTag(int c) {
		return tags.getTag(c);
	}

	public String getPart(int idx) {
		return parts[idx];
	}
	
	public String getAbsolutePart(int idx) {
		return PathFormatter.fromCCPath(getPart(idx));
	}
	
	public void setPart(int idx, String path) {
		parts[idx] = path;
		
		updateDB();
	}
	
	public void resetPart(int idx) {
		setPart(idx, ""); //$NON-NLS-1$
	}

	public void setViewedHistory(String data) throws CCFormatException {
		viewedHistory = CCDateTimeList.parse(data);
		
		updateDB();
	}

	public void setViewedHistory(CCDateTimeList value) {
		viewedHistory = value;
		
		updateDB();
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
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
		play(true);
	}
	
	@Override
	public void play(boolean updateViewedAndHistory) {
		MoviePlayer.play(this);
		
		if (updateViewedAndHistory) {
			setViewed(true);
			addToViewedHistory(CCDateTime.getCurrentDateTime());
		}
	}
	
	@SuppressWarnings("nls")
	@Override
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash, boolean coverData) {
		super.setXMLAttributes(e, fileHash, coverHash, coverData);
		
		e.setAttribute("adddate", addDate.toStringSerialize());
		e.setAttribute("filesize", filesize.getBytes() + "");
		e.setAttribute("format", format.asInt() + "");
		e.setAttribute("length", length  + "");
		
		for (int i = 0; i < parts.length; i++) {
			e.setAttribute("part_"+i, parts[i]);
		}
		
		e.setAttribute("quality", quality.asInt() + "");
		e.setAttribute("tags", tags.asShort() + "");
		e.setAttribute("viewed", viewed + "");
		e.setAttribute("history", viewedHistory.toSerializationString());
		e.setAttribute("year", year + "");
		e.setAttribute("zyklus", zyklus.getTitle());
		e.setAttribute("zyklusnumber", zyklus.getNumber() + "");
		
		if (fileHash) {
			e.setAttribute("filehash", getFastMD5());
		}
	}
	
	@Override
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags, boolean ignoreCoverData) throws CCFormatException {
		beginUpdating();
		
		super.parseFromXML(e, resetAddDate, resetViewed, resetScore, resetTags, ignoreCoverData);
		
		if (e.getAttributeValue("adddate") != null) {
			setAddDate(CCDate.deserialize(e.getAttributeValue("adddate")));
		}
		
		if (resetAddDate) {
			setAddDate(CCDate.getCurrentDate());
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
		
		if (e.getAttributeValue("tags") != null)
			setTags(Short.parseShort(e.getAttributeValue("tags")));
		
		if (resetTags)
			setTags(new CCTagList());
		
		if (e.getAttributeValue("viewed") != null)
			setViewed(e.getAttributeValue("viewed").equals(true + ""));
		
		if (resetViewed)
			setViewed(false);
		
		if (e.getAttributeValue("year") != null)
			setYear(Integer.parseInt(e.getAttributeValue("year")));
		
		if (e.getAttributeValue("zyklus") != null)
			setZyklusTitle(e.getAttributeValue("zyklus"));
		
		if (e.getAttributeValue("zyklusnumber") != null)
			setZyklusID(Integer.parseInt(e.getAttributeValue("zyklusnumber")));
		
		if (e.getAttributeValue("history") != null)
			setViewedHistory(e.getAttributeValue("history"));
		
		endUpdating();
	}
	
	public String getFastMD5() {
		File[] f = new File[getPartcount()];
		for (int i = 0; i < getPartcount(); i++) {
			f[i] = new File(getAbsolutePart(i));
		}
		return LargeMD5Calculator.getMD5(f);
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
			filename += getTitle().replace(": ", " - ");
		}
		
		for (CCGroup group : getGroups()) {
			if (group.DoSerialize) filename += " [["+group.Name+"]]";
		}
				
		if (getLanguage() != CCDBLanguage.GERMAN) {
			filename += " [" + getLanguage().getShortString() + "]";
		}
		
		if (getPartcount() > 1) {
			filename += " (Part " + (part+1) + ")";
		}
		
		filename += "." + getFormat().asString();
		
		filename = PathFormatter.fixStringToFilesystemname(filename);
		
		return filename;
	}

	public boolean hasZyklus() {
		return getZyklus().isSet();
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
}
