package de.jClipCorn.table.filter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomSearchFilterDialog;
import de.jClipCorn.util.datetime.YearRange;
import de.jClipCorn.util.listener.FinishListener;

public class CustomSearchFilter extends AbstractCustomDatabaseElementFilter {
	private String searchTerm = ""; //$NON-NLS-1$
	
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
		
		if (e.getQuality().asString().equalsIgnoreCase(searchTerm)) {
			return true;
		}
		
		if (e.getLanguage().asString().equalsIgnoreCase(searchTerm)) {
			return true;
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
		
		if (e.getOnlineReference().isSet() && (e.getOnlineReference().toSerializationString().equals(searchTerm) || e.getOnlineReference().id.equals(searchTerm))) {
			return true;
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

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SEARCH;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(AbstractCustomFilter.escape(searchTerm));
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;

		setSearchTerm(AbstractCustomFilter.descape(paramsplit[0]));
		
		return true;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomSearchFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomSearchFilter();
	}

	public static CustomSearchFilter create(String data) {
		CustomSearchFilter f = new CustomSearchFilter();
		f.setSearchTerm(data);
		return f;
	}
}
