package de.jClipCorn.util.helper;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.win32.StdCallLibrary;
import de.jClipCorn.util.stream.CCStreams;

import java.awt.*;

@SuppressWarnings("nls")
public class WindowsJNAHelper
{
	private static final User32 user32 = User32.INSTANCE;

	interface User32 extends StdCallLibrary
	{
		User32 INSTANCE = (User32) Native.load("user32", User32.class);

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
}
