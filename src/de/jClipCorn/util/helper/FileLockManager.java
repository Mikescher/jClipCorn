package de.jClipCorn.util.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

/*
 * Cross plattfom lock file implementation
 */
public class FileLockManager {
	private static final String LOCK_EXTENSION = ".~lock"; //$NON-NLS-1$
	
	public static boolean tryLockFile(String path, boolean registerShutdownError) throws IOException {
		String lockpath = path + LOCK_EXTENSION; 
		
		File lockFile = new File(lockpath);
		
		if (lockFile.exists()) {
			String pid = TextFileUtils.readUTF8TextFile(lockFile);
			
			if (pid.equalsIgnoreCase(getPID())) return true;
			
			if (isRunning(pid)) return false;
			
			if (registerShutdownError) {
				CCLog.addWarning(LocaleBundle.getString("LogMessage.IncorrectShutdown")); //$NON-NLS-1$
			}
			
			TextFileUtils.writeTextFile(lockFile, getPID());
			return true;
		}

		TextFileUtils.writeTextFile(lockFile, getPID());
		return true;
	}

	public static boolean unlockFile(String path) throws IOException {
		String lockpath = path + LOCK_EXTENSION; 
		
		File lockFile = new File(lockpath);		

		if (lockFile.exists()) {
			String pid = TextFileUtils.readUTF8TextFile(lockFile);
			
			if (pid.equalsIgnoreCase(getPID()) || !isRunning(pid)) {
				lockFile.delete();
				return true;
			}
			
			return false;
		}
		
		return true;
	}
	
	public static boolean isLocked(String path) throws IOException {
		String lockpath = path + LOCK_EXTENSION; 

		File lockFile = new File(lockpath);

		if (! lockFile.exists()) {
			return false;
		}
		
		String pid = TextFileUtils.readUTF8TextFile(lockFile);
		
		return isRunning(pid);
	}
	
	@SuppressWarnings("nls")
	public static String getPID() {
		if (ApplicationHelper.isUnix()) {
			String pid = ManagementFactory.getRuntimeMXBean().getName();
			
			if (pid.contains("@"))
				pid = pid.split("@")[0];
			
			return pid;
		}
		
		if (ApplicationHelper.isWindows()) {
			String pid = ManagementFactory.getRuntimeMXBean().getName();
			
			if (pid.contains("@"))
				pid = pid.split("@")[0];
			
			return pid;
		}
		
		if (ApplicationHelper.isMac()) {
			String pid = ManagementFactory.getRuntimeMXBean().getName();
			
			if (pid.contains("@"))
				pid = pid.split("@")[0];
			
			return pid;
		}
		
		CCLog.addWarning(LocaleBundle.getString("LogMessage.UnknownOperatingSystem"));
		
		return null;
	}
	
	public static boolean isRunning(String pid) {
		for (String lpid : listPIDs()) {
			if (lpid.equalsIgnoreCase(pid)) return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("nls")
	public static List<String> listPIDs() {
		List<String> result = new ArrayList<>();
		
		if (ApplicationHelper.isUnix() || ApplicationHelper.isMac()) {
			try {
				String line;
		        Process p = Runtime.getRuntime().exec("ps -e");
		        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

		        while ((line = br.readLine()) != null) {
		            String pid = line.split(" ")[0]; //$NON-NLS-1$
		            result.add(pid);
		        }

				return result;
			} catch (Exception e) {
				CCLog.addUndefinied(Thread.currentThread(), e);
				return result;
			}
		}
		
		if (ApplicationHelper.isWindows()) {
			try {
				String line;
		        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe /fo csv /nh");
		        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

		        while ((line = br.readLine()) != null) {
		            String pid = line.split(",")[1];
		            pid = pid.substring(1, pid.length()-1);
		            result.add(pid);
		        }

				return result;
			} catch (Exception e) {
				CCLog.addUndefinied(Thread.currentThread(), e);
				return result;
			}
		}

		CCLog.addWarning(LocaleBundle.getString("LogMessage.UnknownOperatingSystem")); //$NON-NLS-1$

		return result;
	}
}
