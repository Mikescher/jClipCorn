package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.SeriesCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.ISeriesData;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.util.*;
import de.jClipCorn.database.util.iterators.DirectEpisodesIterator;
import de.jClipCorn.database.util.iterators.DirectSeasonsIterator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.comparator.CCSeasonComparator;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple1;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.text.StrBuilder;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCSeries extends CCDatabaseElement implements IEpisodeOwner, ISeriesData {
	private final static int GUIDE_W_BORDER = 2;
	private final static int GUIDE_W_PADDING = 6;
	
	private final List<CCSeason> seasons = new Vector<>();

	private final SeriesCache _cache;

	public CCSeries(CCMovieList ml, int id) {
		super(ml, CCDBElementTyp.SERIES, id);
		_cache = new SeriesCache(this);
	}

	@Override
	protected IEProperty[] listProperties()
	{
		return super.listProperties();
	}

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

	public void directlyInsertSeason(CCSeason s) {
		seasons.add(s);
		_cache.bust();
	}

	public void enforceOrder() {
		seasons.sort(Comparator.comparingInt(CCSeason::getSortedSeasonNumber));
		_cache.bust();
	}

	public CCSeason createNewEmptySeason() {
		return movielist.createNewEmptySeason(this);
	}
	
	public boolean isViewed() { // All parts viewed
		return _cache.getBool(SeriesCache.IS_VIEWED, null, ser->
		{
			boolean v = true;
			for (CCSeason se: seasons) {
				v &= se.isViewed();
			}
			return v;
		});
	}
	
	public boolean isUnviewed() { // All parts not viewed
		return _cache.getBool(SeriesCache.IS_UNVIEWED, null, ser->
		{
			boolean v = true;
			for (CCSeason se: seasons) {
				v &= se.isUnviewed();
			}
			return v;
		});
	}

	public boolean isPartialViewed() { // Some parts viewed - some not
		return !(isViewed() || isUnviewed());
	}

	public boolean isViewedOrPartialViewed() {
		return !isUnviewed();
	}
	
	public int getSeasonCount() {
		return seasons.size();
	}
	
	public int getEpisodeCount() {
		return _cache.getInt(SeriesCache.EPISODE_COUNT, null, ser->
		{
			int c = 0;
			for (CCSeason se : seasons) {
				c += se.getEpisodeCount();
			}
			return c;
		});
	}
	
	public int getLength() {
		return _cache.getInt(SeriesCache.LENGTH, null, ser->
		{
			int l = 0;
			for (CCSeason se: seasons) {
				l += se.getLength();
			}
			return l;
		});
	}

	@Override
	public CCDate getAddDate() {
		switch (CCProperties.getInstance().PROP_SERIES_ADDDATECALCULATION.getValue()) {
			case OLDEST_DATE:
				return calcMinimumAddDate();
			case NEWEST_DATE:
				return calcMaximumAddDate();
			case AVERAGE_DATE:
				return calcAverageAddDate();
			case NEWEST_BY_SEASON:
				return calcMaximumAddDateBySeason();
			default:
				return null;
		}
	}

	public CCDate calcMaximumAddDateBySeason() {
		return _cache.get(SeriesCache.MAXIMUM_ADDDATE_BY_SEASON, null, ser->
		{
			CCDate cd = CCDate.getMinimumDate();
			for (CCSeason se: seasons) {
				if (se.getEpisodeCount()==0) continue;
				CCDate scd = se.calcMinimumAddDate();
				if (scd.isGreaterThan(cd)) cd = scd;
			}
			return cd;
		});
	}
	
	public CCDate calcMaximumAddDate() {
		return _cache.get(SeriesCache.MAXIMUM_ADDDATE, null, ser->
		{
			CCDate cd = CCDate.getMinimumDate();
			for (CCSeason se: seasons) {
				if (se.getEpisodeCount()==0) continue;

				CCDate scd = se.calcMaximumAddDate();
				if (scd.isGreaterThan(cd)) cd = scd;
			}
			return cd;
		});
	}
	
	public CCDate calcMinimumAddDate() {
		return _cache.get(SeriesCache.MINIMUM_ADDDATE, null, ser->
		{
			if (seasons.size() == 0) return CCDate.getMinimumDate();
			CCDate cd = CCDate.getMaximumDate();
			for (CCSeason se: seasons) {
				if (se.getEpisodeCount()==0) continue;

				CCDate scd = se.calcMinimumAddDate();
				if (scd.isLessThan(cd)) cd = scd;
			}
			return cd;
		});
	}
	
	public CCDate calcAverageAddDate() {
		return _cache.get(SeriesCache.AVERAGE_ADDDATE, null, ser->
		{
			List<CCDate> dlist = new ArrayList<>();
			for (CCSeason se: seasons) {
				if (se.getEpisodeCount()==0) continue;

				dlist.add(se.calcAverageAddDate());
			}

			if (dlist.isEmpty()) return CCDate.getMinimumDate();

			return CCDate.getAverageDate(dlist);
		});
	}

	@Override
	public CCTagList getTags() {
		return Tags.get();
	}

	@Override
	public CCFileFormat getFormat() {
		return _cache.get(SeriesCache.FORMAT, null, ser->
		{
			return iteratorEpisodes().findMostCommon(e -> e.Format.get(), CCFileFormat.getWrapper().firstValue());
		});
	}
	
	public CCFileSize getFilesize() {
		if (movielist.isBlocked()) return CCFileSize.ZERO; // why ??

		return _cache.get(SeriesCache.FILESIZE, null, ser->
		{
			long sz = 0;

			for (CCSeason se: seasons) {
				sz += se.getFilesize().getBytes();
			}

			return new CCFileSize(sz);
		});
	}
	
	public YearRange getYearRange() {
		return _cache.get(SeriesCache.YEAR_RANGE, null, ser->
		{
			int miny = Integer.MAX_VALUE;
			int maxy = Integer.MIN_VALUE;

			for (CCSeason se: seasons) {
				miny = Math.min(miny, se.Year.get());
				maxy = Math.max(maxy, se.Year.get());
			}
			return new YearRange(miny, maxy);
		});
	}
	
	@Override
	public int getFirstYear() {
		return getYearRange().getLowestYear();
	}

	public CCSeason getSeasonByArrayIndex(int ss) {
		if (ss < 0 || ss >= seasons.size())
			return null;
		
		return seasons.get(ss);
	}

	public void deleteSeason(CCSeason season) {
		seasons.remove(season);
		
		for (int i = season.getEpisodeCount()-1; i >= 0; i--) {
			season.deleteEpisode(season.getEpisodeByArrayIndex(i));
		}
		
		getMovieList().removeSeasonDatabase(season);
		
		if (season.getCoverID() != -1) {
			getMovieList().getCoverCache().deleteCover(season.getCoverID());
		}
		
		getMovieList().fireOnChangeDatabaseElement(this);

		_cache.bust();
	}

	public int getViewedCount() {
		return _cache.getInt(SeriesCache.VIEWED_COUNT, null, ser->
		{
			int v = 0;
			for (CCSeason se : seasons) {
				v += se.getViewedCount();
			}
			return v;
		});
	}

	public void delete() {
		getMovieList().remove(this);
	}
	
	public CCEpisode getFirstEpisode() {
		for (int i = 0; i < seasons.size(); i++) {
			CCEpisode ep = seasons.get(i).getFirstEpisode();
			if (ep != null) return ep;
		}
		return null;
	}
	
	public List<File> getAbsolutePathList() {
		List<File> result = new ArrayList<>();

		for (int i = 0; i < seasons.size(); i++) {
			result.addAll(getSeasonByArrayIndex(i).getAbsolutePathList());
		}

		return result;
	}
	
	public boolean isFileInList(String path) {
		for (int i = 0; i < seasons.size(); i++) {
			CCSeason s = getSeasonByArrayIndex(i);
			if(s.isFileInList(path)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<CCEpisode> getEpisodeList() {
		List<CCEpisode> result = new ArrayList<>();
		
		for (int i = 0; i < getSeasonCount(); i++) {
			result.addAll(getSeasonByArrayIndex(i).getEpisodeList());
		}
		
		return result;
	}

	public List<CCEpisode> getSortedEpisodeList() {
		List<CCEpisode> result = new ArrayList<>();

		for (var s : getSeasonsSorted()) result.addAll(s.getEpisodeList());

		return result;
	}
	
	public CCEpisode getRandomEpisode() {
		List<CCEpisode> eplist = getEpisodeList();
		if (eplist.size() > 0) {
			return eplist.get((int) (Math.random()*eplist.size()));
		}
		return null;
	}
	
	public CCEpisode getRandomEpisodeWithViewState(boolean viewed) {
		List<CCEpisode> eplist = getEpisodeList();
		
		for (int i = eplist.size() - 1; i >= 0; i--) {
			if (eplist.get(i).isViewed() ^ viewed) {
				eplist.remove(i);
			}
		}
		
		if (eplist.size() > 0) {
			return eplist.get((int) (Math.random()*eplist.size()));
		}
		return null;
	}
	
	public CCEpisode getNextEpisode() {
		return NextEpisodeHelper.findNextEpisode(this);
	}

	public int findSeason(CCSeason ccSeason) {
		return seasons.indexOf(ccSeason);
	}
	
	public List<CCSeason> getSeasonsSorted() {

		List<CCSeason> sortedseasons = new ArrayList<>(seasons);
		sortedseasons.sort(new CCSeasonComparator());
		return sortedseasons;
	}
	
	public int findSeasoninSorted(CCSeason ccSeason) {
		return getSeasonsSorted().indexOf(ccSeason);
	}

	@Override
	public String getQualifiedTitle() {
		return Title.get();
	}

	@Override
	public ICalculationCache getCache() {
		return _cache;
	}

	@Override
	public String toString() {
		return Title.get();
	}
	
	public String getEpisodeGuide() {
		StrBuilder guide = new StrBuilder();
		
		int titlewidth = GUIDE_W_BORDER + GUIDE_W_PADDING + Title.get().length() + GUIDE_W_PADDING + GUIDE_W_BORDER;
		
		guide.appendNewLine();
		guide.appendPadding(titlewidth, '#');
		guide.appendNewLine();
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendPadding(titlewidth - (GUIDE_W_BORDER + GUIDE_W_BORDER), ' ');
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendNewLine();
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendPadding(GUIDE_W_PADDING, ' ');
		guide.append(Title.get());
		guide.appendPadding(GUIDE_W_PADDING, ' ');
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendNewLine();
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendPadding(titlewidth - (GUIDE_W_BORDER + GUIDE_W_BORDER), ' ');
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendNewLine();
		guide.appendPadding(titlewidth, '#');
		
		for (int i = 0; i < getSeasonCount(); i++) {
			CCSeason season = getSeasonByArrayIndex(i);
			String seasontitle = season.Title.get() + ' ' + '(' + season.Year.get() + ')';
			
			guide.appendNewLine();
			guide.appendNewLine();
			guide.appendNewLine();
			guide.appendNewLine();
			
			guide.appendPadding(GUIDE_W_BORDER + GUIDE_W_PADDING, ' ');
			guide.append(seasontitle);
			guide.appendNewLine();
			guide.appendPadding(GUIDE_W_BORDER + GUIDE_W_PADDING + seasontitle.length() + GUIDE_W_PADDING + GUIDE_W_BORDER, '#');
			guide.appendNewLine();
			guide.appendNewLine();
			for (int j = 0; j < season.getEpisodeCount(); j++) {
				CCEpisode episode = season.getEpisodeByArrayIndex(j);
				guide.appendPadding(GUIDE_W_BORDER, ' ');
				guide.appendln(String.format("> [%02d] %s (%s)", episode.EpisodeNumber.get(), episode.Title.get(), TimeIntervallFormatter.formatPointed(episode.Length.get()))); //$NON-NLS-1$
			}
		}

		return guide.toString();
	}
	
	public String getCommonPathStart(boolean extendedSearch) {

		return _cache.get(SeriesCache.COMMON_PATH_START, Tuple1.Create(extendedSearch), ser->
		{
			List<String> all = new ArrayList<>();

			for (int seasi = 0; seasi < getSeasonCount(); seasi++) {
				CCSeason season = getSeasonByArrayIndex(seasi);
				all.add(season.getCommonPathStart());
			}

			while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$

			String common = PathFormatter.getCommonFolderPath(all);

			if (extendedSearch && common.isEmpty()) {
				common = movielist.getCommonSeriesPath();
			}

			return common;
		});
	}
	
	public String guessSeriesRootPath() {
		return _cache.get(SeriesCache.SERIES_ROOT_PATH, null, ser->
		{
			Map<String, Integer> result = new HashMap<>();

			String pathMax = ""; //$NON-NLS-1$
			int countMax = 0;

			for (int seasi = 0; seasi < getSeasonCount(); seasi++) {
				CCSeason season = getSeasonByArrayIndex(seasi);
				for (int epsi = 0; epsi < season.getEpisodeCount(); epsi++) {
					CCEpisode episode = season.getEpisodeByArrayIndex(epsi);

					String path = PathFormatter.getParentPath(episode.getAbsolutePart(), 3); // season-folder -> series-folder -> root-folder

					result.put(path, result.getOrDefault(path, 0) + 1);

					if (result.getOrDefault(path, 0) > countMax) {
						countMax = result.getOrDefault(path, 0);
						pathMax = path;
					}
				}
			}

			return pathMax;
		});
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {

		if (isEmpty())
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, CCDateTimeList.createEmpty(), 0);

		if (isUnviewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_LATER, CCDateTimeList.createEmpty(), getFullViewCount());

		if (!isUnviewed() && Tags.get(CCSingleTag.TAG_WATCH_LATER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_AGAIN, CCDateTimeList.createEmpty(), getFullViewCount());

		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());

		if (Tags.get(CCSingleTag.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, CCDateTimeList.createEmpty(), getFullViewCount());

		if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue() && isPartialViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.PARTIAL_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());

		return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());
	}

	private int getFullViewCount() {
		return _cache.getInt(SeriesCache.FULL_VIEW_COUNT, null, ser->
		{
			int vc = Integer.MAX_VALUE;
			for (CCSeason sea : seasons) {
				for (CCEpisode e : sea.getEpisodeList()) {
					vc = Math.min(e.ViewedHistory.get().count(), vc);
				}
			}
			if (vc == Integer.MAX_VALUE) return 0;
			return vc;
		});
	}

	public CCDBLanguageList getAllLanguages() {
		return _cache.get(SeriesCache.ALL_LANGUAGES, null, ser->
		{
			HashSet<CCDBLanguage> langs = new HashSet<>();
			for (CCSeason s : seasons)
			{
				for (CCEpisode e : s.getEpisodeList())
				{
					langs.addAll(e.Language.get().getInternalData());
				}
			}

			return CCDBLanguageList.createDirect(langs);
		});
	}

	public CCDBLanguageList getCommonLanguages() {
		return _cache.get(SeriesCache.COMMON_LANGUAGES, null, ser->
		{
			HashSet<CCDBLanguage> langs = null;
			for (CCSeason s : seasons)
			{
				for (CCEpisode e : s.getEpisodeList())
				{
					if (langs == null) { langs = new HashSet<>(e.Language.get().getInternalData()); continue; }
					langs.retainAll(e.Language.get().getInternalData());
					if (langs.size()==0) return CCDBLanguageList.EMPTY;
				}
			}
			if (langs == null) return CCDBLanguageList.EMPTY;
			return CCDBLanguageList.createDirect(langs);
		});
	}

	public CCDBLanguageList getSemiCommonOrAllLanguages() {
		CCDBLanguageList com = getSemiCommonLanguages();
		return com.isEmpty() ? getAllLanguages() : com;
	}

	public CCDBLanguageList getSemiCommonLanguages() {
		return _cache.get(SeriesCache.SEMI_COMMON_LANGUAGES, Tuple1.Create(CCProperties.getInstance().PROP_FOLDERLANG_IGNORE_PERC.getValue()), ser->
		{
			if (getEpisodeCount() == 0) return CCDBLanguageList.EMPTY;

			var firstEpisode = getFirstEpisode();

			HashSet<CCDBLanguage> allLanguages = new HashSet<>();

			HashSet<CCDBLanguage> langsCommon = new HashSet<>(firstEpisode.Language.get().getInternalData());
			CCDBLanguageList langsEqual  = firstEpisode.Language.get();

			HashMap<CCDBLanguageList, Integer> langsLength = new HashMap<>();
			int totalLength = 0;

			for (CCSeason s : seasons)
			{
				for (CCEpisode e : s.getEpisodeList())
				{
					allLanguages.addAll(e.Language.get().getInternalData());

					langsCommon.retainAll(e.Language.get().getInternalData());

					if (langsEqual != null && !langsEqual.equals(e.Language.get())) langsEqual = null;

					int addlen = e.Length.get();
					if (langsLength.containsKey(e.Language.get())) addlen += langsLength.get(e.Language.get());
					langsLength.put(e.Language.get(), addlen);

					totalLength+= e.Length.get();
				}
			}

			if (langsEqual != null) return langsEqual;

			var maxPerc = CCProperties.getInstance().PROP_FOLDERLANG_IGNORE_PERC.getValue();

			if (maxPerc > 0) // if maxPerc is set we can use a Language set that satisifies at least (100-maxPerc)% of the total length
			{
				CCDBLanguageList percentageMatch_Lang  = null;
				double           percentageMatch_Score = Integer.MIN_VALUE;
				for (var langset : CCDBLanguageList.create(allLanguages).allSubsets())
				{
					if (langset.isEmpty()) continue;

					var subsetlength = CCStreams
							.iterate(langsLength.entrySet())
							.filter(p -> langset.isSubsetOf(p.getKey()))
							.map(Map.Entry::getValue)
							.sum(Integer::sum, 0);

					var percentage_of_notmatching_lang = ((totalLength - subsetlength) * 100 / totalLength);
					if (percentage_of_notmatching_lang >= maxPerc) continue;

					var score = langset.size() * 10_000 + (100-percentage_of_notmatching_lang);

					if (score > percentageMatch_Score)
					{
						percentageMatch_Lang  = langset;
						percentageMatch_Score = score;
					}
				}
				if (percentageMatch_Lang != null) return percentageMatch_Lang;
			}

			return CCDBLanguageList.createDirect(langsCommon);
		});
	}

	@SuppressWarnings("nls")
	public String getFolderNameForCreatedFolderStructure(CCDBLanguageList fallbackLanguage) {
		return _cache.get(SeriesCache.FOLDER_NAME_FOR_CREATED_FOLDER_STRUCTURE, Tuple1.Create(fallbackLanguage), ser->
		{
			StringBuilder seriesfoldername = new StringBuilder(Title.get());

			for (CCGroup group : getGroups()) {
				if (group.DoSerialize) seriesfoldername.append(" [[").append(group.Name).append("]]");
			}

			CCDBLanguageList lang = getSemiCommonLanguages();
			if (getEpisodeCount() == 0 && fallbackLanguage != null) lang = fallbackLanguage;
			if (!lang.isExact(CCDBLanguage.GERMAN) && !lang.isEmpty()) {
				seriesfoldername.append(String.format(" [%s]", lang.serializeToFilenameString()));
			}

			seriesfoldername = new StringBuilder(PathFormatter.fixStringToFilesystemname(seriesfoldername.toString()));

			return seriesfoldername.toString();
		});
	}

	@SuppressWarnings("nls")
	public Pattern getValidSeasonIndexRegex() {
		Pattern regexInt = Pattern.compile("^[0-9]+$");
		
		for (String rex : CCProperties.getInstance().PROP_SEASON_INDEX_REGEXPRESSIONS.getNonEmptyValues()) {
			Pattern p = Pattern.compile("^" + rex + "$");
			
			boolean fitsAllSeasons = true;
			HashSet<Integer> indiMap = new HashSet<>();
			for (CCSeason season : seasons) {
				Matcher m = p.matcher(season.Title.get());

				if (! m.matches()) {
					fitsAllSeasons = false;
					break;
				}
				
				try	{
					String group = m.group("index");
					
					if (group == null) {
						fitsAllSeasons = false;
						break;
					}
					
					if (! regexInt.matcher(group).matches()) {
						fitsAllSeasons = false;
						break;
					}
					
					if (indiMap.contains(Integer.parseInt(group))) {
						fitsAllSeasons = false;
						break;
					}
					
				} catch (IllegalArgumentException e) {
					fitsAllSeasons = false;
					break;
				}
				
				indiMap.add(Integer.parseInt(m.group("index")));
			}
			if (fitsAllSeasons) {
				return p;
			}
		}
		
		return null;
	}

	public CCSeason getInitialDisplaySeason() {
		if (getSeasonCount() == 0) return null;
		
		CCSeason zero = getSeasonByArrayIndex(0);
		
		if (zero.getIndexForCreatedFolderStructure() == 0) {
			CCSeason first = getSeasonByArrayIndex(1);
			if (first == null) {
				return zero;
			} else {
				return first;
			}
		} else {
			return zero;
		}
	}

	@Override
	public String getFullDisplayTitle() {
		return Title.get();
	}
	
	@Override
	public CCStream<CCEpisode> iteratorEpisodes() {
		return new DirectEpisodesIterator(this);
	}

	@Override
	public CCSeries getSeries() {
		return this; // -.-
	}

	public CCStream<CCSeason> iteratorSeasons() {
		return new DirectSeasonsIterator(this);
	}

	public boolean isEmpty() {
		return _cache.getBool(SeriesCache.IS_EMPTY, null, ser->
		{
			for (CCSeason se : seasons) if (!se.isEmpty()) return false;
			return true;
		});
	}

	public int getAutoEpisodeLength(CCSeason season) {
		Integer i0 = season.getCommonEpisodeLength();
		if (i0 != null) return i0;

		Integer i1 = season.getConsensEpisodeLength();
		if (i1 != null) return i1;

		Integer i2 = season.getAverageEpisodeLength();
		if (i2 != null) return i2;

		for (CCSeason s : iteratorSeasons().reverse()) {
			if (s.isSpecialSeason()) continue;

			Integer i3 = season.getCommonEpisodeLength();
			if (i3 != null) return i3;

			Integer i4 = season.getConsensEpisodeLength();
			if (i4 != null) return i4;

			Integer i5 = season.getAverageEpisodeLength();
			if (i5 != null) return i5;
		}

		return 0;
	}

	public int getAutoEpisodeLength() {
		return _cache.getInt(SeriesCache.AUTO_EPISODE_LENGTH, null, ser->
		{
			Integer i0 = getCommonEpisodeLength();
			if (i0 != null) return i0;

			Integer i1 = getConsensEpisodeLength();
			if (i1 != null) return i1;

			Integer i2 = getAverageEpisodeLength();
			if (i2 != null) return i2;

			return 0;
		});
	}

	@Override
	public Integer getCommonEpisodeLength() {
		return _cache.get(SeriesCache.COMMON_EPISODE_LENGTH, null, ser->
		{
			if (getEpisodeCount() == 0) return null;

			int len = -1;
			for (CCEpisode ep : iteratorEpisodes()) {

				if (ep.Length.get() > 0)
					if (len > 0 && ep.Length.get() != len) {
						return null;
					} else {
						len = ep.Length.get();
					}
			}

			return len;
		});
	}

	public Integer getAverageEpisodeLength() {
		return _cache.get(SeriesCache.AVERAGE_EPISODE_LENGTH, null, ser->
		{
			if (getEpisodeCount() == 0) return null;

			return (int)Math.round((iteratorEpisodes().sumInt(e -> e.Length.get())*1d) / getEpisodeCount());
		});
	}

	@Override
	public Integer getConsensEpisodeLength() {
		return _cache.get(SeriesCache.CONSENS_EPISODE_LENGTH, null, ser->
		{
			if (getEpisodeCount() == 0) return null;

			Map.Entry<Integer, List<CCEpisode>> mostCommon = iteratorEpisodes().groupBy(e -> e.Length.get()).autosortByProperty(e -> e.getValue().size()).lastOrNull();
			if (mostCommon == null) return null;

			if (mostCommon.getValue().size() * 3 >= getEpisodeCount() * 2) { // 66% of episodes have same length
				return mostCommon.getKey();
			}

			return null;
		});
	}

	public CCEpisode getLastAddedEpisode() {
		return iteratorEpisodes().autosortByProperty(e -> e.AddDate.get()).lastOrNull();
	}

	@Override
	@SuppressWarnings("nls")
	public CCQualityCategory getMediaInfoCategory() {
		return _cache.get(SeriesCache.MEDIAINFO_CATEGORY, null, ser->
		{
			if (getEpisodeCount() == 0) return CCQualityCategory.UNSET;

			if (iteratorEpisodes().any(e -> e.MediaInfo.get().isUnset())) return CCQualityCategory.UNSET;

			double avg = iteratorEpisodes().map(e -> e.MediaInfo.get().getCategory(getSeries().Genres.get()).getCategoryType()).avgValueOrDefault(e -> (double)e.asInt(), 0);

			CCQualityResolutionType rtype = iteratorEpisodes()
					.map(e -> e.MediaInfo.get().getCategory(getSeries().Genres.get()).getResolutionType())
					.groupBy(e -> e)
					.map(Map.Entry::getKey)
					.singleOrDefault(null, null);

			if (rtype == null)
				rtype = CCQualityResolutionType.MULTIPLE;

			CCQualityCategoryType ct = CCQualityCategoryType.getWrapper().findOrNull((int)Math.round(avg));
			if (ct == null) return CCQualityCategory.UNSET; // should never happen ??

			String longtext = Str.format(LocaleBundle.getFormattedString("JMediaInfoControl.episodes", getEpisodeCount()));

			int minBitrate = iteratorEpisodes().map(e -> e.MediaInfo.get().getBitrate()).autoMinValueOrDefault(e -> e, 0);
			int maxBitrate = iteratorEpisodes().map(e -> e.MediaInfo.get().getBitrate()).autoMaxValueOrDefault(e -> e, 0);
			int minDuration = iteratorEpisodes().map(e -> (int)e.MediaInfo.get().getDuration()).autoMinValueOrDefault(e -> e, 0);
			int maxDuration = iteratorEpisodes().map(e -> (int)e.MediaInfo.get().getDuration()).autoMaxValueOrDefault(e -> e, 0);
			int minFramerate = iteratorEpisodes().map(e -> (int)Math.round(e.MediaInfo.get().getFramerate())).autoMinValueOrDefault(e -> e, 0);
			int maxFramerate = iteratorEpisodes().map(e -> (int)Math.round(e.MediaInfo.get().getFramerate())).autoMaxValueOrDefault(e -> e, 0);

			StringBuilder b = new StringBuilder();
			b.append("<html>");

			b.append("Bitrate (min): ").append(Str.spacegroupformat((int)Math.round(minBitrate / 1024.0))).append(" kbit/s").append("<br/>");
			b.append("Bitrate (max): ").append(Str.spacegroupformat((int)Math.round(maxBitrate / 1024.0))).append(" kbit/s").append("<br/>");

			b.append("Duration (min): ").append(TimeIntervallFormatter.formatSeconds(minDuration)).append("<br/>");
			b.append("Duration (max): ").append(TimeIntervallFormatter.formatSeconds(maxDuration)).append("<br/>");

			b.append("Framerate (min): ").append(minFramerate).append(" fps").append("<br/>");
			b.append("Framerate (max): ").append(maxFramerate).append(" fps").append("<br/>");

			b.append("</html>");
			String tooltip = b.toString();

			return new CCQualityCategory(ct, rtype, longtext, tooltip, (minBitrate + maxBitrate) / 2);
		});
	}

	@Override
	public Opt<CCDateTime> getLastViewed() {
		return _cache.get(SeriesCache.LAST_VIEWED, null, ser->
		{
			if (getEpisodeCount() == 0) return Opt.empty();

			var dt = iteratorEpisodes()
					.filter(CCEpisode::isViewed)
					.map(CCEpisode::getViewedHistoryLastDateTime)
					.sort(CCDateTime::compare)
					.lastOrNull();

			if (dt == null) return Opt.empty();

			return Opt.of(dt);
		});
	}
}
