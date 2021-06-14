package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.MovieCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IMovieData;
import de.jClipCorn.database.elementValues.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CCMovie extends CCDatabaseElement implements ICCPlayableElement, ICCDatedElement, IMovieData {
	public final static int PARTCOUNT_MAX = 6; // 0 .. 5

	private final MovieCache _cache = new MovieCache(this);

	public final EZyklusProp             Zyklus        = new EZyklusProp(      "Zyklus",        CCMovieZyklus.EMPTY,          this, EPropertyType.OBJECTIVE_METADATA);
	public final EMediaInfoProp          MediaInfo     = new EMediaInfoProp(   "MediaInfo",     CCMediaInfo.EMPTY,            this, EPropertyType.OBJECTIVE_METADATA);
	public final EIntProp                Length        = new EIntProp(         "Length",        0,                            this, EPropertyType.OBJECTIVE_METADATA);
	public final EDateProp               AddDate       = new EDateProp(        "AddDate",       CCDate.getMinimumDate(),      this, EPropertyType.USER_METADATA);
	public final EEnumProp<CCFileFormat> Format        = new EEnumProp<>(      "Format",        CCFileFormat.MKV,             this, EPropertyType.OBJECTIVE_METADATA);
	public final EIntProp                Year          = new EIntProp(         "Year",          1900,                         this, EPropertyType.OBJECTIVE_METADATA);
	public final EFileSizeProp           FileSize      = new EFileSizeProp(    "FileSize",      CCFileSize.ZERO,              this, EPropertyType.OBJECTIVE_METADATA);
	public final EPartArrayProp          Parts         = new EPartArrayProp(   "Parts",         new String[PARTCOUNT_MAX],    this, EPropertyType.LOCAL_FILE_REF);
	public final EDateTimeListProp       ViewedHistory = new EDateTimeListProp("ViewedHistory", CCDateTimeList.createEmpty(), this, EPropertyType.USER_METADATA);
	public final ELanguageListProp       Language      = new ELanguageListProp("Language",      CCDBLanguageList.EMPTY,       this, EPropertyType.OBJECTIVE_METADATA);
	
	public CCMovie(CCMovieList ml, int id) {
		super(ml, CCDBElementTyp.MOVIE, id);
	}

	@Override
	protected IEProperty[] ListProperties()
	{
		return CCStreams
				.iterate(super.ListProperties())
				.append(new IEProperty[]
				{
					Zyklus,
					MediaInfo,
					Length,
					AddDate,
					Format,
					Year,
					FileSize,
					Parts,
					ViewedHistory,
					Language,
				})
				.toArray(new IEProperty[0]);
	}

	public EZyklusProp             zyklus()        { return  Zyklus;        }
	public EMediaInfoProp          mediaInfo()     { return  MediaInfo;     }
	public EIntProp                length()        { return  Length;        }
	public EDateProp               addDate()       { return  AddDate;       }
	public EEnumProp<CCFileFormat> format()        { return  Format;        }
	public EIntProp                year()          { return  Year;          }
	public EFileSizeProp           fileSize()      { return  FileSize;      }
	public EPartArrayProp          parts()         { return  Parts;         }
	public EDateTimeListProp       viewedHistory() { return  ViewedHistory; }
	public ELanguageListProp       language()      { return  Language;      }

	@Override
	public boolean updateDB() {
		if (! isUpdating) {
			return movielist.update(this);
		}
		return true;
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
	public List<String> getParts() {
		List<String> r = new ArrayList<>();
		for (int i = 0; i < PARTCOUNT_MAX; i++) if (!Parts.get(i).isEmpty()) r.add(Parts.get(i));
		return r;
	}

	@Override
	public CCDateTimeList getViewedHistory() {
		return ViewedHistory.get();
	}

	@Override
	public CCDBLanguageList getLanguage() {
		return Language.get();
	}

	public String getAbsolutePart(int idx) {
		return PathFormatter.fromCCPath(Parts.get(idx));
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

			if (Tags.get(CCSingleTag.TAG_WATCH_LATER) && CCProperties.getInstance().PROP_MAINFRAME_AUTOMATICRESETWATCHLATER.getValue()) {
				Tags.setWithException(CCSingleTag.TAG_WATCH_LATER, false);
			}

			_cache.bust();
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
	public void updateViewedAndHistoryFromUI() {
		addToViewedHistoryFromUI(CCDateTime.getCurrentDateTime());
	}

	public String getFastMD5() {
		File[] f = new File[getPartcount()];
		for (int i = 0; i < getPartcount(); i++) {
			f[i] = new File(getAbsolutePart(i));
		}
		return ChecksumHelper.calculateFastMD5(f);
	}
	
	@Override
	public String toString() {
		return getCompleteTitle();
	}
	
	@SuppressWarnings("nls")
	public String generateFilename(int part) { //Test if database has no errors
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
				
		if (!Language.get().isExact(CCProperties.getInstance().PROP_DATABASE_DEFAULTPARSERLANG.getValue())) {
			filename.append(" [").append(Language.get().serializeToFilenameString()).append("]");
		}
		
		if (getPartcount() > 1) {
			filename.append(" (Part ").append(part + 1).append(")");
		}
		
		filename.append(".").append(Format.get().asString());
		
		filename = new StringBuilder(PathFormatter.fixStringToFilesystemname(filename.toString()));
		
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
		return 0;
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
		return MediaInfo.get().getCategory(Genres.get());
	}

	@Override
	public CCGenreList getGenresFromSelfOrParent() {
		return Genres.get();
	}

	@Override
	public Opt<CCDateTime> getLastViewed() {
		return ViewedHistory.get().isEmpty() ? Opt.empty() : Opt.of(ViewedHistory.get().getLastOrInvalid());
	}
}
