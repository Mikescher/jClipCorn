package de.jClipCorn.features.metadata;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.impl.*;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.lambda.Func1to1;
import de.jClipCorn.util.stream.CCStreams;

public enum MetadataSourceType implements ContinoousEnum<MetadataSourceType> {
	MEDIAINFO(0,      "MediaInfo",      Resources.ICN_MENUBAR_MEDIAINFO,    MediaInfoRunner::new),   //$NON-NLS-1$
	FFPROBE_FULL(1,   "ffprobe (full)", Resources.ICN_MENUBAR_FFPROBE_FULL, FFProbeFullRunner::new), //$NON-NLS-1$
	FFPROBE_FAST(2,   "ffprobe (fast)", Resources.ICN_MENUBAR_FFPROBE_FAST, FFProbeFastRunner::new), //$NON-NLS-1$
	MP4BOX(3,         "MP4Box",         Resources.ICN_MENUBAR_MP4BOX,       MP4BoxRunner::new);      //$NON-NLS-1$

	private final int    id;
	private final String name;
	private final Func1to1<CCMovieList, MetadataRunner> getter;
	private final MultiSizeIconRef icon;

	private static final EnumWrapper<MetadataSourceType> wrapper = new EnumWrapper<>(MEDIAINFO);

	MetadataSourceType(int val, String descr, MultiSizeIconRef srcicon, Func1to1<CCMovieList, MetadataRunner> valueGetter) {
		id     = val;
		name   = descr;
		getter = valueGetter;
		icon   = srcicon;
	}
	
	public static EnumWrapper<MetadataSourceType> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(MetadataSourceType s1, MetadataSourceType s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return CCStreams.iterate(wrapper.allValues()).map(p -> p.name).toArray(new String[0]);
	}
	
	@Override
	public String asString() {
		return name;
	}

	@Override
	public MetadataSourceType[] evalues() {
		return MetadataSourceType.values();
	}

	public MetadataRunner getRunner(CCMovieList ml) {
		return getter.invoke(ml);
	}

	public MultiSizeIconRef getIcon() {
		return icon;
	}

}
