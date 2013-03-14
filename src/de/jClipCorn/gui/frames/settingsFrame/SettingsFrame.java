package de.jClipCorn.gui.frames.settingsFrame;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;

public class SettingsFrame extends AutomaticSettingsFrame {
	private static final long serialVersionUID = 4681197289662529891L;

	public SettingsFrame(MainFrame owner, CCProperties properties){
		super(owner, properties);
		
		setPanelCount(7);
		setRowCount(12);
		
		init();
	}
}
