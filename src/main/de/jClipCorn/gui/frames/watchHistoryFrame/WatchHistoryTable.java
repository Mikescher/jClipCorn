package de.jClipCorn.gui.frames.watchHistoryFrame;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

public class WatchHistoryTable extends JCCSimpleTable<WatchHistoryElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final WatchHistoryFrame owner;

	public WatchHistoryTable(WatchHistoryFrame owner) {
		super();
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<WatchHistoryElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<WatchHistoryElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Title",
				e -> e.getName(),
				e -> e.getNameIcon(),
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"WatchHistoryFrame.tableHeaders.Date",
				e -> e.getTimestamp().toStringUIShort(),
				null,
				e -> e.getTimestamp().toStringUINormal()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Quality",
				e -> e.getQuality().asString(),
				e -> e.getQuality().getIcon(),  
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Language",
				e -> TimeIntervallFormatter.formatShort(e.getLength()),
				null,
				e -> TimeIntervallFormatter.format(e.getLength())));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Length",
				e -> e.getLanguage().asString(),
				e -> e.getLanguage().getIcon(), null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Tags",
				null,
				e -> e.getTags().getIcon(),
				e -> e.getTags().getAsString()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Format",
				e -> e.getFormat().asString(),
				e -> e.getFormat().getIcon(),
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"ClipTableModel.Size",
				e -> e.getSize().getFormatted(),
				null,
				e -> FileSizeFormatter.formatBytes(e.getSize())));
		
		return r;
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 400;
	}

	@Override
	protected void OnDoubleClickElement(WatchHistoryElement element) {
		element.open(owner);
	}

	@Override
	protected void OnSelectElement(WatchHistoryElement element) {
		owner.onSelect(element);
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
