package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public class CSFSTable extends JCCSimpleTable<CSFSElement> {

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CSFSElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<CSFSElement>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>("CSFSTable.State", null, e -> CSFSElement.getIcon(e.State), null));
		r.add(new JCCSimpleColumnPrototype<>("CSFSTable.CCOld", e -> e.CCPathOld, null, null));
		r.add(new JCCSimpleColumnPrototype<>("CSFSTable.CCNew", e -> e.CCPathNew, null, null));
		r.add(new JCCSimpleColumnPrototype<>("CSFSTable.FSOld", e -> e.FSPathOld, null, null));
		r.add(new JCCSimpleColumnPrototype<>("CSFSTable.FSNew", e -> e.FSPathNew, null, null));

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
	protected int getColumnAdjusterMaxWidth() {
		return 250;
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
