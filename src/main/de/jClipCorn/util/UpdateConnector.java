package de.jClipCorn.util;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.lambda.Func3to0;
import org.json.JSONObject;
import org.json.JSONTokener;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.http.HTTPUtilities;

public class UpdateConnector implements Runnable {
	private final static String HIGHSCORE_URL = "https://api.github.com/repos/Mikescher/jClipCorn/releases/latest"; //$NON-NLS-1$
	
	private final Func3to0<UpdateConnector, Boolean, String> listener;
	private final String title;
	private final String version;
	
	private String updateURL;
	private String updateVersion;
	private boolean updateAvailable = false;
	private String updateName;

	private final CCMovieList movielist;

	public UpdateConnector(CCMovieList ml, String title, String version, Func3to0<UpdateConnector, Boolean, String> listener, boolean threaded) {
		this.listener = listener;
		this.title = title;
		this.version = version;
		this.movielist = ml;
		
		if (threaded) {
			new Thread(this, "THREAD_CHECK_FOR_UPDATES").start(); //$NON-NLS-1$
		} else {
			run();
		}
	}

	@Override
	@SuppressWarnings("nls")
	public void run() {
		String resultCode = HTTPUtilities.getHTML(movielist, HIGHSCORE_URL, false, true);

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
			updateAvailable = !Main.BETA &&  !versionCompare(version, updateVersion);

			CCLog.addInformation(Str.format("Found version {0} online (local = {1}); updateAvailable := {2}", updateVersion, version, updateAvailable));

		} catch (Exception e) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CouldNotParseUpdateSite", resultCode));
			return;
		}

		listener.invoke(this, updateAvailable, updateVersion);
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

	private static boolean versionCompare(String a, String b) {
		while(a.endsWith(".0")) a = a.substring(0, a.length()-2);
		while(b.endsWith(".0")) b = b.substring(0, b.length()-2);

		return a.equalsIgnoreCase(b);
	}
}
