package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class VLCPlaylistTable extends JCCSimpleTable<VLCPlaylistEntry> {

	@Override
	protected List<JCCSimpleColumnPrototype<VLCPlaylistEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<VLCPlaylistEntry>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"",
				null,
				VLCPlaylistEntry::getIcon,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"VLCRobotFrame.table.col_title",
				VLCPlaylistEntry::getText,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"VLCRobotFrame.table.col_len",
				VLCPlaylistEntry::getLengthText,
				null,
				null));

		return r;
	}

	@Override
	protected void OnDoubleClickElement(VLCPlaylistEntry element) {
		// nothing
	}

	@Override
	protected void OnSelectElement(VLCPlaylistEntry element) {
		// nothing
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 300;
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
