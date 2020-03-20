package de.jClipCorn.util.helper;

import de.jClipCorn.Main;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datatypes.Tuple4;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.stream.CCStreams;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
		
		exitApplication(true);
		
		return true;
	}
	
	public static void exitApplication(boolean force) {
		exitApplication(0, force);
	}

	public static void exitApplication(int errorcode, boolean force) {
		if (force) {
			MainFrame inst = MainFrame.getInstance();
			if (inst != null) {
				inst.terminate();
				inst.dispose();
			}
			System.exit(errorcode);
		} else {

			MainFrame inst = MainFrame.getInstance();
			if (inst != null) {
				if (!inst.tryTerminate()) return;
				inst.terminate();
				inst.dispose();
			}

			System.exit(errorcode);
		}
	}

	@SuppressWarnings("nls")
	public static boolean isWindows() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();
		
		return os_property.contains("win");
	}
	
	@SuppressWarnings("nls")
	public static boolean isUnix() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();
		
		return (os_property.contains("nix") || os_property.contains("nux") || os_property.indexOf("aix") > 0 );
	}
	
	@SuppressWarnings("nls")
	public static boolean isMac() {
		if (os_property == null) os_property = System.getProperty("os.name").toLowerCase();

		return os_property.contains("mac");
	}

	public static String getCurrentUsername() {
		return System.getProperty("user.name"); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	public static String getNullFile() {
		if (isWindows()) return "NUL";
		if (isUnix())    return "/dev/null";
		if (isMac())     return "/dev/null";
		
		CCLog.addError(LocaleBundle.getString("LogMessage.UnknownOperatingSystem"));
		return "";
	}

	public static List<Tuple4<String, String, String, String>> getNetUseSafe()
	{
		if (!ApplicationHelper.isWindows()) return new ArrayList<>();

		try {
			return getNetUse();
		} catch (Exception e) {
			CCLog.addError(e);
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("nls")
	public static List<Tuple4<String, String, String, String>> getNetUse() throws Exception
	{
		Tuple3<Integer, String, String> out = ProcessHelper.procExec("net", "use");
		if (out.Item1 != 0) throw new Exception("Parser Error: Exec of net-use returned: " + out.Item1);

		String[] lines = out.Item2.split("\\r\\n");

		int start = CCStreams.iterate(lines).findIndex(l -> Pattern.matches("^-----+$", l.trim()));
		if (start == -1 || start == 0) throw new Exception("Parser Error: Table start not found");

		String header = lines[start - 1];

		if (header.trim().isEmpty() && start >= 2) header = lines[start - 2];

		int[] colLen = new int[4];

		int ci = 0;
		int mode = 0;
		for (int i = 0; i < header.length(); i++)
		{
			boolean wspace = header.charAt(i) == ' ' || header.charAt(i) == '\t';

			if (mode == 0) // left trim
			{
				colLen[ci]++;

				if (wspace) continue;
				mode = 1;
			}
			else if (mode == 1) // text
			{
				colLen[ci]++;

				if (wspace) mode = 2;
			}
			else if (mode == 2) // spaces
			{
				if (wspace) { colLen[ci]++; continue;}

				ci++;
				if (ci == 4) throw new Exception("Parser Error: Too many columns");
				mode = 1;
			}
		}

		if (colLen[0] == 0) throw new Exception("Parser Error: Empty col [0]");
		if (colLen[1] == 0) throw new Exception("Parser Error: Empty col [1]");
		if (colLen[2] == 0) throw new Exception("Parser Error: Empty col [2]");
		if (colLen[3] == 0) throw new Exception("Parser Error: Empty col [3]");

		int[] cols = new int[]{ 0, colLen[0], colLen[0] + colLen[1], colLen[0] + colLen[1] + colLen[2] };

		List<Tuple4<String, String, String, String>> r = new ArrayList<>();

		for (String line : lines)
		{
			String c0 = Str.safeSubstring(line, cols[0], colLen[0]).trim();
			String c1 = Str.safeSubstring(line, cols[1], colLen[1]).trim();
			String c2 = Str.safeSubstring(line, cols[2], colLen[2]).trim();
			String c3 = Str.safeSubstring(line, cols[3], 9999).trim();

			if (c0.isEmpty() || c1.isEmpty() || c2.isEmpty() || c3.isEmpty()) continue;
			if (!c1.endsWith(":")) continue;
			if (c1.length() != 2) continue;
			if (!c2.startsWith("\\\\")) continue;

			r.add(Tuple4.Create(c0, c1, c2, c3));
		}

		return r;
	}

	@SuppressWarnings("nls")
	public static boolean isProcessRunning(String name)
	{
		if (ApplicationHelper.isUnix() || ApplicationHelper.isMac()) {
			try {
				String line;
				Process p = Runtime.getRuntime().exec("ps -e"); //$NON-NLS-1$
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = br.readLine()) != null) {
					String[] split = line.split(" "); //$NON-NLS-1$
					String pname = split[split.length-1];
					if (pname.trim().toLowerCase().equals(name.trim().toLowerCase())) return true;
				}

				return false;
			} catch (Exception e) {
				CCLog.addError(e);
				return false;
			}
		}

		if (isWindows())
		{
			try {
				String line;
				StringBuilder pidInfo = new StringBuilder();

				Process p =Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");

				BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = input.readLine()) != null) pidInfo.append(line);

				input.close();

				return (pidInfo.toString().toLowerCase().contains(name.toLowerCase()));
			}
			catch (Exception e)
			{
				CCLog.addWarning(e);
				return false;
			}
		}

		return false;
	}
}
