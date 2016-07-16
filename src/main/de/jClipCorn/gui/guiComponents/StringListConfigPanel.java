package de.jClipCorn.gui.guiComponents;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import de.jClipCorn.gui.localization.LocaleBundle;

public class StringListConfigPanel extends JPanel {
	private static final long serialVersionUID = 4776936200847223329L;

	private ArrayList<String> value = new ArrayList<>();
	private JLabel display;
	
	public StringListConfigPanel() {
		super();
		init();
		update();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBackground(UIManager.getColor("TextField.background")); //$NON-NLS-1$
		setBorder(UIManager.getBorder("TextField.border")); //$NON-NLS-1$
		
		display = new JLabel();
		display.setBorder(new EmptyBorder(2, 2, 2, 2));
		
		add(display, BorderLayout.CENTER);
	}
	
	private void update() {
		display.setText(LocaleBundle.getFormattedString("Settingsframe.other.StringListPropertyDisplay", value.size())); //$NON-NLS-1$
	}
	
	public ArrayList<String> getValue() {
		return value;
	}
	
	public void setValue(ArrayList<String> v) {
		value = v;
		update();
	}
}
