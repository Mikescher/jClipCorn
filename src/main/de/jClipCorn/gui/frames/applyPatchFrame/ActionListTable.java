package de.jClipCorn.gui.frames.applyPatchFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.log.CCChangeLogElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class ActionListTable extends JCCSimpleTable<ActionVM> {
	@DesignCreate
	private static ActionListTable designCreate() { return new ActionListTable(new ApplyPatchFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	private final ApplyPatchFrame _owner;

	public ActionListTable(ApplyPatchFrame f) {
		super(f);
		_owner  = f;
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<ActionVM> configureColumns() {
		JCCSimpleColumnList<ActionVM> r = new JCCSimpleColumnList<>(this);

		r.add("ApplyPatchFrame.table.column_State")
		 .withSize("auto")
		 .withIcon(ActionVM::getStateIcon);

		r.add("ApplyPatchFrame.table.column_Ctr")
		 .withSize("auto")
		 .withText(e -> String.valueOf(e.Ctr));

		r.add("ApplyPatchFrame.table.column_Type")
		 .withSize("auto")
		 .withText(e -> e.Type);

		r.add("ApplyPatchFrame.table.column_CmdCount")
		 .withSize("auto")
		 .withText(e -> String.valueOf(e.Commands.size()));

		r.add("ApplyPatchFrame.table.column_Description")
		 .withSize("*,min=auto")
		 .withText(e -> e.Description);

		return r;
	}

	@Override
	protected void OnDoubleClickElement(ActionVM element) {
		/**/
	}

	@Override
	protected void OnSelectElement(ActionVM element) {
		/**/
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
