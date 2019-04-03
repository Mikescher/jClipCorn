package de.jClipCorn.gui.frames.editToolbarFrame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.features.actionTree.CCActionElement;
import de.jClipCorn.features.actionTree.CCActionTree;
import de.jClipCorn.gui.mainFrame.clipToolbar.ClipToolbar;

public class ToolbarElementsCellRenderer extends JLabel implements ListCellRenderer<String> {
	private static final long serialVersionUID = -4383588005689445693L;

	private final static Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private final JPanel SEPERATOR_PANEL = new JPanel(new BorderLayout());
	
	public ToolbarElementsCellRenderer() {
		super();
		setOpaque(true);
		setBorder(getNoFocusBorder());
		
		init();
	}
	
	private void init() {
		SEPERATOR_PANEL.setBorder(new EmptyBorder(7, 0, 7, 0));
		JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
		sep.setPreferredSize(new Dimension(0, 2));
		SEPERATOR_PANEL.add(sep, BorderLayout.CENTER);
	}

	private static Border getNoFocusBorder() {
		return NO_FOCUS_BORDER;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value.equals(ClipToolbar.IDENT_SEPERATOR)) {	
			if (isSelected) {
				SEPERATOR_PANEL.setBackground(list.getSelectionBackground());
			} else {
				SEPERATOR_PANEL.setBackground(list.getBackground());
			}
			
			return SEPERATOR_PANEL;
		}

		CCActionElement element = CCActionTree.getInstance().find(value);

		if (element == null) {
			return null;
		}
		
		setComponentOrientation(list.getComponentOrientation());

		setText(element.getCaption());
		setIcon(element.getSmallIcon());

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());

		Border border = null;
		if (cellHasFocus) {
			if (isSelected) {
				border = UIManager.getBorder("List.focusSelectedCellHighlightBorder"); //$NON-NLS-1$
			}
			if (border == null) {
				border = UIManager.getBorder("List.focusCellHighlightBorder"); //$NON-NLS-1$
			}
		} else {
			border = getNoFocusBorder();
		}
		setBorder(border);

		return this;
	}
}
