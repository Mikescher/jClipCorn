package de.jClipCorn.gui.mainFrame.charSelector;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.mainFrame.MainFrame;

import java.awt.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * For Windows/Metal Themes
 */
@SuppressWarnings("nls")
public class ClipCharSortSelectorFull extends AbstractClipCharSortSelector {
	private static final String[] captions = {"All",  "#",  "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private static final String[] searches = {"",  "0123456789", "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};

	private static final int BUTTONCOUNT = captions.length;

	private final ClipCharButton[] buttons = new ClipCharButton[BUTTONCOUNT];

	@DesignCreate
	private static ClipCharSortSelectorFull designCreate() { return new ClipCharSortSelectorFull(null); }

	public ClipCharSortSelectorFull(MainFrame mf) {
		super(mf);

		createButtonsFull();
		createLayoutFull();
	}

	private void createButtonsFull() {
		for(int i = 0; i < BUTTONCOUNT; i++) {
			buttons[i] = new ClipCharButton(captions[i]);
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			if (LookAndFeelManager.isMetal()) buttons[i].setBorderPainted(false);
		}
	}
	
	private void createLayoutFull() {
		var layout = new GridLayout(0, captions.length);
		this.setLayout(layout);

		layout.setHgap(0);
		layout.setVgap(0);
		
		setFloatable(false);

		for(int i = 0; i < BUTTONCOUNT; i++) {
			add(buttons[i]);

			final int fi = i;
			buttons[i].addActionListener(e -> onClickFull(fi));
		}
		
	}
	
	private void onClickFull(int idx) {
		if (idx == 0) {
			onClick(null);
		} else {
			onClick(searches[idx]);
		}
	}

	@Override
	protected void updateButtonInactive(Set<Character> chars) {

		chars = chars.stream().map(Character::toUpperCase).collect(Collectors.toSet());

		for (int i = 0; i < BUTTONCOUNT; i++) {

			if (searches[i].isEmpty()) {
				buttons[i].setInactive(false);
				continue;
			}

			var match = false;
			for (int cidx = 0; cidx < searches[i].length(); cidx++) {
				if (chars.contains(searches[i].charAt(cidx))) {
					match = true;
					break;
				}
			}
			buttons[i].setInactive(!match);

		}
	}
}
