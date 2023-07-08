package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.history.CCCombinedHistoryEntry;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;

import java.util.ArrayList;
import java.util.List;

public class QuickAddMoviesTable extends JCCSimpleTable<Tuple<FSPath, CCPath>> {
	private static final long serialVersionUID = 3193111290324610278L;

	@DesignCreate
	private static QuickAddMoviesTable designCreate() { return new QuickAddMoviesTable(ICCWindow.Dummy.frame()); }

	public QuickAddMoviesTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected JCCSimpleColumnList<Tuple<FSPath, CCPath>> configureColumns() {
		JCCSimpleColumnList<Tuple<FSPath, CCPath>> r = new JCCSimpleColumnList<>(this);

		r.add("QuickAddMoviesTable.colSource")
				.withSize("auto")
				.withText(p -> p.Item1.toString())
				.sortable();
		r.add("QuickAddMoviesTable.colDestination")
				.withSize("star,min=auto")
				.withText(p -> p.Item2.toString())
				.sortable();
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(Tuple<FSPath, CCPath> element) {
		// 
	}

	@Override
	protected void OnSelectElement(Tuple<FSPath, CCPath> element) {
		// 
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
