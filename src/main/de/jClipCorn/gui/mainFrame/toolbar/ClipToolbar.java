package de.jClipCorn.gui.mainFrame.toolbar;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.properties.CCProperties;


public class ClipToolbar extends AbstractClipToolbar {
	private static final long serialVersionUID = -3817962841171328183L;
	
	public final static String IDENT_SEPERATOR = "#"; //$NON-NLS-1$
	public final static String STANDARD_CONFIG = "Open|#|PlayMovie|#|AddMovie|AddSeries|#|EditMovie|RemMovie|#|ScanFolder|ShowSettings|#|CheckDatabase|ShowStatistics"; //$NON-NLS-1$

	@DesignCreate
	private static ClipToolbar designCreate() { return new ClipToolbar(true); }

	public ClipToolbar() {
		this(false);
	}

	public ClipToolbar(boolean dummy) {
		super();

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
		for (String elem : CCProperties.getInstance().PROP_TOOLBAR_ELEMENTS.getValueAsArray()) {
			if (elem.equals(IDENT_SEPERATOR)) {
				addSeparator();
			} else {
				addAction(elem);
			}
		}
	}
}
