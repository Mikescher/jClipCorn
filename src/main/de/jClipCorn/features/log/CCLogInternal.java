package de.jClipCorn.features.log;

import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

public class CCLogInternal {
	private static List<CCLogElement> log = new Vector<>();
	private static List<CCLogChangedListener> listener = new Vector<>();

	private static boolean isUnitTest = false;

	private static String path = null;
	private static volatile boolean changed = false;

	private static volatile boolean fatalExit = false;

	private static final Object _dataLock = new Object();
	private static final Object _fileLock = new Object();
	private static final Object _obsLock  = new Object();

	public static void initUnitTestMode() {
		synchronized (_dataLock) {
			synchronized (_obsLock) {
				isUnitTest = true;
				log = new Vector<>();
				listener = new Vector<>();
				changed = false;
			}
		}
	}

	public static void setPath(String p) {
		path = p;
	}

	public static void add(String txt, CCLogType type, StackTraceElement[] trace) {
		CCLogElement cle = new CCLogElement(txt, type, trace);

		CCLogElement lle = lastLogElem();

		if (cle.equals(lle)) {
			lle.inc();
			System.out.println("LAST EXCEPTION x " + lle.getCount()); //$NON-NLS-1$
			return;
		} else {
			synchronized (_dataLock) {
				log.add(cle);
			}

			if (isUnitTest) {
				// Don't show infos in unit tests
				if (! cle.isType(CCLogType.LOG_ELEM_INFORMATION))
					System.out.println(cle.getFormatted().trim());
			} else {
				System.out.println(cle.getFormatted().trim());
			}

			if (type == CCLogType.LOG_ELEM_FATALERROR) {
				DialogHelper.showDispatchError(null, LocaleBundle.getString("Main.AbortCaption"), LocaleBundle.getFormattedString("Main.AbortMessage", cle.getFormatted(CCLogElement.FORMAT_LEVEL_MID))); //$NON-NLS-1$ //$NON-NLS-2$
				fatalabort();
			}

			if (type == CCLogType.LOG_ELEM_ERROR) {
				updateMainFrameLabel();
			}
		}

		setChangedFlag();
	}

	private static CCLogElement lastLogElem() {
		synchronized (_dataLock) {
			if (log.size() <= 0) return null;
			else return log.get(log.size() - 1);
		}
	}

	public static void save() {
		if (isUnitTest) return;

		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}

		synchronized (_fileLock) {
			FileWriter file = null;
			PrintWriter out = null;
			try {
				file = new FileWriter(path, CCProperties.getInstance().PROP_LOG_APPEND.getValue());
				out = new PrintWriter(file);

				if (CCProperties.getInstance().PROP_LOG_APPEND.getValue()) {
					out.print('\n');
					out.print("--------------------------------  " + CCDate.getCurrentDate().toStringSerialize() + "  --------------------------------" + '\n'); //$NON-NLS-1$ //$NON-NLS-2$
					out.print('\n');
				}

				synchronized (_dataLock) {
					for (CCLogElement el : log) {
						out.print(el.getFormatted(CCLogElement.FORMAT_LEVEL_FULL) + '\n');
					}
				}
			} catch (IOException e) {
				add(e.toString(), CCLogType.LOG_ELEM_ERROR, e.getStackTrace());
			} finally {
				if (out != null) {
					out.close();
				}
				if (file != null) {
					try {
						file.close();
					} catch (IOException e) {
						add(e.toString(), CCLogType.LOG_ELEM_ERROR, e.getStackTrace());
					}
				}
			}

			if (CCProperties.getInstance().PROP_LOG_APPEND.getValue()) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(path));
					int lines = 0;
					while (reader.readLine() != null) {
						lines++;
					}
					reader.close();

					lines -= CCProperties.getInstance().PROP_LOG_MAX_LINECOUNT.getValue();

					removeFirstNLine(new File(path), lines);
				} catch (IOException e) {
					add(e.toString(), CCLogType.LOG_ELEM_ERROR, e.getStackTrace());
				}
			}
		}
	}

	private static void removeFirstNLine(File f, int toRemove) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(f, "rw"); //$NON-NLS-1$

		long writePos = raf.getFilePointer();

		for (int i = 0; i < toRemove; i++) {
			raf.readLine();
		}

		raf.readLine();
		long readPos = raf.getFilePointer();

		byte[] buf = new byte[2048];
		int bufbyte;
		while (-1 != (bufbyte = raf.read(buf))) {
			raf.seek(writePos);
			raf.write(buf, 0, bufbyte);
			readPos += bufbyte;
			writePos += bufbyte;
			raf.seek(readPos);
		}

		raf.setLength(writePos);
		raf.close();
	}

	public static int getCount(CCLogType type) {
		synchronized (_dataLock) {
			int count = 0;
			for(CCLogElement el : log) {
				if (el.isType(type)) count++;
			}

			return count;
		}
	}

	public static CCLogElement getElement(CCLogType type, int position) {
		synchronized (_dataLock) {
			int count = 0;
			for (CCLogElement el : log) {
				if (el.isType(type)) {
					if (count == position) return el;
					count++;
				}
			}
			return null;
		}
	}

	public static void addDebug(String msg) {
		System.out.println("[DBG] " + msg); //$NON-NLS-1$
	}

	public static void fatalabort() {
		if (fatalExit) System.exit(-9999); // infinite recursion in exitApplication;

		fatalExit = true;

		ApplicationHelper.exitApplication(-1);
	}

	public static void addChangeListener (CCLogChangedListener lst) {
		listener.add(lst);
	}

	public static void setChangedFlag() {
		changed = true;

		if (! SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() ->
			{
				synchronized (_obsLock) {
					for(CCLogChangedListener ls : listener) {
						ls.onChanged();
					}
				}
			});
		} else {
			synchronized (_obsLock) {
				for(CCLogChangedListener ls : listener) {
					ls.onChanged();
				}
			}
		}
	}

	public static boolean isChanged() {
		if (changed) {
			changed = false;
			return true;
		}
		return false;
	}

	private static boolean hasOfType(CCLogType lt) {
		synchronized (_dataLock) {
			for (CCLogElement e : log) {
				if (e.isType(lt)) {
					return true;
				}
			}
			return false;
		}
	}

	public static boolean hasErrors() {
		return hasOfType(CCLogType.LOG_ELEM_ERROR);
	}

	public static boolean hasWarnings() {
		return hasOfType(CCLogType.LOG_ELEM_WARNING);
	}

	public static boolean hasInformations() {
		return hasOfType(CCLogType.LOG_ELEM_INFORMATION);
	}

	public static boolean hasUndefinieds() {
		return hasOfType(CCLogType.LOG_ELEM_UNDEFINED);
	}

	private static void updateMainFrameLabel() {
		if (! SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(() ->
				{
					MainFrame mf =  MainFrame.getInstance();
					if (mf != null) {
						DatabaseElementPreviewLabel pl =  mf.getCoverLabel();
						if (pl != null) pl.setModeError();
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				CCLog.addError(e);
			}
		} else {
			MainFrame mf =  MainFrame.getInstance();
			if (mf != null) {
				DatabaseElementPreviewLabel pl =  mf.getCoverLabel();
				if (pl != null) pl.setModeError();
			}
		}
	}
}
