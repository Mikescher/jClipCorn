package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.MovieCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IMovieData;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.*;
import de.jClipCorn.database.elementProps.packs.EMediaInfoPropPack;
import de.jClipCorn.database.elementProps.packs.EPartArrayPropPack;
import de.jClipCorn.database.elementProps.packs.EZyklusPropPack;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.exceptions.FVHException;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CCMovie extends CCDatabaseElement implements ICCPlayableElement, ICCDatedElement, IMovieData {
	public final static int PARTCOUNT_MAX = EPartArrayPropPack.PARTCOUNT_MAX;

	private final MovieCache _cache = new MovieCache(this);

	public final EZyklusPropPack         Zyklus        = new EZyklusPropPack(   "Zyklus",        CCMovieZyklus.EMPTY,          this, EPropertyType.OBJECTIVE_METADATA);
	public final EPartArrayPropPack      Parts         = new EPartArrayPropPack("Parts",         CCPath.Empty,                 this, EPropertyType.LOCAL_FILE_REF_SUBJECTIVE);
	public final EMediaInfoPropPack      MediaInfo     = new EMediaInfoPropPack("MediaInfo",     CCMediaInfo.EMPTY,       this);
	public final EIntProp                Length        = new EIntProp(          "Length",        0,                            this, EPropertyType.OBJECTIVE_METADATA);
	public final EDateProp               AddDate       = new EDateProp(         "AddDate",       CCDate.getMinimumDate(),      this, EPropertyType.USER_METADATA);
	public final EEnumProp<CCFileFormat> Format        = new EEnumProp<>(       "Format",        CCFileFormat.MKV,             this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EIntProp                Year          = new EIntProp(          "Year",          1900,                         this, EPropertyType.OBJECTIVE_METADATA);
	public final EFileSizeProp           FileSize      = new EFileSizeProp(     "FileSize",      CCFileSize.ZERO,              this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EDateTimeListProp       ViewedHistory = new EDateTimeListProp( "ViewedHistory", CCDateTimeList.createEmpty(), this, EPropertyType.USER_METADATA);
	public final ELanguageSetProp        Language      = new ELanguageSetProp(  "Language",      CCDBLanguageSet.EMPTY,        this, EPropertyType.OBJECTIVE_METADATA);
	public final ELanguageListProp       Subtitles     = new ELanguageListProp( "Subtitles",     CCDBLanguageList.EMPTY,       this, EPropertyType.OBJECTIVE_METADATA);

	public CCMovie(CCMovieList ml, int id) {
		super(ml, id);
	}

	@Override
	protected IEProperty[] listProperties()
	{
		return CCStreams
				.iterate(super.listProperties())
				.append(Zyklus.getProperties())
				.append(new IEProperty[]
				{
					Length,
					AddDate,
					Format,
					Year,
					FileSize,
					ViewedHistory,
					Language,
					Subtitles,
				})
				.append(Parts.getProperties())
				.append(MediaInfo.getProperties())
				.toArray(new IEProperty[0]);
	}

	public EZyklusPropPack         zyklus()        { return Zyklus;        }
	public EMediaInfoPropPack      mediaInfo()     { return MediaInfo;     }
	public EIntProp                length()        { return Length;        }
	public EDateProp               addDate()       { return AddDate;       }
	public EEnumProp<CCFileFormat> format()        { return Format;        }
	public EIntProp                year()          { return Year;          }
	public EFileSizeProp           fileSize()      { return FileSize;      }
	public EPartArrayPropPack      parts()         { return Parts;         }
	public EDateTimeListProp       viewedHistory() { return ViewedHistory; }
	public ELanguageSetProp        language()      { return Language;      }
	public ELanguageListProp       subtitles()     { return Subtitles;     }

	@Override
	public boolean updateDB() {
		if (! isUpdating) {
			return movielist.update(this);
		}
		return true;
	}

	@Override
	public CCDBElementTyp getType() {
		return CCDBElementTyp.MOVIE;
	}

	public void updateDBWithException() throws DatabaseUpdateException {
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

	@Override
	public ICalculationCache getCache() {
		return _cache;
	}

	public String getCompleteTitle() {
		if (Zyklus.get().isSet()) {
			return Zyklus.get().getFormatted() + ' ' + '-' + ' ' + Title.get();
		} else {
			return Title.get();
		}
	}
	
	public String getOrderableTitle() {
		if (Zyklus.get().isSet()) {
			return Zyklus.get().getOrderableFormatted() + ' ' + '-' + ' ' + Title.get();
		} else {
			return Title.get();
		}
	}

	@Override
	public String getFullDisplayTitle() {
		return getCompleteTitle();
	}

	@Override
	public boolean isViewed() {
		return ViewedHistory.get().any();
	}

	public int getPartcount() {
		int pc = 0;
		
		for (int i = 0; i < PARTCOUNT_MAX; i++) {
			pc += Parts.get(i).isEmpty()?0:1; // nett
		}
		
		return pc;
	}

	@Override
	public int getFirstYear() {
		return Year.get();
	}

	@Override
	public List<CCPath> getParts() {
		List<CCPath> r = new ArrayList<>();
		for (int i = 0; i < PARTCOUNT_MAX; i++) if (!Parts.get(i).isEmpty()) r.add(Parts.get(i));
		return r;
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return ViewedHistory.get();
	}

	@Override
	public CCDBLanguageSet getLanguage() {
		return Language.get();
	}

	@Override
	public CCDBLanguageList getSubtitles() {
		return Subtitles.get();
	}

	public void setViewedHistoryFromUI(CCDateTimeList value) {
		if (value == null) { CCLog.addUndefinied("Prevented setting CCMovie.ViewedHistory to NULL"); return; } //$NON-NLS-1$

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

	public void addToViewedHistoryFromUI(CCDateTime datetime) {
		try
		{
			ViewedHistory.setWithException(ViewedHistory.get().add(datetime));

			if (Tags.get(CCSingleTag.TAG_WATCH_LATER) && ccprops().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				Tags.setWithException(CCSingleTag.TAG_WATCH_LATER, false);
			}

			_cache.bust();
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
			ret |= Parts.get(i-1).isEmpty() && ! Parts.get(i).isEmpty();
		}
		
		return ret;
	}
	
	public void delete() {
		movielist.remove(this);
	}

	@Override
	public void play(Component swingOwner, boolean updateViewedAndHistory) {
		play (swingOwner, updateViewedAndHistory, null);
	}

	@Override
	public void play(Component swingOwner, boolean updateViewedAndHistory, NamedPathVar player) {
		if (updateViewedAndHistory && !ViewedHistory.get().getLastOrInvalid().isUnspecifiedOrMinimum())
		{
			var hours = ViewedHistory.get().getLastOrInvalid().getSecondDifferenceTo(CCDateTime.getCurrentDateTime()) / (60.0 * 60.0);
			var max = ccprops().PROP_MAX_FASTREWATCH_HOUR_DIFF.getValue();

			if (hours < max)
			{
				var ok = DialogHelper.showLocaleYesNoDefaultNo(swingOwner, "Dialogs.PlayTooFastWarning");
				if (!ok) return;
			}
		}

		var playSucc = MoviePlayer.play(this, player);

		if (playSucc && updateViewedAndHistory) updateViewedAndHistoryFromUI();
	}

	@Override
	public void updateViewedAndHistoryFromUI() {
		addToViewedHistoryFromUI(CCDateTime.getCurrentDateTime());
	}

	public String getFastViewHashSafe() {
		try {
			return ChecksumHelper.fastVideoHash(CCStreams.iterate(getParts()).map(p -> p.toFSPath(this)).enumerate());
		} catch (IOException | FVHException e) {
			CCLog.addError(e);
			return "00";
		}
	}
	
	@Override
	public String toString() {
		return getCompleteTitle();
	}
	
	@SuppressWarnings("nls")
	public String generateFilename(int part) {
		StringBuilder filename = new StringBuilder();
		
		if (Zyklus.get().isSet()) {
			filename.append(Zyklus.get().getFormatted()).append(" - ");
			
			filename.append(Str.limit(Title.get().replace(": ", " - "), 128));
		} else {
			filename.append(Title.get().replace(": ", " - "));
		}
		
		for (CCGroup group : getGroups()) {
			if (group.DoSerialize) filename.append(" [[").append(group.Name).append("]]");
		}
				
		if (!(Language.get().isExact(ccprops().PROP_DATABASE_DEFAULTPARSERLANG.getValue()) && ccprops().PROP_SKIP_DEFAULT_LANG_IN_FILENAMES.getValue())) {
			filename.append(" [").append(Language.get().serializeToFilenameString()).append("]");
		}
		
		if (getPartcount() > 1) {
			filename.append(" (Part ").append(part + 1).append(")");
		}
		
		filename.append(".").append(Format.get().asString());
		
		filename = new StringBuilder(FilesystemUtils.fixStringToFilesystemname(filename.toString()));
		
		return filename.toString();
	}

	public boolean hasZyklus() {
		return Zyklus.get().isSet();
	}

	@Override
	public CCTagList getTags() {
		return Tags.get();
	}

	@Override
	public CCFileFormat getFormat() {
		return Format.get();
	}

	@Override
	public int getYear() {
		return Year.get();
	}

	@Override
	public CCFileSize getFilesize() {
		return FileSize.get();
	}

	@Override
	public CCMovieZyklus getZyklus() {
		return Zyklus.get();
	}

	@Override
	public CCMediaInfo getMediaInfo() {
		return MediaInfo.get();
	}

	@Override
	public int getLength() {
		return Length.get();
	}

	@Override
	public CCDate getAddDate() {
		return AddDate.get();
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (!isViewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, ViewedHistory.get(), null);

		if (isViewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, ViewedHistory.get(), null);

		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, ViewedHistory.get(), null);

		if (Tags.get(CCSingleTag.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, ViewedHistory.get(), null);

		return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, ViewedHistory.get(), null);
	}

	@Override
	public CCQualityCategory getMediaInfoCategory(){
		return MediaInfo.get().getCategory(Genres.get()).orElse(CCQualityCategory.UNSET);
	}

	@Override
	public CCGenreList getGenresFromSelfOrParent() {
		return Genres.get();
	}

	@Override
	public Opt<CCDateTime> getLastViewed() {
		return ViewedHistory.get().isEmpty() ? Opt.empty() : Opt.of(ViewedHistory.get().getLastOrInvalid());
	}

	@Override
	@SuppressWarnings("nls")
	public boolean shouldHighlightAction(CCActionElement e) {
		if (Str.equals(e.getName(), "SetMovRatingNO")  && Score.get() == CCUserScore.RATING_NO)   return true;
		if (Str.equals(e.getName(), "SetMovRating0")   && Score.get() == CCUserScore.RATING_0)    return true;
		if (Str.equals(e.getName(), "SetMovRating1")   && Score.get() == CCUserScore.RATING_I)    return true;
		if (Str.equals(e.getName(), "SetMovRating2")   && Score.get() == CCUserScore.RATING_II)   return true;
		if (Str.equals(e.getName(), "SetMovRatingMID") && Score.get() == CCUserScore.RATING_MID)  return true;
		if (Str.equals(e.getName(), "SetMovRating3")   && Score.get() == CCUserScore.RATING_III)  return true;
		if (Str.equals(e.getName(), "SetMovRating4")   && Score.get() == CCUserScore.RATING_IV)   return true;
		if (Str.equals(e.getName(), "SetMovRating5")   && Score.get() == CCUserScore.RATING_V)    return true;

		for (final CCSingleTag tag : CCTagList.TAGS) {
			if (Str.equals(e.getName(), String.format("SwitchTag_Movie_%02d", tag.Index)) && Tags.get(tag)) return true;
		}

		return false;
	}
}
