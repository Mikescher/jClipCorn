package de.jClipCorn.gui.frames.settingsFrame;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.filesystem.PathFormatter;
import de.jClipCorn.util.helper.DialogHelper;

public class SettingsFrame extends AutomaticSettingsFrame {
	private static final long serialVersionUID = 4681197289662529891L;

	public SettingsFrame(MainFrame owner, CCProperties properties){
		super(owner, properties);
		
		init();
	}

	@Override
	protected boolean validateValues() {
		if (!PathFormatter.validateDatabaseName((String)getCurrentInputValue(CCProperties.getInstance().PROP_DATABASE_NAME))) {
			DialogHelper.showLocalError(this, "Dialogs.DatabasenameAssertion"); //$NON-NLS-1$
			
			return false;
		}
		
		return true;
	}
}
