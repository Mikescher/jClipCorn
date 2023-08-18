package de.jClipCorn.gui.mainFrame.toolbar;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.features.actionTree.UIActionTree;

import java.awt.*;
import java.util.List;


public class ClipToolbar extends AbstractClipToolbar {
	private static final long serialVersionUID = -3817962841171328183L;
	
	public final static String IDENT_SEPERATOR = "#"; //$NON-NLS-1$
	public final static String STANDARD_CONFIG = "Open|#|PlayMovie|#|AddMovie|AddSeries|#|EditMovie|RemMovie|#|ScanFolder|ShowSettings|#|CheckDatabase|ShowStatistics"; //$NON-NLS-1$

	private final CCMovieList movielist;

	@DesignCreate
	private static ClipToolbar designCreate() { return new ClipToolbar(null); }

	public ClipToolbar(CCMovieList ml) {
		super();
		movielist = ml;

		setFloatable(false);

		if (ml != null) {
			create(CCActionTree.getInstance(), movielist.ccprops().PROP_TOOLBAR_ELEMENTS.getValueAsArray());
		} else {
			// dummy data for DesignCreate
			create(new CCActionTree(CCMovieList.createStub()), List.of(ClipToolbar.STANDARD_CONFIG.split("\\|")));
		}
	}

	public void recreate()
	{
		removeAll();
		create(CCActionTree.getInstance(), movielist.ccprops().PROP_TOOLBAR_ELEMENTS.getValueAsArray());

		invalidate();
		repaint();
	}

	private void create(UIActionTree tree, java.util.List<String> btns) {
		for (String elem : btns) {
			if (elem.equals(IDENT_SEPERATOR)) {
				addSeparator();
			} else {
				addAction(tree, elem);
			}
		}
	}

	@Override
	public void setLayout(LayoutManager mgr) {
		if (mgr != null) super.setLayout(mgr); // make setLayout(null) a noop, because JGoodiesFormLayout always wants to add a call to it -.-
	}
}
