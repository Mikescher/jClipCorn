package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.datatypes.Tuple;

public class QuickAddMoviesTable extends JCCSimpleTable<Tuple<String, String>> {
	private static final long serialVersionUID = 3193111290324610278L;

	@Override
	protected List<JCCSimpleColumnPrototype<Tuple<String, String>>> configureColumns() {
		List<JCCSimpleColumnPrototype<Tuple<String, String>>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>("QuickAddMoviesTable.colSource", p -> p.Item1, null, null)); //$NON-NLS-1$
		r.add(new JCCSimpleColumnPrototype<>("QuickAddMoviesTable.colDestination", p -> p.Item2, null, null)); //$NON-NLS-1$
		
		return r;
	}

	@Override
	protected void OnDoubleClickElement(Tuple<String, String> element) {
		// 
	}

	@Override
	protected void OnSelectElement(Tuple<String, String> element) {
		// 
	}

	@Override
	protected int getColumnAdjusterMaxWidth() {
		return 250;
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

}
