package de.jClipCorn.gui.frames.databaseHistoryFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.database.history.CCHistorySingleChange;
import de.jClipCorn.database.history.CCHistoryTable;
import de.jClipCorn.gui.frames.coverPreviewFrame.CoverPreviewFrame;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHistoryTable extends JCCSimpleTable<CCCombinedHistoryEntry> {
	private static final long serialVersionUID = -7175124665697003916L;

	private final DatabaseHistoryFrame _parent;

	@DesignCreate
	private static DatabaseHistoryTable designCreate() { return new DatabaseHistoryTable(null); }

	public DatabaseHistoryTable(DatabaseHistoryFrame frame) {
		_parent = frame;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<CCCombinedHistoryEntry>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				Str.Empty,
				null,
				e -> e.Table.getIcon(),
				e -> Str.toProperCase(e.Table.Name)));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnAction",
				e -> Str.toProperCase(e.Action.Name),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"*",
				"DatabaseHistoryFrame.Table.ColumnElement",
				e -> format(e, e.getSourceElement()),
				null,
				e -> e.ID));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnTime",
				CCCombinedHistoryEntry::formatTime,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"DatabaseHistoryFrame.Table.ColumnChangeCount",
				e -> Integer.toString(e.Changes.size()),
				null,
				e -> "Rows: " + e.HistoryRowCount));
		
		return r;
	}

	private String format(CCCombinedHistoryEntry entry, ICCDatabaseStructureElement e) {
		if (e == null) {

			if (entry.Table == CCHistoryTable.COVERS) {
				CCHistorySingleChange n = CCStreams.iterate(entry.Changes).firstOrNull(c -> Str.equals(c.Field, "FILENAME")); //$NON-NLS-1$
				if (n != null && n.NewValue != null) return n.NewValue;
				if (n != null && n.OldValue != null) return n.OldValue;
			} else if (entry.Table == CCHistoryTable.ELEMENTS || entry.Table == CCHistoryTable.SEASONS || entry.Table == CCHistoryTable.EPISODES) {
				CCHistorySingleChange n = CCStreams.iterate(entry.Changes).firstOrNull(c -> Str.equals(c.Field, "NAME")); //$NON-NLS-1$
				if (n != null && n.NewValue != null) return n.NewValue;
				if (n != null && n.OldValue != null) return n.OldValue;
			} else if (entry.Table == CCHistoryTable.GROUPS) {
				return entry.ID;
			} else if (entry.Table == CCHistoryTable.INFO) {
				return entry.ID;
			}

			return "{{ID := "+entry.ID+"}}"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return e.getQualifiedTitle();
	}

	@Override
	protected void OnDoubleClickElement(CCCombinedHistoryEntry element) {
		ICCDatabaseStructureElement dse = element.getSourceElement();
		if (dse instanceof CCMovie)   { PreviewMovieFrame.show( _parent, (CCMovie)dse);   return; }
		if (dse instanceof CCSeries)  { PreviewSeriesFrame.show(_parent, (CCSeries)dse);  return; }
		if (dse instanceof CCSeason)  { PreviewSeriesFrame.show(_parent, (CCSeason)dse);  return; }
		if (dse instanceof CCEpisode) { PreviewSeriesFrame.show(_parent, (CCEpisode)dse); return; }
		
		if (element.Table == CCHistoryTable.COVERS) {
			if (!Str.isInteger(element.ID)) return;
			int cid = Integer.parseInt(element.ID);

			CCCoverData data = _parent.getMovieList().getCoverCache().getInfoOrNull(cid);
			if (data == null) return;

			Opt<String> hash = element.getNewValue("HASH_FILE"); //$NON-NLS-1$
			if (!hash.isPresent()) return;
			if (hash.get() == null) return;
			if (!hash.get().equalsIgnoreCase(data.Checksum)) return;

			BufferedImage bi = _parent.getMovieList().getCoverCache().getCover(data);
			if (bi == null) return;

			new CoverPreviewFrame(_parent, bi).setVisible(true);
		}
	}

	@Override
	protected void OnSelectElement(CCCombinedHistoryEntry element) {
		_parent.showChanges(element);
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
