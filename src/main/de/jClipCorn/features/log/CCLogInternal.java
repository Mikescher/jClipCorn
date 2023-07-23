package de.jClipCorn.features.log;

import de.jClipCorn.gui.guiComponents.cover.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.sqlwrapper.StatementType;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class CCLogInternal {
	private static List<CCLogElement>         log       = new Vector<>();
	private static List<CCLogChangedListener> listener  = new Vector<>();
	private static List<CCSQLLogElement>      sqlLog    = new Vector<>();
	private static List<CCChangeLogElement>   changeLog = new Vector<>();

	private static boolean changeLogEnabled = true;

	private static boolean isUnitTest = false;

	private static String path = null;
	private static CCProperties ccprops = null;

	private static volatile boolean changed = false;

	private static volatile boolean fatalExit = false;

	private static final Object _dataLock   = new Object();
	private static final Object _fileLock   = new Object();
	private static final Object _obsLock    = new Object();
	private static final Object _sqlLock    = new Object();
	private static final Object _changeLock = new Object();

	private static final AtomicBoolean hasUnwatchedInformation = new AtomicBoolean(false);
	private static final AtomicBoolean hasUnwatchedWarnings    = new AtomicBoolean(false);
	private static final AtomicBoolean hasUnwatchedErrors      = new AtomicBoolean(false);
	private static final AtomicBoolean hasUnwatchedUndefineds  = new AtomicBoolean(false);

	public static void initUnitTestMode() {
		synchronized (_dataLock) {
			synchronized (_obsLock) {
				isUnitTest       = true;
				log              = new Vector<>();
				listener         = new Vector<>();
				sqlLog           = new Vector<>();
				changeLog        = new Vector<>();
				changed          = false;
				changeLogEnabled = true;
			}
		}
	}

	public static void setPath(String p) {
		path = p;
	}

	public static void setCCProps(CCProperties ccp) {
		ccprops = ccp;
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
				System.out.println(cle.getFormatted(CCLogElement.FORMAT_LEVEL_FULL).trim());
			}

			if (type == CCLogType.LOG_ELEM_FATALERROR) {
				DialogHelper.showDispatchError(null, LocaleBundle.getString("Main.AbortCaption"), LocaleBundle.getFormattedString("Main.AbortMessage", cle.getFormatted(CCLogElement.FORMAT_LEVEL_MID))); //$NON-NLS-1$ //$NON-NLS-2$
				fatalabort();
			}

			if (type == CCLogType.LOG_ELEM_ERROR) {
				updateMainFrameLabel();
			}
		}

		if (cle.isType(CCLogType.LOG_ELEM_ERROR)) hasUnwatchedErrors.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_WARNING)) hasUnwatchedWarnings.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_INFORMATION)) hasUnwatchedInformation.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_UNDEFINED)) hasUnwatchedUndefineds.set(true);

		setChangedFlag();
	}

	public static void addSQL(String method, StatementType stype, String sql, long startMillis, long endMillis, String error) {
		CCSQLLogElement cle = new CCSQLLogElement(method, stype, sql, startMillis, endMillis, error);

		synchronized (_sqlLock) {
			sqlLog.add(cle);
		}

		triggerSQLChanged(cle);
	}

	public static void addChange(String rootType, int rootID, String actualType, int actualID, String[] properties) {
		CCChangeLogElement cle = new CCChangeLogElement(rootType, rootID, actualType, actualID, properties);

		synchronized (_changeLock) {
			if (!changeLogEnabled) return;

			changeLog.add(cle);
		}

		triggerChangesChanged(cle);
	}

	private static CCLogElement lastLogElem() {
		synchronized (_dataLock) {
			if (log.size() <= 0) return null;
			else return log.get(log.size() - 1);
		}
	}

	public static void save() {
		if (isUnitTest) return;

		if (ccprops.ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}

		synchronized (_fileLock) {
			FileWriter file = null;
			PrintWriter out = null;
			try {
				file = new FileWriter(path, ccprops.PROP_LOG_APPEND.getValue());
				out = new PrintWriter(file);

				if (ccprops.PROP_LOG_APPEND.getValue()) {
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

			if (ccprops.PROP_LOG_APPEND.getValue()) {
				try {
					BufferedReader reader = new BufferedReader(new FileReader(path));
					int lines = 0;
					while (reader.readLine() != null) {
						lines++;
					}
					reader.close();

					lines -= ccprops.PROP_LOG_MAX_LINECOUNT.getValue();

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

	public static int getSQLCount() {
		synchronized (_sqlLock) {
			return sqlLog.size();
		}
	}

	public static int getChangeCount() {
		synchronized (_changeLock) {
			return changeLog.size();
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

	public static CCSQLLogElement getSQLElement(int idx) {
		if (idx < 0) return null;
		synchronized (_sqlLock) {
			if (idx >= sqlLog.size()) return null;
			return sqlLog.get(idx);
		}
	}

	public static void addDebug(String msg) {
		System.out.println("[DBG] " + msg); //$NON-NLS-1$
	}

	public static void fatalabort() {
		if (fatalExit) System.exit(-9999); // infinite recursion in exitApplication;

		fatalExit = true;

		ApplicationHelper.exitApplication(-1, true);
	}

	public static void addChangeListener (CCLogChangedListener lst) {
		listener.add(lst);
	}

	public static void removeChangeListener (CCLogChangedListener lst) {
		listener.remove(lst);
	}

	public static void setChangedFlag() {
		changed = true;

		if (! SwingUtilities.isEventDispatchThread()) {
			SwingUtils.invokeLater(() -> { synchronized (_obsLock) { for(CCLogChangedListener ls : listener) { ls.onChanged(); } } });
		} else {
			synchronized (_obsLock) {
				for(CCLogChangedListener ls : listener) { ls.onChanged(); }
			}
		}
	}

	public static void triggerSQLChanged(final CCSQLLogElement e) {
		if (! SwingUtilities.isEventDispatchThread()) {
			SwingUtils.invokeLater(() -> { synchronized (_obsLock) { for(CCLogChangedListener ls : listener) { ls.onSQLChanged(e); } } });
		} else {
			synchronized (_obsLock) { for(CCLogChangedListener ls : listener) { ls.onSQLChanged(e); } }
		}
	}

	public static void triggerChangesChanged(final CCChangeLogElement e) {
		if (! SwingUtilities.isEventDispatchThread()) {
			SwingUtils.invokeLater(() -> { synchronized (_obsLock) { for(CCLogChangedListener ls : listener) { ls.onPropsChanged(e); } } });
		} else {
			synchronized (_obsLock) { for(CCLogChangedListener ls : listener) { ls.onPropsChanged(e); } }
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
		try {
			SwingUtils.invokeAndWaitConditionalThrow(() ->
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
	}

	public static void setAllWatched() {
		hasUnwatchedErrors.set(false);
		hasUnwatchedWarnings.set(false);
		hasUnwatchedInformation.set(false);
		hasUnwatchedUndefineds.set(false);

		setChangedFlag();
	}

	public static boolean hasUnwatchedErrorsOrUndef() {
		return hasUnwatchedErrors.get() || hasUnwatchedUndefineds.get();
	}

	public static boolean hasUnwatched(CCLogType type) {
		switch (type) {
			case LOG_ELEM_UNDEFINED:   return hasUnwatchedUndefineds.get();
			case LOG_ELEM_INFORMATION: return hasUnwatchedInformation.get();
			case LOG_ELEM_WARNING:     return hasUnwatchedWarnings.get();
			case LOG_ELEM_ERROR:       return hasUnwatchedErrors.get();
			case LOG_ELEM_FATALERROR:  return false;
		}
		return false;
	}

	public static boolean isUnitTest() {
		return isUnitTest;
	}

	public static List<CCChangeLogElement> getChangeLogElementsCopy() {
		synchronized (_changeLock) {
			return new ArrayList<>(changeLog);
		}
	}

	public static List<CCSQLLogElement> getSQLLogElementsCopy() {
		synchronized (_sqlLock) {
			return new ArrayList<>(sqlLog);
		}
	}

	public static void setChangeEventsEnabled(boolean b) {
		synchronized (_changeLock) {
			changeLogEnabled = b;
		}
	}
}
