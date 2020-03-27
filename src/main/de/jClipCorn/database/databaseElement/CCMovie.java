package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CCMovie extends CCDatabaseElement implements ICCPlayableElement, ICCDatedElement {
	public final static int PARTCOUNT_MAX = 6; // 0 - 5

	private CCMovieZyklus zyklus;
	private CCMediaInfo mediainfo;
	private int length; // in minutes
	private CCDate addDate;
	private CCFileFormat format;
	private	int year;
	private CCFileSize filesize;
	private String[] parts;
	private CCDateTimeList viewedHistory;
	private CCDBLanguageList language;
	
	public CCMovie(CCMovieList ml, int id) {
		super(ml, CCDBElementTyp.MOVIE, id);
		parts = new String[PARTCOUNT_MAX];

		zyklus = new CCMovieZyklus();
		filesize = CCFileSize.ZERO;
		addDate = CCDate.getMinimumDate();
		viewedHistory = CCDateTimeList.createEmpty();
		language = CCDBLanguageList.EMPTY;
	}
	
	@Override
	public void setDefaultValues(boolean updateDB) {
		super.setDefaultValues(false);
		zyklus.reset();
		mediainfo = CCMediaInfo.EMPTY;
		length = 0;
		addDate = CCDate.getMinimumDate();
		format = CCFileFormat.MKV;
		year = 1900;
		filesize = CCFileSize.ZERO;
		parts[0] = ""; //$NON-NLS-1$
		parts[1] = ""; //$NON-NLS-1$
		parts[2] = ""; //$NON-NLS-1$
		parts[3] = ""; //$NON-NLS-1$
		parts[4] = ""; //$NON-NLS-1$
		parts[5] = ""; //$NON-NLS-1$
		viewedHistory = CCDateTimeList.createEmpty();
		language = CCDBLanguageList.EMPTY;

		if (updateDB) updateDB();
	}

	@Override
	protected boolean updateDB() {
		if (! isUpdating) {
			return movielist.update(this);
		}
		return true;
	}

	private void updateDBWithException() throws DatabaseUpdateException {
		var ok = updateDB();
		if (!ok) throw new DatabaseUpdateException("updateDB() failed"); //$NON-NLS-1$
	}
	
	//------------------------------------------------------------------------\
	//						   GETTER  /  SETTER					   		  |
	//------------------------------------------------------------------------/

	@Override
	public String getQualifiedTitle() {
		return getCompleteTitle();
	}

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
		return viewedHistory.any();
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

	@Override
	public void setLength(int length) {
		this.length = length;
		
		updateDB();
	}

	@Override
	public CCDate getAddDate() {
		return addDate;
	}

	public void setAddDate(CCDate date) {
		if (date == null) {CCLog.addUndefinied("Prevented setting CCMovie.AddDate to NULL"); return; } //$NON-NLS-1$

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
		if (format == null) {CCLog.addUndefinied("Prevented setting CCMovie.Format to NULL"); return; } //$NON-NLS-1$

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

	@Override
	public void setFilesize(long filesize) {
		this.filesize = new CCFileSize(filesize);
		
		updateDB();
	}
	
	public void setFilesize(CCFileSize filesize) {
		setFilesize(filesize.getBytes());
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(CCDBLanguageList language) {
		if (language == null) {CCLog.addUndefinied("Prevented setting CCMovie.Language to NULL"); return; } //$NON-NLS-1$

		this.language = language;

		updateDB();
	}

	public void setLanguage(long dbdata) {
		this.language = CCDBLanguageList.fromBitmask(dbdata);

		updateDB();
	}

	public String getPart(int idx) {
		return parts[idx];
	}

	@Override
	public List<String> getParts() {
		List<String> r = new ArrayList<>();
		for (int i = 0; i < PARTCOUNT_MAX; i++) if (!parts[i].isEmpty()) r.add(parts[i]);
		return r;
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
		setViewedHistory(CCDateTimeList.parse(data));
	}

	public void setViewedHistory(CCDateTimeList value) {
		if (value == null) { CCLog.addUndefinied("Prevented setting CCMovie.ViewedHistory to NULL"); return; } //$NON-NLS-1$

		viewedHistory = value;

		updateDB();
	}

	public void setViewedHistoryFromUI(CCDateTimeList value) {
		if (value == null) { CCLog.addUndefinied("Prevented setting CCMovie.ViewedHistory to NULL"); return; } //$NON-NLS-1$

		try
		{
			viewedHistory = value;
			updateDBWithException();
		}
		catch (Throwable e1)
		{
			DialogHelper.showLocalError(MainFrame.getInstance(), "Dialogs.UpdateViewedFailed"); //$NON-NLS-1$
			CCLog.addError(e1);
		}
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
	}
	
	public void addToViewedHistory(CCDateTime datetime) {
		this.viewedHistory = this.viewedHistory.add(datetime);

		updateDB();
	}

	public void addToViewedHistoryFromUI(CCDateTime datetime) {
		try
		{
			this.viewedHistory = this.viewedHistory.add(datetime);

			if (getTag(CCTagList.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				setTag(CCTagList.TAG_WATCH_LATER, false);
			}

			updateDBWithException();
		}
		catch (Throwable e1)
		{
			DialogHelper.showLocalError(MainFrame.getInstance(), "Dialogs.UpdateViewedFailed"); //$NON-NLS-1$
			CCLog.addError(e1);
		}
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

	@Override
	public void play(boolean updateViewedAndHistory) {
		MoviePlayer.play(this);
		
		if (updateViewedAndHistory) updateViewedAndHistoryFromUI();
	}

	@Override
	public void updateViewedAndHistoryFromUI() {
		addToViewedHistoryFromUI(CCDateTime.getCurrentDateTime());
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
		StringBuilder filename = new StringBuilder();
		
		if (getZyklus().isSet()) {
			filename.append(getZyklus().getFormatted()).append(" - ");
			
			filename.append(Str.limit(getTitle().replace(": ", " - "), 128));
		} else {
			filename.append(getTitle().replace(": ", " - "));
		}
		
		for (CCGroup group : getGroups()) {
			if (group.DoSerialize) filename.append(" [[").append(group.Name).append("]]");
		}
				
		if (!getLanguage().isExact(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue())) {
			filename.append(" [").append(getLanguage().serializeToFilenameString()).append("]");
		}
		
		if (getPartcount() > 1) {
			filename.append(" (Part ").append(part + 1).append(")");
		}
		
		filename.append(".").append(getFormat().asString());
		
		filename = new StringBuilder(PathFormatter.fixStringToFilesystemname(filename.toString()));
		
		return filename.toString();
	}

	public boolean hasZyklus() {
		return getZyklus().isSet();
	}
	
	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (!isViewed() && getTag(CCTagList.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, getViewedHistory(), null);

		if (isViewed() && getTag(CCTagList.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, getViewedHistory(), null);

		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, getViewedHistory(), null);

		if (getTag(CCTagList.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, getViewedHistory(), null);

		return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, getViewedHistory(), null);
	}

	@Override
	public CCMediaInfo getMediaInfo() {
		return mediainfo;
	}

	@Override
	public void setMediaInfo(CCMediaInfo minfo) {
		if (minfo == null) {CCLog.addUndefinied("Prevented setting CCMovie.MediaInfo to NULL"); return; } //$NON-NLS-1$

		this.mediainfo = minfo;

		updateDB();
	}

	@Override
	public CCQualityCategory getMediaInfoCategory(){
		return getMediaInfo().getCategory(getGenres());
	}

	@Override
	public CCGenreList getGenresFromSelfOrParent() {
		return getGenres();
	}
}
