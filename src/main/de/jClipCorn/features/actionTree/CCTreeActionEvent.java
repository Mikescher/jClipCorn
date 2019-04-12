package de.jClipCorn.features.actionTree;

import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.util.lambda.Func1to0;
import de.jClipCorn.util.listener.ActionCallbackListener;

import java.awt.*;
import java.util.List;

public class CCTreeActionEvent {

	public final Component SwingOwner;

	public final CCActionElement Source;
	public final ActionSource SourceType;

	public final List<IActionSourceObject> SourceObject;
	public final ActionCallbackListener SpecialListener;

	public CCTreeActionEvent(Component swowner, CCActionElement src, ActionSource type, List<IActionSourceObject> obj, ActionCallbackListener ucl) {
		SwingOwner = swowner;
		Source = src;
		SourceType = type;
		SourceObject = obj;
		SpecialListener = ucl;
	}

	public void ifMovieSource(Func1to0<CCMovie> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCMovie) { fn.invoke((CCMovie)sobj); return; }
		}
	}

	public void ifSeriesSource(Func1to0<CCSeries> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCSeries) { fn.invoke((CCSeries)sobj); return; }
		}
	}

	public void ifSeasonSource(Func1to0<CCSeason> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCSeason) { fn.invoke((CCSeason)sobj); return; }
		}
	}

	public void ifEpisodeSource(Func1to0<CCEpisode> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCEpisode) { fn.invoke((CCEpisode)sobj); return; }
		}
	}

	public void ifDatabaseElementSource(Func1to0<CCDatabaseElement> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCDatabaseElement) { fn.invoke((CCDatabaseElement)sobj); return; }
		}
	}

	public void ifTaggedElementSource(Func1to0<ICCTaggedElement> fn) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof ICCTaggedElement) { fn.invoke((ICCTaggedElement)sobj); return; }
		}
	}

	public void ifMovieOrSeriesSource(Func1to0<CCMovie> fn1, Func1to0<CCSeries> fn2) {
		for (IActionSourceObject sobj : SourceObject) {
			if (sobj instanceof CCMovie) { fn1.invoke((CCMovie)sobj); return; }
			if (sobj instanceof CCSeries) { fn2.invoke((CCSeries)sobj); return; }
		}
	}
}