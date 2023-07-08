package de.jClipCorn.gui.frames.createSeriesFolderStructureFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.frames.watchHistoryFrame.element.WatchHistoryElement;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
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
	protected JCCSimpleColumnList<CSFSElement> configureColumns() {
		JCCSimpleColumnList<CSFSElement> r = new JCCSimpleColumnList<>(this);

		r.add(Str.Empty)
				.withSize("auto")
				.withIcon(e -> CSFSElement.getIcon(e.State))
				.sortable();
		r.add("CSFSTable.CCOld")
				.withSize("star,min=auto")
				.withText(e -> e.CCPathOld.toString())
				.sortable();
		r.add("CSFSTable.CCNew")
				.withSize("star,min=auto")
				.withText(e -> e.CCPathNew.toString())
				.sortable();
		r.add("CSFSTable.FSOld")
				.withSize("star,min=auto")
				.withText(e -> e.FSPathOld.toString())
				.sortable();
		r.add("CSFSTable.FSNew")
				.withSize("star,min=auto")
				.withText(e -> e.FSPathNew.toString())
				.sortable();

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
