package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.*;
import de.jClipCorn.database.util.iterators.DirectEpisodesIterator;
import de.jClipCorn.database.util.iterators.DirectSeasonsIterator;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.comparator.CCSeasonComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStream;
import org.apache.commons.lang.text.StrBuilder;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCSeries extends CCDatabaseElement implements IEpisodeOwner {
	private final static int GUIDE_W_BORDER = 2;
	private final static int GUIDE_W_PADDING = 6;
	
	private final List<CCSeason> seasons = new Vector<>();
	
	public CCSeries(CCMovieList ml, int id) {
		super(ml, CCDBElementTyp.SERIES, id);
	}
	
	@Override
	protected boolean updateDB() {
		if (! isUpdating) {
			return movielist.update(this);
		}
		return true;
	}

	public void directlyInsertSeason(CCSeason s) { // !! ONLY CALLED FROM CCDatabase
		seasons.add(s);
	}

	public void enforceOrder() {
		seasons.sort(Comparator.comparingInt(CCSeason::getSortedSeasonNumber));
	}

	public CCSeason createNewEmptySeason() {
		return movielist.createNewEmptySeason(this);
	}
	
	public boolean isViewed() { // All parts viewed
		boolean v = true;
		for (CCSeason se: seasons) {
			v &= se.isViewed();
		}
		return v;
	}
	
	public boolean isUnviewed() { // All parts not viewed
		boolean v = true;
		for (CCSeason se: seasons) {
			v &= se.isUnviewed();
		}
		return v;
	}
	
	public boolean isPartialViewed() { // Some parts viewed - some not
		return !(isViewed() || isUnviewed());
	}
	
	public int getSeasonCount() {
		return seasons.size();
	}
	
	public int getEpisodeCount() {
		int c = 0;
		for (CCSeason se : seasons) {
			c += se.getEpisodeCount();
		}
		return c;
	}
	
	public int getLength() {
		int l = 0;
		for (CCSeason se: seasons) {
			l += se.getLength();
		}
		return l;
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
		CCDate cd = CCDate.getMinimumDate();
		for (CCSeason se: seasons) {
			if (se.getEpisodeCount()==0) continue;
			CCDate scd = se.calcMinimumAddDate();
			if (scd.isGreaterThan(cd)) cd = scd;
		}
		return cd;
	}
	
	public CCDate calcMaximumAddDate() {
		CCDate cd = CCDate.getMinimumDate();
		for (CCSeason se: seasons) {
			if (se.getEpisodeCount()==0) continue;

			CCDate scd = se.calcMaximumAddDate();
			if (scd.isGreaterThan(cd)) cd = scd;
		}
		return cd;
	}
	
	public CCDate calcMinimumAddDate() {
		CCDate cd = CCDate.getMaximumDate();
		for (CCSeason se: seasons) {
			if (se.getEpisodeCount()==0) continue;

			CCDate scd = se.calcMinimumAddDate();
			if (scd.isLessThan(cd)) cd = scd;
		}
		return cd;
	}
	
	public CCDate calcAverageAddDate() {
		List<CCDate> dlist = new ArrayList<>();
		for (CCSeason se: seasons) {
			if (se.getEpisodeCount()==0) continue;

			dlist.add(se.calcAverageAddDate());
		}
		
		if (dlist.isEmpty()) return CCDate.getMinimumDate();
		
		return CCDate.getAverageDate(dlist);
	}
	
	@Override
	public CCFileFormat getFormat() {
		return iteratorEpisodes().findMostCommon(CCEpisode::getFormat, CCFileFormat.getWrapper().firstValue());
	}
	
	@Override
	public CCFileSize getFilesize() {
		if (movielist.isBlocked()) {
			return CCFileSize.ZERO;
		}
		
		long sz = 0;
		
		for (CCSeason se: seasons) {
			sz += se.getFilesize().getBytes();
		}
		
		return new CCFileSize(sz);
	}
	
	public YearRange getYearRange() {
		int miny = Integer.MAX_VALUE;
		int maxy = Integer.MIN_VALUE;
		
		for (CCSeason se: seasons) {
			miny = Math.min(miny, se.getYear());
			maxy = Math.max(maxy, se.getYear());
		}
		return new YearRange(miny, maxy);
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
	}

	public int getViewedCount() {
		int v = 0;
		for (CCSeason se : seasons) {
			v += se.getViewedCount();
		}
		return v;
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
		return getTitle();
	}

	@Override
	public String toString() {
		return getTitle();
	}
	
	public String getEpisodeGuide() {
		StrBuilder guide = new StrBuilder();
		
		int titlewidth = GUIDE_W_BORDER + GUIDE_W_PADDING + getTitle().length() + GUIDE_W_PADDING + GUIDE_W_BORDER;
		
		guide.appendNewLine();
		guide.appendPadding(titlewidth, '#');
		guide.appendNewLine();
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendPadding(titlewidth - (GUIDE_W_BORDER + GUIDE_W_BORDER), ' ');
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendNewLine();
		guide.appendPadding(GUIDE_W_BORDER, '#');
		guide.appendPadding(GUIDE_W_PADDING, ' ');
		guide.append(getTitle());
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
			String seasontitle = season.getTitle() + ' ' + '(' + season.getYear() + ')';
			
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
				guide.appendln(String.format("> [%02d] %s (%s)", episode.getEpisodeNumber(), episode.getTitle(), TimeIntervallFormatter.formatPointed(episode.getLength()))); //$NON-NLS-1$
			}
		}

		return guide.toString();
	}
	
	public String getCommonPathStart(boolean extendedSearch) {
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
	}
	
	public String guessSeriesRootPath() {
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
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());

		if (getTag(CCTagList.TAG_WATCH_NEVER))
			return new ExtendedViewedState(ExtendedViewedStateType.MARKED_FOR_NEVER, CCDateTimeList.createEmpty(), getFullViewCount());

		if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue() && isPartialViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.PARTIAL_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());

		return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());
	}

	private int getFullViewCount() {
		int vc = Integer.MAX_VALUE;
		for (CCSeason s : seasons) {
			for (CCEpisode e : s.getEpisodeList()) {
				vc = Math.min(e.getViewedHistory().count(), vc);
			}
		}
		if (vc == Integer.MAX_VALUE) return 0;
		return vc;
	}

	public CCDBLanguageList getAllLanguages() {
		HashSet<CCDBLanguage> langs = new HashSet<>();
		for (CCSeason s : seasons)
		{
			for (CCEpisode e : s.getEpisodeList())
			{
				langs.addAll(e.getLanguage().getInternalData());
			}
		}

		return CCDBLanguageList.createDirect(langs);
	}

	public CCDBLanguageList getCommonLanguages() {
		HashSet<CCDBLanguage> langs = null;
		for (CCSeason s : seasons)
		{
			for (CCEpisode e : s.getEpisodeList())
			{
				if (langs == null) { langs = new HashSet<>(e.getLanguage().getInternalData()); continue; }
				langs.retainAll(e.getLanguage().getInternalData());
				if (langs.size()==0) return CCDBLanguageList.EMPTY;
			}
		}
		if (langs == null) return CCDBLanguageList.EMPTY;
		return CCDBLanguageList.createDirect(langs);
	}

	public CCDBLanguageList getSemiCommonOrAllLanguages() {
		CCDBLanguageList com = getSemiCommonLanguages();
		return com.isEmpty() ? getAllLanguages() : com;
	}

	public CCDBLanguageList getSemiCommonLanguages() {
		if (getEpisodeCount() == 0) return CCDBLanguageList.EMPTY;

		var firstEpisode = getFirstEpisode();

		HashSet<CCDBLanguage> langsCommon = new HashSet<>(firstEpisode.getLanguage().getInternalData());
		CCDBLanguageList langsEqual  = firstEpisode.getLanguage();

		HashMap<CCDBLanguageList, Integer> langsLength = new HashMap<>();
		int totalLength = 0;

		for (CCSeason s : seasons)
		{
			for (CCEpisode e : s.getEpisodeList())
			{
				langsCommon.retainAll(e.getLanguage().getInternalData());

				if (langsEqual != null && !langsEqual.equals(e.getLanguage())) langsEqual = null;

				int addlen = e.getLength();
				if (langsLength.containsKey(e.getLanguage())) addlen += langsLength.get(e.getLanguage());
				langsLength.put(e.getLanguage(), addlen);

				totalLength+= e.getLength();
			}
		}

		if (langsEqual != null) return langsEqual;

		var maxPerc = CCProperties.getInstance().PROP_FOLDERLANG_IGNORE_PERC.getValue();

		if (maxPerc == 0) return CCDBLanguageList.createDirect(langsCommon);

		for (var lng : langsLength.entrySet())
		{
			if (((totalLength - lng.getValue()) * 100 / totalLength) < maxPerc) return lng.getKey();
		}

		return CCDBLanguageList.createDirect(langsCommon);
	}

	@SuppressWarnings("nls")
	public String getFolderNameForCreatedFolderStructure(CCDBLanguageList fallbackLanguage) {
		StringBuilder seriesfoldername = new StringBuilder(getTitle());
		
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
	}

	@SuppressWarnings("nls")
	public Pattern getValidSeasonIndexRegex() {
		Pattern regexInt = Pattern.compile("^[0-9]+$");
		
		for (String rex : CCProperties.getInstance().PROP_SEASON_INDEX_REGEXPRESSIONS.getNonEmptyValues()) {
			Pattern p = Pattern.compile("^" + rex + "$");
			
			boolean fitsAllSeasons = true;
			HashSet<Integer> indiMap = new HashSet<>();
			for (CCSeason season : seasons) {
				Matcher m = p.matcher(season.getTitle());

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
		return getTitle();
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
		for (CCSeason se : seasons) if (!se.isEmpty()) return false;
		return true;
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
		Integer i0 = getCommonEpisodeLength();
		if (i0 != null) return i0;

		Integer i1 = getConsensEpisodeLength();
		if (i1 != null) return i1;

		Integer i2 = getAverageEpisodeLength();
		if (i2 != null) return i2;

		return 0;
	}

	@Override
	public Integer getCommonEpisodeLength() {
		if (getEpisodeCount() == 0) return null;

		int len = -1;
		for (CCEpisode ep : iteratorEpisodes()) {

			if (ep.getLength() > 0)
				if (len > 0 && ep.getLength() != len) {
					return null;
				} else {
					len = ep.getLength();
				}
		}

		return len;
	}

	public Integer getAverageEpisodeLength() {
		if (getEpisodeCount() == 0) return null;

		return (int)Math.round((iteratorEpisodes().sumInt(CCEpisode::getLength)*1d) / getEpisodeCount());
	}

	@Override
	public Integer getConsensEpisodeLength() {
		if (getEpisodeCount() == 0) return null;

		Map.Entry<Integer, List<CCEpisode>> mostCommon = iteratorEpisodes().groupBy(CCEpisode::getLength).autosortByProperty(e -> e.getValue().size()).lastOrNull();
		if (mostCommon == null) return null;

		if (mostCommon.getValue().size() * 3 >= getEpisodeCount() * 2) { // 66% of episodes have same length
			return mostCommon.getKey();
		}

		return null;
	}

	public CCEpisode getLastAddedEpisode() {
		return iteratorEpisodes().autosortByProperty(CCEpisode::getAddDate).lastOrNull();
	}

	@Override
	@SuppressWarnings("nls")
	public CCQualityCategory getMediaInfoCategory() {
		if (getEpisodeCount() == 0) return CCQualityCategory.UNSET;

		if (iteratorEpisodes().any(e -> e.getMediaInfo().isUnset())) return CCQualityCategory.UNSET;

		double avg = iteratorEpisodes().map(e -> e.getMediaInfo().getCategory(getSeries().getGenres()).getCategoryType()).avgValueOrDefault(e -> (double)e.asInt(), 0);

		CCQualityResolutionType rtype = iteratorEpisodes()
				.map(e -> e.getMediaInfo().getCategory(getSeries().getGenres()).getResolutionType())
				.groupBy(e -> e)
				.map(Map.Entry::getKey)
				.singleOrDefault(null, null);

		if (rtype == null)
			rtype = CCQualityResolutionType.MULTIPLE;

		CCQualityCategoryType ct = CCQualityCategoryType.getWrapper().findOrNull((int)Math.round(avg));
		if (ct == null) return CCQualityCategory.UNSET; // should never happen ??

		String longtext = Str.format(LocaleBundle.getFormattedString("JMediaInfoControl.episodes", getEpisodeCount()));

		int minBitrate = iteratorEpisodes().map(e -> e.getMediaInfo().getBitrate()).autoMinValueOrDefault(e -> e, 0);
		int maxBitrate = iteratorEpisodes().map(e -> e.getMediaInfo().getBitrate()).autoMaxValueOrDefault(e -> e, 0);
		int minDuration = iteratorEpisodes().map(e -> (int)e.getMediaInfo().getDuration()).autoMinValueOrDefault(e -> e, 0);
		int maxDuration = iteratorEpisodes().map(e -> (int)e.getMediaInfo().getDuration()).autoMaxValueOrDefault(e -> e, 0);
		int minFramerate = iteratorEpisodes().map(e -> (int)Math.round(e.getMediaInfo().getFramerate())).autoMinValueOrDefault(e -> e, 0);
		int maxFramerate = iteratorEpisodes().map(e -> (int)Math.round(e.getMediaInfo().getFramerate())).autoMaxValueOrDefault(e -> e, 0);

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
	}
}
