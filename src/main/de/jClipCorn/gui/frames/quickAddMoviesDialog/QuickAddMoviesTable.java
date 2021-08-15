package de.jClipCorn.gui.frames.quickAddMoviesDialog;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
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
	private static QuickAddMoviesTable designCreate() { return new QuickAddMoviesTable(null); }

	public QuickAddMoviesTable(ICCWindow f) {
		super(f);
	}

	@Override
	protected List<JCCSimpleColumnPrototype<Tuple<FSPath, CCPath>>> configureColumns() {
		List<JCCSimpleColumnPrototype<Tuple<FSPath, CCPath>>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(this, "auto",          "QuickAddMoviesTable.colSource",      p -> p.Item1.toString(), null, null, true)); //$NON-NLS-1$ //$NON-NLS-2$
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "QuickAddMoviesTable.colDestination", p -> p.Item2.toString(), null, null, true)); //$NON-NLS-1$ //$NON-NLS-2$
		
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
