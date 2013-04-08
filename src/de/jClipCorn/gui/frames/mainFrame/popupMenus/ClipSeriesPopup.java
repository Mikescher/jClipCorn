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
		addAction("PrevMovie");
		
		//#############
		addSeparator();
		//#############
		
		addAction("AddSeason");
		
		//#############
		addSeparator();
		//#############
		
		addActionMenu("SetSeriesRating").add("SetRatingNO").add("SetRating0").add("SetRating1").add("SetRating2").add("SetRating3").add("SetRating4").add("SetRating5");
		addAction("OpenFolder");
		addAction("ShowInIMDB");
		addAction("MoveSeries");
		
		//#############
		addSeparator();
		//#############
		
		addAction("EditSeries");
		addAction("RemSeries");
	}
}
