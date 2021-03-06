package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.datetime.YearRange;

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
			String zyklus = ((CCMovie)e).getZyklus().getTitle();
			if (StringUtils.containsIgnoreCase(zyklus.replace(".", ""), searchTerm.replace(".", ""))) {
				return true;
			}
		}

		if (e.isMovie()) {
			for (CCDBLanguage lang : ((CCMovie)e).getLanguage()) {
				if (lang.asString().equalsIgnoreCase(searchTerm)) {
					return true;
				}
			}

			CCMediaInfo minfo = ((CCMovie)e).mediaInfo().get();
			if (minfo.isSet())
			{
				if (minfo.getVideoCodec().equalsIgnoreCase(searchTerm)) return true;
				if (minfo.getVideoFormat().equalsIgnoreCase(searchTerm)) return true;
				if (minfo.getAudioCodec().equalsIgnoreCase(searchTerm)) return true;
				if (minfo.getAudioFormat().equalsIgnoreCase(searchTerm)) return true;
			}

		} else if (e.isSeries()) {
			for (CCDBLanguage lang : ((CCSeries)e).getAllLanguages()) {
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
			if (new YearRange(((CCMovie)e).getYear()).asString().equalsIgnoreCase(searchTerm)) {
				return true;
			}
		} else if (e.isSeries()) {
			if (((CCSeries)e).getYearRange().asString().equalsIgnoreCase(searchTerm)) {
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
