package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.MovieCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IMovieData;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.*;
import de.jClipCorn.database.elementProps.packs.EMediaInfoPropPack;
import de.jClipCorn.database.elementProps.packs.EZyklusPropPack;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.types.NamedPathVar;
import de.jClipCorn.util.MoviePlayer;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.exceptions.FVHException;
import de.jClipCorn.features.nfo.MovieNFOWriter;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CCMovie extends CCDatabaseElement implements ICCPlayableElement, ICCDatedElement, IMovieData {
	public final static int PARTCOUNT_MAX = CCPathList.PARTCOUNT_MAX;

	private final MovieCache _cache = new MovieCache(this);

	public FSPath NfoPath = FSPath.Empty;
	public FSPath NfoCoverPath = FSPath.Empty;

	public final EZyklusPropPack         Zyklus        = new EZyklusPropPack(   "Zyklus",        CCMovieZyklus.EMPTY,          this, EPropertyType.OBJECTIVE_METADATA);
	public final ECCPathListProp         Parts         = new ECCPathListProp(   "Parts",         CCPathList.EMPTY,             this, EPropertyType.LOCAL_FILE_REF_SUBJECTIVE);
	public final EMediaInfoPropPack      MediaInfo     = new EMediaInfoPropPack("MediaInfo",     CCMediaInfo.EMPTY,       this);
	public final EIntProp                Length        = new EIntProp(          "Length",        0,                            this, EPropertyType.OBJECTIVE_METADATA);
	public final EDateProp               AddDate       = new EDateProp(         "AddDate",       CCDate.getMinimumDate(),      this, EPropertyType.USER_METADATA);
	public final EEnumProp<CCFileFormat> Format        = new EEnumProp<>(       "Format",        CCFileFormat.MKV,             this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EOptIntProp             Year          = new EOptIntProp(       "Year",          Opt.empty(),                  this, EPropertyType.OBJECTIVE_METADATA);
	public final EFileSizeProp           FileSize      = new EFileSizeProp(     "FileSize",      CCFileSize.ZERO,              this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EDateTimeListProp       ViewedHistory = new EDateTimeListProp( "ViewedHistory", CCDateTimeList.createEmpty(), this, EPropertyType.USER_METADATA);
	public final ELanguageSetProp        Language      = new ELanguageSetProp(  "Language",      CCDBLanguageSet.EMPTY,        this, EPropertyType.OBJECTIVE_METADATA);
	public final ELanguageListProp       Subtitles     = new ELanguageListProp( "Subtitles",     CCDBLanguageList.EMPTY,       this, EPropertyType.OBJECTIVE_METADATA);
	public final EOptStringProp          ChecksumCRC32 = new EOptStringProp(    "ChecksumCRC32", Opt.empty(),                  this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EOptStringProp          ChecksumMD5   = new EOptStringProp(    "ChecksumMD5",   Opt.empty(),                  this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EOptStringProp          ChecksumSHA256= new EOptStringProp(    "ChecksumSHA256",Opt.empty(),                  this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EOptStringProp          ChecksumSHA512= new EOptStringProp(    "ChecksumSHA512",Opt.empty(),                  this, EPropertyType.LOCAL_FILE_REF_OBJECTIVE);
	public final EStringListProp         AnimeSeason   = new EStringListProp(   "AnimeSeason",   CCStringList.EMPTY,           this, EPropertyType.OBJECTIVE_METADATA);
	public final EStringListProp         AnimeStudio   = new EStringListProp(   "AnimeStudio",   CCStringList.EMPTY,           this, EPropertyType.OBJECTIVE_METADATA);

	public CCMovie(CCMovieList ml, int id) {
		super(ml, id);
	}

	public void clearChecksums() {
		ChecksumCRC32.setOnly(Opt.empty());
		ChecksumMD5.setOnly(Opt.empty());
		ChecksumSHA256.setOnly(Opt.empty());
		ChecksumSHA512.setOnly(Opt.empty());
	}

	public void initNfoPaths() {
		NfoPath = MovieNFOWriter.getNFOPath(this);
		NfoCoverPath = MovieNFOWriter.getPosterPath(this);
	}

	@Override
	protected IEProperty[] listProperties()
	{
		return CCStreams
				.iterate(super.listProperties())
				.append(Zyklus.getProperties())
				.append(new IEProperty[]
				{
					Parts,
					Length,
					AddDate,
					Format,
					Year,
					FileSize,
					ViewedHistory,
					Language,
					Subtitles,
				})
				.append(MediaInfo.getProperties())
				.append(new IEProperty[]
				{
					ChecksumCRC32,
					ChecksumMD5,
					ChecksumSHA256,
					ChecksumSHA512,
					AnimeSeason,
					AnimeStudio,
				})
				.toArray(new IEProperty[0]);
	}

	public EZyklusPropPack         zyklus()        { return Zyklus;        }
	public EMediaInfoPropPack      mediaInfo()     { return MediaInfo;     }
	public EIntProp                length()        { return Length;        }
	public EDateProp               addDate()       { return AddDate;       }
	public EEnumProp<CCFileFormat> format()        { return Format;        }
	public EOptIntProp             year()          { return Year;          }
	public EFileSizeProp           fileSize()      { return FileSize;      }
	public ECCPathListProp         parts()         { return Parts;         }
	public EDateTimeListProp       viewedHistory() { return ViewedHistory; }
	public ELanguageSetProp        language()      { return Language;      }
	public ELanguageListProp       subtitles()     { return Subtitles;     }
	public EOptStringProp          checksumCRC32() { return ChecksumCRC32; }
	public EOptStringProp          checksumMD5()   { return ChecksumMD5;   }
	public EOptStringProp          checksumSHA256(){ return ChecksumSHA256;}
	public EOptStringProp          checksumSHA512(){ return ChecksumSHA512;}
	public EStringListProp         animeSeason()   { return AnimeSeason;    }
	public EStringListProp         animeStudio()   { return AnimeStudio;    }

	@Override
	public CCStringList getAnimeSeason() {
		return AnimeSeason.get();
	}

	@Override
	public CCStringList getAnimeStudio() {
		return AnimeStudio.get();
	}

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

	@Override
	public CCDBStructureElementTyp getStructureType() {
		return CCDBStructureElementTyp.MOVIE;
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
		return Year.get().orElse(0);
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

	public Opt<Integer> getZyklusYear() {
		if (!Zyklus.get().isSet()) return Year.get();

		List<Integer> years = CCStreams
				.iterate(this.movielist.getByZyklus(Zyklus.get().getTitle()))
				.map(p -> p.Year.get())
				.filter(Opt::isPresent)
				.map(Opt::get)
				.enumerate();

		if (years.isEmpty()) return Year.get();
		return Opt.of(Collections.min(years));
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

		if (ccprops().PROP_SERIALIZE_ANIMESTUDIO_IN_FILENAMES.getValue()) {
			for (var studio : getAnimeStudio()) {
				filename.append(" [[").append(studio).append("]]");
			}
		}

		if (ccprops().PROP_SERIALIZE_ANIMESEASON_IN_FILENAMES.getValue()) {
			for (var season : getAnimeSeason()) {
				filename.append(" [[").append(season).append("]]");
			}
		}

		if (ccprops().PROP_SERIALIZE_SPECIALVERSION_IN_FILENAMES.getValue()) {
			for (var vers : getSpecialVersion()) {
				filename.append(" [[").append(vers).append("]]");
			}
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

	// Per-movie leaf directory name used for zyklus movies (see generateRelativePath).
	// Contains the movie title with its zyklus prefix but *without* the zyklus number,
	// without the language tag and with the (individual) movie year appended - e.g.
	// "S.W.A.T. - Die Spezialeinheit (2003)".
	@SuppressWarnings("nls")
	public String generateFoldername() {
		StringBuilder foldername = new StringBuilder();

		if (Zyklus.get().isSet()) {
			foldername.append(Zyklus.get().getTitle()).append(" - ");
		}
		foldername.append(Str.limit(Title.get().replace(": ", " - "), 128));

		foldername.append(" (").append(Year.get().mapOrElse(String::valueOf, "0000")).append(")");

		return FilesystemUtils.fixStringToFilesystemname(foldername.toString());
	}

	public String generateRelativePath(int part) {
		var year = getZyklusYear().mapOrElse(String::valueOf, "0000"); //$NON-NLS-1$

		var name = Zyklus.get().isSet() ? Zyklus.get().getTitle() : Title.get();
		if (name.contains(": ")) name = name.substring(0, name.indexOf(": ")); //$NON-NLS-1$
		name = FilesystemUtils.fixStringToFilesystemname(name);

		var filename = generateFilename(part);

		if (Zyklus.get().isSet()) {
			// Zyklus movies get an additional per-movie leaf directory so that each movie lives
			// in its own folder (required by e.g. Jellyfin, which wants one directory per movie).
			return FilesystemUtils.combineWithFSPathSeparator(year, name, generateFoldername(), filename);
		}

		return FilesystemUtils.combineWithFSPathSeparator(year, name, filename);
	}

	public FSPath generateGuessedAbsolutePath(int part)  {
		var currentPath = Parts.get(part).toFSPath(this);
		var expectedRelPath = generateRelativePath(part);

		// Determine the base directory:
		// The generated relative path always starts with a {year} folder, so we walk up the
		// current file's ancestors to locate that year folder - the base is its parent.
		// This supports the flat (base/file), old (base/year/name/file) and new zyklus
		// (base/year/name/movie/file) layouts alike.
		// If no year folder is found we fall back to a flat structure (the file's parent).
		FSPath basePath = currentPath.getParent();

		FSPath probe = currentPath.getParent();
		for (int i = 0; i < 3; i++) {
			if (probe.getDirectoryName().matches("\\d{4}")) { //$NON-NLS-1$
				basePath = probe.getParent();
				break;
			}
			probe = probe.getParent();
		}

		return basePath.append(expectedRelPath);
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
	public Opt<Integer> getYear() {
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
		return ExtendedViewedState.create(this);
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

		for (final CCSingleTag tag : CCSingleTag.values()) {
			if (Str.equals(e.getName(), String.format("SwitchTag_Movie_%02d", tag.Index)) && Tags.get(tag)) return true;
		}

		return false;
	}
}
