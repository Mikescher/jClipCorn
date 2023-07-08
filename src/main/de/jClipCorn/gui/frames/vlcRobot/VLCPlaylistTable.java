package de.jClipCorn.gui.frames.vlcRobot;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.frames.updateMetadataFrame.UpdateMetadataTableElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
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
	protected JCCSimpleColumnList<VLCPlaylistEntry> configureColumns() {
		JCCSimpleColumnList<VLCPlaylistEntry> r = new JCCSimpleColumnList<>(this);

		r.add("")
		 .withSize("auto")
		 .withIcon(VLCPlaylistEntry::getIcon);

		r.add("VLCRobotFrame.table.col_title")
		 .withSize("*,min=auto")
		 .withText(VLCPlaylistEntry::getText);

		r.add("VLCRobotFrame.table.col_len")
		 .withSize("auto")
		 .withText(VLCPlaylistEntry::getLengthText);

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
