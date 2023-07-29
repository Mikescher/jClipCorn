package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

public class CustomUserCommentFilter extends AbstractCustomStructureElementFilter {
	private boolean hasComment = false;

	public CustomUserCommentFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCMovie e) {
		return Str.isNullOrEmpty(e.ScoreComment.get()) != hasComment;
	}

	@Override
	public boolean includes(CCSeries e) {
		return Str.isNullOrEmpty(e.ScoreComment.get()) != hasComment;
	}

	@Override
	public boolean includes(CCEpisode e) {
		return Str.isNullOrEmpty(e.ScoreComment.get()) != hasComment;
	}

	@Override
	public boolean includes(CCSeason e) {
		return Str.isNullOrEmpty(e.ScoreComment.get()) != hasComment;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.ScoreComment", hasComment ? "true" : "false"); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.ScoreComment").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_USERSCORECOMMENT;
	}

	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addBool("hasComment", (d) -> this.hasComment = d,  () -> this.hasComment);
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomUserCommentFilter(ml);
	}

	public static CustomUserCommentFilter create(CCMovieList ml, boolean data) {
		CustomUserCommentFilter f = new CustomUserCommentFilter(ml);
		f.hasComment = data;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterBoolConfig(ml, () -> hasComment, a -> hasComment = a, LocaleBundle.getString("FilterTree.Custom.CustomUserCommentFilter.HasComment")), //$NON-NLS-1$
		};
	}
}
