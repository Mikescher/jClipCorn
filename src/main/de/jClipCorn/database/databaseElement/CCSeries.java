package de.jClipCorn.database.databaseElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.text.StrBuilder;
import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.comparator.CCSeasonComparator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;

public class CCSeries extends CCDatabaseElement {
	private final static int GUIDE_W_BORDER = 2;
	private final static int GUIDE_W_PADDING = 6;
	
	private List<CCSeason> seasons = new Vector<>();
	
	public CCSeries(CCMovieList ml, int id, int seriesID) {
		super(ml, CCMovieTyp.SERIES, id, seriesID);
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
	
	public CCMovieQuality getQuality() {
		int qs = 0;
		int qc = 0;
		for (CCSeason se: seasons) {
			qc++;
			qs += se.getQuality().asInt();
		}
		
		if (qc > 0) {
			int qual = (int) Math.round((qs*1d) / qc);
			
			qual = Math.max(0, qual);
			qual = Math.min(qual, CCMovieQuality.values().length - 1);
			
			return CCMovieQuality.find(qual);
		} else {
			return CCMovieQuality.STREAM;
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
		case 0:
			return calcMinimumAddDate();
		case 1:
			return calcMaximumAddDate();
		case 2:
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
	
	public CCMovieFormat getFormat() {
		int l = CCMovieFormat.values().length;
		int[] ls = new int[l];
		for (int i = 0; i < l; i++) {
			ls[i] = 0;
		}
		
		for (CCSeason se: seasons) {
			ls[se.getFormat().asInt()]++;
		}
		
		int max = 0;
		int maxid = 0;
		for (int i = 0; i < l; i++) {
			if (ls[i] > max) {
				max = ls[i];
				maxid = i;
			}
		}
		
		return CCMovieFormat.find(maxid);
	}
	
	@Override
	public CCMovieSize getFilesize() {
		if (CCMovieList.isBlocked()) {
			return new CCMovieSize();
		}
		
		CCMovieSize sz = new CCMovieSize();
		
		for (CCSeason se: seasons) {
			sz.add(se.getFilesize());
		}
		
		return sz;
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
	
	public CCMovieTags getTags() {
		CCMovieTags i = new CCMovieTags();
		
		for (int j = 0; j < getSeasonCount(); j++) {
			i.doUnion(getSeason(j).getTags());
		}
		
		return i;
	}

	public CCSeason getSeason(int ss) {
		if (ss < 0 || ss >= seasons.size())
			return null;
		
		return seasons.get(ss);
	}

	public void deleteSeason(CCSeason season) {
		seasons.remove(season);
		
		for (int i = season.getEpisodeCount()-1; i >= 0; i--) {
			season.deleteEpisode(season.getEpisode(i));
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
			result.addAll(getSeason(i).getAbsolutePathList());
		}
		
		return result;
	}
	
	public boolean isFileInList(String path) {
		for (int i = 0; i < seasons.size(); i++) {
			CCSeason s = getSeason(i);
			if(s.isFileInList(path)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<CCEpisode> getEpisodeList() {
		List<CCEpisode> result = new ArrayList<>();
		
		for (int i = 0; i < getSeasonCount(); i++) {
			result.addAll(getSeason(i).getEpisodeList());
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
		List<CCEpisode> el = getEpisodeList();
		
		if (el.size() == 0) {
			return null;
		}
		
		if (isViewed()) {
			CCDate d = CCDate.getMinimumDate();
			
			for (int i = 0; i < el.size(); i++) {
				if (d.isGreaterThan(el.get(i).getViewedHistoryLast())) {
					return el.get(i);
				}
				d = el.get(i).getViewedHistoryLast();
			}
			
			return el.get(0);
		} else {
			for (int i = 0; i < el.size(); i++) {
				if (! el.get(i).isViewed()) {
					return el.get(i);
				}
			}
		}
		
		return null;
	}

	public int findSeason(CCSeason ccSeason) {
		return seasons.indexOf(ccSeason);
	}
	
	public int findSeasoninSorted(CCSeason ccSeason) {
		List<CCSeason> sortedseasons = new ArrayList<>();
		
		for (CCSeason cs : seasons) {
			sortedseasons.add(cs);
		}
		
		Collections.sort(sortedseasons, new CCSeasonComparator());
		
		return sortedseasons.indexOf(ccSeason);
	}
	
	@SuppressWarnings("nls")
	@Override
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash, boolean coverData) {
		super.setXMLAttributes(e, fileHash, coverHash, coverData);
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
		
		if (coverData) {
			e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(getCover())));
		}
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
	
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
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
			CCSeason season = getSeason(i);
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
				CCEpisode episode = season.getEpisode(j);
				guide.appendPadding(GUIDE_W_BORDER, ' ');
				guide.appendln(String.format("> [%02d] %s (%s)", episode.getEpisode(), episode.getTitle(), TimeIntervallFormatter.formatPointed(episode.getLength()))); //$NON-NLS-1$
			}
		}

		return guide.toString();
	}
	
	public String getCommonPathStart(boolean extendedSearch) {
		List<String> all = new ArrayList<>();
		
		for (int seasi = 0; seasi < getSeasonCount(); seasi++) {
			CCSeason season = getSeason(seasi);
			all.add(season.getCommonPathStart());
		}
		
		while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$
		
		String common = PathFormatter.getCommonFolderPath(all);
		
		if (extendedSearch && common.isEmpty()) {
			common = movielist.getCommonSeriesPath();
		}
		
		return common;
	}

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
			seriesfoldername += " [["+group.Name+"]]";
		}
		
		if (getLanguage() != CCMovieLanguage.GERMAN) {
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
		
		CCSeason zero = getSeason(0);
		
		if (zero.getIndexForCreatedFolderStructure() == 0) {
			CCSeason first = getSeason(1);
			if (first == null) {
				return zero;
			} else {
				return first;
			}
		} else {
			return zero;
		}
	}
}