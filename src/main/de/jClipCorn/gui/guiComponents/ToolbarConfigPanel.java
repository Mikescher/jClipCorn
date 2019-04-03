package de.jClipCorn.gui.guiComponents;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.UIManager;

import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;
import de.jClipCorn.properties.property.CCToolbarProperty;

public class ToolbarConfigPanel extends JPanel {
	private static final long serialVersionUID = 4776936100847223329L;

	private String value = ""; //$NON-NLS-1$
	
	public ToolbarConfigPanel() {
		super();
		init();
		update();
	}
	
	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
		setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		setBorder(UIManager.getBorder("TextField.border")); //$NON-NLS-1$
	}
	
	private void update() {
		removeAll();
		List<String> list = CCToolbarProperty.splitStringList(value);
		
		for (int i = 0; i < list.size(); i++) {
			String elem = list.get(i);
			
			if (elem.equals(ClipToolbar.IDENT_SEPERATOR)) {
				JSeparator sep = new JSeparator(JSeparator.VERTICAL);
				sep.setPreferredSize(new Dimension(3, 16));
				add(sep);
			} else {
				CCActionElement aelem = CCActionTree.getInstance().find(elem);
				if (aelem != null) {
					add(new JLabel(aelem.getSmallIcon()));
				}
			}
		}
		
		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}
		
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String v) {
		value = v;
		update();
	}
}
