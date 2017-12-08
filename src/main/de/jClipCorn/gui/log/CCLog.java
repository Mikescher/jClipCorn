package de.jClipCorn.gui.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import de.jClipCorn.gui.frames.mainFrame.MainFrame;
import de.jClipCorn.gui.guiComponents.DatabaseElementPreviewLabel;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.DialogHelper;

public class CCLog {
	private static List<CCLogElement> log = new Vector<>();
	private static List<CCLogChangedListener> listener = new Vector<>();
	
	private static boolean isUnitTest = false;
	
	private static String path = null;
	private static boolean changed = false;

	private static boolean fatalExit = false;
	
	public static void initUnitTestMode() {
		isUnitTest = true;
		log = new Vector<>();
		listener = new Vector<>();
		changed = false;
	}
	
	public static void setPath(String p) {
		path = p;
	}

	public static void addInformation(Exception e) {
		add(e.toString(), CCLogType.LOG_ELEM_INFORMATION, e.getStackTrace());
	}
	
	public static void addInformation(String e) {
		add(e, CCLogType.LOG_ELEM_INFORMATION);
	}

	public static void addWarning(String e) {
		add(e, CCLogType.LOG_ELEM_WARNING);
	}
	
	public static void addWarning(String s, Exception e) {
		add(s + '\n' + "\tcaused by" + e.toString(), CCLogType.LOG_ELEM_WARNING, e.getStackTrace()); //$NON-NLS-1$
	}

	public static void addError(String e) {
		add(e, CCLogType.LOG_ELEM_ERROR);
	}
	
	public static void addError(String e, StackTraceElement[] trace) {
		add(e, CCLogType.LOG_ELEM_ERROR, trace);
	}

	public static void addError(Exception e) {
		add(e.toString(), CCLogType.LOG_ELEM_ERROR, e.getStackTrace());
	}
	
	public static void addError(String s, Exception e) {
		add(s + '\n' + "\tcaused by" + e.toString(), CCLogType.LOG_ELEM_ERROR, e.getStackTrace()); //$NON-NLS-1$
	}
	
	public static void addFatalError(Exception e) {
		add(e.toString(), CCLogType.LOG_ELEM_FATALERROR, e.getStackTrace());
	}
	
	public static void addFatalError(String e) {
		add(e, CCLogType.LOG_ELEM_FATALERROR);
	}
	
	public static void addFatalError(String e, StackTraceElement[] trace) {
		add(e, CCLogType.LOG_ELEM_FATALERROR, trace);
	}
	
	public static void addFatalError(String s, Exception e) {
		add(s + '\n' + "\tcaused by" + e.toString(), CCLogType.LOG_ELEM_FATALERROR, e.getStackTrace()); //$NON-NLS-1$
	}

	private static void add(String txt, CCLogType type) {
		add(txt, type, new Throwable().getStackTrace());
	}
	
	public static void addUndefinied(Thread thread, Throwable throwable) {
		add('[' + thread.toString() + ']' + ' ' + throwable.toString(), CCLogType.LOG_ELEM_UNDEFINED, throwable.getStackTrace());
	}

	public static void addUndefinied(String msg) {
		add('[' + Thread.currentThread().toString() + ']' + ' ' + msg, CCLogType.LOG_ELEM_UNDEFINED, new Exception().getStackTrace());
	}

