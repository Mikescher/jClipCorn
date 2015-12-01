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
		MainFrame.getInstance().terminate();
		System.exit(errorcode);
	}
}
