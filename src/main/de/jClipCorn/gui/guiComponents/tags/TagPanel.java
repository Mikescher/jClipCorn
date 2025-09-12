package de.jClipCorn.gui.guiComponents.tags;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TagPanel extends JPanel {
	private static final long serialVersionUID = -6093081428307402687L;

	private final boolean designmode;

	private CCTagList value = CCTagList.EMPTY;
	private boolean readOnly = false;

	@DesignCreate
	private static TagPanel designCreate()
	{
		return new TagPanel(true);
	}

	public TagPanel() {
		this(false);
	}

	public TagPanel(boolean designmode) {
		super();
		this.designmode = designmode;
		init();
		update();
	}

	private void init() {
		setLayout(new GridBagLayout());
		setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		setBorder(UIManager.getBorder("TextField.border")); //$NON-NLS-1$
	}

	public void addTagsChangedListener(final TagsChangedListener l) {
		listenerList.add(TagsChangedListener.class, l);
	}
	public void removeTagsChangedListener(final TagsChangedListener l) {
		listenerList.add(TagsChangedListener.class, l);
	}

	@Override
	public Dimension getPreferredSize() {
		if (designmode || LookAndFeelManager.isFlatLaf()) {
			return new Dimension(80, 30);
		}
		return new Dimension(80, 20);
	}

	private void update() {
		removeAll();

		for (int i = 0; i < CCSingleTag.count(); i++) {

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 0;
			gbc.gridx = i;
			gbc.anchor = GridBagConstraints.WEST;
			gbc.insets = new Insets(0, 5, 0, 1);
			gbc.fill = GridBagConstraints.NONE;
			gbc.weightx = 0;
			gbc.weighty = 1.0;

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

			add(l, gbc);
		}

		GridBagConstraints filler = new GridBagConstraints();
		filler.gridx = CCSingleTag.count();
		filler.gridy = 0;
		filler.weightx = 1.0;                 // take up remaining horizontal space
		filler.weighty = 1.0;
		filler.fill = GridBagConstraints.BOTH;
		add(Box.createGlue(), filler);

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
