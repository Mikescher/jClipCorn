package de.jClipCorn.gui.frames.applyPatchFrame;

import de.jClipCorn.util.filesystem.FSPath;

public class PatchExecOptions
{
	public final FSPath PatchFilepath;
	public final FSPath StateFilepath;
	public final FSPath DestinationMovies;
	public final FSPath DestinationSeries;
	public final boolean AutoDesinationSeries;
	public final FSPath DestinationTrashMovies;
	public final FSPath DestinationTrashSeries;
	public final boolean Porcelain;
	public final FSPath DataDir;

	public PatchExecOptions(FSPath patchFilepath, FSPath stateFilepath,
							FSPath destinationMovies, FSPath destinationSeries, boolean autoDestSeries,
							FSPath destinationTrashMov, FSPath destinationTrashSer,
							FSPath datadir,
							boolean porcelain)
	{
		PatchFilepath          = patchFilepath;
		StateFilepath          = stateFilepath;
		DestinationMovies      = destinationMovies;
		DestinationSeries      = destinationSeries;
		DestinationTrashMovies = destinationTrashMov;
		DestinationTrashSeries = destinationTrashSer;
		AutoDesinationSeries   = autoDestSeries;
		DataDir                = datadir;
		Porcelain              = porcelain;
	}
}
