package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;

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

	@Override
	protected IActionSourceObject getSourceObject() {
		return ser;
	}
}
