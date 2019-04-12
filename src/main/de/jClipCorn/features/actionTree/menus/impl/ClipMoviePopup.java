package de.jClipCorn.features.actionTree.menus.impl;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.actionTree.menus.ClipPopupMenu;

public class ClipMoviePopup extends ClipPopupMenu {
	private static final long serialVersionUID = -3030683884876620182L;

	private final CCMovie mov;
	
	public ClipMoviePopup(CCMovie m) {
		super();
		mov = m;
		init();
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
		addActionMenuTree("SetTags_Movies");
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
		addOpenInBrowserAction(mov, mov.getOnlineReference());
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditMovie");
		addAction("RemMovie");
	}

	@Override
	protected IActionSourceObject getSourceObject() {
		return mov;
	}
}
