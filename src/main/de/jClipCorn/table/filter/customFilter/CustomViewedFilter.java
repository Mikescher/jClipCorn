package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;

public class CustomViewedFilter extends AbstractCustomFilter {
	private boolean viewed = false;

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return ! viewed ^ e.getExtendedViewedState().toBool();
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Viewed", (viewed) ? (LocaleBundle.getString("FilterTree.Viewed.Viewed")) : (LocaleBundle.getString("FilterTree.Viewed.Unviewed"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Viewed").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public boolean getViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_VIEWED;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append((viewed)?("1"):("0"));
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
		
		int intval;
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		boolean f;
		if (intval == 0) f = false;
		else if (intval == 1) f = true;
		else return false;

		setViewed(f);
		
		return true;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomViewedFilter();
	}

	public static AbstractCustomFilter create(boolean data) {
		CustomViewedFilter f = new CustomViewedFilter();
		f.setViewed(data);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterBoolConfig(() -> viewed, a -> viewed = a, LocaleBundle.getString("FilterTree.Viewed")), //$NON-NLS-1$
		};
	}
}
