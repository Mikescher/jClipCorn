package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.vlcquery.VLCStatus;

import java.awt.*;

public class VLCRobotLogEntry {

	private enum RLogType { VLCStatus, SkipUpdateCycle, UpdateWindowPos, Play, QueuePreemptive }

	public final RLogType LogType;

	public final int Index;

	public final CCDateTime Timestamp;

	public final VLCStatus StatusOld;
	public final VLCStatus StatusNew;

	public final ICCPlayableElement NewEntry;

	public final Rectangle WindowRectNew;
	public final Rectangle WindowRectOld;

	public final int SkipCount;

	public final String Reason;

	private VLCRobotLogEntry(RLogType t, int i, CCDateTime ts, VLCStatus so, VLCStatus sn, ICCPlayableElement e, Rectangle ro, Rectangle rn, int sc, String r)
	{
		LogType       = t;
		Index         = i;
		Timestamp     = ts;
		StatusOld     = so;
		StatusNew     = sn;
		NewEntry      = e;
		WindowRectNew = rn;
		WindowRectOld = ro;
		SkipCount     = sc;
		Reason        = r;
	}

	public static VLCRobotLogEntry StateChange(int idx, CCDateTime ts, VLCStatus sold, VLCStatus snew) {
		return new VLCRobotLogEntry(
				RLogType.VLCStatus,
				idx,
				ts,
				sold,
				snew,
				null,
				null,
				null,
				-1,
				"StateChange"
		);
	}

	public static VLCRobotLogEntry Play(int idx, CCDateTime ts, ICCPlayableElement e, String reason) {
		return new VLCRobotLogEntry(
				RLogType.Play,
				idx,
				ts,
				null,
				null,
				e,
				null,
				null,
				-1,
				reason
		);
	}

	public static VLCRobotLogEntry QueuePreemptive(int idx, CCDateTime ts, ICCPlayableElement e, String reason) {
		return new VLCRobotLogEntry(
				RLogType.QueuePreemptive,
				idx,
				ts,
				null,
				null,
				e,
				null,
				null,
				-1,
				reason
		);
	}

	public static VLCRobotLogEntry UpdateWindowPos(int idx, CCDateTime ts, Rectangle rold, Rectangle rnew, String reason) {
		return new VLCRobotLogEntry(
				RLogType.UpdateWindowPos,
				idx,
				ts,
				null,
				null,
				null,
				rold,
				rnew,
				-1,
				reason
		);
	}

	public static VLCRobotLogEntry TickSkipped(int idx, CCDateTime ts, VLCStatus sold, VLCStatus snew, int skipCount) {
		return new VLCRobotLogEntry(
				RLogType.SkipUpdateCycle,
				idx,
				ts,
				sold,
				snew,
				null,
				null,
				null,
				skipCount,
				"TickSkipped"
		);
	}

	@SuppressWarnings("nls")
	public String format()
	{
		switch (this.LogType) {
			case VLCStatus:       return VLCStatus.formatShortDiff(StatusOld, StatusNew);
			case SkipUpdateCycle: return "Skipped a single refresh cycle (" + SkipCount + ")";
			case UpdateWindowPos: return Str.format("UpdateWindowPos [{0}]", Reason);
			case Play:            return Str.format("''{0}'' [{1}]", NewEntry.getTitle(), Reason);
			case QueuePreemptive: return Str.format("''{0}'' [{1}] (queued)", NewEntry.getTitle(), Reason);
		}

		return "???";
	}

	@SuppressWarnings("nls")
	public String getEventType() {
		switch (this.LogType) {
			case VLCStatus:       return "VLCStatus";
			case SkipUpdateCycle: return "SkipUpdateCycle";
			case UpdateWindowPos: return "UpdateWindowPos";
			case Play:            return "Play";
			case QueuePreemptive: return "Queued";
		}

		return "???";
	}
}
