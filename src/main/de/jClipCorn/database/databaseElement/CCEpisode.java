package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.EpisodeCache;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IEpisodeData;
import de.jClipCorn.database.elementProps.*;
import de.jClipCorn.database.elementProps.impl.*;
import de.jClipCorn.database.elementProps.packs.EMediaInfoPropPack;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class CCEpisode implements ICCPlayableElement, ICCDatabaseStructureElement, IActionSourceObject, ICCTaggedElement, IEpisodeData, IPropertyParent {
	private final CCSeason owner;

	private final EpisodeCache _cache = new EpisodeCache(this);

	public final EIntProp                LocalID       = new EIntProp(          "LocalID",       -1,                           this, EPropertyType.DATABASE_PRIMARY_ID);
	public final EMediaInfoPropPack      MediaInfo     = new EMediaInfoPropPack("MediaInfo",     CCMediaInfo.EMPTY,            this);
	public final EIntProp                EpisodeNumber = new EIntProp(          "EpisodeNumber", 0,                            this, EPropertyType.OBJECTIVE_METADATA);
	public final EStringProp             Title         = new EStringProp(       "Title",         Str.Empty,                    this, EPropertyType.OBJECTIVE_METADATA);
	public final EIntProp                Length        = new EIntProp(          "Length",        0,                            this, EPropertyType.OBJECTIVE_METADATA);
	public final ETagListProp            Tags          = new ETagListProp(      "Tags",          CCTagList.EMPTY,              this, EPropertyType.USER_METADATA);
	public final EEnumProp<CCFileFormat> Format        = new EEnumProp<>(       "Format",        CCFileFormat.MKV,             this, EPropertyType.OBJECTIVE_METADATA);
	public final EFileSizeProp           FileSize      = new EFileSizeProp(     "FileSize",      CCFileSize.ZERO,              this, EPropertyType.OBJECTIVE_METADATA);
	public final EStringProp             Part          = new EStringProp(       "Part",          Str.Empty,                    this, EPropertyType.LOCAL_FILE_REF_SUBJECTIVE);
	public final EDateProp               AddDate       = new EDateProp(         "AddDate",       CCDate.getMinimumDate(),      this, EPropertyType.USER_METADATA);
	public final EDateTimeListProp       ViewedHistory = new EDateTimeListProp( "ViewedHistory", CCDateTimeList.createEmpty(), this, EPropertyType.USER_METADATA);
	public final ELanguageListProp       Language      = new ELanguageListProp( "Language",      CCDBLanguageList.EMPTY,       this, EPropertyType.OBJECTIVE_METADATA);

	private IEProperty[] _properties = null;

	private boolean isUpdating = false;
	
	public CCEpisode(CCSeason owner, int localID) {
		this.owner   = owner;
		LocalID.setReadonlyPropToInitial(localID);
	}

	public IEProperty[] getProperties()
	{
		if (_properties == null) _properties = listProperties();
		return _properties;
	}

	protected IEProperty[] listProperties()
	{
		return CCStreams.<IEProperty>empty()
				.append(new IEProperty[]
				{
					LocalID,
					EpisodeNumber,
					Title,
					Length,
					Tags,
					Format,
					FileSize,
					Part,
					AddDate,
					ViewedHistory,
					Language,
				})
				.append(MediaInfo.getProperties())
				.toArray(new IEProperty[0]);
	}

	public EIntProp                episodeNumber() { return EpisodeNumber; }
	public EStringProp             title()         { return Title;         }
	public EMediaInfoPropPack      mediaInfo()     { return MediaInfo;     }
	public EIntProp                length()        { return Length;        }
	public ETagListProp            tags()          { return Tags;          }
	public EEnumProp<CCFileFormat> format()        { return Format;        }
	public EFileSizeProp           fileSize()      { return FileSize;      }
	public EStringProp             part()          { return Part;          }
	public EDateProp               addDate()       { return AddDate;       }
	public EDateTimeListProp       viewedHistory() { return ViewedHistory; }
	public ELanguageListProp       language()      { return Language;      }

	@Override
	public CCTagList getTags() {
		return Tags.get();
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return Language.get();
	}

	@Override
	public CCMediaInfo getMediaInfo() {
		return MediaInfo.get();
	}

	@Override
	public int getEpisodeNumber() {
		return EpisodeNumber.get();
	}

	@Override
	public String getTitle() {
		return Title.get();
	}

	@Override
	public int getLength() {
		return Length.get();
	}

	@Override
	public CCFileFormat getFormat() {
		return Format.get();
	}

	@Override
	public CCFileSize getFilesize() {
		return FileSize.get();
	}

	@Override
	public String getPart() {
		return Part.get();
	}

	@Override
	public CCDate getAddDate() {
		return AddDate.get();
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return ViewedHistory.get();
	}

	public void setDefaultValues(boolean updateDB) {
		beginUpdating();

		for (IEProperty prop : getProperties()) if (!prop.isReadonly()) prop.resetToDefault();

		if (updateDB) endUpdating(); else abortUpdating();
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

	public boolean updateDB() {
		if (! isUpdating) {
			return getSeries().getMovieList().update(this);
		}
		return true;
	}

	public void updateDBWithException() throws DatabaseUpdateException {
		var ok = updateDB();
		if (!ok) throw new DatabaseUpdateException("updateDB() failed"); //$NON-NLS-1$
	}

	public void addToViewedHistory(CCDateTime datetime) {
		ViewedHistory.set(ViewedHistory.get().add(datetime));

		if (Tags.get(CCSingleTag.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
			Tags.set(CCSingleTag.TAG_WATCH_LATER, false);
		}
	}

	public void addToViewedHistoryFromUI(CCDateTime datetime) {
		try
		{
			ViewedHistory.setWithException(ViewedHistory.get().add(datetime));

			if (Tags.get(CCSingleTag.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				Tags.setWithException(CCSingleTag.TAG_WATCH_LATER, false);
			}

			if (getSeries().Tags.get(CCSingleTag.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				getSeries().Tags.setWithException(CCSingleTag.TAG_WATCH_LATER, false);
			}
		}
		catch (Throwable e1)
		{
			DialogHelper.showLocalError(MainFrame.getInstance(), "Dialogs.UpdateViewedFailed"); //$NON-NLS-1$
			CCLog.addError(e1);
		}
	}

	public void setViewedHistoryFromUI(CCDateTimeList value) {
		if (value == null) { CCLog.addUndefinied("Prevented setting CCEpisode.ViewedHistory to NULL"); return; } //$NON-NLS-1$

		try
		{
			ViewedHistory.setWithException(value);
		}
		catch (Throwable e1)
		{
			DialogHelper.showLocalError(MainFrame.getInstance(), "Dialogs.UpdateViewedFailed"); //$NON-NLS-1$
			CCLog.addError(e1);
		}
	}

	public CCSeason getSeason() {
		return owner;
	}
	
	public CCSeries getSeries() {
		return owner.getSeries();
	}

	public String getShortQualifiedTitle() {
		return Str.format("{0} - {1}", getStringIdentifier(), Title.get()); //$NON-NLS-1$
	}

	@Override
	public String getQualifiedTitle() {
		return Str.format("{0} {1} - {2}", getSeries().Title.get(), getStringIdentifier(), Title.get()); //$NON-NLS-1$
	}

	@Override
	public int getLocalID() {
		return LocalID.get();
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
		return ViewedHistory.get().any();
	}

	@Override
	public CCQualityCategory getMediaInfoCategory() {
		return MediaInfo.get().getCategory(getSeries().Genres.get());
	}

	public String getAbsolutePart() {
		return PathFormatter.fromCCPath(Part.get());
	}

	@Override
	public List<String> getParts() {
		return Collections.singletonList(Part.get());
	}

	public CCDateTime getViewedHistoryLastDateTime() {
		return ViewedHistory.get().getLastOrInvalid();
	}

	public CCDate getViewedHistoryLast() {
		return ViewedHistory.get().getLastDateOrInvalid();
	}

	public CCDate getViewedHistoryFirst() {
		return ViewedHistory.get().getFirstDateOrInvalid();
	}

	public CCDate getViewedHistoryAverage() {
		return ViewedHistory.get().getAverageDateOrInvalid();
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
	public void play(Component swingOwner, boolean updateViewedAndHistory) {
		play(swingOwner, updateViewedAndHistory, null);
	}

	@Override
	public void play(Component swingOwner, boolean updateViewedAndHistory, NamedPathVar player) {
		if (updateViewedAndHistory && !ViewedHistory.get().getLastOrInvalid().isUnspecifiedOrMinimum())
		{
			var hours = ViewedHistory.get().getLastOrInvalid().getSecondDifferenceTo(CCDateTime.getCurrentDateTime()) / (60.0 * 60.0);
			var max = CCProperties.getInstance().PROP_MAX_FASTREWATCH_HOUR_DIFF.getValue();

			if (hours < max)
			{
				var ok = DialogHelper.showLocaleYesNoDefaultNo(swingOwner, "Dialogs.PlayTooFastWarning");
				if (!ok) return;
			}
		}

		MoviePlayer.play(this, player);

		if (updateViewedAndHistory) updateViewedAndHistoryFromUI();
	}

	@Override
	public void updateViewedAndHistoryFromUI()
	{
		addToViewedHistoryFromUI(CCDateTime.getCurrentDateTime());
	}

	@Override
	public CCGenreList getGenresFromSelfOrParent() {
		return getSeries().Genres.get();
	}

	/**
	 * @return the Number of the Episode (as it is in the Season-List) (NOT THE ID)
	 */
	public int getEpisodeIndexInSeason() {
		return getSeason().findEpisode(this);
	}
	
	public String getStringIdentifier() {
		return String.format("S%02dE%02d", getSeason().getSortedSeasonNumber() + 1, EpisodeNumber.get()); //$NON-NLS-1$
	}
	
	public void delete() {
		getSeason().deleteEpisode(this);
	}
	
	@Override
	public String toString() {
		return Title.get();
	}

	public String getFastMD5() {
		File[] f = new File[1];
		f[0] = new File(getAbsolutePart());
		
		return ChecksumHelper.calculateFastMD5(f);
	}

	public File getFileForCreatedFolderstructure() {
		var root = getSeries().guessSeriesRootPath();
		return getFileForCreatedFolderstructure(new File(root));
	}

	public File getFileForCreatedFolderstructure(File parentfolder) {
		return getSeason().getFileForCreatedFolderstructure(parentfolder, Title.get(), EpisodeNumber.get(), Format.get(), null);
	}
	
	public String getRelativeFileForCreatedFolderstructure() {
		return getSeason().getRelativeFileForCreatedFolderstructure(Title.get(), EpisodeNumber.get(), Format.get(), null);
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (!isViewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, ViewedHistory.get(), null);
		else if (isViewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, ViewedHistory.get(), null);
		else if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, ViewedHistory.get(), null);
		else if (Tags.get(CCSingleTag.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, ViewedHistory.get(), null);
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, ViewedHistory.get(), null);
	}

	public boolean checkFolderStructure() {
		if (! getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase())) {
			CCLog.addDebug(getAbsolutePart() + " <> " + getRelativeFileForCreatedFolderstructure()); //$NON-NLS-1$
		}
		
		return getAbsolutePart().toLowerCase().endsWith(getRelativeFileForCreatedFolderstructure().toLowerCase());
	}

	@Override
	public CCMovieList getMovieList() {
		return getSeries().getMovieList();
	}

	@Override
	public ICalculationCache getCache() {
		return _cache;
	}
}
