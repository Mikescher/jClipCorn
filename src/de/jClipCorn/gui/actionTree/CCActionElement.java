package de.jClipCorn.gui.actionTree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CCActionElement {
	private final String name;
	private final String captionIdent;
	private final String iconRes;
	
	private boolean visible = true;
	
	private final ArrayList<ActionListener> listener;
	private final ArrayList<CCActionElement> children;
	
	public CCActionElement(String name, String caption, String iconRes) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
	}
	
	public CCActionElement(String name, String caption, String iconRes, boolean vis) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = vis;
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

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
