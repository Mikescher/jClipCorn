package de.jClipCorn.gui.mainFrame.toolbar;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;


public class ClipToolbar extends AbstractClipToolbar {
	private static final long serialVersionUID = -3817962841171328183L;
	
	public final static String IDENT_SEPERATOR = "#"; //$NON-NLS-1$
	public final static String STANDARD_CONFIG = "Open|#|PlayMovie|#|AddMovie|AddSeries|#|EditMovie|RemMovie|#|ScanFolder|ShowSettings|#|CheckDatabase|ShowStatistics"; //$NON-NLS-1$

	private final CCMovieList movielist;

	@DesignCreate
	private static ClipToolbar designCreate() { return new ClipToolbar(null, true); }

	public ClipToolbar(CCMovieList ml) {
		this(ml, false);
	}

	public ClipToolbar(CCMovieList ml, boolean dummy) {
		super();
		movielist = ml;

		setFloatable(false);

		if (!dummy) create();
	}

	public void recreate()
	{
		removeAll();
		create();

		invalidate();
		repaint();
	}

	private void create() {
		for (String elem : movielist.ccprops().PROP_TOOLBAR_ELEMENTS.getValueAsArray()) {
			if (elem.equals(IDENT_SEPERATOR)) {
				addSeparator();
			} else {
				addAction(elem);
			}
		}
	}
}
