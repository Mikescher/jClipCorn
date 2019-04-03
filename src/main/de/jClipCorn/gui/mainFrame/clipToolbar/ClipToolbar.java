package de.jClipCorn.gui.mainFrame.clipToolbar;

import java.util.List;

import de.jClipCorn.properties.CCProperties;


public class ClipToolbar extends AbstractClipToolbar {
	private static final long serialVersionUID = -3817962841171328183L;
	
	public final static String IDENT_SEPERATOR = "#"; //$NON-NLS-1$
	public final static String STANDARD_CONFIG = "Open|#|PlayMovie|#|AddMovie|AddSeries|#|EditMovie|RemMovie|#|ScanFolder|ShowSettings|#|CheckDatabase|ShowStatistics"; //$NON-NLS-1$

	public ClipToolbar() {
		super();
		
		setFloatable(false);
		
		create();
	}
	
	private void create() {
		List<String> config = CCProperties.getInstance().PROP_TOOLBAR_ELEMENTS.getValueAsArray();

		for (int i = 0; i < config.size(); i++) {
			String elem = config.get(i);
			
			if (elem.equals(IDENT_SEPERATOR)) {
				addSeparator();
			} else {
				addAction(elem);
			}
		}
	}
}
