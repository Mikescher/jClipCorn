package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.text.StrBuilder;
import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.util.NextEpisodeHelper;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.database.util.iterators.DirectEpisodesIterator;
import de.jClipCorn.database.util.iterators.DirectSeasonsIterator;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.comparator.CCSeasonComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStream;

public class CCSeries extends CCDatabaseElement  {
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
			
			return CCQuality.getWrapper().find(qual);
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
		default:
			return null;
		}
	}
	
	public CCDate calcMaximumAddDate() {
		CCDate cd = CCDate.getMinimumDate();
		for (CCSeason se: seasons) {
			CCDate scd = se.calcMaximumAddDate();
			
			if (scd.isGreaterThan(cd)) {
				cd = scd;
			}
		}
		return cd;
	}
	
	public CCDate calcMinimumAddDate() {
		CCDate cd = CCDate.getMaximumDate();
		for (CCSeason se: seasons) {
			CCDate scd = se.calcMinimumAddDate();
			
			if (scd.isLessThan(cd)) {
				cd = scd;
			}
		}
		return cd;
	}
	
	public CCDate calcAverageAddDate() {
		List<CCDate> dlist = new ArrayList<>();
		for (CCSeason se: seasons) {
			dlist.add(se.calcAverageAddDate());
		}
		
		if (dlist.isEmpty()) {
			return CCDate.getMinimumDate();
		}
		
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
	
	@Override
	public CCTagList getTags() {
		CCTagList i = new CCTagList();
		
		for (int j = 0; j < getSeasonCount(); j++) {
			i.doUnion(getSeasonByArrayIndex(j).getTags());
		}
		
		return i;
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
		
		if ((! (season.getCoverName() == null)) && (!season.getCoverName().isEmpty())) {
			getMovieList().getCoverCache().deleteCover(season.getCoverName());
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
		List<CCSeason> sortedseasons = new ArrayList<>();
		
		for (CCSeason cs : seasons) {
			sortedseasons.add(cs);
		}
		
		Collections.sort(sortedseasons, new CCSeasonComparator());
		
		return sortedseasons;
	}
	
	public int findSeasoninSorted(CCSeason ccSeason) {
		return getSeasonsSorted().indexOf(ccSeason);
	}
	
	@Override
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash, boolean coverData) {
		super.setXMLAttributes(e, fileHash, coverHash, coverData);
		
		// series has no more attributes than CCDatabaseElement - for now
	}
	
	@Override
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags, boolean ignoreCoverData) throws CCFormatException {
		beginUpdating();
		
		super.parseFromXML(e, resetAddDate, resetViewed, resetScore, resetTags, ignoreCoverData);
		
		for (Element e2 : e.getChildren("season")) {
			createNewEmptySeason().parseFromXML(e2, resetAddDate, resetViewed, resetTags);
		}
		
		endUpdating();
	}
	
	@Override
	public Element generateXML(Element el, boolean fileHash, boolean coverHash, boolean coverData) {
		Element ser = super.generateXML(el, fileHash, coverHash, coverData);
		
		for (int i = 0; i < seasons.size(); i++) {
			seasons.get(i).generateXML(ser, fileHash, coverHash, coverData);
		}
		
		return ser;
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
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, CCDateTimeList.createEmpty());
		else if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue() && isPartialViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.PARTIAL_VIEWED, CCDateTimeList.createEmpty());
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, CCDateTimeList.createEmpty());
	}

	@SuppressWarnings("nls")
	public String getFolderNameForCreatedFolderStructure() {
		String seriesfoldername = getTitle();
		
		for (CCGroup group : getGroups()) {
			if (group.DoSerialize) seriesfoldername += " [["+group.Name+"]]";
		}
		
		if (getLanguage() != CCDBLanguage.GERMAN) {
			seriesfoldername += String.format(" [%s]", getLanguage().getShortString());
		}
		
		seriesfoldername = PathFormatter.fixStringToFilesystemname(seriesfoldername);
		
		return seriesfoldername;
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
}
