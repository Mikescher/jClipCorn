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

	public enum VPEType { VLC_OLD, VLC_ACTIVE, VLC_QUEUE, JCC_QUEUE_SINGLE, JCC_QUEUE_AUTO }

	public VPEType Type;

	public ICCPlayableElement Element;
	public List<ICCPlayableElement> ElementQueue;

	public String VLCFilename;

	public String AutoDisplayString;

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
		e.Length = elem.getLength();
		return e;
	}

	public static VLCPlaylistEntry createQueueAuto(List<ICCPlayableElement> elems) {
		VLCPlaylistEntry e = new VLCPlaylistEntry();
		e.Type = VPEType.JCC_QUEUE_SINGLE;
		e.ElementQueue = elems;
		e.Length = CCStreams.iterate(elems).sumInt(ICCPlayableElement::getLength);
		return e;
	}

	public Icon getIcon() {

		switch (Type)
		{

			case VLC_QUEUE:
			case VLC_OLD:
				return Resources.ICN_MENUBAR_HIDDENPLAY.get16x16();

			case VLC_ACTIVE:
				return Resources.ICN_MENUBAR_PLAY.get16x16();

			case JCC_QUEUE_SINGLE:
				if (Element instanceof CCMovie)   return Resources.ICN_TABLE_MOVIE.get();
				if (Element instanceof CCEpisode) return Resources.ICN_TABLE_MOVIE.get();

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
				return Element.getTitle();

			case JCC_QUEUE_AUTO:
				return AutoDisplayString;
		}
		return "";
	}

	public String getLengthText() {
		if (Type == VPEType.JCC_QUEUE_AUTO) return Str.Empty;
		return TimeIntervallFormatter.formatLengthSeconds(Length);
	}
}
