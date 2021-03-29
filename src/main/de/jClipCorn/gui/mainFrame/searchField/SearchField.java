package de.jClipCorn.gui.mainFrame.searchField;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.helper.SwingUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class SearchField extends JTextField implements FocusListener, DocumentListener {
	private static final long serialVersionUID = -93240990085507647L;

	private final static String EMPTY_VAL = LocaleBundle.getString("MainFrame.searchTerm_standard"); //$NON-NLS-1$

	private final MainFrame owner;

	@DesignCreate
	private static SearchField designCreate() { return new SearchField(null); }

	public SearchField(MainFrame owner) {
		super();
		this.owner = owner;
		reset(false);

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
			reset(false);
		}
	}
	
	private Color getInactiveColor() {
		Color c1 = UIManager.getColor("TextField.foreground"); //$NON-NLS-1$
		int r1 = c1.getRed();
		int g1 = c1.getGreen();
		int b1 = c1.getBlue();
		
		Color c2 = UIManager.getColor("TextPane.background"); //$NON-NLS-1$
		int r2 = c2.getRed();
		int g2 = c2.getGreen();
		int b2 = c2.getBlue();
		
		return new Color((r1 + r2) / 2, (g1 + g2) / 2, (b1 + b2) / 2);
	}

	public void reset(boolean force) {
		if (!hasFocus() || force) {
			setText(EMPTY_VAL);
			
			setForeground(getInactiveColor());
			setHorizontalAlignment(CENTER);
		}
	}

	private void onChange() {
		SwingUtils.invokeLater(new Runnable() {
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
