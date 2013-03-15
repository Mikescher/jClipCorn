package de.jClipCorn.gui.frames.mainFrame.searchField;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.localization.LocaleBundle;

public class SearchField extends JTextField implements FocusListener, DocumentListener {
	private static final long serialVersionUID = -93240990085507647L;

	private final static String EMPTY_VAL = LocaleBundle.getString("MainFrame.searchTerm_standard"); //$NON-NLS-1$

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
			setForeground(UIManager.getColor("TextField.foreground")); //$NON-NLS-1$
			setHorizontalAlignment(LEFT);
		}
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		if (getText().isEmpty()) {
			reset();
		}
	}
	
	private Color getInactiveColor() {
		Color c = UIManager.getColor("TextField.foreground"); //$NON-NLS-1$
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		
		Color c2 = UIManager.getColor("TextPane.background"); //$NON-NLS-1$
		int r2 = c2.getRed();
		int g2 = c2.getGreen();
		int b2 = c2.getBlue();
		
		return new Color((r+r2)/2, (g+g2)/2, (b+b2)/2);
	}

	public void reset() {
		if (!hasFocus()) {
			setText(EMPTY_VAL);
			
			setForeground(getInactiveColor());
			setHorizontalAlignment(CENTER);
		}
	}

	private void onChange() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!getText().equals(EMPTY_VAL)) {
					owner.startSearch();
					setForeground(UIManager.getColor("TextField.foreground")); //$NON-NLS-1$
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
