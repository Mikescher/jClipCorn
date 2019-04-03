package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datatypes.DimensionAxisType;
import de.jClipCorn.util.datatypes.Tuple;

public class CustomCoverDimensionFilter extends AbstractCustomFilter {
	private DimensionAxisType axis = DimensionAxisType.WIDTH;
	private CCIntArea range = new CCIntArea();

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof ICCCoveredElement) {
			Tuple<Integer, Integer> cvr = ((ICCCoveredElement)e).getCoverDimensions();

			if (axis == DimensionAxisType.WIDTH) return range.contains(cvr.Item1);
			if (axis == DimensionAxisType.HEIGHT) return range.contains(cvr.Item2);
		}
		return false;
	}
	
	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.CoverDimension", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.CoverDimension").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (range.type) {
		case LESSER:
			return axis.asString() + " < " + range.high; //$NON-NLS-1$
		case GREATER:
			return range.low + " < " + axis.asString(); //$NON-NLS-1$
		case IN_RANGE:
			return range.low + " < " + axis.asString() + " < " + range.high; //$NON-NLS-1$ //$NON-NLS-2$
		case EXACT:
			return axis.asString() + " == " + range.low; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_COVERDIMENSION;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("low", (d) -> this.range.low = d,  () -> this.range.low);
		cfg.addInt("high", (d) -> this.range.high = d, () -> this.range.high);
		cfg.addCCEnum("type", DecimalSearchType.getWrapper(), (d) -> this.range.type = d, () -> this.range.type);
		cfg.addCCEnum("axis", DimensionAxisType.getWrapper(), (d) -> this.axis = d, () -> this.axis);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomCoverDimensionFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> axis, p -> axis = p, DimensionAxisType.getWrapper()),
			new CustomFilterIntAreaConfig(() -> range, a -> range = a, 0, null),
		};
	}
}
