package de.jClipCorn.util.helper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.Main;
import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.PathFormatter;

public class ApplicationHelper {

	private static String os_property = null;
	
	public static void SetOverrideModeUnix() {
		os_property = "generic_unix"; //$NON-NLS-1$
		PathFormatter.SetPathModeUnix();
	}

	public static void SetOverrideModeWindows() {
		os_property = "generic_windows"; //$NON-NLS-1$
		PathFormatter.SetPathModeWindows();
	}
	
	@SuppressWarnings("nls")
	public static boolean restartApplication() { //Will fail in Eclipse, cause there is no .jar File
		String javaBin = PathFormatter.combine(System.getProperty("java.home"), "bin", "java");
		File currentJar;
		
		try {
			currentJar = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			CCLog.addError(e);
			return false;
		}

		if (!currentJar.getName().endsWith(".jar")) return false;

		List<String> command = new ArrayList<>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		try {
			builder.start();
		} catch (IOException e) {
			CCLog.addError(e);
			return false;
		}
		
		exitApplication();
		
		return true;
	}
	
	public static void exitApplication() {
		exitApplication(0);
	}

	public static void exitApplication(int errorcode) {
		MainFrame inst = MainFrame.getInstance();
		if (inst != null) {
			inst.terminate();
			inst.dispose();
		}
		System.exit(errorcode);
	}

	@SuppressWarnings("nls")
	public static boolean isWindows() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();
		
		return os_property.indexOf("win") >= 0;
	}
	
	@SuppressWarnings("nls")
	public static boolean isUnix() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();
		
		return (os_property.indexOf("nix") >= 0 || os_property.indexOf("nux") >= 0 || os_property.indexOf("aix") > 0 );
	}
	
	@SuppressWarnings("nls")
	public static boolean isMac() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();

		return os_property.indexOf("mac") >= 0;
	}

	public static String getCurrentUsername() {
		return System.getProperty("user.name"); //$NON-NLS-1$
	}
}
