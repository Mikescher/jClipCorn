package de.jClipCorn.util.vlcquery;

public class VLCStatusPlaylistEntry
{
	public final String Uri;
	public final String Name;

	public final int Length;

	public VLCStatusPlaylistEntry(String uri, String name, int length) {
		Uri = uri;
		Name = name;
		Length = length;
	}
}
