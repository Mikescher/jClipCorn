package de.jClipCorn.gui.frames.watchHistoryFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.frames.databaseHistoryFrame.DatabaseHistoryTable;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;

import java.util.ArrayList;
import java.util.List;

public class WatchHistoryTable extends JCCSimpleTable<WatchHistoryElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final WatchHistoryFrame owner;

	@DesignCreate
	private static WatchHistoryTable designCreate() { return new WatchHistoryTable(null); }

	public WatchHistoryTable(WatchHistoryFrame owner) {
		super();
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<WatchHistoryElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<WatchHistoryElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				"star,min=auto,max=400,expandonly",
				"ClipTableModel.Title",
				WatchHistoryElement::getName,
				WatchHistoryElement::getNameIcon,
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"WatchHistoryFrame.tableHeaders.Date",
				e -> e.getTimestamp().toStringUIShort(),
				null,
				e -> e.getTimestamp().toStringUINormal()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"ClipTableModel.Quality",
				e -> e.getMediaInfoCategory().getShortText(),
				e -> e.getMediaInfoCategory().getIcon(),
				e -> e.getMediaInfoCategory().getTooltip()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				Str.Empty,
				e -> Str.Empty,
				e -> e.getLanguage().getIcon(),
				e -> e.getLanguage().toOutputString()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"ClipTableModel.Length",
				e -> TimeIntervallFormatter.formatShort(e.getLength()),
				null,
				e -> TimeIntervallFormatter.format(e.getLength())));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"ClipTableModel.Tags",
				null,
				e -> e.getTags().getIcon(),
				e -> e.getTags().getAsString()));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"ClipTableModel.Format",
				e -> e.getFormat().asString(),
				e -> e.getFormat().getIcon(),
				null));
		
		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"ClipTableModel.Size",
				e -> e.getSize().getFormatted(),
				null,
				e -> FileSizeFormatter.formatBytes(e.getSize())));
		
		return r;
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

	@Override
	protected boolean isSortable(int col) {
		return true;
	}
}
