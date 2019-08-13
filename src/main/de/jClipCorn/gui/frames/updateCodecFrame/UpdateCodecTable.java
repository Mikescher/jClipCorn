package de.jClipCorn.gui.frames.updateCodecFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;

public class UpdateCodecTable extends JCCSimpleTable<UpdateCodecTableElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final UpdateCodecFrame owner;

	public UpdateCodecTable(UpdateCodecFrame owner) {
		super();
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<UpdateCodecTableElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<UpdateCodecTableElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				"",
				null,
				e -> e.getStatusIcon(),
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnTitle",
				e -> e.getFullDisplayTitle(),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnLangOld",
				e -> e.getOldLanguage().toShortOutputString(Str.Empty),
				e -> e.getOldLanguage().getIcon(),
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnLangNew",
				e -> e.getNewLanguage().toShortOutputString(Str.Empty),
				e -> e.getNewLanguage().isEmpty() ? Resources.ICN_TRANSPARENT.get16x16() : e.getNewLanguage().getIcon(),
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnLengthOld",
				e -> e.getOldLengthStr(),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnLengthNew",
				e -> e.getNewLengthStr(),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnQualityOld",
				e -> e.getOldMediaInfo().getCategory().getLongText(),
				e -> e.getOldMediaInfo().getCategory().getIcon(),
				e -> e.getOldMediaInfo().getCategory().getTooltip()));

		r.add(new JCCSimpleColumnPrototype<>(
				"UpdateCodecFrame.Table.ColumnQualityNew",
				e -> e.getNewMediaInfo().getCategory().getLongText(),
				e -> e.getNewMediaInfo().getCategory().getIcon(),
				e -> e.getNewMediaInfo().getCategory().getTooltip()));
		
		return r;
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 300;
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

	@Override
	protected boolean isSortable(int col) {
		return true;
	}

}
