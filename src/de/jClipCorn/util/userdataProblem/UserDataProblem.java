package de.jClipCorn.util.userdataProblem;

import de.jClipCorn.gui.localization.LocaleBundle;

public class UserDataProblem {
	public final static int PROBLEM_NO_PATH = 1;
	public final static int PROBLEM_HOLE_IN_PATH = 2;
	public final static int PROBLEM_EMPTY_TITLE = 3;
	public final static int PROBLEM_ZYKLUSID_IS_SET = 4; // Zyklus is empty but ZyklusID != -1
	public final static int PROBLEM_ZYKLUSID_IS_ZERO = 5; // Zyklus is set but ZyklusID is ZERO
	public final static int PROBLEM_NO_COVER_SET = 6;
	public final static int PROBLEM_INVALID_LENGTH = 7;
	public final static int PROBLEM_DATE_TOO_LESS = 8;
	public final static int PROBLEM_INVALID_ONLINESCORE = 9;
	public final static int PROBLEM_FSK_NOT_SET = 10;
	public final static int PROBLEM_INVALID_YEAR = 11;
	public final static int PROBLEM_INVALID_FILESIZE = 12;
	public final static int PROBLEM_EXTENSION_UNEQUALS_FILENAME = 13;
	public final static int PROBLEM_NO_GENRE_SET = 14;
	public final static int PROBLEM_HOLE_IN_GENRE = 15;
	public final static int PROBLEM_EPISODENUMBER_ALREADY_EXISTS = 16;
	
	private final int pid; // Problem ID
	
	public UserDataProblem(int problemID) {
		this.pid = problemID;
	}
	
	public String getText() {
		return LocaleBundle.getString("UserDataErrors.ERROR_" + getPID()); //$NON-NLS-1$
	}
	
	public int getPID() {
		return pid;
	}
}
