package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.util.ExtendedViewedStateType;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomExtendedViewedFilter extends AbstractCustomFilter {
	private ExtendedViewedStateType state = ExtendedViewedStateType.NOT_VIEWED;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return e.getExtendedViewedState().Type == state;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.ExtViewed", state.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.ExtViewed").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public ExtendedViewedStateType getState() {
		return state;
	}

	public void setState(ExtendedViewedStateType state) {
		this.state = state;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_EXTVIEWED;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(state.asInt());
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
		
		ExtendedViewedStateType f = ExtendedViewedStateType.getWrapper().find(intval);
		if (f == null) return false;

		setState(f);
		
		return true;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomExtendedViewedFilter();
	}

	public static CustomExtendedViewedFilter create(ExtendedViewedStateType data) {
		CustomExtendedViewedFilter f = new CustomExtendedViewedFilter();
		f.setState(data);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> state, p -> state = p, ExtendedViewedStateType.getWrapper()),
		};
	}
}
