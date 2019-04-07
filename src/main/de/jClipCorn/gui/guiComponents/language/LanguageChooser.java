package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.util.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LanguageChooser extends JPanel {
	private static final long serialVersionUID = 2017286148720080712L;
	
	private CCDBLanguageList value = CCDBLanguageList.EMPTY;
	private boolean readOnly = false;
	private ActionListener action = null;

	private JPanel pnlIcons;

	public LanguageChooser() {
		super();
		init();
		update();
	}

	public LanguageChooser(CCDBLanguageList v) {
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
		JButton btnEdit = new JButton("..."); //$NON-NLS-1$
		btnEdit.setMargin(new Insets(2, 4, 2, 4));
		add(btnEdit, BorderLayout.EAST);
		btnEdit.addActionListener(e -> onClicked());
		btnEdit.setFocusable(false);
	}

	private void update() {
		pnlIcons.removeAll();

		for (CCDBLanguage ilang : value.iterate().autosort()) {
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
		new LanguageChooserDialog(this, this::setValue, getValue()).setVisible(true);
	}

	private void onClicked(CCDBLanguage lang) {
		if (readOnly) return;
		if (!isEnabled()) return;
		if (value.isEmpty()) return;
		if (value.isSingle())
		{
			setValue(new CCDBLanguageList(value.iterate().firstOrNull().nextLanguage()));
			return;
		}
		setValue(value.getRemove(lang));
	}

	public CCDBLanguageList getValue() {
		return value;
	}

	public void setValue(CCDBLanguageList v) {
		value = v;
		update();
		if (action != null) action.actionPerformed(new ActionEvent(this, 0, Str.Empty));
	}

	public void setReadOnly(boolean ro) {
		readOnly = ro;
	}

	public void addActionListener(ActionListener a) {
		action = a;
	}
}
