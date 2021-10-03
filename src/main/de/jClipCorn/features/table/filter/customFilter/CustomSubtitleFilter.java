package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomSubtitleFilter extends AbstractCustomStructureElementFilter {
	private CCDBLanguage subtitleLang = CCDBLanguage.GERMAN;

	public CustomSubtitleFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCMovie e) {
		return e.getSubtitles().contains(subtitleLang);
	}

	@Override
	public boolean includes(CCSeries e) {
		return e.getAllSubtitles().contains(subtitleLang);
	}

	@Override
	public boolean includes(CCEpisode e) {
		return e.getSubtitles().contains(subtitleLang);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Subtitles", subtitleLang); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Subtitles").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_SUBTITLE;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("subtitle", CCDBLanguage.getWrapper(), (d) -> this.subtitleLang = d,  () -> this.subtitleLang);
	}
	
	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomSubtitleFilter(ml);
	}

	public static CustomSubtitleFilter create(CCMovieList ml, CCDBLanguage data) {
		CustomSubtitleFilter f = new CustomSubtitleFilter(ml);
		f.subtitleLang = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(ml, () -> subtitleLang, p -> subtitleLang = p, CCDBLanguage.getWrapper()),
		};
	}
}
