package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

public class MultiEpisodesTable extends JCCSimpleTable<NewEpisodeVM> {
	private static final long serialVersionUID = 6919528266265643678L;

	private final CCSeries _src;

	@DesignCreate
	private static MultiEpisodesTable designCreate() { return new MultiEpisodesTable(null); }

	public MultiEpisodesTable(CCSeries src) {
		super(src != null ? src.getMovieList() : null);
		_src = src;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<NewEpisodeVM> configureColumns() {
		JCCSimpleColumnList<NewEpisodeVM> r = new JCCSimpleColumnList<>(this);

		r.add(Str.Empty)
				.withSize("auto")
				.withIcon(e -> e.IsValid ? Resources.ICN_GENERIC_ORB_GREEN.get16x16() : Resources.ICN_GENERIC_ORB_RED.get16x16())
				.withTooltip(e -> Str.isNullOrWhitespace(e.Problems) ? null : e.Problems);

		r.add("MultiEpisodesTable.Source")
				.withSize("auto")
				.withText(e -> e.SourcePath.getFilenameWithExt())
				.withTooltip(e -> e.SourcePath.toString());
		
		r.add("MultiEpisodesTable.Target")
				.withSize("auto")
				.withText(e -> e.TargetPath.toString());

		r.add("MultiEpisodesTable.Episode")
				.withSize("auto")
				.withText(e -> e.EpisodeNumber + Str.Empty);

		r.add("MultiEpisodesTable.Title")
				.withSize("*,min=auto")
				.withText(e -> e.Title);

		r.add("MultiEpisodesTable.Length")
				.withSize("auto")
				.withText(e -> e.Length + Str.Empty);

		r.add("MultiEpisodesTable.Language")
				.withSize("auto")
				.withIcon(e -> e.Language.getFullIcon())
				.withTooltip(e -> e.Language.toTooltipString());

		r.add("MultiEpisodesTable.Subtitles")
				.withSize("auto")
				.withIcon(e -> e.Subtitles.getIcon(-1))
				.withTooltip(e -> e.Subtitles.toOutputString());

		r.add("MultiEpisodesTable.Quality")
				.withSize("auto")
				.withText(e -> e.MediaInfo.getCategory(_src.getGenres()).map(CCQualityCategory::getLongText).orElse(Str.Empty))
				.withIcon(e -> e.MediaInfo.getCategory(_src.getGenres()).map(CCQualityCategory::getIcon).orElse(null))
				.withTooltip(e -> e.MediaInfo.getCategory(_src.getGenres()).map(CCQualityCategory::getTooltip).orElse(Str.Empty));

		return r;
	}

	@Override
	protected void OnDoubleClickElement(NewEpisodeVM element) {
		// 
	}

	@Override
	protected void OnSelectElement(NewEpisodeVM element) {
		// 
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
