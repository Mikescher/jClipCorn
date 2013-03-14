package de.jClipCorn.gui.frames.mainFrame.searchField;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;

public class SearchField extends JTextField implements FocusListener, DocumentListener {
	private static final long serialVersionUID = -93240990085507647L;

	private final static String EMPTY_VAL = LocaleBundle.getString("MainFrame.searchTerm_standard"); //$NON-NLS-1$
	private final static Color RESET_COLOR = Color.GRAY;

	private final MainFrame owner;

	public SearchField(MainFrame owner) {
		super();
		this.owner = owner;
		reset();

		addFocusListener(this);
		getDocument().addDocumentListener(this);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		if (getText().equals(EMPTY_VAL)) {
			setText(""); //$NON-NLS-1$
			setForeground(Color.BLACK);
			setHorizontalAlignment(LEFT);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if (getText().isEmpty()) {
			reset();
		}
	}

	public void reset() {
		if (!hasFocus()) {
			setText(EMPTY_VAL);
			setForeground(RESET_COLOR);
			setHorizontalAlignment(CENTER);
		}
	}

	private void onChange() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!getText().equals(EMPTY_VAL)) {
					owner.startSearch();
					setForeground(Color.BLACK);
					setHorizontalAlignment(LEFT);
				}
			}
		});
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		onChange();
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		onChange();
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		onChange();
	}

	public String getRealText() {
		return getText().equals(EMPTY_VAL) ? "" : getText(); //$NON-NLS-1$
	}
}
