package de.jClipCorn.table.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.table.filter.customFilter.CustomAddDateFilter;
import de.jClipCorn.table.filter.customFilter.CustomCoverDimensionFilter;
import de.jClipCorn.table.filter.customFilter.CustomEpisodecountFilter;
import de.jClipCorn.table.filter.customFilter.CustomExtendedViewedFilter;
import de.jClipCorn.table.filter.customFilter.CustomFSKFilter;
import de.jClipCorn.table.filter.customFilter.CustomFormatFilter;
import de.jClipCorn.table.filter.customFilter.CustomGenreFilter;
import de.jClipCorn.table.filter.customFilter.CustomGroupFilter;
import de.jClipCorn.table.filter.customFilter.CustomHistoryFilter;
import de.jClipCorn.table.filter.customFilter.CustomLanguageFilter;
import de.jClipCorn.table.filter.customFilter.CustomOnlinescoreFilter;
import de.jClipCorn.table.filter.customFilter.CustomQualityFilter;
import de.jClipCorn.table.filter.customFilter.CustomReferenceFilter;
import de.jClipCorn.table.filter.customFilter.CustomTagFilter;
import de.jClipCorn.table.filter.customFilter.CustomTitleFilter;
import de.jClipCorn.table.filter.customFilter.CustomTypFilter;
import de.jClipCorn.table.filter.customFilter.CustomUserScoreFilter;
import de.jClipCorn.table.filter.customFilter.CustomViewcountFilter;
import de.jClipCorn.table.filter.customFilter.CustomViewedFilter;
import de.jClipCorn.table.filter.customFilter.CustomYearFilter;
import de.jClipCorn.table.filter.customFilter.CustomZyklusFilter;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAllEpisodeAggregator;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAllSeasonAggregator;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAnyEpisodeAggregator;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAnySeasonAggregator;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.table.filter.customFilter.operators.CustomNandOperator;
import de.jClipCorn.table.filter.customFilter.operators.CustomNorOperator;
import de.jClipCorn.table.filter.customFilter.operators.CustomOrOperator;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.util.comparator.StringComparator;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

public abstract class AbstractCustomFilter {
	public final static int CUSTOMFILTERID_AND            = 0;
	public final static int CUSTOMFILTERID_NAND           = 1;
	public final static int CUSTOMFILTERID_NOR            = 2;
	public final static int CUSTOMFILTERID_OR             = 3;
	public final static int CUSTOMFILTERID_FORMAT         = 4;
	public final static int CUSTOMFILTERID_FSK            = 5;
	public final static int CUSTOMFILTERID_GENRE          = 6;
	public final static int CUSTOMFILTERID_LANGUAGE       = 7;
	public final static int CUSTOMFILTERID_ONLINESCORE    = 8;
	public final static int CUSTOMFILTERID_QUALITY        = 9;
	public final static int CUSTOMFILTERID_USERSCORE      = 10;
	public final static int CUSTOMFILTERID_TAG            = 11;
	public final static int CUSTOMFILTERID_TITLE          = 12;
	public final static int CUSTOMFILTERID_TYP            = 13;
	public final static int CUSTOMFILTERID_VIEWED         = 14;
	public final static int CUSTOMFILTERID_YEAR           = 15;
	public final static int CUSTOMFILTERID_ZYKLUS         = 16;
	public final static int CUSTOMFILTERID_GROUP          = 17;
	public final static int CUSTOMFILTERID_REFERENCE      = 18;
	public final static int CUSTOMFILTERID_HISTORY        = 19;
	public final static int CUSTOMFILTERID_SEARCH         = 20;
	public final static int CUSTOMFILTERID_CHAR           = 21;
	public final static int CUSTOMFILTERID_ADDDATE        = 22;
	public final static int CUSTOMFILTERID_VIEWCOUNT      = 23;
	public final static int CUSTOMFILTERID_ANYEPISODE     = 24;
	public final static int CUSTOMFILTERID_ALLEPISODE     = 25;
	public final static int CUSTOMFILTERID_ANYSEASON      = 26;
	public final static int CUSTOMFILTERID_ALLSEASON      = 27;
	public final static int CUSTOMFILTERID_EPISODECOUNT   = 28;
	public final static int CUSTOMFILTERID_EXTVIEWED      = 29;
	public final static int CUSTOMFILTERID_COVERDIMENSION = 30;
		
	public abstract String getName();
	public abstract String getPrecreateName();
	public abstract int getID();

