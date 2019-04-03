package de.jClipCorn.gui.mainFrame.clipCharSelector;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import de.jClipCorn.gui.mainFrame.MainFrame;

/**
 * For Windows/Metal Themes
 */
public class FullClipCharSortSelector extends AbstractClipCharSortSelector {
	private static final long serialVersionUID = -8270219279263812975L;
	
	@SuppressWarnings("nls")
	private static String  captions[] = {"All",  "#",  "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};

	@SuppressWarnings("nls")
	private static String  searches[] = {"",  "0123456789", "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	private static int BUTTONCOUNT = captions.length;

	private GridLayout layout;
	
	private JButton buttons[] = new JButton[BUTTONCOUNT];
	
	public FullClipCharSortSelector(MainFrame mf) {
		super(mf);
		
		createButtons();
		createLayout();
	}
	
	private void createButtons() {
		for(int i = 0; i < BUTTONCOUNT; i++) {
			buttons[i] = new JButton(captions[i]);
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].setBorderPainted(false);
		}
	}
	
	private void createLayout() {
		this.setLayout(layout = new GridLayout(0, captions.length));

		layout.setHgap(0);
		layout.setVgap(0);
		
		setFloatable(false);

		for(int i = 0; i < BUTTONCOUNT; i++) {
			add(buttons[i]);

			final int fi = i;
			buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onClick(fi);
				}
			});
		}
		
	}
	
	private void onClick(int idx) {
		if (idx == 0) {
			onClick(null);
		} else {
			onClick(searches[idx]);
		}
	}
}
