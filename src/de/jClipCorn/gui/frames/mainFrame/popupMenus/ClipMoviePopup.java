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
		addActionMenu("SetMovieStatus").add("SetStatus0").add("SetStatus1").add("SetStatus2");
		
		addAction("SetMovieViewed");
		addAction("SetMovieUnviewed");
		
		//#############
		addSeparator();
		//#############
		
		addAction("OpenFolder");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditMovie");
		addAction("RemMovie");
	}
}
