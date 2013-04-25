package de.jClipCorn.gui.actionTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.Main;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCCaptionedKeyStrokeProperty;
import de.jClipCorn.properties.property.CCKeyStrokeProperty;
import de.jClipCorn.util.KeyStrokeUtil;

public class CCActionElement {
	private final String name;
	private final String captionIdent;
	private final String iconRes;
	private final boolean visible;
	private final KeyStroke defaultKeyStroke;
	
	private CCKeyStrokeProperty keyStrokeProperty = null;
	
	private final List<ActionListener> listener;
	private final List<CCActionElement> children;
	
	public CCActionElement(String name, KeyStroke stroke, String caption, String iconRes) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = true;
		this.defaultKeyStroke = stroke;
	}
	
	public CCActionElement(String name, KeyStroke stroke, String caption, String iconRes, boolean vis) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = vis;
		this.defaultKeyStroke = stroke;
	}

	public String getName() {
		return name;
	}
	
	public String getCaption() {
		if (captionIdent.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		
		return LocaleBundle.getString(captionIdent);
	}
	
	public String getCaptionIdent() {
		return captionIdent;
	}
	
	public ImageIcon getSmallIcon() {
		if (iconRes == null || iconRes.isEmpty()) {
			return null;
		}
		
		return CachedResourceLoader.getSmallImageIcon(iconRes);
	}
	
	public ImageIcon getIcon() {
		if (iconRes == null || iconRes.isEmpty()) {
			return null;
		}
		
		return CachedResourceLoader.getImageIcon(iconRes);
	}
	
	public void addListener(ActionListener al) {
		listener.add(al);
	}
	
	public boolean removeListener(ActionListener al) {
		return listener.remove(al);
	}
	
	public void clearActionListeners() {
		listener.clear();
	}
	
	public void execute() {
		execute(""); //$NON-NLS-1$
	}
	
	public void execute(String cmd) {
		for (ActionListener al : listener) {
			al.actionPerformed(new ActionEvent(this, 0, cmd));
		}
	}
	
	public Iterator<CCActionElement> getChildren() {
		return children.iterator();
	}
	
	public List<CCActionElement> getAllChildren() {
		List<CCActionElement> childs = new ArrayList<>();
		
		childs.addAll(children);
		
		for (int i = 0; i < children.size(); i++) {
			childs.addAll(children.get(i).getAllChildren());
		}
		
		return childs;
	}
	
	public CCActionElement addChild(CCActionElement cae) {
		children.add(cae);
		return cae;
	}
	
	public CCActionElement find(String name) {
		for (CCActionElement cae : children) {
			if (cae.getName().equalsIgnoreCase(name)) {
				return cae;
			} else {
				CCActionElement found = cae.find(name);
				if (found != null) {
					return found;
				}
			}
		}
		
		return null;
	}
	
	public boolean hasChildren() {
		return ! children.isEmpty();
	}

	public boolean isVisible() {
		return visible;
	}

	public void testTree() {
		List<CCActionElement> childs = getAllChildren();
		
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			for (int j = 0; j < childs.size(); j++) {
				if (i != j && StringUtils.equalsIgnoreCase(childs.get(i).getName(), childs.get(j).getName())) {
					System.out.println(String.format("[DBG] Duplicate Item (%s) in ActionTree found", childs.get(j).getCaptionIdent())); //$NON-NLS-1$
				}
			}
		}
	}
	
	public void printTree(int deep) {
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < deep; i++) {
			build.append("    "); //$NON-NLS-1$
		}
		build.append("|>"); //$NON-NLS-1$
		build.append(name);
		System.out.println(build.toString());
		for (CCActionElement el : children) {
			el.printTree(deep+1);
		}
	}
	
	public KeyStroke getKeyStroke() {
		if (keyStrokeProperty == null) {
			return null;
		}
		return keyStrokeProperty.getValue();
	}
	
	private void implementKeyListener(JComponent comp, final CCActionElement e) {
		final KeyStroke stroke = getKeyStroke();
		
		if (KeyStrokeUtil.isEmpty(stroke)) {
			return;
		}
		
		InputMap map = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = comp.getActionMap();
		
		map.put(stroke, name);
		act.put(name, new AbstractAction() {
			private static final long serialVersionUID = 19873468234L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				e.onKeyPressed(stroke);
			}
		});
	}
	
	private void onKeyPressed(KeyStroke stroke) {
		List<CCActionElement> childs = getAllChildren();
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			if (stroke.equals(childs.get(i).getKeyStroke())) {
				childs.get(i).execute();
			}
		}
	}
	
	public void implementAllKeyListener(JComponent comp) {
		List<CCActionElement> childs = getAllChildren();
		
		implementKeyListener(comp, this);
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).implementKeyListener(comp, this);
		}
	}
	
	private void createProperty(CCProperties props) {
		if (defaultKeyStroke == null) {
			keyStrokeProperty = new CCCaptionedKeyStrokeProperty(CCProperties.CAT_KEYSTROKES, props, "PROP_KEYSTROKE_" + name.toUpperCase(), captionIdent, KeyStrokeUtil.getEmptyKeyStroke()); //$NON-NLS-1$
		} else {
			keyStrokeProperty = new CCCaptionedKeyStrokeProperty(CCProperties.CAT_KEYSTROKES, props, "PROP_KEYSTROKE_" + name.toUpperCase(), captionIdent, defaultKeyStroke); //$NON-NLS-1$
		}
	}
	
	public void createAllProperties(CCProperties props) {
		List<CCActionElement> childs = new ArrayList<>();
		
		for (int i = 0; i < children.size(); i++) {
			childs.addAll(children.get(i).getAllChildren());
		}
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).createProperty(props);
		}
		
		if (Main.DEBUG) {
			System.out.println(String.format("[DBG] %d Properties in ActionTree intialized", childs.size())); //$NON-NLS-1$
		}
	}
}
