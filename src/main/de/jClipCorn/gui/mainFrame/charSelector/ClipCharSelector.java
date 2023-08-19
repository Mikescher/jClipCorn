package de.jClipCorn.gui.mainFrame.charSelector;

import com.jformdesigner.annotations.DesignCreate;
import de.jClipCorn.gui.LookAndFeelManager;
import de.jClipCorn.gui.mainFrame.MainFrame;

import javax.swing.*;
import java.awt.*;

public class ClipCharSelector extends JPanel {

	@DesignCreate
	private static ClipCharSelector designCreate() { return new ClipCharSelector(null); }

	private final AbstractClipCharSortSelector _component;

	public ClipCharSelector(MainFrame mf) {
		super();

		this.setLayout(new BorderLayout());

		var isFull = !LookAndFeelManager.isRadiance();

		if (isFull) {
			this.add(_component = new ClipCharSortSelectorFull(mf));
		} else {
			this.add(_component = new ClipCharSortSelectorSmall(mf));
		}
	}

	public void reset() {
		_component.reset();
	}
}
