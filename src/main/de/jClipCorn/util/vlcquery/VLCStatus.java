package de.jClipCorn.util.vlcquery;

import java.awt.*;
import java.util.List;

public class VLCStatus
{
	public final VLCPlayerStatus Status;

	public final int CurrentTime;
	public final int CurrentLength;

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
	}

	public boolean isPlayingLastPlaylistEntry() {
		return ActiveEntry != null && Playlist.size() > 0 && Playlist.get(Playlist.size()-1) == ActiveEntry;
	}

	public boolean isEntryNearlyFinished(int delta) {
		return CurrentTime >= (CurrentLength - delta);
	}

	public boolean isEntryJustStarted(int delta) {
		return CurrentTime < delta;
	}

}
