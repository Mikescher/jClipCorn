package de.jClipCorn.gui.log;

import java.util.ArrayList;
import java.util.Arrays;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.CCTime;

public class CCLogElement {
	public final static int FORMAT_LEVEL_PREVIEW = -1;
	public final static int FORMAT_LEVEL_SHORT = 0;
	public final static int FORMAT_LEVEL_MID = 1;
	public final static int FORMAT_LEVEL_FULL = 2;
	public final static int FORMAT_LEVEL_LIST_FULL = 3;

	private final static String EXCLUSION_CLASS = "CCLog"; //$NON-NLS-1$
	private final static String FATAL_ERROR_MSG = LocaleBundle.getString("LogMessage.ErrorTerminating"); //$NON-NLS-1$

	public String text;
	public CCLogType type;
	public ArrayList<StackTraceElement> sTrace;
	public CCTime time;

	public CCLogElement() {
		type = CCLogType.LOG_ELEM_UNDEFINED;
		text = ""; //$NON-NLS-1$
		time = new CCTime();
		
		sTrace = null;
	}

	public CCLogElement(String txt, CCLogType tp, StackTraceElement[] stackTrace) {
		text = txt;
		type = tp;
		time = new CCTime();

		sTrace = new ArrayList<StackTraceElement>(Arrays.asList(stackTrace));
	}

	public String getFormatted() {
		if (type == CCLogType.LOG_ELEM_FATALERROR) {
			return getFormatted(FORMAT_LEVEL_FULL) + FATAL_ERROR_MSG;
		} else if (type == CCLogType.LOG_ELEM_ERROR) {
			return getFormatted(FORMAT_LEVEL_FULL);
		} else if (type == CCLogType.LOG_ELEM_WARNING) {
			return getFormatted(FORMAT_LEVEL_MID);
		} else if (type == CCLogType.LOG_ELEM_UNDEFINED) {
			return getFormatted(FORMAT_LEVEL_FULL);
		} else {
			return getFormatted(FORMAT_LEVEL_SHORT);
		}
	}

	public String getFormatted(int level) {
		switch (level) {
		case FORMAT_LEVEL_PREVIEW:
			return getTypeStringRepresentation() + ": " + getFirstLine(text) + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		case FORMAT_LEVEL_SHORT:
			return getTypeStringRepresentation() + ": " + text + "\n"; //$NON-NLS-1$ //$NON-NLS-2$
		case FORMAT_LEVEL_MID:
			return getTypeStringRepresentation() + ": " + text + "\n" + getCallerTrace() + '\n'; //$NON-NLS-1$ //$NON-NLS-2$
		case FORMAT_LEVEL_FULL:
		default:
			return getTypeStringRepresentation() + ": " + text + "\n" + getFormattedStackTrace() + '\n'; //$NON-NLS-1$ //$NON-NLS-2$
		case FORMAT_LEVEL_LIST_FULL:
			return getTypeStringRepresentation() + " (" + time.getSimpleStringRepresentation() + "): " + text + "\n" + getFormattedStackTrace() + '\n'; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	private static String getFirstLine(String txt) {
		int i = txt.indexOf('\n');
		if (i == -1) {
			return txt;
		} else {
			return txt.substring(0, i);
		}
		
	}

	private String getTypeStringRepresentation() {
		switch (type) {
		case LOG_ELEM_FATALERROR:
			return LocaleBundle.getString("CCLog.FatalError").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_ERROR:
			return LocaleBundle.getString("CCLog.Error").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_INFORMATION:
			return LocaleBundle.getString("CCLog.Information").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_UNDEFINED:
			return LocaleBundle.getString("CCLog.Undefinied").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_WARNING:
			return LocaleBundle.getString("CCLog.Warning").toUpperCase(); //$NON-NLS-1$
		default:
			return "[???]"; //$NON-NLS-1$
		}
	}

	private String getFormattedStackTrace() {
		String s = ""; //$NON-NLS-1$
		for (StackTraceElement ste : sTrace) {
			if (! ste.getClassName().endsWith("." + EXCLUSION_CLASS)) { //$NON-NLS-1$
				s += formatStackTraceElement(ste);
			}
		}
		return s.substring(0, s.length() - 1);
	}

	private String getCallerTrace() {
		for (StackTraceElement ste : sTrace) {
			if (! ste.getClassName().endsWith("." + EXCLUSION_CLASS)) { //$NON-NLS-1$
				return formatStackTraceElement(ste);
			}
		}
		return formatStackTraceElement(sTrace.get(0));
	}
	
	private String formatStackTraceElement(StackTraceElement ste) {
		if (ste.getFileName() != null && ste.getLineNumber() >= 0) {
			return "\tat " + ste.getClassName() + "." + ste.getMethodName() + " (" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$//$NON-NLS-5$
		} else {
			return "\tat " + ste.getClassName() + "." + ste.getMethodName() + "\n";   //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}
	}
}
