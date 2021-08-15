package de.jClipCorn.gui.frames.watchHistoryFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WatchHistoryTable extends JCCSimpleTable<WatchHistoryElement> {
	private static final long serialVersionUID = 3308858204018846266L;
	
	private final WatchHistoryFrame owner;

	@DesignCreate
	private static WatchHistoryTable designCreate() { return new WatchHistoryTable(new WatchHistoryFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	public WatchHistoryTable(@NotNull WatchHistoryFrame owner) {
		super(owner);
		this.owner = owner;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<WatchHistoryElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<WatchHistoryElement>> r = new ArrayList<>();
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"star,min=auto,max=400,expandonly",
				"ClipTableModel.Title",
				WatchHistoryElement::getName,
				WatchHistoryElement::getNameIcon,
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"WatchHistoryFrame.tableHeaders.Date",
				e -> e.getTimestamp().toStringUIShort(),
				null,
				e -> e.getTimestamp().toStringUINormal(),
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"ClipTableModel.Quality",
				e -> e.getMediaInfoCategory().getShortText(),
				e -> e.getMediaInfoCategory().getIcon(),
				e -> e.getMediaInfoCategory().getTooltip(),
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				Str.Empty,
				e -> Str.Empty,
				e -> e.getLanguage().getIcon(),
				e -> e.getLanguage().toOutputString(),
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"ClipTableModel.Length",
				e -> TimeIntervallFormatter.formatShort(e.getLength()),
				null,
				e -> TimeIntervallFormatter.format(e.getLength()),
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"ClipTableModel.Tags",
				null,
				e -> e.getTags().getIcon(),
				e -> e.getTags().getAsString(),
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"ClipTableModel.Format",
				e -> e.getFormat().asString(),
				e -> e.getFormat().getIcon(),
				null,
				true));
		
		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"ClipTableModel.Size",
				e -> e.getSize().getFormatted(),
				null,
				e -> FileSizeFormatter.formatBytes(e.getSize()),
				true));
		
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
}
