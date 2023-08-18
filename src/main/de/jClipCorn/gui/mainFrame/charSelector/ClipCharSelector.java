package de.jClipCorn.gui.mainFrame.charSelector;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.mainFrame.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ClipCharSelector extends JPanel {

	@DesignCreate
	private static ClipCharSelector designCreate() { return new ClipCharSelector(null); }

	public ClipCharSelector(MainFrame mf) {
		super();

		this.setLayout(new BorderLayout());

		var isFull = !LookAndFeelManager.isRadiance();

		if (isFull) {
			this.add(new ClipCharSortSelectorFull(mf));
		} else {
			this.add(new ClipCharSortSelectorSmall(mf));
		}
	}
}
