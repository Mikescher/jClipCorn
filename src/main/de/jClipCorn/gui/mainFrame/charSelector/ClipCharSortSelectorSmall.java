package de.jClipCorn.gui.mainFrame.charSelector;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.mainFrame.MainFrame;

import javax.swing.*;
import java.util.Set;

/**
 * For Windows/Metal Themes
 */
@SuppressWarnings("nls")
public class ClipCharSortSelectorSmall extends AbstractClipCharSortSelector {
	private static final String[] captions = {"All",  "#",  "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};
	private static final int[] sizes = {32,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  42};
	private static final String[] searches = {"",  "0123456789", "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};

	private static final int BUTTONCOUNT = captions.length;

	private final ClipCharButton[] buttons = new ClipCharButton[BUTTONCOUNT];

	@DesignCreate
	private static ClipCharSortSelectorSmall designCreate() { return new ClipCharSortSelectorSmall(null); }

	public ClipCharSortSelectorSmall(MainFrame mf) {
		super(mf);

		createButtons();
		createLayout();
	}

	private void createButtons() {
		for(int i = 0; i < BUTTONCOUNT; i++) {
			buttons[i] = new ClipCharButton(captions[i]);
			if (LookAndFeelManager.isMetal()) buttons[i].setBorderPainted(false);
		}
	}

	private void createLayout() {
		var layout = new GroupLayout(this);
		this.setLayout(layout);

		setFloatable(false);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.ParallelGroup pGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		boolean isExtLnF = LookAndFeelManager.isExternal();
		boolean isRad = LookAndFeelManager.isRadiance();
		boolean isFlaf = LookAndFeelManager.isFlatLaf();

		for(int i = 0; i < BUTTONCOUNT; i++) {
			if (isRad) {
				hGroup.addComponent(buttons[i], sizes[i], buttons[i].getPreferredSize().width, Short.MAX_VALUE);
				pGroup.addComponent(buttons[i], 0, buttons[i].getPreferredSize().height, Short.MAX_VALUE);
			} else if (isFlaf) {
				hGroup.addComponent(buttons[i]);
				pGroup.addComponent(buttons[i]);
			} else {
				hGroup.addComponent(buttons[i]);
				pGroup.addComponent(buttons[i]);
			}

			hGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE);
			final int fi = i;
			buttons[i].addActionListener(arg0 -> onClick(fi));
		}

		if (isExtLnF) {
			hGroup.addGap(4);
		}

		layout.setHorizontalGroup(hGroup);

		layout.setVerticalGroup(pGroup);
	}

	private void onClick(int idx) {
		if (idx == 0) {
			onClick(null);
		} else {
			onClick(searches[idx]);
		}
	}

	@Override
	protected void updateButtonInactive(Set<Character> chars) {
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
