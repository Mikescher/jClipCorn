package de.jClipCorn.gui.frames.mainFrame.popupMenus;

public class ClipMoviePopup extends ClipPopupMenu {
	private static final long serialVersionUID = -3030683884876620182L;

	public ClipMoviePopup() {
		super();
	}

	@SuppressWarnings("nls")
	@Override
	protected void init() {
		addAction("PlayMovie");
		addAction("PlayMovieAnonymous");
		addAction("PrevMovie");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenuTree("SetMovieRating");
		addActionMenuTree("SetTags");
		addAction("SetMovieViewed");
		addAction("SetMovieUnviewed");
		addAction("UndoMovieViewed");
		
		//#############
		addSeparator();
		//#############
		
		addAction("ExportSingleMovie");
		addAction("AddMovieToExportList");
		
		//#############
		addSeparator();
		//#############
		
		addAction("OpenFolder");
		addAction("ShowInBrowser");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditMovie");
		addAction("RemMovie");
	}
}
