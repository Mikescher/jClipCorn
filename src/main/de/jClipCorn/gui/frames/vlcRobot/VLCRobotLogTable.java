package de.jClipCorn.gui.frames.vlcRobot;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class VLCRobotLogTable extends JCCSimpleTable<VLCRobotLogEntry> {
	private static final long serialVersionUID = 630505973662401189L;

	private final VLCRobotFrame owner;

	@DesignCreate
	private static VLCRobotLogTable designCreate() { return new VLCRobotLogTable(new VLCRobotFrame(new JFrame(), CCMovieList.createStub())); }

	public VLCRobotLogTable(@NotNull VLCRobotFrame frame) {
		super(frame);
		owner = frame;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<VLCRobotLogEntry> configureColumns() {
		JCCSimpleColumnList<VLCRobotLogEntry> r = new JCCSimpleColumnList<>(this);

		r.add("@Index")
		 .withSize("auto")
		 .withText(e -> String.valueOf(e.Index));

		r.add("@Event")
		 .withSize("auto")
		 .withText(VLCRobotLogEntry::getEventType);

		r.add("@Timestamp")
		 .withSize("auto")
		 .withText(e -> e.Timestamp.toStringUINormal());

		r.add("@Change")
		 .withSize("*")
		 .withText(VLCRobotLogEntry::format);

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

}
