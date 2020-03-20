package de.jClipCorn.util.helper;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;
import java.io.File;
import java.nio.file.FileStore;
import java.util.List;

@SuppressWarnings("nls")
public class WindowsJNAHelper
{
	private static final User32   user32   = User32.INSTANCE;
	private static final Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);

	interface User32 extends StdCallLibrary
	{
		User32 INSTANCE = Native.load("user32", User32.class);

		interface WNDENUMPROC extends StdCallLibrary.StdCallCallback {
			boolean callback(Pointer hWnd, Pointer arg);
		}

		boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer userData);
		boolean MoveWindow(Pointer hWnd, int x, int y, int nWidth, int nHeight, boolean bRepaint);
		int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);

	}

	public static Rectangle getSingleWindowPositionOrNull(String filename)
	{
		return CCStreams
				.iterate(WindowUtils.getAllWindows(true))
				.filter(e -> e.getFilePath().toLowerCase().endsWith(filename.toLowerCase()))
				.map(DesktopWindow::getLocAndSize)
				.singleOrNull();
	}

	public static void setSingleWindowPositionOrNull(String filename, Rectangle rect)
	{
		DesktopWindow win = CCStreams
				.iterate(WindowUtils.getAllWindows(true))
				.filter(e -> e.getFilePath().toLowerCase().endsWith(filename.toLowerCase()))
				.singleOrNull();

		if (win == null) return;

		user32.MoveWindow(win.getHWND().getPointer(), rect.x, rect.y, rect.width, rect.height, true);
	}

	public static String getDriveNetworkIdent(List<FileStore> fsl, File f, String name) {

		if (!ApplicationHelper.isWindows()) return null;

		try
		{
			String path = name + "://";
			var volName     = new char[256];
			var fsName      = new char[256];
			var volSerNbr    = new IntByReference();
			var maxCompLen   = new IntByReference();
			var fileSysFlags = new IntByReference();
			boolean ok = kernel32.GetVolumeInformation(path, volName, 256, volSerNbr, maxCompLen, fileSysFlags, fsName, 256);
			if (ok)
			{
				var strFSName  = new String(fsName);
				//var strVolName = new String(volName);
				return strFSName;
			}
			else return null;
		}
		catch (Exception e)
		{
			CCLog.addError(e);
			return null;
		}
	}
}
