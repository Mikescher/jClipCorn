package de.jClipCorn.features.table.filter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.customFilter.*;
import de.jClipCorn.features.table.filter.customFilter.aggregators.*;
import de.jClipCorn.features.table.filter.customFilter.operators.*;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.comparator.StringComparator;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomFilter {
	public final static int CUSTOMFILTERID_AND              = 0;
	public final static int CUSTOMFILTERID_NAND             = 1;
	public final static int CUSTOMFILTERID_NOR              = 2;
	public final static int CUSTOMFILTERID_OR               = 3;
	public final static int CUSTOMFILTERID_FORMAT           = 4;
	public final static int CUSTOMFILTERID_FSK              = 5;
	public final static int CUSTOMFILTERID_GENRE            = 6;
	public final static int CUSTOMFILTERID_LANGUAGE         = 7;
	public final static int CUSTOMFILTERID_ONLINESCORE      = 8;
	public final static int CUSTOMFILTERID_QUALITY__XXX     = 9;  // deprecated
	public final static int CUSTOMFILTERID_USERSCORE        = 10;
	public final static int CUSTOMFILTERID_TAG              = 11;
	public final static int CUSTOMFILTERID_TITLE            = 12;
	public final static int CUSTOMFILTERID_TYP              = 13;
	public final static int CUSTOMFILTERID_VIEWED           = 14;
	public final static int CUSTOMFILTERID_YEAR             = 15;
	public final static int CUSTOMFILTERID_ZYKLUS           = 16;
	public final static int CUSTOMFILTERID_GROUP            = 17;
	public final static int CUSTOMFILTERID_MAINREFERENCE    = 18;
	public final static int CUSTOMFILTERID_HISTORY          = 19;
	public final static int CUSTOMFILTERID_SEARCH           = 20;
	public final static int CUSTOMFILTERID_CHAR             = 21;
	public final static int CUSTOMFILTERID_ADDDATE          = 22;
	public final static int CUSTOMFILTERID_VIEWCOUNT        = 23;
	public final static int CUSTOMFILTERID_ANYEPISODE       = 24;
	public final static int CUSTOMFILTERID_ALLEPISODE       = 25;
	public final static int CUSTOMFILTERID_ANYSEASON        = 26;
	public final static int CUSTOMFILTERID_ALLSEASON        = 27;
	public final static int CUSTOMFILTERID_EPISODECOUNT     = 28;
	public final static int CUSTOMFILTERID_EXTVIEWED        = 29;
	public final static int CUSTOMFILTERID_COVERDIMENSION   = 30;
	public final static int CUSTOMFILTERID_ANYREFERENCE     = 31;
	public final static int CUSTOMFILTERID_EPISODECOUNTER   = 32;
	public final static int CUSTOMFILTERID_SEASONCOUNTER    = 33;
	public final static int CUSTOMFILTERID_RELATIVE_HISTORY = 34;
	public final static int CUSTOMFILTERID_EXACTLANGUAGE    = 35;
	public final static int CUSTOMFILTERID_QUALITYCAT       = 36;
	public final static int CUSTOMFILTERID_MI_SET           = 36;
	public final static int CUSTOMFILTERID_MI_VALUE         = 37;
	public final static int CUSTOMFILTERID_SUBTITLE         = 38;
	public final static int CUSTOMFILTERID_USERSCORECOMMENT = 39;
	public final static int CUSTOMFILTERID_SPECIALVERSION   = 40;
	public final static int CUSTOMFILTERID_ANIMESEASON      = 41;
	public final static int CUSTOMFILTERID_ANIMESTUDIO      = 42;
		
	public abstract String getName();
	public abstract String getPrecreateName();
	public abstract int getID();

	public abstract AbstractCustomFilter createNew(CCMovieList ml);
	public abstract CustomFilterConfig[] createConfig(CCMovieList ml);
	
	public abstract boolean includes(ICCDatabaseStructureElement elem);
	
	protected abstract void initSerialization(FilterSerializationConfig cfg);

	private FilterSerializationConfig _cfgSerialization = null;

	protected final CCMovieList movielist;

	public AbstractCustomFilter(CCMovieList ml) {
		movielist = ml;
	}

	private FilterSerializationConfig getSerializationConfig() {
		if (_cfgSerialization == null) {
			_cfgSerialization = new FilterSerializationConfig(movielist, getID());
			initSerialization(_cfgSerialization);
		}
		return _cfgSerialization;
	}
	
	public String exportToString() {
		return getSerializationConfig().serialize();
	}

	public boolean importFromString(String txt) {
		try {
			txt = txt.replaceAll("\\s", ""); // remove whitespaces //$NON-NLS-1$ //$NON-NLS-2$
			return getSerializationConfig().deserialize(txt);
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getString("LogMessage.ExceptionInFilterParse"), e); //$NON-NLS-1$
			return false;
		}
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
	
	public static AbstractCustomFilter getFilterByID(CCMovieList ml, int id) {

		for (AbstractCustomFilter f : getAllOperatorFilter(ml)) {
			if (f.getID() == id) return f;
		}
		
		for (AbstractCustomFilter f : getAllSimpleFilter(ml)) {
			if (f.getID() == id) return f;
		}
		
		for (AbstractCustomFilter f : getAllAggregatorFilter(ml)) {
			if (f.getID() == id) return f;
		}

		for (AbstractCustomFilter f : getAllHiddenFilter(ml)) {
			if (f.getID() == id) return f;
		}

		return null;
	}

	public static CCStream<AbstractCustomFilter> iterateAllSimpleFilterSorted(CCMovieList ml) {
		return CCStreams.iterate(getAllSimpleFilter(ml)).sortByProperty(f -> f.getPrecreateName(), new StringComparator());
	}
	
	public static AbstractCustomFilter[] getAllSimpleFilter(CCMovieList ml) {
		return new AbstractCustomFilter[] { 
			new CustomTitleFilter(ml),
			new CustomFormatFilter(ml),
			new CustomFSKFilter(ml),
			new CustomGenreFilter(ml),
			new CustomLanguageFilter(ml),
			new CustomSubtitleFilter(ml),
			new CustomExactLanguageFilter(ml),
			new CustomOnlinescoreFilter(ml),
			new CustomQualityCategoryTypeFilter(ml),
			new CustomMediaInfoSetFilter(ml),
			new CustomMediaInfoValueFilter(ml),
			new CustomUserScoreFilter(ml),
			new CustomUserCommentFilter(ml),
			new CustomMainReferenceFilter(ml),
			new CustomAnyReferenceFilter(ml),
			new CustomTagFilter(ml),
			new CustomGroupFilter(ml),
			new CustomTypFilter(ml),
			new CustomViewedFilter(ml),
			new CustomExtendedViewedFilter(ml),
			new CustomHistoryFilter(ml),
			new CustomYearFilter(ml),
			new CustomZyklusFilter(ml),
			new CustomAddDateFilter(ml),
			new CustomViewcountFilter(ml),
			new CustomEpisodecountFilter(ml),
			new CustomCoverDimensionFilter(ml),
			new CustomRelativeHistoryFilter(ml),
			new CustomSpecialVersionFilter(ml),
			new CustomAnimeSeasonFilter(ml),
			new CustomAnimeStudioFilter(ml),
		};
	}
	
	public static CustomOperator[] getAllOperatorFilter(CCMovieList ml) {
		return new CustomOperator[] {
			new CustomAndOperator(ml),
			new CustomOrOperator(ml),
			new CustomNandOperator(ml),
			new CustomNorOperator(ml),
		};
	}
	
	public static CustomAggregator[] getAllAggregatorFilter(CCMovieList ml) {
		return new CustomAggregator[] {
			new CustomAnyEpisodeAggregator(ml),
			new CustomAllEpisodeAggregator(ml),

			new CustomAnySeasonAggregator(ml),
			new CustomAllSeasonAggregator(ml),

			new CustomEpisodeCountAggregator(ml),
			new CustomSeasonCountAggregator(ml),
		};
	}

	public static AbstractCustomFilter[] getAllHiddenFilter(CCMovieList ml) {
		return new AbstractCustomFilter[] {
			new CustomSearchFilter(ml),
			new CustomCharFilter(ml),
		};
	}
	
	public static AbstractCustomFilter createFilterFromExport(CCMovieList ml, String txt) {

		int id = FilterSerializationConfig.getIDFromExport(txt);
		if (id < 0) return null;
		
		AbstractCustomFilter f = getFilterByID(ml, id);
		if (f == null) return null;
		
		if (f.importFromString(txt)) {
			return f;
		} else {
			return null;
		}
	}

	public AbstractCustomFilter createCopy(CCMovieList ml) {
		AbstractCustomFilter f = createNew(ml);
		boolean r = f.importFromString(exportToString());
		if (!r)return null;
		return f;
	}
	
	public IconRef getListIcon() {
		return Resources.ICN_FILTER_METHOD;
	}

	@Override
	public String toString() {
		return getPrecreateName();
	}

	public CCMovieList getMovieList() {
		return movielist;
	}
}
