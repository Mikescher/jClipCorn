package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.caches.SeasonCache;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.ISeasonData;
import de.jClipCorn.database.elementValues.*;
import de.jClipCorn.database.util.*;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.DatabaseUpdateException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCSeason implements ICCDatedElement, ICCDatabaseStructureElement, ICCCoveredElement, IActionSourceObject, IEpisodeOwner, ISeasonData, IPropertyParent {
	private final CCSeries owner;
	private final int seasonID;

	private int coverid;

	private final SeasonCache _cache = new SeasonCache(this);

	public final EStringProp Title = new EStringProp("Title", Str.Empty, this, EPropertyType.OBJECTIVE_METADATA);
	public final EIntProp    Year  = new EIntProp(   "Year",  1900,      this, EPropertyType.OBJECTIVE_METADATA);

	private IEProperty[] _properties = null;

	private final List<CCEpisode> episodes = new Vector<>();

	private boolean isUpdating = false;
	
	public CCSeason(CCSeries owner, int seasonID) {
		this.owner    = owner;
		this.seasonID = seasonID;
	}

	public IEProperty[] GetProperties()
	{
		if (_properties == null) _properties = ListProperties();
		return _properties;
	}

	protected IEProperty[] ListProperties()
	{
		return new IEProperty[]
		{
			Title,
			Year,
		};
	}

	public EIntProp    year()  { return  Year;  }
	public EStringProp title() { return  Title; }

	public void setDefaultValues(boolean updateDB) {
		beginUpdating();

		coverid = -1;
		for (IEProperty prop : GetProperties()) prop.resetToDefault();

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
			return owner.getMovieList().update(this);
		}
		return true;
	}

	public void updateDBWithException() throws DatabaseUpdateException {
		var ok = updateDB();
		if (!ok) throw new DatabaseUpdateException("updateDB() failed"); //$NON-NLS-1$
	}
	
	public void directlyInsertEpisode(CCEpisode ep) {
		episodes.add(ep);
		_cache.bust();
	}

	public void enforceOrder() {
		episodes.sort(Comparator.comparingInt(e -> e.EpisodeNumber.get()));
		_cache.bust();
	}

	@Override
	public int getLocalID() {
		return seasonID;
	}

	public void setCover(int cid) {
		this.coverid = cid;
		
		_cache.bust();
		updateDB();
	}
	
	public void setCover(BufferedImage cvr) {
		if (coverid != -1 && cvr.equals(getCover())) {
			return;
		}
		
		if (coverid != -1) {
			getSeries().getMovieList().getCoverCache().deleteCover(this.coverid);
		}
		
		this.coverid = getSeries().getMovieList().getCoverCache().addCover(cvr);
		
		_cache.bust();
		updateDB();
	}

	@Override
	public CCSeries getSeries() {
		return owner;
	}

	@Override
	public String getQualifiedTitle() {
		return Str.format("{0} - {1}", getSeries().Title.get(), Title.get()); //$NON-NLS-1$
	}

	@Override
	public int getCoverID() {
		return coverid;
	}

	@Override
	public String getTitle() {
		return Title.get();
	}

	@Override
	public int getYear() {
		return Year.get();
	}

	@Override
	public BufferedImage getCover() {
		return owner.getMovieList().getCoverCache().getCover(coverid);
	}

	@Override
	public Tuple<Integer, Integer> getCoverDimensions() {
		return owner.getMovieList().getCoverCache().getDimensions(coverid);
	}

	@Override
	public CCCoverData getCoverInfo() {
		return owner.getMovieList().getCoverCache().getInfoOrNull(coverid);
	}
	
	public boolean isViewed() { // All parts viewed
		return _cache.getBool(SeasonCache.IS_VIEWED, null, sea->
		{
			boolean v = true;
			for (CCEpisode ep : episodes) {
				v &= ep.isViewed(); // Shortcut Evaluation ftw
			}
			return v;
		});
	}
	
	public boolean isUnviewed() { // All parts not viewed
		return _cache.getBool(SeasonCache.IS_UNVIEWED, null, sea->
		{
			boolean v = true;
			for (CCEpisode ep : episodes) {
				v &= ! ep.isViewed(); // Shortcut Evaluation ftw
			}
			return v;
		});
	}
	
	public boolean isPartialViewed() { // Some parts viewed - some not
		return !(isViewed() || isUnviewed());
	}

	@Override
	public CCTagList getTags() {
		return _cache.get(SeasonCache.TAGS, null, sea->
		{
			CCTagList i = CCTagList.EMPTY;

			for (int j = 0; j < getEpisodeCount(); j++) {
				i = i.getUnion(getEpisodeByArrayIndex(j).Tags.get());
			}
			return i;
		});
	}
	
	public int getEpisodeCount() {
		return episodes.size();
	}
	
	public boolean isEmpty() {
		return episodes.isEmpty();
	}
	
	public int getLength() {
		return _cache.get(SeasonCache.LENGTH, null, sea->
		{
			int l = 0;
			for (CCEpisode ep : episodes) {
				l += ep.Length.get();
			}
			return l;
		});
	}
	
	@Override
	public CCDate getAddDate() {
		switch (CCProperties.getInstance().PROP_SERIES_ADDDATECALCULATION.getValue()) {
			case OLDEST_DATE:
			case NEWEST_BY_SEASON:
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
		return _cache.get(SeasonCache.MAXIMUM_ADDDATE, null, sea->
		{
			CCDate cd = CCDate.getMinimumDate();
			for (CCEpisode ep : episodes) {
				if (ep.getAddDate().isGreaterThan(cd)) {
					cd = ep.getAddDate();
				}
			}
			return cd;
		});
	}
	
	public CCDate calcMinimumAddDate() {
		return _cache.get(SeasonCache.MINIMUM_ADDDATE, null, sea->
		{
			CCDate cd = CCDate.getMaximumDate();
			for (CCEpisode ep : episodes) {
				if (ep.getAddDate().isLessThan(cd)) {
					cd = ep.getAddDate();
				}
			}
			return cd;
		});
	}
	
	public CCDate calcAverageAddDate() {
		return _cache.get(SeasonCache.AVERAGE_ADDDATE, null, sea->
		{
			List<CCDate> dlist = new ArrayList<>();
			for (CCEpisode ep : episodes) {
				dlist.add(ep.getAddDate());
			}
			return CCDate.getAverageDate(dlist);
		});
	}
	
	@Override
	public CCFileFormat getFormat() {
		return _cache.get(SeasonCache.FORMAT, null, sea->
		{
			int l = CCFileFormat.values().length;
			int[] ls = new int[l];
			for (int i = 0; i < l; i++) {
				ls[i] = 0;
			}

			for (CCEpisode ep : episodes) {
				ls[ep.getFormat().asInt()]++;
			}

			int max = 0;
			int maxid = 0;
			for (int i = 0; i < l; i++) {
				if (ls[i] > max) {
					max = ls[i];
					maxid = i;
				}
			}

			CCFileFormat f = CCFileFormat.getWrapper().findOrNull(maxid);
			if (f != null) return f;
			return getFirstEpisode().getFormat(); // shouldn't be possible
		});
	}
	
	public CCFileSize getFilesize() {
		return _cache.get(SeasonCache.FILESIZE, null, sea->
		{
			long sz = 0;

			for (CCEpisode ep : episodes) {
				sz += ep.FileSize.get().getBytes();
			}

			return new CCFileSize(sz);
		});
	}

	/**
	 * ATTENTION: gets the n-te Episode | NOT THE Episode with the number n
	 */
	public CCEpisode getEpisodeByArrayIndex(int ee) {
		return episodes.get(ee);
	}
	
	public CCEpisode getEpisodeByNumber(int en) {
		for (int i = 0; i < episodes.size(); i++) {
			if (episodes.get(i).EpisodeNumber.get() == en) {
				return episodes.get(i);
			}
		}
		return null;
	}

	public void deleteEpisode(CCEpisode episode) {
		episodes.remove(episode);
		
		getSeries().getMovieList().removeEpisodeFromDatabase(episode);
		
		getMovieList().fireOnChangeDatabaseElement(getSeries());

		_cache.bust();
	}
	
	@Override
	public CCMovieList getMovieList() {
		return getSeries().getMovieList();
	}

	@Override
	public ICalculationCache getCache() {
		return _cache;
	}

	public int getViewedCount() {
		return _cache.getInt(SeasonCache.VIEWED_COUNT, null, sea->
		{
			int v = 0;
			for (CCEpisode ep : episodes) {
				v += ep.isViewed() ? 1 : 0;
			}
			return v;
		});
	}
	
	public CCEpisode createNewEmptyEpisode() {
		return getMovieList().createNewEmptyEpisode(this);
	}
	
	public int getNewUnusedEpisodeNumber() {
		for (int i = 1;; i++) {
			if (getEpisodeByNumber(i) == null) {
				return i;
			}
		}
	}
	
	@Override
	public Integer getCommonEpisodeLength() {
		return _cache.get(SeasonCache.COMMON_EPISODE_LENGTH, null, sea->
		{
			if (getEpisodeCount() == 0) return null;

			int len = -1;
			for (int i = 0; i < getEpisodeCount(); i++) {
				CCEpisode ep = getEpisodeByArrayIndex(i);

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
		return _cache.get(SeasonCache.AVERAGE_EPISODE_LENGTH, null, sea->
		{
			if (getEpisodeCount() == 0) return null;

			return (int)Math.round((iteratorEpisodes().sumInt(e -> e.Length.get())*1d) / getEpisodeCount());
		});
	}

	@Override
	public Integer getConsensEpisodeLength() {
		return _cache.get(SeasonCache.CONSENS_EPISODE_LENGTH, null, sea->
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
	
	public int getNextEpisodeNumber() {
		return _cache.get(SeasonCache.NEXT_EPISODE_NUMBER, null, sea->
		{
			int ep = 0;

			for (CCEpisode ccEpisode : episodes) {
				ep = Math.max(ep, ccEpisode.EpisodeNumber.get());
			}

			return ep + 1;
		});
	}

	public void delete() {
		getSeries().deleteSeason(this);
	}
	
	public CCEpisode getFirstEpisode() {
		if (episodes.size() > 0) {
			return episodes.get(0);
		} else {
			return null;
		}
	}
	
	public List<File> getAbsolutePathList() {
		List<File> result = new ArrayList<>();
		
		for (int i = 0; i < episodes.size(); i++) {
			result.add(new File(getEpisodeByArrayIndex(i).getAbsolutePart()));
		}
		
		return result;
	}
	
	public boolean isFileInList(String path) {
		for (int i = 0; i < episodes.size(); i++) {
			if (getEpisodeByArrayIndex(i).getAbsolutePart().equals(path)) {
				return true;
			}
		}
		
		return false;
	}

	public List<CCEpisode> getEpisodeList() {
		return episodes;
	}
	
	/**
	 * @return the Number of the Season (as it is in the Series-List) (NOT THE ID)
	 */
	public int getSeasonNumber() {
		return getSeries().findSeason(this);
	}
	
	/**
	 * @return the Number of the Season (as it should be) (NOT THE ID)
	 */
	public int getSortedSeasonNumber() {
		return getSeries().findSeasoninSorted(this);
	}

	public int findEpisode(CCEpisode ccEpisode) {
		return episodes.indexOf(ccEpisode);
	}

	@Override
	public String toString() {
		return Title.get();
	}

	public String getCommonPathStart() {
		return _cache.get(SeasonCache.COMMON_PATH_START, null, sea->
		{
			List<String> all = new ArrayList<>();

			for (int seasi = 0; seasi < getEpisodeCount(); seasi++) {
				CCEpisode episode = getEpisodeByArrayIndex(seasi);
				all.add(episode.Part.get());
			}

			while (all.contains("")) all.remove(""); //$NON-NLS-1$ //$NON-NLS-2$

			return PathFormatter.getCommonFolderPath(all);
		});
	}
	
	public String getFolderNameForCreatedFolderStructure() {
		return PathFormatter.fixStringToFilesystemname(Title.get());
	}

	public int getIndexForCreatedFolderStructure() {
		Pattern regex = owner.getValidSeasonIndexRegex();
		
		if (regex != null) {
			Matcher m = regex.matcher(Title.get());
			m.matches();
			return Integer.parseInt(m.group("index")); //$NON-NLS-1$
		}
		
		return getSortedSeasonNumber() + 1;
	}

	public boolean isSpecialSeason() {
		return getIndexForCreatedFolderStructure() == 0;
	}
	
	public int getFirstEpisodeNumber() {
		return _cache.getInt(SeasonCache.FIRST_EPISODE_NUMBER, null, sea->
		{
			if (getEpisodeCount() == 0) return -1;

			int n = episodes.get(0).EpisodeNumber.get();

			for (int i = 0; i < episodes.size(); i++) {
				n = Math.min(n, episodes.get(i).EpisodeNumber.get());
			}

			return n;
		});
	}
	
	public int getLastEpisodeNumber() {
		return _cache.getInt(SeasonCache.LAST_EPISODE_NUMBER, null, sea->
		{
			if (getEpisodeCount() == 0) return -1;

			int n = episodes.get(0).EpisodeNumber.get();

			for (int i = 0; i < episodes.size(); i++) {
				n = Math.max(n, episodes.get(i).EpisodeNumber.get());
			}

			return n;
		});
	}

	public boolean isContinoousEpisodeNumbers() {
		return _cache.getBool(SeasonCache.IS_CONTINOOUS_EPISODE_NUMBERS, null, sea->
		{
			if (getEpisodeCount() == 0) return true;

			int first = getFirstEpisodeNumber();
			int last = getLastEpisodeNumber();

			if (getEpisodeCount() != (1 + last - first))
				return false;

			for (int i = first; i < last; i++) {
				if (getEpisodeByNumber(i) == null)
					return false;
			}

			return true;
		});
	}

	@Override
	public ExtendedViewedState getExtendedViewedState() {
		if (isViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());
		else if (CCProperties.getInstance().PROP_SHOW_PARTIAL_VIEWED_STATE.getValue() && isPartialViewed())
			return new ExtendedViewedState(ExtendedViewedStateType.PARTIAL_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());
		else
			return new ExtendedViewedState(ExtendedViewedStateType.NOT_VIEWED, CCDateTimeList.createEmpty(), getFullViewCount());
	}

	private int getFullViewCount() {
		return _cache.get(SeasonCache.FULL_VIEW_COUNT, null, sea->
		{
			int vc = Integer.MAX_VALUE;
			for (CCEpisode e : episodes) {
				vc = Math.min(e.ViewedHistory.get().count(), vc);
			}
			if (vc == Integer.MAX_VALUE) return 0;
			return vc;
		});
	}

	@Override
	public CCStream<CCEpisode> iteratorEpisodes() {
		return CCStreams.iterate(episodes);
	}

	public File getFileForCreatedFolderstructure(File parentfolder, String title, int episodeNumber, CCFileFormat format, CCDBLanguageList fallbackLanguage) {
		if (! parentfolder.isDirectory()) {
			return null; // meehp
		}

		String parent = PathFormatter.appendSeparator(parentfolder.getAbsolutePath());

		String path = parent + getRelativeFileForCreatedFolderstructure(title, episodeNumber, format, fallbackLanguage);

		return new File(path);
	}

	@SuppressWarnings("nls")
	public String getRelativeFileForCreatedFolderstructure(String title, int episodeNumber, CCFileFormat format, CCDBLanguageList fallbackLanguage) {
		DecimalFormat decFormattter = new DecimalFormat("00");

		String seriesfoldername = getSeries().getFolderNameForCreatedFolderStructure(fallbackLanguage);
		String seasonfoldername = getFolderNameForCreatedFolderStructure();
		int seasonIndex = getIndexForCreatedFolderStructure();

		String filename = PathFormatter.fixStringToFilesystemname(String.format("S%sE%s - %s.%s", decFormattter.format(seasonIndex), decFormattter.format(episodeNumber), Str.limit(title, 128), format.asString()));

		return PathFormatter.combine(seriesfoldername, seasonfoldername, filename);
	}


	@SuppressWarnings("nls")
	public CCQualityCategory getMediaInfoCategory() {
		return _cache.get(SeasonCache.MEDIAINFO_CATEGORY, null, sea->
		{
			if (getEpisodeCount() == 0) return CCQualityCategory.UNSET;

			if (iteratorEpisodes().any(e -> e.MediaInfo.get().isUnset())) return CCQualityCategory.UNSET;

			double avg = iteratorEpisodes().map(e -> e.MediaInfo.get().getCategory(getSeries().Genres.get()).getCategoryType()).avgValueOrDefault(e -> (double)e.asInt(), 0);

			CCQualityResolutionType rtype = iteratorEpisodes()
					.map(e -> e.MediaInfo.get().getCategory(getSeries().Genres.get()).getResolutionType())
					.groupBy(e -> e)
					.map(Map.Entry::getKey)
					.singleOrDefault(null, null);

			if (rtype == null) rtype = CCQualityResolutionType.MULTIPLE;

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
}
