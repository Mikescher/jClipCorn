package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LanguageSetChooser extends JPanel {
	private static final long serialVersionUID = 2017286148720080712L;
	
	private CCDBLanguageSet value = CCDBLanguageSet.EMPTY;
	private boolean readOnly = false;

	private JPanel pnlIcons;
	private JButton btnEdit;

	public LanguageSetChooser() {
		super();
		init();
		update();
	}

	public LanguageSetChooser(CCDBLanguageSet v) {
		super();
		value = v;
		init();
		update();
	}

	private void init() {
		setLayout(new BorderLayout());
		setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		setBorder(UIManager.getBorder("TextField.border")); //$NON-NLS-1$

		add(pnlIcons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2)), BorderLayout.CENTER);
		pnlIcons.setBackground(new Color(0, 0, 0, 0));
		btnEdit = new JButton("..."); //$NON-NLS-1$
		btnEdit.setMargin(new Insets(2, 4, 2, 4));
		add(btnEdit, BorderLayout.EAST);
		btnEdit.addActionListener(e -> onClicked());
		btnEdit.setFocusable(false);
	}

	private void update() {
		pnlIcons.removeAll();

		for (CCDBLanguage ilang : value.ccstream().autosort()) {
			final CCDBLanguage lng = ilang;
			JLabel l = new JLabel(lng.getIcon());
			l.setToolTipText(lng.asString());
			pnlIcons.add(l);
			l.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					onClicked(lng);
				}
			});
		}

		pnlIcons.validate();

		Component parent = getParent();
		if (parent != null) {
			validate();
			getParent().revalidate();
		}

		pnlIcons.repaint();
		repaint();
	}

	private void onClicked() {
		if (readOnly) return;
		if (!isEnabled()) return;
		new LanguageSetChooserDialog(this, this::setValue, getValue()).setVisible(true);
	}

	private void onClicked(CCDBLanguage lang) {
		if (readOnly) return;
		if (!isEnabled()) return;
		if (value.isEmpty()) return;
		if (value.isSingle())
		{
			setValue(CCDBLanguageSet.single(value.ccstream().firstOrNull().nextLanguage()));
			return;
		}
		setValue(value.getRemove(lang));
	}

	public CCDBLanguageSet getValue() {
		return value;
	}

	public void setValue(CCDBLanguageSet v) {
		value = v;
		update();
		for (var l: listenerList.getListeners(LanguageChangedListener.class)) l.languageChanged(new ActionEvent(this, 0, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		readOnly = ro;
		btnEdit.setEnabled(!ro);
	}

	public void addLanguageChangedListener(final LanguageChangedListener l) {
		listenerList.add(LanguageChangedListener.class, l);
	}
	public void removeLanguageChangedListener(final LanguageChangedListener l) {
		listenerList.add(LanguageChangedListener.class, l);
	}
}
