package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class VLCRobotLogTable extends JCCSimpleTable<VLCRobotLogEntry> {
	private static final long serialVersionUID = 630505973662401189L;

	private final VLCRobotFrame owner;
	
	public VLCRobotLogTable(VLCRobotFrame frame) {
		owner = frame;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<VLCRobotLogEntry>> configureColumns() {
		List<JCCSimpleColumnPrototype<VLCRobotLogEntry>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"@Index",
				e -> String.valueOf(e.Index),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"@Event",
				VLCRobotLogEntry::getEventType,
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"auto",
				"@Timestamp",
				e -> e.Timestamp.toStringUINormal(),
				null,
				null));

		r.add(new JCCSimpleColumnPrototype<>(
				"*",
				"@Change",
				VLCRobotLogEntry::format,
				null,
				null));

		return r;
	}

	@Override
	protected void OnDoubleClickElement(VLCRobotLogEntry element) {
		// nothing
	}

	@Override
	protected void OnSelectElement(VLCRobotLogEntry element) {
		owner.showLogEntry(element);
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
