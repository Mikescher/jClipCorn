package de.jClipCorn.features.actionTree;

import java.awt.event.ActionEvent;
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

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.MultiIconRef;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCCaptionedKeyStrokeProperty;
import de.jClipCorn.properties.property.CCKeyStrokeProperty;
import de.jClipCorn.util.helper.KeyStrokeUtil;

public class CCActionElement {
	private final String name;
	private final String captionIdent;
	private final MultiIconRef iconRes;
	private final boolean visible;
	private final KeyStroke defaultKeyStroke;
	
	private boolean isProhibitedInReadOnly = false;
	
	private CCKeyStrokeProperty keyStrokeProperty = null;
	
	private final List<CCActionTreeListener> listener;
	private final List<CCActionElement> children;
	
	private CCActionElement parent = null;
	
	public CCActionElement(String name, KeyStroke stroke, String caption, MultiIconRef iconRes) {
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = true;
		this.defaultKeyStroke = stroke;
	}
	
	public CCActionElement(String name, KeyStroke stroke, String caption, MultiIconRef iconRes, boolean vis) {
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
	
	public boolean hasCaption() {
		return ! captionIdent.isEmpty();
	}
	
	public String getCaptionIdent() {
		return captionIdent;
	}
	
	public ImageIcon getSmallIcon() {
		if (iconRes == null) {
			return null;
		}
		
		return CachedResourceLoader.getIcon(iconRes.icon16x16);
	}
	
	public ImageIcon getIcon() {
		if (iconRes == null) {
			return null;
		}

		return CachedResourceLoader.getIcon(iconRes.icon32x32);
	}
	
	public void addListener(CCActionTreeListener al) {
		listener.add(al);
	}
	
	public boolean removeListener(CCActionTreeListener al) {
		return listener.remove(al);
	}
	
	public void clearActionListeners() {
		listener.clear();
	}
	
	public void execute(ActionSource src) {
		execute("", src); //$NON-NLS-1$
	}
	
	public void execute(String cmd, ActionSource src) {
		if (isProhibitedInReadOnly && CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		
		for (CCActionTreeListener al : listener) {
			al.onTreeAction(new CCTreeActionEvent(this, cmd, src));
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
		cae.setParent(this);
		return cae;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public CCActionElement getParent() {
		return parent;
	}
	
	public void setParent(CCActionElement p) {
		this.parent = p;
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
					CCLog.addDebug(String.format("Duplicate Item (%s) in ActionTree found", childs.get(j).getCaptionIdent())); //$NON-NLS-1$
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
		CCLog.addDebug(build.toString());
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
		if (CCMovieList.isBlocked()) {
			return;
		}
		
		List<CCActionElement> childs = getAllChildren();
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			if (stroke.equals(childs.get(i).getKeyStroke())) {
				childs.get(i).execute(ActionSource.SHORTCUT);
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
		if (!hasChildren()) {
			KeyStroke stroke = (defaultKeyStroke == null) ? KeyStrokeUtil.getEmptyKeyStroke() : defaultKeyStroke;
			
			keyStrokeProperty = new CCCaptionedKeyStrokeProperty(CCProperties.CAT_KEYSTROKES, props, "PROP_KEYSTROKE_" + name.toUpperCase(), getFullCaption(), stroke); //$NON-NLS-1$
		}
	}
	
	public String getFullCaption() {
		StringBuilder builder = new StringBuilder();
		
		CCActionElement curr = this;
		
		while(curr != null && curr.hasCaption()) {
			builder.insert(0, curr.getCaption() + ": "); //$NON-NLS-1$
			
			curr = curr.getParent();
		}
		
		builder.delete(builder.length() - 2, builder.length());
		
		return builder.toString();
	}
	
	public void createAllProperties(CCProperties props) {
		List<CCActionElement> childs = new ArrayList<>();
		
		for (int i = 0; i < children.size(); i++) {
			childs.addAll(children.get(i).getAllChildren());
		}
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).createProperty(props);
		}
		
		CCLog.addDebug(String.format("%d Keystrokeproperties in ActionTree intialized", childs.size())); //$NON-NLS-1$
	}
	
	public String getRootToolbarConfig() {
		StringBuilder b = new StringBuilder();
		
		for (int i = 0; i < children.size(); i++) {
			b.append(children.get(i).getToolbarConfig());
			
			if (i+1 < children.size()) {
				b.append('|');
				b.append(ClipToolbar.IDENT_SEPERATOR);
				b.append('|');
			}
		}
		
		return b.toString();
	}
	
	public String getToolbarConfig() {
		if (hasChildren()) {
			StringBuilder b = new StringBuilder();
			
			for (int i = 0; i < children.size(); i++) {
				b.append(children.get(i).getToolbarConfig());
				
				if (i+1 < children.size()) {
					b.append('|');
				}
			}
			
			return b.toString();
		} else {
			return getName();
		}
	}

	public void setReadOnlyRestriction() {
		isProhibitedInReadOnly = true;
	}
}