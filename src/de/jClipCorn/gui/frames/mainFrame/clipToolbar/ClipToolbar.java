package de.jClipCorn.gui.frames.mainFrame.clipToolbar;


public class ClipToolbar extends AbstractClipToolbar{
	private static final long serialVersionUID = -3817962841171328183L;

	public ClipToolbar() {
		super();
		
		setFloatable(false);
		
		create();
	}
	
	@SuppressWarnings("nls")
	private void create() {
		//TODO Make Toolbar changeable in Settings
		addAction("PlayMovie");
		addSeparator();
		addAction("AddMovie");
		addAction("AddSeries");
		addSeparator();
		addAction("EditMovie");
		addAction("RemMovie");
		addSeparator();
		addAction("ScanFolder");
		addAction("ShowSettings");
	}
}
