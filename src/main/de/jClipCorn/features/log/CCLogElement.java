package de.jClipCorn.features.log;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CCLogElement {
	public final static int FORMAT_LEVEL_PREVIEW = -1;
	public final static int FORMAT_LEVEL_SHORT = 0;
	public final static int FORMAT_LEVEL_MID = 1;
	public final static int FORMAT_LEVEL_FULL = 2;
	public final static int FORMAT_LEVEL_LIST_FULL = 3;

	private final static String EXCLUSION_CLASS = "CCLog"; //$NON-NLS-1$
	private final static String FATAL_ERROR_MSG = LocaleBundle.getString("LogMessage.ErrorTerminating"); //$NON-NLS-1$

	private final String text;
	private final CCLogType type;
	private final List<StackTraceElement> sTrace;
	private final CCTime time;

	private int count;

	public CCLogElement() {
		type = CCLogType.LOG_ELEM_UNDEFINED;
		text = ""; //$NON-NLS-1$
		time = new CCTime();
		count = 1;
		
		sTrace = null;
	}

	public CCLogElement(String txt, CCLogType tp, StackTraceElement[] stackTrace) {
		text = txt;
		type = tp;
		time = new CCTime();

		sTrace = new ArrayList<>(Arrays.asList(stackTrace));
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
			return getTypeStringRepresentation() + " (" + time.toStringUINormal() + "): " + text + "\n" + getFormattedStackTrace() + '\n'; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		String pref = count > 1 ? "[" + count + "x] " : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
		switch (type) {
		case LOG_ELEM_FATALERROR:
			return pref + LocaleBundle.getString("CCLog.FatalError").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_ERROR:
			return pref + LocaleBundle.getString("CCLog.Error").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_INFORMATION:
			return pref + LocaleBundle.getString("CCLog.Information").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_UNDEFINED:
			return pref + LocaleBundle.getString("CCLog.Undefinied").toUpperCase(); //$NON-NLS-1$
		case LOG_ELEM_WARNING:
			return pref + LocaleBundle.getString("CCLog.Warning").toUpperCase(); //$NON-NLS-1$
		default:
			return pref + "[???]"; //$NON-NLS-1$
		}
	}

	private String getFormattedStackTrace() {
		StringBuilder stbuilder = new StringBuilder();
		for (StackTraceElement ste : sTrace) {
			if (! ste.getClassName().endsWith("." + EXCLUSION_CLASS)) { //$NON-NLS-1$
				stbuilder.append(formatStackTraceElement(ste));
			}
		}
		
		if (stbuilder.length() == 0) return ""; //$NON-NLS-1$
		
		stbuilder.deleteCharAt(stbuilder.length() - 1);
		
		return stbuilder.toString();
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
	
	public boolean isType(CCLogType t) {
		return type == t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sTrace == null) ? 0 : sTrace.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		CCLogElement other = (CCLogElement) obj;
		
		if (sTrace == null) {
			if (other.sTrace != null) {
				return false;
			}
		} else if (!sTrace.equals(other.sTrace)) {
			return false;
		}
		
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		
		if (type != other.type) {
			return false;
		}
		
		return true;
	}

	public void inc() {
		count++;
	}

	public int getCount() {
		return count;
	}
}
