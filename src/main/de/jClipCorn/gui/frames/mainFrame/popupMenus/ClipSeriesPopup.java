package de.jClipCorn.gui.frames.mainFrame.popupMenus;

import de.jClipCorn.database.databaseElement.CCSeries;

public class ClipSeriesPopup extends ClipPopupMenu {
	private static final long serialVersionUID = -6475272518552625501L;

	private final CCSeries ser;
	
	public ClipSeriesPopup(CCSeries s) {
		super();
		ser = s;
		init();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {
		addAction("PrevSeries");
		
		//#############
		addSeparator();
		//#############
		
		addAction("AddSeason");
		
		//#############
		addSeparator();
		//#############
		
		addAction("ExportSingleSeries");
		addAction("AddSeriesToExportList");
		addAction("SaveTXTEpisodeguide");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenuTree("SetSeriesRating");
		addActionMenuTree("SetTags_Series");

		//#############
		addSeparator();
		//#############

		addAction("OpenFolder");
		addOpenInBrowserAction(ser, ser.getOnlineReference());

		//#############
		addSeparator();
		//#############

		addAction("MoveSeries");
		addAction("CreateFolderStructSeries");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditSeries");
		addAction("RemSeries");
	}
}
