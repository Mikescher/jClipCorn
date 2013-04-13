package de.jClipCorn.gui.actionTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CCActionElement {
	private final String name;
	private final String captionIdent;
	private final String iconRes;
	private final boolean visible;
	private final KeyStroke keyStroke;
	
	private final ArrayList<ActionListener> listener;
	private final ArrayList<CCActionElement> children;
	
	public CCActionElement(String name, KeyStroke stroke, String caption, String iconRes) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = true;
		this.keyStroke = stroke;
	}
	
	public CCActionElement(String name, KeyStroke stroke, String caption, String iconRes, boolean vis) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = vis;
		this.keyStroke = stroke;
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
	
	public ArrayList<CCActionElement> getAllChildren() {
		ArrayList<CCActionElement> childs = new ArrayList<>();
		
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
		ArrayList<CCActionElement> childs = getAllChildren();
		
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			for (int j = 0; j < childs.size(); j++) {
				if (i != j && childs.get(i).getName().equals(childs.get(j).getName())) {
					System.out.println(String.format("[DBG] Duplicate Item (%s) in ActionTree found", childs.get(j).getCaptionIdent())); //$NON-NLS-1$
				}
			}
		}
	}
	
	public KeyStroke getKeyStroke() {
		return keyStroke;
	}
	
	private void implementKeyListener(JComponent comp, final CCActionElement e) {
		if (keyStroke == null) {
			return;
		}
		
		InputMap map = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap act = comp.getActionMap();
		
		map.put(keyStroke, name);
		act.put(name, new AbstractAction() {
			private static final long serialVersionUID = 19873468234L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				e.onKeyPressed(keyStroke);
			}
		});
	}
	
	private void onKeyPressed(KeyStroke stroke) {
		ArrayList<CCActionElement> childs = getAllChildren();
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i).getKeyStroke() != null && childs.get(i).getKeyStroke().equals(stroke)) {
				childs.get(i).execute();
			}
		}
	}
	
	public void implementAllKeyListener(JComponent comp) {
		ArrayList<CCActionElement> childs = getAllChildren();
		
		implementKeyListener(comp, this);
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).implementKeyListener(comp, this);
		}
	}
}
