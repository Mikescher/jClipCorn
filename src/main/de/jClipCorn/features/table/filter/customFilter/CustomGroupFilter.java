package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroup;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.util.stream.CCStreams;

public class CustomGroupFilter extends AbstractCustomDatabaseElementFilter {
	private String group = ""; //$NON-NLS-1$
	private boolean allowSubgroups = true;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		
		if (allowSubgroups) {
			
			return e.getGroups().iterate().any(g -> RecursiveMatch(e.getMovieList(), g, 0));
			
		} else {
			
			return e.getGroups().contains(group);
			
		}
	}
	
	private boolean RecursiveMatch(CCMovieList ml, CCGroup g, int depth) {
		if (depth > CCGroup.MAX_SUBGROUP_DEPTH) return false;
		
		if (g.Name.equalsIgnoreCase(group)) return true;
		
		if (g.Parent.isEmpty()) return false;
		
		CCGroup pg = ml.getGroupOrNull(g.Parent);
		if (pg == null) return false;
		
		return RecursiveMatch(ml, pg, depth + 1);
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
		cfg.addBool("subgroups", (d) -> this.allowSubgroups = d,  () -> this.allowSubgroups);
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomGroupFilter();
	}

	public static CustomGroupFilter create(CCGroup data, boolean subgroupmatches) {
		CustomGroupFilter f = new CustomGroupFilter();
		f.group = data.Name;
		f.allowSubgroups = subgroupmatches;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringChooserConfig(() -> group, a -> group = a, CCStreams.iterate(ml.getGroupList()).map(g -> g.Name).enumerate(), true, true),
			new CustomFilterBoolConfig(() -> allowSubgroups, a -> allowSubgroups = a, LocaleBundle.getString("FilterTree.Custom.AllowSubgroups")), //$NON-NLS-1$
		};
	}
}
