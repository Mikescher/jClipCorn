package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomFSKFilter extends AbstractCustomDatabaseElementFilter {
	private CCFSK fsk = CCFSK.RATING_0;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return fsk.equals(e.getFSK());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.FSK", fsk.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.FSK").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public CCFSK getFSK() {
		return fsk;
	}

	public void setFSK(CCFSK fsk) {
		this.fsk = fsk;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_FSK;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(fsk.asInt()+"");
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
		
		CCFSK f = CCFSK.getWrapper().find(intval);
		if (f == null) return false;
		setFSK(f);
		
		return true;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> fsk, f -> fsk = f, CCFSK.getWrapper()),
		};
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomFSKFilter();
	}

	public static CustomFSKFilter create(CCFSK data) {
		CustomFSKFilter f = new CustomFSKFilter();
		f.setFSK(data);
		return f;
	}
}
