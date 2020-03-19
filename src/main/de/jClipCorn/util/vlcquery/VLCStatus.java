package de.jClipCorn.util.vlcquery;

import java.util.List;

public class VLCStatus
{
	public enum VLCPlayerStatus { PLAYING, STOPPED, DISABLED, NOT_RUNNING, ERROR }

	public final VLCPlayerStatus Status;

	public final int CurrentTime;
	public final int CurrentLength;

	public final String CurrentFilename;
	public final String CurrentTitle;

	public List<VLCStatusPlaylistEntry> Playlist;
	public VLCStatusPlaylistEntry ActiveEntry;

	public VLCStatus(VLCPlayerStatus status, int currentTime, int currentLength, String currentFilename, String currentTitle, List<VLCStatusPlaylistEntry> playlist, VLCStatusPlaylistEntry activeEntry) {
		Status = status;
		CurrentTime = currentTime;
		CurrentLength = currentLength;
		CurrentFilename = currentFilename;
		CurrentTitle = currentTitle;
		Playlist = playlist;
		ActiveEntry = activeEntry;
	}
}
