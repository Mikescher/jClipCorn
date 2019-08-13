package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.util.CCMediaInfoField;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

public class CustomMediaInfoValueFilter extends AbstractCustomStructureElementFilter {
	private CCMediaInfoField field = CCMediaInfoField.IS_SET;
	private String value = Str.Empty;

	@Override
	public boolean includes(CCMovie mov) {
		return value.equals(field.get(mov.getMediaInfo()));
	}

	@Override
	public boolean includes(CCSeries ser) {
		return ser.iteratorEpisodes().all(e -> value.equals(field.get(e.getMediaInfo())));
	}

	@Override
	public boolean includes(CCSeason sea) {
		return sea.iteratorEpisodes().all(e -> value.equals(field.get(e.getMediaInfo())));
	}

	@Override
	public boolean includes(CCEpisode epi) {
		return value.equals(field.get(epi.getMediaInfo()));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.MediaInfoValue", field.asString() + " == '"+value+"'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.MediaInfoValue").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_MI_VALUE;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("field", CCMediaInfoField.getWrapper(), (d) -> this.field = d,  () -> this.field); //$NON-NLS-1$
		cfg.addString("value", (d) -> this.value = d,  () -> this.value); //$NON-NLS-1$
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomMediaInfoValueFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) { //TODO isless|equals|ismore   //TODO show db value sin list below
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> field, a -> field = a, CCMediaInfoField.getWrapper()), //$NON-NLS-1$
			new CustomFilterStringConfig(() -> value, a -> value = a), //$NON-NLS-1$
		};
	}
}
