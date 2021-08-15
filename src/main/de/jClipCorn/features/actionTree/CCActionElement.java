package de.jClipCorn.features.actionTree;

import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.toolbar.ClipToolbar;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.property.CCCaptionedKeyStrokeProperty;
import de.jClipCorn.properties.property.CCKeyStrokeProperty;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.helper.KeyStrokeUtil;
import de.jClipCorn.util.listener.ActionCallbackListener;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CCActionElement {
	private final String name;
	private final String captionIdent;
	private final MultiSizeIconRef iconRes;
	private final boolean visible;
	private final KeyStroke defaultKeyStroke;

	private final UIActionTree tree;

	private boolean isProhibitedInReadOnly = false;
	
	private CCKeyStrokeProperty keyStrokeProperty = null;
	
	private final List<CCActionTreeListener> listener;
	private final List<CCActionElement> children;
	
	private CCActionElement parent = null;
	
	public CCActionElement(UIActionTree tree, String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes) {
		this.tree = tree;
		this.name = name;
		this.captionIdent = caption;
		this.iconRes = iconRes;
		this.listener = new ArrayList<>();
		this.children = new ArrayList<>();
		this.visible = true;
		this.defaultKeyStroke = stroke;
	}
	
	public CCActionElement(UIActionTree tree, String name, KeyStroke stroke, String caption, MultiSizeIconRef iconRes, boolean vis) {
		this.tree = tree;
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
		
		return iconRes.get16x16();
	}
	
	public ImageIcon getIcon() {
		if (iconRes == null) {
			return null;
		}

		return iconRes.get32x32();
	}

	public MultiSizeIconRef getIconRef() {
		return iconRes;
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

	public void execute(Component swingsrc, ActionSource src, IActionSourceObject obj, ActionCallbackListener ltnr) {
		execute(swingsrc, src, Collections.singletonList(obj), ltnr);
	}

	public void execute(Component swingsrc, ActionSource src, List<IActionSourceObject> obj, ActionCallbackListener ltnr) {
		if (isProhibitedInReadOnly && tree.isDatabaseReadonly()) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		
		for (CCActionTreeListener al : listener) {
			al.onTreeAction(new CCTreeActionEvent(swingsrc, this, src, obj, ltnr));
		}
	}
	
	public Iterator<CCActionElement> getChildren() {
		return children.iterator();
	}
	
	public List<CCActionElement> getAllChildren() {

		List<CCActionElement> childs = new ArrayList<>(children);

		for (CCActionElement child : children) childs.addAll(child.getAllChildren());
		
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
		build.append(Str.repeat("    ", Math.max(0, deep))); //$NON-NLS-1$
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

	public void implementDirectKeyListener(JFrame frame, IActionRootFrame f, JComponent comp) {
		final KeyStroke stroke = getKeyStroke();

		if (KeyStrokeUtil.isEmpty(stroke)) {
			return;
		}

		InputMap map = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = comp.getActionMap();

		map.put(stroke, name);
		act.put(name, new AbstractAction()
		{
			private static final long serialVersionUID = 19873468235L;

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (tree.getMovieList().isBlocked()) return;

				if (stroke.equals(CCActionElement.this.getKeyStroke())) {
					var srcobj = f.getSelectedActionSource();
					if (srcobj == null) return;
					CCActionElement.this.execute(frame, ActionSource.SHORTCUT, Collections.singletonList(srcobj), null);
				}
			}
		});
	}
	
	private void implementKeyListener(MainFrame frame, JComponent comp, final CCActionElement root) {
		final KeyStroke stroke = getKeyStroke();
		
		if (KeyStrokeUtil.isEmpty(stroke)) return;
		
		InputMap map = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		ActionMap act = comp.getActionMap();
		
		map.put(stroke, name);
		act.put(name, new AbstractAction()
		{
			private static final long serialVersionUID = 19873468234L;

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				if (tree.getMovieList().isBlocked()) return;

				List<CCActionElement> childs = root.getAllChildren();
				childs.add(root);

				for (CCActionElement child : childs) {
					if (stroke.equals(child.getKeyStroke())) {
						child.execute(frame, ActionSource.SHORTCUT, Collections.singletonList(frame.getSelectedElement()), null);
					}
				}
			}
		});
	}
	
	private void onKeyPressed(Component comp, IActionRootFrame f, KeyStroke stroke) {

		// method is always called on [ROOT] node
		// [ROOT] node has events for all shortcut of all it's children

		if (tree.getMovieList().isBlocked()) return;
		
		List<CCActionElement> childs = getAllChildren();
		childs.add(this);
		
		for (int i = 0; i < childs.size(); i++) {
			if (stroke.equals(childs.get(i).getKeyStroke())) {
				childs.get(i).execute(comp, ActionSource.SHORTCUT, Collections.singletonList(f.getSelectedActionSource()), null);
			}
		}
	}
	
	public void implementAllKeyListener(MainFrame f, JComponent comp) {
		List<CCActionElement> childs = getAllChildren();
		
		implementKeyListener(f, comp, this);
		
		for (int i = 0; i < childs.size(); i++) {
			childs.get(i).implementKeyListener(f, comp, this);
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
		
		CCLog.addDebug(String.format("%d Keystroke properties in ActionTree intialized", childs.size())); //$NON-NLS-1$
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

	public boolean isReadOnlyRestricted() {
		return isProhibitedInReadOnly;
	}
}
