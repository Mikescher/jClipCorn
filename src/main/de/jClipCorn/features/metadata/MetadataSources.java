package de.jClipCorn.features.metadata;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.ffprobe.FFProbeFastRunner;
import de.jClipCorn.features.metadata.ffprobe.FFProbeFullRunner;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.metadata.mp4box.MP4BoxRunner;
import de.jClipCorn.util.lambda.Func1to1;

public final class MetadataSources {

	public final static Func1to1<CCMovieList, MetadataSource> MEDIAQUERY   = MediaQueryRunner::new;
	public final static Func1to1<CCMovieList, MetadataSource> FFPROBE_FULL = FFProbeFullRunner::new;
	public final static Func1to1<CCMovieList, MetadataSource> FFPROBE_FAST = FFProbeFastRunner::new;
	public final static Func1to1<CCMovieList, MetadataSource> MP4BOX       = MP4BoxRunner::new;

}
