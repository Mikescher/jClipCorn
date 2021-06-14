package de.jClipCorn.gui.frames.vlcRobot;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStreams;
import de.jClipCorn.util.vlcquery.VLCStatusPlaylistEntry;

import javax.swing.*;
import java.util.List;

public class VLCPlaylistEntry {

	public enum VPEType { VLC_OLD, VLC_ACTIVE, VLC_QUEUE, VLC_QUEUE_PREEMPTIVE, JCC_QUEUE_PREEMPTIVE, JCC_QUEUE_SINGLE, JCC_QUEUE_AUTO }

	public VPEType Type;

	public ICCPlayableElement Element;
	public List<ICCPlayableElement> ElementQueue;

	public String VLCFilename;

	public String AutoDisplayString;
	public List<String> PreemptiveUris;

	public int Length;

	private VLCPlaylistEntry() { }

	public static VLCPlaylistEntry createVLCPlayList(VPEType t, VLCStatusPlaylistEntry pe) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = t;
		e.VLCFilename = pe.Name;
		e.Length = pe.Length;
		return e;
	}

	public static VLCPlaylistEntry createQueueSingle(ICCPlayableElement elem) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = VPEType.JCC_QUEUE_SINGLE;
		e.Element = elem;
		e.Length = elem.length().get()*60;
		return e;
	}

	public static VLCPlaylistEntry createClientQueuePreemptive(List<String> uris, ICCPlayableElement elem, int length) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = VPEType.JCC_QUEUE_PREEMPTIVE;
		e.Element = elem;
		e.Length = length;
		e.PreemptiveUris = uris;
		return e;
	}

	public static VLCPlaylistEntry createVLCQueuePreemptive(VLCStatusPlaylistEntry pe, ICCPlayableElement elem, int length) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = VPEType.VLC_QUEUE_PREEMPTIVE;
		e.VLCFilename = pe.Name;
		e.Element = elem;
		e.Length = length;
		return e;
	}

	public static VLCPlaylistEntry createQueueAuto(String disp, List<ICCPlayableElement> elems) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = VPEType.JCC_QUEUE_AUTO;
		e.ElementQueue = elems;
		e.Length = CCStreams.iterate(elems).sumInt(ipe -> ipe.length().get()*60);
		e.AutoDisplayString = disp;
		return e;
	}

	public boolean IsPreemptiveAndEquals(String otheruri) {
		if (Type != VPEType.JCC_QUEUE_PREEMPTIVE) return false;

		if (PreemptiveUris == null) return false;
		for (var thisuri : PreemptiveUris) {
			if (Str.equals(thisuri.toLowerCase(), otheruri.toLowerCase())) return true;
		}
		return false;
	}

	public Icon getIcon() {

		switch (Type)
		{

			case VLC_QUEUE:
			case VLC_OLD:
				return Resources.ICN_MENUBAR_PLAY.get16x16();

			case VLC_ACTIVE:
				return Resources.ICN_MENUBAR_PLAY_ACTIVE.get16x16();

			case VLC_QUEUE_PREEMPTIVE:
			case JCC_QUEUE_PREEMPTIVE:
				return Resources.ICN_MENUBAR_PLAY_QUEUED.get16x16();

			case JCC_QUEUE_SINGLE:
				if (Element instanceof CCMovie)   return Resources.ICN_TABLE_MOVIE.get();
				if (Element instanceof CCEpisode) return Resources.ICN_TABLE_EPISODES.get();

			case JCC_QUEUE_AUTO:
				return Resources.ICN_MENUBAR_VLCROBOT.get16x16();
		}

		return null;
	}

	public String getText() {
		switch (Type)
		{
			case VLC_OLD:
			case VLC_ACTIVE:
			case VLC_QUEUE:
				return VLCFilename;

			case JCC_QUEUE_SINGLE:
			case JCC_QUEUE_PREEMPTIVE:
			case VLC_QUEUE_PREEMPTIVE:
				if (Element instanceof CCEpisode) return ((CCEpisode)Element).getShortQualifiedTitle();
				return Element.title().get();

			case JCC_QUEUE_AUTO:
				return "[AUTO] " + AutoDisplayString + " [" + ElementQueue.size() + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return ""; //$NON-NLS-1$
	}

	public String getLengthText() {
		if (Type == VPEType.JCC_QUEUE_AUTO) return Str.Empty;
		if (Length < 0) return "-"; //$NON-NLS-1$
		return TimeIntervallFormatter.formatLengthSeconds(Length);
	}

	public boolean isEqual(VLCPlaylistEntry other) {
		if (Type != other.Type) return false;
		if (Element != other.Element) return false;
		if (!Str.equals(VLCFilename, other.VLCFilename)) return false;
		if (!Str.equals(AutoDisplayString,other.AutoDisplayString)) return false;
		if (Length != other.Length) return false;

		if (!(ElementQueue == null && other.ElementQueue == null))
		{
			if (ElementQueue == null || other.ElementQueue == null) return false;
			if (ElementQueue.size() != other.ElementQueue.size()) return false;
			for (int i=0; i < ElementQueue.size(); i++) {
				if (ElementQueue.get(i) != other.ElementQueue.get(i)) return false;
			}
		}

		if (!(PreemptiveUris == null && other.PreemptiveUris == null))
		{
			if (PreemptiveUris == null || other.PreemptiveUris == null) return false;
			if (PreemptiveUris.size() != other.PreemptiveUris.size()) return false;
			for (int i=0; i < PreemptiveUris.size(); i++) {
				if (!Str.equals(PreemptiveUris.get(i), other.PreemptiveUris.get(i))) return false;
			}
		}

		return true;
	}
}
