package de.jClipCorn.gui.mainFrame.charSelector;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.mainFrame.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * For Windows/Metal Themes
 */
@SuppressWarnings("nls")
public class ClipCharSortSelector extends AbstractClipCharSortSelector {
	private static final long serialVersionUID = -8270219279263812975L;

	private static final String[]  captions_full = {"All",  "#",  "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private static final String[]  searches_full = {"",  "0123456789", "A", "B", "C", "D", "E", "F",  "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",  "S", "T", "U", "V", "W", "X", "Y", "Z"};

	private static final int BUTTONCOUNT_FULL = captions_full.length;

	private final JButton[] buttons_full = new JButton[BUTTONCOUNT_FULL];

	private static final String[]  captions_small = {"All",  "#",  "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};
	private static final int[]     sizes_small    = {32,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  42};
	private static final String[]  searches_small = {"",  "0123456789", "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};

	private static final int BUTTONCOUNT_SMALL = captions_small.length;

	private final JButton[] buttons_small = new JButton[BUTTONCOUNT_SMALL];

	@DesignCreate
	private static ClipCharSortSelector designCreate() { return new ClipCharSortSelector(null); }

	public ClipCharSortSelector(MainFrame mf) {
		super(mf);

		var isFull = !LookAndFeelManager.isRadiance();

		if (isFull) {
			createButtonsFull();
			createLayoutFull();
		} else {
			createButtonsSmall();
			createLayoutSmall();
		}
	}
	
	private void createButtonsFull() {
		for(int i = 0; i < BUTTONCOUNT_FULL; i++) {
			buttons_full[i] = new JButton(captions_full[i]);
			buttons_full[i].setMargin(new Insets(0, 0, 0, 0));
			if (LookAndFeelManager.isMetal()) buttons_full[i].setBorderPainted(false);
		}
	}
	
	private void createLayoutFull() {
		var layout = new GridLayout(0, captions_full.length);
		this.setLayout(layout);

		layout.setHgap(0);
		layout.setVgap(0);
		
		setFloatable(false);

		for(int i = 0; i < BUTTONCOUNT_FULL; i++) {
			add(buttons_full[i]);

			final int fi = i;
			buttons_full[i].addActionListener(e -> onClickFull(fi));
		}
		
	}
	
	private void onClickFull(int idx) {
		if (idx == 0) {
			onClick(null);
		} else {
			onClick(searches_full[idx]);
		}
	}

	private void createButtonsSmall() {
		for(int i = 0; i < BUTTONCOUNT_SMALL; i++) {
			buttons_small[i] = new JButton(captions_small[i]);
			if (LookAndFeelManager.isMetal()) buttons_full[i].setBorderPainted(false);
		}
	}

	private void createLayoutSmall() {
		var layout = new GroupLayout(this);
		this.setLayout(layout);

		setFloatable(false);

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		GroupLayout.ParallelGroup pGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

		boolean isExtLnF = LookAndFeelManager.isExternal();
		boolean isRad = LookAndFeelManager.isRadiance();
		boolean isFlaf = LookAndFeelManager.isFlatLaf();

		for(int i = 0; i < BUTTONCOUNT_SMALL; i++) {
			if (isRad) {
				hGroup.addComponent(buttons_small[i], sizes_small[i], buttons_small[i].getPreferredSize().width, Short.MAX_VALUE);
				pGroup.addComponent(buttons_small[i], 0, buttons_small[i].getPreferredSize().height, Short.MAX_VALUE);
			} else if (isFlaf) {
				hGroup.addComponent(buttons_small[i]);
				pGroup.addComponent(buttons_small[i]);
			} else {
				hGroup.addComponent(buttons_small[i]);
				pGroup.addComponent(buttons_small[i]);
			}

			hGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE);
			final int fi = i;
			buttons_small[i].addActionListener(arg0 -> onClickSmall(fi));
		}

		if (isExtLnF) {
			hGroup.addGap(4);
		}

		layout.setHorizontalGroup(hGroup);

		layout.setVerticalGroup(pGroup);
	}

	private void onClickSmall(int idx) {
		if (idx == 0) {
			onClick(null);
		} else {
			onClick(searches_small[idx]);
		}
	}
}