	public static void addDefaultSwitchError(String owner, Object value) {
		if (value == null)
			addUndefinied("Invalid jump to default switch statement for " + owner + " (value = NULL)"); //$NON-NLS-1$ //$NON-NLS-2$
		else
			addUndefinied("Invalid jump to default switch statement for " + owner + " (value = '" + value + "')"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static void addDefaultSwitchError(Class<?> c, Object value) {
		addDefaultSwitchError(c.getSimpleName(), value);
	}

	public static void addDefaultSwitchError(Object o, Object value) {
		if (o == null) 
			addDefaultSwitchError("NULL", value); //$NON-NLS-1$
		else
			addDefaultSwitchError(o.getClass(), value);
	}
	
	public static void addDebug(String msg) {
		System.out.println("[DBG] " + msg); //$NON-NLS-1$
	}

	private static void add(String txt, CCLogType type, StackTraceElement[] trace) {
		CCLogElement cle = new CCLogElement(txt, type, trace);

		if (cle.equals(lastLogElem())) {
			lastLogElem().inc();
			System.out.println("LAST EXCEPTION x " + lastLogElem().getCount()); //$NON-NLS-1$
			return;
		} else {
			log.add(cle);

			if (isUnitTest) {
				// Don't show infos in unit tests
				if (! cle.isType(CCLogType.LOG_ELEM_INFORMATION))
					System.out.println(cle.getFormatted().trim());
			} else {
				System.out.println(cle.getFormatted().trim());
			}

			if (type == CCLogType.LOG_ELEM_FATALERROR) {
				DialogHelper.showDispatchError(LocaleBundle.getString("Main.AbortCaption"), LocaleBundle.getFormattedString("Main.AbortMessage", cle.getFormatted(CCLogElement.FORMAT_LEVEL_MID))); //$NON-NLS-1$ //$NON-NLS-2$
				fatalabort();
			}

			if (type == CCLogType.LOG_ELEM_ERROR) {
				updateMainFrameLabel();
			}

		}
		
		setChangedFlag();
	}
	
	public static CCLogElement lastLogElem() {
		if (log.size() <= 0) return null;
		else return log.get(log.size() - 1);
	}

	public static void save() {
		if (isUnitTest) return;
		
		if (CCProperties.getInstance().ARG_READONLY) {
			CCLog.addInformation(LocaleBundle.getString("LogMessage.OperationFailedDueToReadOnly")); //$NON-NLS-1$
			return;
		}
		
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

			for (CCLogElement el : log) {
				out.print(el.getFormatted(CCLogElement.FORMAT_LEVEL_FULL) + '\n');
			}
		} catch (IOException e) {
			addError(e);
		} finally {
			if (out != null) {
				out.close();
			}
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					addError(e);
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
				addError(e);
			}
		}
	}
	
	public static void removeFirstNLine(File f, int toRemove) throws IOException {
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
		int count = 0;
		for(CCLogElement el : log) {
			if (el.isType(type)) count++;
		}
		
		return count;
	}
	
	public static CCLogElement getElement(CCLogType type, int position) {
		int count = 0;
		for(CCLogElement el : log) {
			if (el.isType(type)) {
				if (count == position) return el;
				count++;
			}
		}
		
		return null;
	}
	
	private static void fatalabort() {
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
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					for(CCLogChangedListener ls : listener) {
						ls.onChanged();
					}
				}
			});
		} else {
			for(CCLogChangedListener ls : listener) {
				ls.onChanged();
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
	
	public static boolean hasErrors() {
		for (CCLogElement e : log) {
			if (e.isType(CCLogType.LOG_ELEM_ERROR)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasWarnings() {
		for (CCLogElement e : log) {
			if (e.isType(CCLogType.LOG_ELEM_WARNING)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean hasInformations() {
		for (CCLogElement e : log) {
			if (e.isType(CCLogType.LOG_ELEM_INFORMATION)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasUndefinieds() {
		for (CCLogElement e : log) {
			if (e.isType(CCLogType.LOG_ELEM_UNDEFINED)) {
				return true;
			}
		}
		return false;
	}
	
	public static CCLogType getHighestType() {
		if (hasErrors()) {
			return CCLogType.LOG_ELEM_ERROR;
		}
		
		if (hasUndefinieds()) {
			return CCLogType.LOG_ELEM_UNDEFINED;
		}
		
		if (hasWarnings()) {
			return CCLogType.LOG_ELEM_WARNING;
		}
		
		return CCLogType.LOG_ELEM_INFORMATION;
	}
	
	private static void updateMainFrameLabel() {
		if (! SwingUtilities.isEventDispatchThread()) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						MainFrame mf =  MainFrame.getInstance();
						if (mf != null) {
							DatabaseElementPreviewLabel pl =  mf.getCoverLabel();
							if (pl != null) pl.setModeError();
						}
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
