package de.jClipCorn.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.FilterSerializationConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;

public class CustomUserScoreFilter extends AbstractCustomDatabaseElementFilter {
	private CCUserScore score = CCUserScore.RATING_NO;
	
	@Override
	public boolean includes(CCDatabaseElement e) {
		return e.getScore() == score;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Score", score.asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Score").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_USERSCORE;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("score", CCUserScore.getWrapper(), (d) -> this.score = d,  () -> this.score);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomUserScoreFilter();
	}

	public static CustomUserScoreFilter create(CCUserScore data) {
		CustomUserScoreFilter f = new CustomUserScoreFilter();
		f.score = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> score, p -> score = p, CCUserScore.getWrapper()),
		};
	}
}
