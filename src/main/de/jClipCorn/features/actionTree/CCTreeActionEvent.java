package de.jClipCorn.features.actionTree;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.util.lambda.Func1to0;

public class CCTreeActionEvent {

	public final CCActionElement Source;
	public final String Command;
	public final ActionSource SourceType;

	public final IActionSourceObject SourceObject;

	public CCTreeActionEvent(CCActionElement src, String command, ActionSource type, IActionSourceObject obj) {
		Source = src;
		Command = command;
		SourceType = type;
		SourceObject = obj;
	}

	public void ifMovieSource(Func1to0<CCMovie> fn) {
		if (SourceObject != null && SourceObject instanceof CCMovie) fn.invoke((CCMovie)SourceObject);
	}

	public void ifSeriesSource(Func1to0<CCSeries> fn) {
		if (SourceObject != null && SourceObject instanceof CCSeries) fn.invoke((CCSeries)SourceObject);
	}

	public void ifDatabaseElementSource(Func1to0<CCDatabaseElement> fn) {
		if (SourceObject != null && SourceObject instanceof CCDatabaseElement) fn.invoke((CCDatabaseElement)SourceObject);
	}
}