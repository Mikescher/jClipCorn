package de.jClipCorn.gui.frames.mainFrame.popupMenus;


public class ClipSeriesPopup extends ClipPopupMenu {
	private static final long serialVersionUID = -6475272518552625501L;

	public ClipSeriesPopup() {
		super();
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
		addAction("OpenFolder");
		addAction("ShowInBrowser");
		addAction("MoveSeries");
		addAction("CreateFolderStructSeries");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditSeries");
		addAction("RemSeries");
	}
}
