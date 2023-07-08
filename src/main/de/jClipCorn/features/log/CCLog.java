package de.jClipCorn.features.log;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.sqlwrapper.StatementType;

import java.util.List;

public class CCLog {

	public static void initUnitTestMode() {
		CCLogInternal.initUnitTestMode();
	}
	
	public static void setPath(String p) {
		CCLogInternal.setPath(p);
	}

	public static void setCCProps(CCProperties ccprops) {
		CCLogInternal.setCCProps(ccprops);
	}

	public static void addInformation(Throwable e) {
		CCLogInternal.add(format(e), CCLogType.LOG_ELEM_INFORMATION, e.getStackTrace());
	}
	
	public static void addInformation(String e) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_INFORMATION, new Throwable().getStackTrace());
	}

	public static void addWarning(String e) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_WARNING, new Throwable().getStackTrace());
	}

	public static void addWarning(Throwable e) {
		CCLogInternal.add(format(e), CCLogType.LOG_ELEM_WARNING, e.getStackTrace());
	}

	public static void addWarning(String s, Throwable e) {
		CCLogInternal.add(s + '\n' + "\t caused by " + format(e), CCLogType.LOG_ELEM_WARNING, e.getStackTrace()); //$NON-NLS-1$
	}

	public static void addError(String e) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_ERROR, new Throwable().getStackTrace());
	}
	
	public static void addError(String e, StackTraceElement[] trace) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_ERROR, trace);
	}

	public static void addError(Throwable e) {
		CCLogInternal.add(format(e), CCLogType.LOG_ELEM_ERROR, e.getStackTrace());
	}

	public static void addError(String s, Throwable e) {
		CCLogInternal.add(s + '\n' + "\t caused by " + format(e), CCLogType.LOG_ELEM_ERROR, e.getStackTrace()); //$NON-NLS-1$
	}
	
	public static void addFatalError(Throwable e) {
		CCLogInternal.add(format(e), CCLogType.LOG_ELEM_FATALERROR, e.getStackTrace());
	}
	
	public static void addFatalError(String e) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_FATALERROR, new Throwable().getStackTrace());
	}
	
	public static void addFatalError(String e, StackTraceElement[] trace) {
		CCLogInternal.add(e, CCLogType.LOG_ELEM_FATALERROR, trace);
	}
	
	public static void addFatalError(String s, Throwable e) {
		CCLogInternal.add(s + '\n' + "\t caused by " + format(e), CCLogType.LOG_ELEM_FATALERROR, e.getStackTrace()); //$NON-NLS-1$
	}

	public static void addUndefinied(Thread thread, Throwable e) {
		CCLogInternal.add('[' + thread.toString() + ']' + ' ' + format(e), CCLogType.LOG_ELEM_UNDEFINED, e.getStackTrace());
	}

	public static void addUndefinied(String msg) {
		CCLogInternal.add('[' + Thread.currentThread().toString() + ']' + ' ' + msg, CCLogType.LOG_ELEM_UNDEFINED, new Exception().getStackTrace());
	}

	public static void addMovieListChangeEvent(CCDatabaseElement root, ICCDatabaseStructureElement el, String[] p) {
		CCLogInternal.addChange(root.getType().asString(), root.getLocalID(), el.getClass().getSimpleName(), el.getLocalID(), p);
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
		CCLogInternal.addDebug(msg);
	}

	public static void addSQL(String method, StatementType stype, String sql) {
		CCLogInternal.addSQL(method, stype, sql);
	}

	public static void addChangeListener (CCLogChangedListener lst) {
		CCLogInternal.addChangeListener(lst);
	}

	public static void removeChangeListener (CCLogChangedListener lst) {
		CCLogInternal.removeChangeListener(lst);
	}

	public static void setChangedFlag() {
		CCLogInternal.setChangedFlag();
	}
	
	public static boolean isChanged() {
		return CCLogInternal.isChanged();
	}
	
	public static boolean hasErrors() {
		return CCLogInternal.hasErrors();
	}

	public static boolean hasWarnings() {
		return CCLogInternal.hasWarnings();
	}
	
	public static boolean hasInformations() {
		return CCLogInternal.hasInformations();
	}

	public static boolean hasUndefinieds() {
		return CCLogInternal.hasUndefinieds();
	}
	
	public static CCLogType getHighestType() {
		if (CCLogInternal.hasErrors()) {
			return CCLogType.LOG_ELEM_ERROR;
		}
		
		if (CCLogInternal.hasUndefinieds()) {
			return CCLogType.LOG_ELEM_UNDEFINED;
		}
		
		if (CCLogInternal.hasWarnings()) {
			return CCLogType.LOG_ELEM_WARNING;
		}
		
		return CCLogType.LOG_ELEM_INFORMATION;
	}

	public static int getCount(CCLogType lt) {
		return CCLogInternal.getCount(lt);
	}

	public static int getSQLCount() {
		return CCLogInternal.getSQLCount();
	}

	public static int getChangeCount() {
		return CCLogInternal.getChangeCount();
	}

	public static void save() {
		CCLogInternal.save();
	}

	public static CCLogElement getElement(CCLogType type, int idx) {
		return CCLogInternal.getElement(type, idx);
	}

	public static void setAllWatched() {
		CCLogInternal.setAllWatched();
	}

	public static boolean hasUnwatchedErrorsOrUndef() {
		return CCLogInternal.hasUnwatchedErrorsOrUndef();
	}

	public static boolean hasUnwatched(CCLogType type) {
		return CCLogInternal.hasUnwatched(type);
	}

	public static CCSQLLogElement getSQLElement(int idx) {
		return CCLogInternal.getSQLElement(idx);
	}

	public static boolean isUnitTest() {
		return CCLogInternal.isUnitTest();
	}

	private static String format(Throwable e)
	{
		if (e == null) return "NULL"; //$NON-NLS-1$

		StringBuilder str = new StringBuilder(e.toString());
		var c = e.getCause();

		while (c != null) { str.append("\n\n").append("-------[ CAUSED BY ]-------").append("\n\n").append(c.toString()); c = c.getCause(); } //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return str.toString();
	}

	public static List<CCChangeLogElement> getChangeElements() {
		return CCLogInternal.getChangeLogElementsCopy();
	}

	public static void disableChangeEvents() {
		CCLogInternal.setChangeEventsEnabled(false);
	}

	public static void reenableChangeEvents() {
		CCLogInternal.setChangeEventsEnabled(true);
	}
}
