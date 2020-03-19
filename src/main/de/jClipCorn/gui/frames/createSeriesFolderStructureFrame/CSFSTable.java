package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class CSFSTable extends JCCSimpleTable<CSFSElement> {
	private static final long serialVersionUID = 1363346001268230117L;

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CSFSElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<CSFSElement>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>("auto", Str.Empty, null, e -> CSFSElement.getIcon(e.State), null));
		r.add(new JCCSimpleColumnPrototype<>("star,min=auto", "CSFSTable.CCOld", e -> e.CCPathOld, null, null));
		r.add(new JCCSimpleColumnPrototype<>("star,min=auto", "CSFSTable.CCNew", e -> e.CCPathNew, null, null));
		r.add(new JCCSimpleColumnPrototype<>("star,min=auto", "CSFSTable.FSOld", e -> e.FSPathOld, null, null));
		r.add(new JCCSimpleColumnPrototype<>("star,min=auto", "CSFSTable.FSNew", e -> e.FSPathNew, null, null));

		return r;
	}

	@Override
	protected void OnDoubleClickElement(CSFSElement element) {
		//
	}

	@Override
	protected void OnSelectElement(CSFSElement element) {
		//
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}

	@Override
	protected boolean isSortable(int col) {
		return true;
	}
}
