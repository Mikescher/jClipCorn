package de.jClipCorn.features.log;

import de.jClipCorn.Main;
import de.jClipCorn.gui.guiComponents.cover.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.sqlwrapper.StatementType;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * New log elements are appended to the log file in the background while the application runs, so a crash
 * loses at most FLUSH_DELAY_MILLIS of log and the shutdown only has to write the last few elements.
 * <p>
 * Once the file exceeds PROP_LOG_MAX_LINECOUNT it is cut down to TRIM_TO_RATIO of the limit - cutting
 * rewrites the whole file, which is slow on a network share, so it must not happen on every write.
 */
public class CCLogInternal {
	private static final long   FLUSH_DELAY_MILLIS = 2000;
	private static final double TRIM_TO_RATIO      = 0.75;

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
	private static final Object _flushLock  = new Object();

	// guarded by _flushLock
	private static ScheduledExecutorService _flushExec = null;
	private static boolean _flushScheduled = false;
	private static boolean _shutdownHookRegistered = false;

	// guarded by _fileLock
	private static int     _writtenCount = 0;     // elements of `log` that are already in the file
	private static boolean _headerWritten = false;
	private static long    _fileLineCount = -1;   // -1 = not counted yet
	private static boolean _writeErrorReported = false;

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

		synchronized (_flushLock) {
			if (!_shutdownHookRegistered) {
				// covers every System.exit and SIGTERM - must not report errors, that would block on the EDT
				Runtime.getRuntime().addShutdownHook(new Thread(() -> flush(false), "THREAD_LOG_SHUTDOWN_FLUSH")); //$NON-NLS-1$
				_shutdownHookRegistered = true;
			}
		}

