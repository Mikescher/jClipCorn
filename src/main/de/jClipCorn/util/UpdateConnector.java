package de.jClipCorn.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.http.HTTPUtilities;

public class UpdateConnector implements Runnable {
	private final static String HIGHSCORE_URL = "http://www.mikescher.com/update.php?Name=jClipCorn"; //$NON-NLS-1$
	
	private final ActionListener listener;
	private final String title;
	private final String version;
	
	private String updateURL;
	private String updateVersion;
	private boolean updateAvailable = false;
	private String updateName;
	
	public UpdateConnector(String title, String version, ActionListener listener, boolean threaded) {
		this.listener = listener;
		this.title = title;
		this.version = version;
		
		if (threaded) {
			new Thread(this, "THREAD_CHECK_FOR_UPDATES").start(); //$NON-NLS-1$
		} else {
			run();
		}
	}

	@Override
	public void run() {
		String resultCode = HTTPUtilities.getHTML(HIGHSCORE_URL, false, true);

		if (resultCode == null || resultCode.isEmpty()) {
			updateName = title;
			updateVersion = version;
			updateAvailable = false;
			updateURL = ""; //$NON-NLS-1$
			CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotConnectToUpdateSite")); //$NON-NLS-1$
			return;
		}

		updateName = resultCode.split("<hr>")[0]; //$NON-NLS-1$
		updateVersion = resultCode.split("<hr>")[1]; //$NON-NLS-1$
		updateURL = resultCode.split("<hr>")[2]; //$NON-NLS-1$
		updateAvailable = !version.equals(updateVersion);
		
		if (updateAvailable) {
			listener.actionPerformed(new ActionEvent(this, 1, updateVersion));
		} else {
			listener.actionPerformed(new ActionEvent(this, 0, updateVersion));
		}
	}
	
	public boolean isUpdateAvaiable() {
		return updateAvailable;
	}
	
	public String getUpdateURL() {
		return updateURL;
	}
	
	public String getUpdateName() {
		return updateName;
	}
	
	public String getUpdateVersion() {
		return updateVersion;
	}
	
	public void openURL() {
		if (updateAvailable) {
			HTTPUtilities.openInBrowser(updateURL);
		}
	}
}
