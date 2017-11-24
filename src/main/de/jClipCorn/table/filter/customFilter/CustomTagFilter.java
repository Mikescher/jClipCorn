package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterNamedIntChooserConfig;
import de.jClipCorn.util.stream.CCStreams;

public class CustomTagFilter extends AbstractCustomFilter {
	private int tag = 0;
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return e.getTags().getTag(tag);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Tag", CCTagList.getName(tag)); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Tag").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_TAG;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addInt("tag", (d) -> this.tag = d,  () -> this.tag);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomTagFilter();
	}

	public static CustomTagFilter create(int data) {
		CustomTagFilter f = new CustomTagFilter();
		f.tag = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterNamedIntChooserConfig(() -> tag, p -> tag = p, CCStreams.iterate(CCTagList.getList()).enumerate()),
		};
	}
}