		scheduleFlush(false);
	}

	public static void setCCProps(CCProperties ccp) {
		ccprops = ccp;
	}

	public static void add(String txt, CCLogType type, StackTraceElement[] trace) {
		CCLogElement cle = new CCLogElement(txt, type, trace);

		CCLogElement lle = lastLogElem();

		synchronized (_dataLock) {
			log.add(cle);
		}

		scheduleFlush(type == CCLogType.LOG_ELEM_ERROR || type == CCLogType.LOG_ELEM_FATALERROR || type == CCLogType.LOG_ELEM_UNDEFINED);

		if (isUnitTest)
		{
			// Don't show infos in unit tests
			if (! cle.isType(CCLogType.LOG_ELEM_INFORMATION)) System.out.println(cle.getFormatted().trim());
		}
		else
		{
			if (cle.isType(CCLogType.LOG_ELEM_INFORMATION)) System.out.println(cle.getFormatted(CCLogElement.FORMAT_LEVEL_SHORT).trim());
			else                                            System.out.println(cle.getFormatted(CCLogElement.FORMAT_LEVEL_FULL).trim());
		}

		if (type == CCLogType.LOG_ELEM_FATALERROR) {
			DialogHelper.showDispatchError(null, LocaleBundle.getString("Main.AbortCaption"), LocaleBundle.getFormattedString("Main.AbortMessage", cle.getFormatted(CCLogElement.FORMAT_LEVEL_MID))); //$NON-NLS-1$ //$NON-NLS-2$
			fatalabort();
		}

		if (type == CCLogType.LOG_ELEM_ERROR) {
			updateMainFrameLabel();
		}

		if (cle.isType(CCLogType.LOG_ELEM_ERROR))       hasUnwatchedErrors.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_WARNING))     hasUnwatchedWarnings.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_INFORMATION)) hasUnwatchedInformation.set(true);
		if (cle.isType(CCLogType.LOG_ELEM_UNDEFINED))   hasUnwatchedUndefineds.set(true);

		setChangedFlag();
	}

	public static void addSQL(String method, StatementType stype, String sql, long startMillis, long endMillis, String error) {
		CCSQLLogElement cle = new CCSQLLogElement(method, stype, sql, startMillis, endMillis, error);

		synchronized (_sqlLock) {
			sqlLog.add(cle);
		}

		triggerSQLChanged(cle);
	}

	public static void addChange(String rootType, CCUUID rootID, String actualType, CCUUID actualID, String[] properties) {
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

	/** Writes all elements that are not yet in the log file - synchronously, on the calling thread. */
	public static void save() {
		flush(true);
	}

	private static void scheduleFlush(boolean immediate) {
		if (isUnitTest || Main.ARG_READONLY) return;

		synchronized (_flushLock) {
			if (_flushScheduled && !immediate) return;
			_flushScheduled = true;

			if (_flushExec == null) {
				_flushExec = Executors.newSingleThreadScheduledExecutor(r ->
				{
					Thread t = new Thread(r, "THREAD_LOG_WRITER"); //$NON-NLS-1$
					t.setDaemon(true);
					return t;
				});
			}

			_flushExec.schedule(CCLogInternal::runScheduledFlush, immediate ? 0 : FLUSH_DELAY_MILLIS, TimeUnit.MILLISECONDS);
		}
	}

	private static void runScheduledFlush() {
		synchronized (_flushLock) {
			_flushScheduled = false;
		}

		flush(true);
	}

	private static void flush(boolean reportErrors) {
		if (isUnitTest || Main.ARG_READONLY || path == null) return;

		IOException error = null;

		synchronized (_fileLock) {
			List<CCLogElement> pending;
			synchronized (_dataLock) {
				if (_writtenCount >= log.size()) return;
				pending = new ArrayList<>(log.subList(_writtenCount, log.size()));
			}

			try {
				File file = new File(path);

				if (_fileLineCount < 0) _fileLineCount = countLines(file);

				long newLines = 0;
				try (Writer out = new BufferedWriter(new FileWriter(file, true))) {
					if (!_headerWritten) {
						String header = "\n--------------------------------  " + CCDate.getCurrentDate().toStringSerialize() + "  --------------------------------\n\n"; //$NON-NLS-1$ //$NON-NLS-2$
						out.write(header);
						newLines += countLines(header);
					}

					for (CCLogElement el : pending) {
						String str = el.getFormattedForFile() + '\n';
						out.write(str);
						newLines += countLines(str);
					}
				}

				_headerWritten = true;
				_writtenCount += pending.size();
				_fileLineCount += newLines;
				_writeErrorReported = false;

				if (ccprops != null) {
					int maxLines = ccprops.PROP_LOG_MAX_LINECOUNT.getValue();
					if (_fileLineCount > maxLines) {
						long keep = (long) (maxLines * TRIM_TO_RATIO);
						removeFirstLines(file, _fileLineCount - keep);
						_fileLineCount = keep;
					}
				}
			} catch (IOException e) {
				// reported once - the error element itself triggers the next flush, which would fail again
				if (!_writeErrorReported) error = e;
				_writeErrorReported = true;
			}
		}

		if (error != null) {
			if (reportErrors) add(error.toString(), CCLogType.LOG_ELEM_ERROR, error.getStackTrace());
			else error.printStackTrace();
		}
	}

	private static long countLines(String str) {
		long n = 0;
		for (int i = 0; i < str.length(); i++) if (str.charAt(i) == '\n') n++;
		return n;
	}

	public static long countLines(File f) throws IOException {
		if (!f.exists()) return 0;

		long n = 0;
		byte[] buf = new byte[64 * 1024];
		try (InputStream in = new FileInputStream(f)) {
			int len;
			while ((len = in.read(buf)) != -1) {
				for (int i = 0; i < len; i++) if (buf[i] == '\n') n++;
			}
		}
		return n;
	}

	/** Rewrites the file without its first `count` lines - into a temp file that then replaces the original. */
	public static void removeFirstLines(File f, long count) throws IOException {
		if (count <= 0) return;

		File tmp = new File(f.getPath() + ".tmp"); //$NON-NLS-1$
		try {
			try (InputStream in = new BufferedInputStream(new FileInputStream(f), 64 * 1024); OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp), 64 * 1024)) {
				int b;
				while (count > 0 && (b = in.read()) != -1) {
					if (b == '\n') count--;
				}
				in.transferTo(out);
			}

			try {
				Files.move(tmp.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(tmp.toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			Files.deleteIfExists(tmp.toPath());
		}
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
