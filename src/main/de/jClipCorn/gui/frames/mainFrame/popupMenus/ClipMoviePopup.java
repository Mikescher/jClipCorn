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
		
		addActionMenu("SetMovieRating").add("SetRatingNO").add("SetRating0").add("SetRating1").add("SetRating2").add("SetRating3").add("SetRating4").add("SetRating5");
		ActionMenuWrapper wrapper_Tags = addActionMenu("SetTags");
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
