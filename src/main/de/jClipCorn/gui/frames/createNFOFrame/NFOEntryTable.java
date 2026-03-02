package de.jClipCorn.gui.frames.createNFOFrame;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.nfo.NFOEntry;
import de.jClipCorn.gui.guiComponents.ICCWindow;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleColumnList;
import de.jClipCorn.gui.guiComponents.jCCSimpleTable.JCCSimpleTable;
import de.jClipCorn.gui.resources.Resources;

import javax.swing.*;

@SuppressWarnings("nls")
public class NFOEntryTable extends JCCSimpleTable<NFOEntry> {
	@DesignCreate
	private static NFOEntryTable designCreate() { return new NFOEntryTable(new CreateNFOFrame(ICCWindow.Dummy.frame(), CCMovieList.createStub())); }

	private final CreateNFOFrame _owner;

	public NFOEntryTable(CreateNFOFrame f) {
		super(f);
		_owner = f;
	}

	@Override
	protected JCCSimpleColumnList<NFOEntry> configureColumns() {
		JCCSimpleColumnList<NFOEntry> r = new JCCSimpleColumnList<>(this);

		r.add("")
		 .withSize("auto")
		 .withIcon(NFOEntryTable::getStatusIcon);

		r.add("CreateNFOFrame.table.column_StatusText")
		 .withSize("auto,max=100")
		 .withText(e -> e.getStatus().getLocalized());

		r.add("CreateNFOFrame.table.column_Type")
		 .withSize("auto")
		 .withText(e -> e.getElementType().getLocalized());

		r.add("CreateNFOFrame.table.column_Title")
		 .withSize("auto,min=100")
		 .withText(NFOEntry::getElementTitle);

		r.add("CreateNFOFrame.table.column_Path")
		 .withSize("*,min=200")
		 .withText(e -> e.getFilePath().toString());

		return r;
	}

	private static Icon getStatusIcon(NFOEntry entry) {
		switch (entry.getStatus()) {
			case UNCHANGED: return Resources.ICN_GENERIC_ORB_GRAY.get16x16();
			case CHANGED:   return Resources.ICN_GENERIC_ORB_YELLOW.get16x16();
			case CREATE:    return Resources.ICN_GENERIC_ORB_GREEN.get16x16();
			case DELETE:    return Resources.ICN_GENERIC_ORB_ORANGE.get16x16();
			case ERROR:     return Resources.ICN_GENERIC_ORB_RED.get16x16();
			default:        return Resources.ICN_GENERIC_ORB_GRAY.get16x16();
		}
	}

	@Override
	protected void OnDoubleClickElement(NFOEntry element) {
		// Could potentially show NFO content preview
	}

	@Override
	protected void OnSelectElement(NFOEntry element) {
		// No action needed
	}

	@Override
	protected boolean isMultiselect() {
		return false;
	}
}
