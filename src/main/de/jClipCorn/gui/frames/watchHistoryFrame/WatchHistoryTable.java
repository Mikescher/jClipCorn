package de.jClipCorn.gui.frames.watchHistoryFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.vlcRobot.VLCRobotLogEntry;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.FileSizeFormatter;
import de.jClipCorn.util.formatter.OrdinalFormatter;
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
	protected JCCSimpleColumnList<WatchHistoryElement> configureColumns() {
		JCCSimpleColumnList<WatchHistoryElement> r = new JCCSimpleColumnList<>(this);

		r.add("ClipTableModel.Title")
			.withSize("star,min=auto,max=400,priority=max,expandonly")
			.withText(WatchHistoryElement::getName)
			.withIcon(WatchHistoryElement::getNameIcon)
			.sortable();

		r.add("@#")
			.withSize("auto")
			.withText(p -> OrdinalFormatter.formatOrdinal(p.getCounter()))
			.unsortable();

		r.add("WatchHistoryFrame.tableHeaders.Date")
		 .withSize("auto")
		 .withText(e -> e.getTimestamp().toStringUIShort())
		 .withTooltip(e -> e.getTimestamp().toStringUINormal())
		 .sortable();
		
		r.add("ClipTableModel.Quality")
		 .withSize("auto")
		 .withText(e -> e.getMediaInfoCategory().getShortText())
		 .withIcon(e -> e.getMediaInfoCategory().getIcon())
		 .withTooltip(e -> e.getMediaInfoCategory().getTooltip())
		 .sortable();
		
		r.add(Str.Empty)
		 .withSize("auto")
		 .withIcon(e -> e.getLanguage().getIcon())
		 .withTooltip(e -> e.getLanguage().toOutputString())
		 .sortable();
		
		r.add("ClipTableModel.Length")
		 .withSize("auto")
		 .withText(e -> TimeIntervallFormatter.formatShort(e.getLength()))
		 .withTooltip(e -> TimeIntervallFormatter.format(e.getLength()))
		 .sortable();
		
		r.add("ClipTableModel.Tags")
		 .withSize("auto")
		 .withIcon(e -> e.getTags().getIcon())
		 .withTooltip(e -> e.getTags().getAsString())
		 .sortable();
		
		r.add("ClipTableModel.Format")
		 .withSize("auto")
		 .withText(e -> e.getFormat().asString())
		 .withIcon(e -> e.getFormat().getIcon())
		 .sortable();
		
		r.add("ClipTableModel.Size")
		 .withSize("auto")
		 .withText(e -> e.getSize().getFormatted())
		 .withTooltip(e -> FileSizeFormatter.formatBytes(e.getSize()))
		 .sortable();
		
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
