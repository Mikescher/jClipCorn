package de.jClipCorn.gui.frames.mainFrame.popupMenus;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;

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
		ActionMenuWrapper wrapper_Tags = addActionMenuTree("SetTags");
		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) wrapper_Tags.add(String.format("SwitchTag_%02d", i));
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
