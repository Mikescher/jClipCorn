package de.jClipCorn.features.metadata.ffprobe;

public class FFProbeFastRunner extends FFProbeRunner {
	@Override
	protected String[] getArgs(String filename) {
		return new String[]
		{
			"-i", filename,          //$NON-NLS-1$
			"-print_format", "json", //$NON-NLS-1$ //$NON-NLS-2$
			"-loglevel", "fatal",    //$NON-NLS-1$ //$NON-NLS-2$
			"-show_streams",         //$NON-NLS-1$
			"-show_format",          //$NON-NLS-1$
		};
	}
}
