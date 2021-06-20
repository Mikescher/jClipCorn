package de.jClipCorn.gui.frames.applyPatchFrame;

public class PatchExecOptions
{
	public final String PatchFilepath;
	public final String StateFilepath;
	public final String DestinationMovies;
	public final String DestinationSeries;
	public final String DestinationTrashMovies;
	public final String DestinationTrashSeries;
	public final boolean Porcelain;
	public final String DataDir;

	public PatchExecOptions(String patchFilepath, String stateFilepath,
							String destinationMovies, String destinationSeries,
							String destinationTrashMov, String destinationTrashSer,
							String datadir,
							boolean porcelain)
	{
		PatchFilepath          = patchFilepath;
		StateFilepath          = stateFilepath;
		DestinationMovies      = destinationMovies;
		DestinationSeries      = destinationSeries;
		DestinationTrashMovies = destinationTrashMov;
		DestinationTrashSeries = destinationTrashSer;
		DataDir                = datadir;
		Porcelain              = porcelain;
	}
}