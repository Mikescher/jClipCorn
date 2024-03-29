package de.jClipCorn.gui.guiComponents.tags;

import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TagPanel extends JPanel {
	private static final long serialVersionUID = -6093081428307402687L;

	private CCTagList value = CCTagList.EMPTY;
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

	public void addTagsChangedListener(final TagsChangedListener l) {
		listenerList.add(TagsChangedListener.class, l);
	}
	public void removeTagsChangedListener(final TagsChangedListener l) {
		listenerList.add(TagsChangedListener.class, l);
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
			for (var a : listenerList.getListeners(TagsChangedListener.class)) a.tagsChanged(new ActionEvent(value, -1, Str.Empty));
		}
	}

	public CCTagList getValue() {
		return value;
	}

	public void setValue(CCTagList v) {
		value = v;
		update();
		for (var a : listenerList.getListeners(TagsChangedListener.class)) a.tagsChanged(new ActionEvent(value, -1, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		readOnly = ro;
	}
}
