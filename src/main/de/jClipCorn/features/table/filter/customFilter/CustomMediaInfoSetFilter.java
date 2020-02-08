package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

public class CustomMediaInfoSetFilter extends AbstractCustomStructureElementFilter {
	private boolean isset = false;

	@Override
	public boolean includes(CCMovie mov) {
		return isset == mov.getMediaInfo().isSet();
	}

	@Override
	public boolean includes(CCSeries ser) {
		return isset == ser.iteratorEpisodes().all(e -> e.getMediaInfo().isSet());
	}

	@Override
	public boolean includes(CCSeason sea) {
		return isset == sea.iteratorEpisodes().all(e -> e.getMediaInfo().isSet());
	}

	@Override
	public boolean includes(CCEpisode epi) {
		return isset == epi.getMediaInfo().isSet();
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.MediaInfoSet", isset ? "X" : Str.SingleSpace); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.MediaInfoSet").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_MI_SET;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addBool("isset", (d) -> this.isset = d,  () -> this.isset); //$NON-NLS-1$
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomMediaInfoSetFilter();
	}

	public static AbstractCustomFilter create(boolean data) {
		CustomMediaInfoSetFilter f = new CustomMediaInfoSetFilter();
		f.isset = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterBoolConfig(() -> isset, a -> isset = a, LocaleBundle.getString("FilterTree.HasMediaInfo")), //$NON-NLS-1$
		};
	}
}
