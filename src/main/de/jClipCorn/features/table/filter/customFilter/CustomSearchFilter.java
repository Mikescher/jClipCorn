package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.YearRange;
import org.apache.commons.lang.StringUtils;

public class CustomSearchFilter extends AbstractCustomDatabaseElementFilter {
	private String searchTerm = ""; //$NON-NLS-1$

	public CustomSearchFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	@SuppressWarnings("nls")
	public boolean includes(CCDatabaseElement e) {
		String title = e.getTitle();
		
		if (StringUtils.containsIgnoreCase(title.replace(".", ""), searchTerm.replace(".", ""))) {
			return true;
		}
		
		if (e.isMovie()) {
			String zyklus = e.asMovie().getZyklus().getTitle();
			if (StringUtils.containsIgnoreCase(zyklus.replace(".", ""), searchTerm.replace(".", ""))) {
				return true;
			}
		}

		if (e.isMovie()) {
			for (CCDBLanguage lang : e.asMovie().getLanguage()) {
				if (lang.asString().equalsIgnoreCase(searchTerm)) {
					return true;
				}
			}

			for (CCDBLanguage lang : e.asMovie().getSubtitles()) {
				if (lang.asString().equalsIgnoreCase(searchTerm)) {
					return true;
				}
			}

			var minfo = e.asMovie().mediaInfo().get();
			if (minfo.VideoCodec  .map(v -> v.equalsIgnoreCase(searchTerm)) .orElse(false)) return true;
			if (minfo.VideoFormat .map(v -> v.equalsIgnoreCase(searchTerm)) .orElse(false)) return true;
			if (minfo.AudioCodec  .map(v -> v.equalsIgnoreCase(searchTerm)) .orElse(false)) return true;
			if (minfo.AudioFormat .map(v -> v.equalsIgnoreCase(searchTerm)) .orElse(false)) return true;

		} else if (e.isSeries()) {
			for (CCDBLanguage lang : e.asSeries().getAllLanguages()) {
				if (lang.asString().equalsIgnoreCase(searchTerm)) {
					return true;
				}
			}
			for (CCDBLanguage lang : e.asSeries().getAllSubtitles()) {
				if (lang.asString().equalsIgnoreCase(searchTerm)) {
					return true;
				}
			}
		}
		
		if (e.getFormat().asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (e.getAddDate().toStringSerialize().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (e.getAddDate().toStringSQL().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (e.getAddDate().toStringUINormal().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		CCGenreList gl = e.getGenres();
		for (int i = 0; i < gl.getGenreCount(); i++) {
			if (gl.getGenre(i).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		if (e.isMovie()) {
			if (new YearRange(e.asMovie().getYear()).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		} else if (e.isSeries()) {
			if (e.asSeries().getYearRange().asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		if (e.getGroups().containsIgnoreCase(searchTerm)) {
			return true;
		}
		
		for (CCSingleOnlineReference soref : e.getOnlineReference()) {
			if ((soref.toSerializationString().equals(searchTerm) || soref.id.equals(searchTerm))) {
				return true;
			}
		}

		for (String version : e.SpecialVersion.get()) {
			if (version.equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		for (String season : e.AnimeSeason.get()) {
			if (season.equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		for (String studio : e.AnimeStudio.get()) {
			if (studio.equalsIgnoreCase(searchTerm)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Search", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Search").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		return searchTerm;
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SEARCH;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("searchterm", (d) -> this.searchTerm = d,  () -> this.searchTerm);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomSearchFilter(ml);
	}

	public static CustomSearchFilter create(CCMovieList ml, String data) {
		CustomSearchFilter f = new CustomSearchFilter(ml);
		f.searchTerm = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringConfig(ml, () -> searchTerm, p -> searchTerm = p),
		};
	}
}
