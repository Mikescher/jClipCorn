package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomQualityFilter extends AbstractCustomFilter {
	private CCQuality quality = CCQuality.STREAM;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return quality.equals(e.getQuality());
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Quality", quality.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Quality").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_QUALITY;
	}
		
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("quality", CCQuality.getWrapper(), (d) -> this.quality = d,  () -> this.quality);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomQualityFilter();
	}

	public static CustomQualityFilter create(CCQuality data) {
		CustomQualityFilter f = new CustomQualityFilter();
		f.quality = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> quality, p -> quality = p, CCQuality.getWrapper()),
		};
	}
}
