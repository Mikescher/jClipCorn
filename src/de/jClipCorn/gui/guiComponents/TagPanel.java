package de.jClipCorn.gui.guiComponents;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;

public class TagPanel extends JPanel {
	private static final long serialVersionUID = -6093081428307402687L;

	private CCMovieTags value = new CCMovieTags();
	private boolean readOnly = false;

	public TagPanel() {
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

		for (int i = 0; i < CCMovieTags.ACTIVETAGS; i++) {
			JLabel l = new JLabel(value.getTagIcon(i));

			final int pos = i;
			l.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent arg0) {
					// empty
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					onClicked(pos);
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// empty
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// empty
				}

				@Override
				public void mouseClicked(MouseEvent arg0) {
					// empty
				}
			});

			add(l);
		}

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}

	}

	private void onClicked(int c) {
		if (!readOnly) {
			value.switchTag(c);
			update();
		}
	}

	public CCMovieTags getValue() {
		return value;
	}

	public void setValue(CCMovieTags v) {
		value = v.copy();
		update();
	}

	public void setReadOnly(boolean ro) {
		readOnly = ro;
	}
}
