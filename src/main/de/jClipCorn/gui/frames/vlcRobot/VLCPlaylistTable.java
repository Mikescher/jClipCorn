package de.jClipCorn.gui.frames.vlcRobot;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class VLCPlaylistTable extends JCCSimpleTable<VLCPlaylistEntry> {
	private static final long serialVersionUID = 630505973662401189L;

	@DesignCreate
	private static VLCPlaylistTable designCreate() { return new VLCPlaylistTable(ICCWindow.Dummy.frame()); }

	public VLCPlaylistTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<VLCPlaylistEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<VLCPlaylistEntry>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
				"",
				null,
				VLCPlaylistEntry::getIcon,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"*,min=auto",
				"VLCRobotFrame.table.col_title",
				VLCPlaylistEntry::getText,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				this,
				"auto",
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
	protected boolean isMultiselect() {
		return false;
	}
}
