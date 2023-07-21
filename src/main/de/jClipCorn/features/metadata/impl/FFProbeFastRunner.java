package de.jClipCorn.features.metadata.impl;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.util.filesystem.FSPath;

public class FFProbeFastRunner extends FFProbeRunner
{
	public FFProbeFastRunner(CCMovieList ml)
	{
		super(ml);
	}

	@Override
	@SuppressWarnings("nls")
	protected String[] getArgs(FSPath filename)
	{
		return new String[]
		{
			"-i", filename.toAbsolutePathString(),
			"-print_format", "json",
			"-loglevel", "fatal",
			"-show_streams",
			"-show_format",
		};
	}

	@Override
	public MetadataSourceType getSourceType()
	{
		return MetadataSourceType.FFPROBE_FAST;
	}
}
