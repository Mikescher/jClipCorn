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
		addAction("PrevMovie");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenu("SetMovieRating").add("SetRatingNO").add("SetRating0").add("SetRating1").add("SetRating2").add("SetRating3").add("SetRating4").add("SetRating5");
		addActionMenu("SetTags").add("SwitchTag_00").add("SwitchTag_01").add("SwitchTag_02").add("SwitchTag_03").add("SwitchTag_04");
		addAction("SetMovieViewed");
		addAction("SetMovieUnviewed");
		
		//#############
		addSeparator();
		//#############
		
		addAction("ExportSingleMovie");
		addAction("AddMovieToExportList");
		
		//#############
		addSeparator();
		//#############
		
		addAction("OpenFolder");
		addAction("ShowInIMDB");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditMovie");
		addAction("RemMovie");
	}
}
