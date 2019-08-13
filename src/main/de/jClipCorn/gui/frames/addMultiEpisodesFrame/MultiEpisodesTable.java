package de.jClipCorn.gui.frames.addMultiEpisodesFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;

public class MultiEpisodesTable extends JCCSimpleTable<NewEpisodeVM> {
	private static final long serialVersionUID = 6919528266265643678L;

	private final CCSeries _src;

	public MultiEpisodesTable(CCSeries src) {
		_src = src;
	}

	@Override
	protected List<JCCSimpleColumnPrototype<NewEpisodeVM>> configureColumns() {
		List<JCCSimpleColumnPrototype<NewEpisodeVM>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				Str.Empty,
				null,
				e -> e.IsValid ? Resources.ICN_GENERIC_ORB_GREEN.get16x16() : Resources.ICN_GENERIC_ORB_RED.get16x16(),
				e -> Str.isNullOrWhitespace(e.Problems) ? null : e.Problems));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Source", //$NON-NLS-1$
				e -> PathFormatter.getFilenameWithExt(e.SourcePath),
				null,
				e -> e.SourcePath));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Target", //$NON-NLS-1$
				e -> e.TargetPath,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Episode", //$NON-NLS-1$
				e -> e.EpisodeNumber + "", //$NON-NLS-1$
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Title", //$NON-NLS-1$
				e -> e.Title,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Length", //$NON-NLS-1$
				e -> e.Length + "", //$NON-NLS-1$
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Language", //$NON-NLS-1$
				null,
				e -> e.Language.getFullIcon(),
				e -> e.Language.toOutputString()));

		r.add(new JCCSimpleColumnPrototype<>(
				"MultiEpisodesTable.Quality", //$NON-NLS-1$
				e -> e.MediaInfo.getCategory(_src.getGenres()).getLongText(),
				e -> e.MediaInfo.getCategory(_src.getGenres()).getIcon(),
				e -> e.MediaInfo.getCategory(_src.getGenres()).getTooltip()));

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
	protected int getColumnAdjusterMaxWidth() {
		return 550;
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

	@Override
	protected boolean isSortable(int col) {
		return false;
	}

}
