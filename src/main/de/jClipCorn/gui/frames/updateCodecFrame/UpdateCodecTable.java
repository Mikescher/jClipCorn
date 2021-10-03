package de.jClipCorn.gui.frames.updateCodecFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class UpdateCodecTable extends JCCSimpleTable<UpdateCodecTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final UpdateCodecFrame owner;

	@DesignCreate
	private static UpdateCodecTable designCreate() { return new UpdateCodecTable(new UpdateCodecFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public UpdateCodecTable(UpdateCodecFrame owner) {
		super(owner);
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<UpdateCodecTableElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<UpdateCodecTableElement>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"",
				null,
				e -> e.getStatusIcon(),
				e -> Str.limit(Str.firstLine(Str.trim(e.getStatusText())), 128),
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"*,min=auto,max=600,priority=max",
				"UpdateCodecFrame.Table.ColumnTitle",
				e -> e.getFullDisplayTitle(),
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnLangOld",
				e -> e.getOldLanguage().toShortOutputString(Str.Empty),
				e -> e.getOldLanguage().getIcon(),
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnLangNew",
				e -> e.getNewLanguage().toShortOutputString(Str.Empty),
				e -> e.getNewLanguage().isEmpty() ? Resources.ICN_TRANSPARENT.get16x16() : e.getNewLanguage().getIcon(),
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnSubsOld",
				e -> Str.Empty,
				e -> e.getOldSubtitles().getIcon(),
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnSubsNew",
				e -> Str.Empty,
				e -> e.getNewSubtitles().getIcon(),
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnLengthOld",
				e -> e.getOldLengthStr(),
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnLengthNew",
				e -> e.getNewLengthStr(),
				null,
				null,
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnQualityOld",
				e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).getLongText(),
				e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).getIcon(),
				e -> e.getOldMediaInfo().getCategory(e.getSourceGenres()).getTooltip(),
				true));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"UpdateCodecFrame.Table.ColumnQualityNew",
				e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getLongText(),
				e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getIcon(),
				e -> e.getNewMediaInfo().getCategory(e.getSourceGenres()).getTooltip(),
				true));
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(UpdateCodecTableElement element) {
		element.preview(owner);
	}

	@Override
	protected void OnSelectElement(UpdateCodecTableElement element) {
		owner.setSelection(element);
	}

	@Override
	protected boolean isMultiselect() {
		return true;
	}
}
