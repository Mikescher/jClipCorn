package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterStringChooserConfig;
import de.jClipCorn.util.stream.CCStreams;

public class CustomGroupFilter extends AbstractCustomDatabaseElementFilter {
	private String group = ""; //$NON-NLS-1$
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.getGroups().contains(group);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Groups", group); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Groups").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_GROUP;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("group", (d) -> this.group = d,  () -> this.group);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomGroupFilter();
	}

	public static CustomGroupFilter create(CCGroup data) {
		CustomGroupFilter f = new CustomGroupFilter();
		f.group = data.Name;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringChooserConfig(() -> group, a -> group = a, CCStreams.iterate(ml.getGroupList()).map(g -> g.Name).enumerate(), true, true),
		};
	}
}