	public abstract AbstractCustomFilter createNew();
	public abstract CustomFilterConfig[] createConfig(CCMovieList ml);
	
	public abstract boolean includes(ICCDatabaseStructureElement elem);
	
	protected abstract void initSerialization(FilterSerializationConfig cfg);

	private FilterSerializationConfig _cfgSerialization = null;

	private FilterSerializationConfig getSerializationConfig() {
		if (_cfgSerialization == null) {
			_cfgSerialization = new FilterSerializationConfig(getID());
			initSerialization(_cfgSerialization);
		}
		return _cfgSerialization;
	}
	
	public String exportToString() {
		return getSerializationConfig().serialize();
	}

	public boolean importFromString(String txt) {
		return getSerializationConfig().deserialize(txt);
	}

	public List<AbstractCustomFilter> getList() {
		return new ArrayList<>();
	}
	
	public static String escape(String txt) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < txt.length(); i++) {
			char c = txt.charAt(i);
			
			if (c == '|' || c == '[' || c == ']' || c == ',' || c == '&') {
				builder.append('&');
			}
			builder.append(c);
		}
		
		return builder.toString();
	}
	
	public static String descape(String txt) {
		StringBuilder builder = new StringBuilder();
		
		boolean escape = false;
		for (int i = 0; i < txt.length(); i++) {
			boolean skip = false;
			char c = txt.charAt(i);
			
			if (escape) {
				escape = false;
			} else if (c == '&') {
				escape = true;
				skip = true;
			}
			
			if (! skip) {
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	@SuppressWarnings("nls")
	public static int getIDFromExport(String txt) {
		if (txt.length() < 4) return -1;
		txt = txt.substring(1, txt.length() - 1);
		String[] split = txt.split(Pattern.quote("|"));
		if (split.length < 2) return -1;
		try {
			return Integer.parseInt(split[0]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	public static AbstractCustomFilter getFilterByID(int id) {

		for (AbstractCustomFilter f : getAllOperatorFilter()) {
			if (f.getID() == id) return f;
		}
		
		for (AbstractCustomFilter f : getAllSimpleFilter()) {
			if (f.getID() == id) return f;
		}
		
		for (AbstractCustomFilter f : getAllAggregatorFilter()) {
			if (f.getID() == id) return f;
		}

		return null;
	}

	public static CCStream<AbstractCustomFilter> iterateAllSimpleFilterSorted() {
		return CCStreams.iterate(getAllSimpleFilter()).sortByProperty(f -> f.getPrecreateName(), new StringComparator());
	}
	
	public static AbstractCustomFilter[] getAllSimpleFilter() {
		return new AbstractCustomFilter[] { 
			new CustomTitleFilter(),
			new CustomFormatFilter(),
			new CustomFSKFilter(),
			new CustomGenreFilter(),
			new CustomLanguageFilter(),
			new CustomOnlinescoreFilter(),
			new CustomQualityFilter(),
			new CustomUserScoreFilter(),
			new CustomReferenceFilter(),
			new CustomTagFilter(),
			new CustomGroupFilter(),
			new CustomTypFilter(),
			new CustomViewedFilter(),
			new CustomExtendedViewedFilter(),
			new CustomHistoryFilter(),
			new CustomYearFilter(),
			new CustomZyklusFilter(),
			new CustomAddDateFilter(),
			new CustomViewcountFilter(),
			new CustomEpisodecountFilter(),
			new CustomCoverDimensionFilter(),
		};
	}
	
	public static AbstractCustomFilter[] getAllOperatorFilter() {
		return new AbstractCustomFilter[] {
			new CustomAndOperator(),
			new CustomOrOperator(),
			new CustomNandOperator(),
			new CustomNorOperator(),
		};
	}
	
	public static AbstractCustomFilter[] getAllAggregatorFilter() {
		return new AbstractCustomFilter[] {
			new CustomAnyEpisodeAggregator(),
			new CustomAllEpisodeAggregator(),

			new CustomAnySeasonAggregator(),
			new CustomAllSeasonAggregator(),
		};
	}
	
	public static AbstractCustomFilter createFilterFromExport(String txt) {
		int id = getIDFromExport(txt);
		if (id < 0) return null;
		
		AbstractCustomFilter f = getFilterByID(id);
		if (f == null) return null;
		
		if (f.importFromString(txt)) {
			return f;
		} else {
			return null;
		}
	}

	public AbstractCustomFilter createCopy() {
		AbstractCustomFilter f = createNew();
		f.importFromString(exportToString());
		return f;
	}
}
