package de.jClipCorn.features.actionTree;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCBoolProperty;

public abstract class UIActionTree {
	private static UIActionTree instance = null;

	protected CCActionElement root;

	public UIActionTree() {
		createStructure();
		createProperties();

		instance = this;
	}

	protected void createRoot() {
		root = new CCActionElement("ROOT", null, "", null); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected CCActionElement addMaster(String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes, boolean vis) {
		return root.addChild(new CCActionElement(name, stroke, caption, iconRes, vis));
	}
	
	protected CCActionElement addMaster(String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes, CCBoolProperty vis) {
		return root.addChild(new CCActionElement(name, stroke, caption, iconRes, vis.getValue()));
	}
	
	protected CCActionElement addMaster(String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes) {
		return addMaster(name, stroke, caption, iconRes, true);
	}

	protected CCActionElement add(CCActionElement parent, String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes, boolean readOnlyRestriction, CCActionTreeListener action) {
		CCActionElement e = parent.addChild(new CCActionElement(name, stroke, caption, iconRes));
		if (action != null) e.addListener(action);
		if (readOnlyRestriction) e.setReadOnlyRestriction();
		return e;
	}

	protected CCActionElement add(CCActionElement parent, String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes, CCActionTreeListener action) {
		return add(parent, name, stroke, caption, iconRes, false, action);
	}

	protected CCActionElement add(CCActionElement parent, String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes) {
		return add(parent, name, stroke, caption, iconRes, false, null);
	}

	public CCActionElement getRoot() {
		return root;
	}

	public static UIActionTree getInstance() {
		return instance;
	}

	public CCActionElement find(String name) {
		return root.find(name);
	}
	
	public void implementKeyListener(MainFrame f, JComponent comp) {
		getRoot().implementAllKeyListener(f, comp);
	}
	
	private void createProperties() {
		getRoot().createAllProperties(CCProperties.getInstance());
	}
	
	public String getCompleteToolbarConfig() {
		return getRoot().getRootToolbarConfig();
	}
	
	public void printTree() {
		root.printTree(0);
	}
	
	protected abstract void createStructure();
}
