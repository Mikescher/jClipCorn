package de.jClipCorn.features.metadata;

import de.jClipCorn.features.metadata.ffprobe.FFProbeFastRunner;
import de.jClipCorn.features.metadata.ffprobe.FFProbeFullRunner;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.metadata.mp4box.MP4BoxRunner;

public final class MetadataSources {

	public final static MetadataSource MEDIAQUERY   = new MediaQueryRunner();
	public final static MetadataSource FFPROBE_FULL = new FFProbeFullRunner();
	public final static MetadataSource FFPROBE_FAST = new FFProbeFastRunner();
	public final static MetadataSource MP4BOX       = new MP4BoxRunner();

}
