package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import org.apache.commons.lang.text.StrBuilder;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.database.util.iterators.DirectEpisodesIterator;
import de.jClipCorn.database.util.iterators.DirectSeasonsIterator;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.comparator.CCSeasonComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStream;

public class CCSeries extends CCDatabaseElement {
	private final static int GUIDE_W_BORDER = 2;
	private final static int GUIDE_W_PADDING = 6;
	
	private List<CCSeason> seasons = new Vector<>();
	
	public CCSeries(CCMovieList ml, int id, int seriesID) {
		super(ml, CCDBElementTyp.SERIES, id, seriesID);
	}
	
	@Override
	protected void updateDB() {
		if (! isUpdating) {
			movielist.update(this);
		}
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
	
	@Override
	public CCQuality getQuality() {
		int qs = 0;
		int qc = 0;
		for (CCSeason se: seasons) {
			qc++;
			qs += se.getQuality().asInt();
		}
		
		if (qc > 0) {
			int qual = (int) Math.round((qs*1d) / qc);
			
			qual = Math.max(0, qual);
			qual = Math.min(qual, CCQuality.values().length - 1);
			
			CCQuality q = CCQuality.getWrapper().findOrNull(qual);
			if (q != null) return q;
			return getFirstEpisode().getQuality(); // shouldn't be possible
		} else {
			return CCQuality.STREAM;
		}
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
		return iteratorEpisodes().findMostCommon(e -> e.getFormat(), CCFileFormat.getWrapper().firstValue());
	}
	
	@Override
	public CCFileSize getFilesize() {
		if (CCMovieList.isBlocked()) {
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

	public CCDBLanguageList getCommonOrAllLanguages() {
		CCDBLanguageList com = getCommonLanguages();
		return com.isEmpty() ? getAllLanguages() : com;
	}

	@SuppressWarnings("nls")
	public String getFolderNameForCreatedFolderStructure() {
		StringBuilder seriesfoldername = new StringBuilder(getTitle());
		
		for (CCGroup group : getGroups()) {
			if (group.DoSerialize) seriesfoldername.append(" [[").append(group.Name).append("]]");
		}

		CCDBLanguageList lang = getCommonLanguages();
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
	
	public CCStream<CCEpisode> iteratorEpisodes() {
		return new DirectEpisodesIterator(this);
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

	public CCEpisode getLastAddedEpisode() {
		return iteratorEpisodes().autosortByProperty(CCEpisode::getAddDate).lastOrNull();
	}
}
