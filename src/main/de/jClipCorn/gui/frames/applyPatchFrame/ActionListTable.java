package de.jClipCorn.gui.frames.applyPatchFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class ActionListTable extends JCCSimpleTable<ActionVM> {
	@DesignCreate
	private static ActionListTable designCreate() { return new ActionListTable(null); }

	private final ApplyPatchFrame _owner;

	public ActionListTable(ApplyPatchFrame f) {
		super(f);
		_owner  = f;
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<ActionVM>> configureColumns() {
		List<JCCSimpleColumnPrototype<ActionVM>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(this, "auto",       "ApplyPatchFrame.table.column_State",       null,                                   ActionVM::getStateIcon, null, false));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",       "ApplyPatchFrame.table.column_Ctr",         e -> String.valueOf(e.Ctr),             null,                   null, false));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",       "ApplyPatchFrame.table.column_Type",        e -> e.Type,                            null,                   null, false));
		r.add(new JCCSimpleColumnPrototype<>(this, "auto",       "ApplyPatchFrame.table.column_CmdCount",    e -> String.valueOf(e.Commands.size()), null,                   null, false));
		r.add(new JCCSimpleColumnPrototype<>(this, "*,min=auto", "ApplyPatchFrame.table.column_Description", e -> e.Description,                     null,                   null, false));

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
