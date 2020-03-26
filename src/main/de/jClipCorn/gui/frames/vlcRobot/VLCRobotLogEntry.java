package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.vlcquery.VLCStatus;

import java.awt.*;

public class VLCRobotLogEntry {

	public final int Index;

	public final CCDateTime Timestamp;

	public final VLCStatus StatusOld;
	public final VLCStatus StatusNew;

	public final ICCPlayableElement NewEntry;

	public final Rectangle WindowRectNew;
	public final Rectangle WindowRectOld;

	public final String Reason;

	public VLCRobotLogEntry(int idx, CCDateTime ts, VLCStatus sold, VLCStatus snew)
	{
		Index         = idx;
		Timestamp     = ts;
		StatusOld     = sold;
		StatusNew     = snew;
		NewEntry      = null;
		WindowRectNew = null;
		WindowRectOld = null;
		Reason        = "StateChange"; //$NON-NLS-1$
	}

	public VLCRobotLogEntry(int idx, CCDateTime ts, ICCPlayableElement e, String reason)
	{
		Index         = idx;
		Timestamp     = ts;
		StatusOld     = null;
		StatusNew     = null;
		NewEntry      = e;
		WindowRectNew = null;
		WindowRectOld = null;
		Reason        = reason;
	}

	public VLCRobotLogEntry(int idx, CCDateTime ts, Rectangle rold, Rectangle rnew, String reason)
	{
		Index         = idx;
		Timestamp     = ts;
		StatusOld     = null;
		StatusNew     = null;
		NewEntry      = null;
		WindowRectOld = rold;
		WindowRectNew = rnew;
		Reason        = reason;
	}

	@SuppressWarnings("nls")
	public String format()
	{
		if (StatusOld != null && StatusNew != null) return VLCStatus.formatShortDiff(StatusOld, StatusNew);
		if (NewEntry != null) return Str.format("''{0}'' [{1}]", NewEntry.getTitle(), Reason);
		if (WindowRectOld != null && WindowRectNew != null) return Str.format("UpdateWindowPos [{0}]", Reason);

		return "???";
	}

	@SuppressWarnings("nls")
	public String getEventType() {
		if (StatusOld != null && StatusNew != null) return "VLCStatus";
		if (NewEntry != null) return "Play";
		if (WindowRectOld != null && WindowRectNew != null) return "UpdateWindowPos";

		return "???";
	}
}
