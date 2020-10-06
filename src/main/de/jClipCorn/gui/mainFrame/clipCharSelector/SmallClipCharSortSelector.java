package de.jClipCorn.gui.mainFrame.clipCharSelector;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.LayoutStyle;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.LookAndFeelManager;

/**
 * For Substance Themes
 */
public class SmallClipCharSortSelector extends AbstractClipCharSortSelector {
	private static final long serialVersionUID = -8270219279263812975L;
	
	@SuppressWarnings("nls")
	private static String  captions[] = {"All",  "#",  "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};
	private static int  sizes[] = {32,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  30,  42};
	@SuppressWarnings("nls")
	private static String  searches[] = {"",  "0123456789", "AB", "CD", "EF",  "GH", "IJ", "KL",  "MN", "OP", "QR",  "ST", "UV", "WX", "YZ"};
	
	private static int BUTTONCOUNT = captions.length;

	private GroupLayout layout;
	
	private JButton buttons[] = new JButton[BUTTONCOUNT];
	
	public SmallClipCharSortSelector(MainFrame mf) {
		super(mf);
		
		createButtons();
		createLayout();
	}
	
	private void createButtons() {
		for(int i = 0; i < BUTTONCOUNT; i++) {
			buttons[i] = new JButton(captions[i]);
		}
	}
	
	private void createLayout() {
		this.setLayout(layout = new GroupLayout(this));

		setFloatable(false);
		
		SequentialGroup hGroup = layout.createSequentialGroup();
		ParallelGroup pGroup = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		
		boolean isSubst = LookAndFeelManager.isSubstance();
		
		for(int i = 0; i < BUTTONCOUNT; i++) {
			if (isSubst) {
				hGroup.addComponent(buttons[i], sizes[i], buttons[i].getPreferredSize().width, Short.MAX_VALUE);
				pGroup.addComponent(buttons[i], 0, buttons[i].getPreferredSize().height, Short.MAX_VALUE);
			} else {
				hGroup.addComponent(buttons[i]);
				pGroup.addComponent(buttons[i]);
			}
			
			hGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE);
			final int fi = i;
			buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					onClick(fi);
				}
			});
		}
		
		if (isSubst) {
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
}
