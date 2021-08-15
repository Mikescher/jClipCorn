package de.jClipCorn.features.metadata.ffprobe;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.filesystem.FSPath;

public class FFProbeFastRunner extends FFProbeRunner {

	public FFProbeFastRunner(CCMovieList ml) {
		super(ml);
	}

	@Override
	protected String[] getArgs(FSPath filename) {
		return new String[]
		{
			"-i", filename.toAbsolutePathString(), //$NON-NLS-1$
			"-print_format", "json",               //$NON-NLS-1$ //$NON-NLS-2$
			"-loglevel", "fatal",                  //$NON-NLS-1$ //$NON-NLS-2$
			"-show_streams",                       //$NON-NLS-1$
			"-show_format",                        //$NON-NLS-1$
		};
	}
}
