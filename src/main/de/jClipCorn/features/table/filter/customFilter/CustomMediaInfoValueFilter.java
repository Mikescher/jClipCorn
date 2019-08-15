package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.util.CCMediaInfoField;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.*;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.AnyMatchType;

import java.util.List;

public class CustomMediaInfoValueFilter extends AbstractCustomStructureElementFilter {
	private CCMediaInfoField field = CCMediaInfoField.IS_SET;
	private boolean caseSensitive = true;
	private String value = Str.Empty;
	private AnyMatchType match = AnyMatchType.EXACT;

	@Override
	public boolean includes(CCMovie mov) {
		return matches(mov.getMediaInfo());
	}

	@Override
	public boolean includes(CCSeries ser) {
		return ser.iteratorEpisodes().all(e -> matches(e.getMediaInfo()));
	}

	@Override
	public boolean includes(CCSeason sea) {
		return sea.iteratorEpisodes().all(e -> matches(e.getMediaInfo()));
	}

	@Override
	public boolean includes(CCEpisode epi) {
		return matches(epi.getMediaInfo());
	}

	private boolean matches(CCMediaInfo mi)
	{
		if (field != CCMediaInfoField.IS_SET && mi.isUnset()) return false;

		return matches(field.get(mi));
	}

	private boolean matches(String str)
	{
		String val = value;
		if (!caseSensitive) {
			str = str.toLowerCase();
			val = val.toLowerCase();
		}

		if (match == AnyMatchType.LESSER) {
			if (Str.isInteger(val) && Str.isInteger(str)) return Integer.parseInt(str)   < Integer.parseInt(val);
			if (Str.isDouble(val)  && Str.isDouble(str))  return Double.parseDouble(str) < Double.parseDouble(val);
			return false;
		}

		if (match == AnyMatchType.GREATER) {
			if (Str.isInteger(val) && Str.isInteger(str)) return Integer.parseInt(str)   > Integer.parseInt(val);
			if (Str.isDouble(val)  && Str.isDouble(str))  return Double.parseDouble(str) > Double.parseDouble(val);
			return false;
		}

		if (match == AnyMatchType.EXACT) {
			if (Str.isInteger(val) && Str.isInteger(str)) return Integer.parseInt(val)   == Integer.parseInt(str);
			if (Str.isDouble(val)  && Str.isDouble(str))  return Double.parseDouble(val) == Double.parseDouble(str);
			return str.equals(val);
		}

		if (match == AnyMatchType.SM_INCLUDES) return val.contains(str);

		if (match == AnyMatchType.SM_STARTSWITH) return val.startsWith(str);

		if (match == AnyMatchType.SM_ENDSWITH) return val.endsWith(str);

		CCLog.addDefaultSwitchError(this, match);
		return false;
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
		cfg.addCCEnum("match", AnyMatchType.getWrapper(), (d) -> this.match = d,  () -> this.match); //$NON-NLS-1$
		cfg.addBool("casesensitive", (d) -> this.caseSensitive = d,  () -> this.caseSensitive); //$NON-NLS-1$
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomMediaInfoValueFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> field, a -> field = a, CCMediaInfoField.getWrapper()), 
			new CustomFilterEnumOptionConfig<>(() -> match, a -> match = a, AnyMatchType.getWrapper()),
			new CustomFilterStringConfig(() -> value, a -> value = a), 
			new CustomFilterBoolConfig(() -> caseSensitive, p -> caseSensitive = p, LocaleBundle.getString("FilterTree.Custom.FilterFrames.CaseSensitive")), //$NON-NLS-1$
			new CustomFilterPreviewListConfig(this::getPreview), 
		};
	}

	private List<String> getPreview() {
		return CCMovieList
				.getInstance()
				.iteratorPlayables()
				.map(ICCPlayableElement::getMediaInfo)
				.filter(p -> field == CCMediaInfoField.IS_SET || p.isSet())
				.map(p -> field.get(p))
				.unique()
				.autosort()
				.enumerate();
	}
}
