package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnPrototype;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.util.Str;

import java.util.ArrayList;
import java.util.List;

public class CSFSTable extends JCCSimpleTable<CSFSElement> {
	private static final long serialVersionUID = 1363346001268230117L;

	@DesignCreate
	private static CSFSTable designCreate() { return new CSFSTable(ICCWindow.Dummy.frame()); }

	public CSFSTable(ICCWindow f) {
		super(f);
	}

	@Override
	@SuppressWarnings("nls")
	protected List<JCCSimpleColumnPrototype<CSFSElement>> configureColumns() {
		List<JCCSimpleColumnPrototype<CSFSElement>> r = new ArrayList<>();

		r.add(new JCCSimpleColumnPrototype<>(this, "auto", Str.Empty, null, e -> CSFSElement.getIcon(e.State), null, true));
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "CSFSTable.CCOld", e -> e.CCPathOld.toString(), null, null, true));
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "CSFSTable.CCNew", e -> e.CCPathNew.toString(), null, null, true));
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "CSFSTable.FSOld", e -> e.FSPathOld.toString(), null, null, true));
		r.add(new JCCSimpleColumnPrototype<>(this, "star,min=auto", "CSFSTable.FSNew", e -> e.FSPathNew.toString(), null, null, true));

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
}
