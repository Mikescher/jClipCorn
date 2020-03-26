package de.jClipCorn.util.vlcquery;

import de.jClipCorn.util.Str;

import java.awt.*;
import java.util.List;

public class VLCStatus
{
	// only if current entry position is greater {TIME_STARTING} seconds we will
	// remember the current window position for later reference
	private static final int TIME_STARTING = 10;

	// if the current entry is {TIME_NEARLY_FIN} seconds before it's end we
	// enqueue the next entry from the client queue
	private static final int TIME_NEARLY_FIN = 15;

	// if we transition from the last playlist entry with more than {TIME_FINISHED} seconds
	// remaining to the first playlist entry with less than {TIME_STARTING} seconds playtime
	// we interpret this as a manual skip and enqueue the next entry from the client queue
	private static final int TIME_FINISHED =  5;

	public enum PositionArea { STARTING, MIDDLE, NEARLY_FINISHED, FINISHED, NONE }

	public final VLCPlayerStatus Status;

	public final int CurrentTime;
	public final int CurrentLength;

	public final PositionArea Position;

	public final int Volume;
	public final boolean Random;
	public final boolean Loop;
	public final boolean Repeat;
	public final boolean Fullscreen;
	public final Rectangle WindowRect;

	public final String CurrentFilename;
	public final String CurrentTitle;

	public List<VLCStatusPlaylistEntry> Playlist;
	public VLCStatusPlaylistEntry ActiveEntry;

	public VLCStatus(VLCPlayerStatus status, int currentTime, int currentLength, String currentFilename, String currentTitle,
					 List<VLCStatusPlaylistEntry> playlist, VLCStatusPlaylistEntry activeEntry,
					 boolean fullscreen, int volume, boolean random, boolean loop, boolean repeat,
					 Rectangle windowRect)
	{
		Status          = status;
		CurrentTime     = currentTime;
		CurrentLength   = currentLength;
		CurrentFilename = currentFilename;
		CurrentTitle    = currentTitle;
		Playlist        = playlist;
		ActiveEntry     = activeEntry;
		Fullscreen      = fullscreen;
		Volume          = volume;
		Random          = random;
		Loop            = loop;
		Repeat          = repeat;
		WindowRect      = windowRect;
		Position        = getPositionArea(status, currentTime, currentLength);
	}

	private PositionArea getPositionArea(VLCPlayerStatus status, int currentTime, int currentLength)
	{
		if (status != VLCPlayerStatus.PLAYING && status != VLCPlayerStatus.PAUSED) return PositionArea.NONE;

		if (currentLength <= 0) return PositionArea.NONE;

		if (CurrentTime >= (CurrentLength - TIME_FINISHED)) return PositionArea.FINISHED;
		if (CurrentTime >= (CurrentLength - TIME_NEARLY_FIN)) return PositionArea.NEARLY_FINISHED;
		if (CurrentTime <= TIME_STARTING) return PositionArea.STARTING;

		return PositionArea.MIDDLE;
	}

	public boolean isPlayingLastPlaylistEntry() {
		return ActiveEntry != null && Playlist.size() > 0 && Playlist.get(Playlist.size()-1) == ActiveEntry;
	}

	public static boolean isDiff(VLCStatus sold, VLCStatus snew)
	{
		if (sold.Status != snew.Status) return true;

		if (!Str.equals(sold.CurrentFilename, snew.CurrentFilename)) return true;
		if (sold.CurrentLength != snew.CurrentLength) return true;

		if (sold.Position != snew.Position) return true;

		if (sold.Fullscreen != snew.Fullscreen) return true;

		if (sold.WindowRect != null && snew.WindowRect != null && !rectEq(sold.WindowRect, snew.WindowRect)) return true;

		if (sold.Loop != snew.Loop) return true;
		if (sold.Repeat != snew.Repeat) return true;
		if (sold.Random != snew.Random) return true;

		if (sold.Volume != snew.Volume) return true;

		return false;
	}

	private static boolean rectEq(Rectangle a, Rectangle b)
	{
		if (a == null && b == null) return true;
		if (a == null) return false;
		if (b == null) return false;

		return a.equals(b);
	}

	@SuppressWarnings("nls")
	public static String formatShortDiff(VLCStatus sold, VLCStatus snew)
	{
		if (sold.Status != snew.Status) return Str.format("[{0}] --> [{1}]", sold.Status, snew.Status);

		if (!Str.equals(sold.CurrentFilename, snew.CurrentFilename)) return "File changed (Filename)";
		if (sold.CurrentLength != snew.CurrentLength) return "File changed (Length)";

		if (sold.Position != snew.Position) return Str.format("[Pos.{0}] --> [Pos.{1}]", sold.Position, snew.Position);

		if (sold.Fullscreen != snew.Fullscreen) return Str.format("Fullscreen: \"{0}\" -> \"{1}\"", sold.Fullscreen, snew.Fullscreen);

		if (!rectEq(sold.WindowRect, snew.WindowRect)) return "Window position changed";

		if (sold.Loop   != snew.Loop)   return Str.format("VLC.Loop: \"{0}\" -> \"{1}\"", sold.Loop, snew.Loop);
		if (sold.Repeat != snew.Repeat) return Str.format("VLC.Repeat: \"{0}\" -> \"{1}\"", sold.Repeat, snew.Repeat);
		if (sold.Random != snew.Random) return Str.format("VLC.Random: \"{0}\" -> \"{1}\"", sold.Random, snew.Random);

		if (sold.Volume != snew.Volume) return Str.format("Volume: {0} -> {1}", sold.Volume, snew.Volume);

		return Str.Empty;
	}

	@SuppressWarnings({"nls", "StringBufferReplaceableByString"})
	public String format()
	{
		StringBuilder b = new StringBuilder();

		b.append("Status          := ").append(Status).append("\n");
		b.append("CurrentFilename := ").append(CurrentFilename).append("\n");
		b.append("Position        := ").append(CurrentTime).append(" / ").append(CurrentLength).append(" (").append(Position).append(")").append("\n");
		b.append("\n");
		b.append("Fullscreen      := ").append(Fullscreen).append("\n");
		b.append("\n");
		b.append("Window          := ").append((WindowRect==null) ? "NULL" : Str.format("[{0,number,#};{1,number,#}]:[{2,number,#}x{3,number,#}]", WindowRect.x, WindowRect.y, WindowRect.width, WindowRect.height)).append("\n");
		b.append("\n");
		b.append("Loop            := ").append(Loop).append("\n");
		b.append("Repeat          := ").append(Repeat).append("\n");
		b.append("Random          := ").append(Random).append("\n");
		b.append("\n");
		b.append("Volume          := ").append(Volume).append("\n");

		return b.toString();
	}
}
