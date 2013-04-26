package de.jClipCorn.gui.frames.editToolbarFrame.toolbarElementsList;

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

import de.jClipCorn.gui.actionTree.CCActionElement;
import de.jClipCorn.gui.actionTree.CCActionTree;
import de.jClipCorn.gui.frames.mainFrame.clipToolbar.ClipToolbar;

public class ToolbarElementsCellRenderer extends JLabel implements ListCellRenderer<String> {
	private static final long serialVersionUID = -4383588005689445693L;

	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private static final Border SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	public ToolbarElementsCellRenderer() {
		super();
		setOpaque(true);
		setBorder(getNoFocusBorder());
	}

	private static Border getNoFocusBorder() {
		if (System.getSecurityManager() != null) {
			return SAFE_NO_FOCUS_BORDER;
		} else {
			return noFocusBorder;
		}
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		if (value.equals(ClipToolbar.IDENT_SEPERATOR)) {
			JPanel p = new JPanel(new BorderLayout());
			p.setBorder(new EmptyBorder(0, 10, 0, 10));
			
			JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
			sep.setPreferredSize(new Dimension(0, 2));
			p.add(sep, BorderLayout.CENTER);
			
			if (isSelected) {
				p.setBackground(list.getSelectionBackground());
			} else {
				p.setBackground(list.getBackground());
			}
			
			p.setBorder(new EmptyBorder(7, 0, 7, 0));
			
			return p;
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
