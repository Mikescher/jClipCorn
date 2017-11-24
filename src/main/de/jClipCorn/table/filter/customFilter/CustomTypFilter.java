package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomTypFilter extends AbstractCustomDatabaseElementFilter {
	private CCDBElementTyp typ = CCDBElementTyp.MOVIE;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.getType() == typ;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Typ", typ.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Typ").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_TYP;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("typ", CCDBElementTyp.getWrapper(), (d) -> this.typ = d,  () -> this.typ);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomTypFilter();
	}

	public static CustomTypFilter create(CCDBElementTyp data) {
		CustomTypFilter f = new CustomTypFilter();
		f.typ = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> typ, p -> typ = p, CCDBElementTyp.getWrapper()),
		};
	}
}
