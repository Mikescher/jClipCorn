package de.jClipCorn.gui.guiComponents;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.util.Str;

public class TagPanel extends JPanel {
	private static final long serialVersionUID = -6093081428307402687L;

	private CCTagList value = CCTagList.EMPTY;
	private boolean readOnly = false;

	private List<ActionListener> _changeListener = new ArrayList<>();

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

	public void addChangeListener(ActionListener a) {
		_changeListener.add(a);
	}

	private void update() {
		removeAll();

		for (int i = 0; i < CCTagList.ACTIVETAGS; i++) {
			JLabel l = new JLabel(value.getTagIcon(i));
			l.setToolTipText(CCTagList.getName(i));

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
			value = value.getSwitchTag(c);
			update();
		}
	}

	public CCTagList getValue() {
		return value;
	}

	public void setValue(CCTagList v) {
		value = v;
		update();
		for (ActionListener a : _changeListener) a.actionPerformed(new ActionEvent(v, -1, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		readOnly = ro;
	}
}
