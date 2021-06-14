package de.jClipCorn.features.metadata;

import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.lambda.Func0to1;
import de.jClipCorn.util.stream.CCStreams;

@SuppressWarnings("nls")
public enum MetadataSourceType implements ContinoousEnum<MetadataSourceType> {
	MEDIAINFO(0, "MediaInfo",      Resources.ICN_MENUBAR_MEDIAINFO,    () -> MetadataSources.MEDIAQUERY),
	FFPROBE(1,   "ffprobe (full)", Resources.ICN_MENUBAR_FFPROBE_FULL, () -> MetadataSources.FFPROBE_FULL),
	FFMPEG(2,    "ffprobe (fast)", Resources.ICN_MENUBAR_FFPROBE_FAST, () -> MetadataSources.FFPROBE_FAST),
	MP4BOX(3,    "MP4Box",         Resources.ICN_MENUBAR_MP4BOX,       () -> MetadataSources.MP4BOX);

	private final int id;
	private final String desc;
	private final Func0to1<MetadataSource> getter;
	private final MultiSizeIconRef icon;

	private static final EnumWrapper<MetadataSourceType> wrapper = new EnumWrapper<>(MEDIAINFO);

	private MetadataSourceType(int val, String description, MultiSizeIconRef srcicon, Func0to1<MetadataSource> valueGetter) {
		id     = val;
		desc   = description;
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

	public MetadataSource getMetadataSource() {
		return getter.invoke();
	}

	public MultiSizeIconRef getIcon() {
		return icon;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return desc;
	}

	@Override
	public String[] getList() {
		return CCStreams.iterate(values()).map(MetadataSourceType::asString).toArray(new String[0]);
	}

	@Override
	public MetadataSourceType[] evalues() {
		return MetadataSourceType.values();
	}

}
