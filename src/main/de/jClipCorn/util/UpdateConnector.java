package de.jClipCorn.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.JSONObject;
import org.json.JSONTokener;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.http.HTTPUtilities;

public class UpdateConnector implements Runnable {
	private final static String HIGHSCORE_URL = "https://api.github.com/repos/Mikescher/jClipCorn/releases/latest"; //$NON-NLS-1$
	
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
	@SuppressWarnings("nls")
	public void run() {
		String resultCode = HTTPUtilities.getHTML(HIGHSCORE_URL, false, true);

		if (resultCode == null || resultCode.isEmpty()) {
			updateName = title;
			updateVersion = version;
			updateAvailable = false;
			updateURL = "";
			CCLog.addError(LocaleBundle.getString("LogMessage.CouldNotConnectToUpdateSite"));
			return;
		}
		
		try {

			JSONObject root = new JSONObject(new JSONTokener(resultCode));
			
			if (!root.has("tag_name")) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseUpdateSite", resultCode));
				return;
			}
			
			String tag = root.getString("tag_name");
			if (tag.startsWith("v")) tag = tag.substring(1);
			if (tag.startsWith("V")) tag = tag.substring(1);
			
			updateName = "jClipCorn";
			updateVersion = tag;
			updateURL = root.getString("html_url");
			updateAvailable = !version.equals(updateVersion);
			
		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseUpdateSite", resultCode));
			return;
		}

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
