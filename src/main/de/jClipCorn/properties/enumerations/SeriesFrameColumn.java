package de.jClipCorn.properties.enumerations;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryColumnPrototype;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

import java.util.List;

@SuppressWarnings("nls")
public enum SeriesFrameColumn implements ContinoousEnum<SeriesFrameColumn> {
	EPISODE(    0, 100 ),
	TITLE(      1, 101 ),
	VIEWED(     2, 102 ),
	LASTVIEWED( 3, 104 ),
	QUALITY(    4, 105 ),
	LANGUAGE(   5, 106 ),
	SUBTITLES(  6, 108 ),
	LENGTH(     7, 109 ),
	TAGS(       8, 110 ),
	ADDDATE(    9, 111 ),
	FORMAT(     0, 112 ),
	FILESIZE(   1, 113 );

	private final static String[] NAMES = {
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Episode"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Name"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Viewed"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.LastViewed"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Quality"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Language"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Subtitles"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Length"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Tags"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Added"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Format"),
		LocaleBundle.getString("PreviewSeriesFrame.serTable.Size"),
	};

	private final int id;
	private final int order;

	private static final EnumWrapper<SeriesFrameColumn> wrapper = new EnumWrapper<>(TITLE, v -> v.order);

	SeriesFrameColumn(int val, int order) {
		this.id = val;
		this.order = order;
	}
	
	public static EnumWrapper<SeriesFrameColumn> getWrapper() {
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

	public static int compare(SeriesFrameColumn s1, SeriesFrameColumn s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public SeriesFrameColumn[] evalues() {
		return SeriesFrameColumn.values();
	}

	public int getColumnModelIndex(List<JCCPrimaryColumnPrototype<CCEpisode, SeriesFrameColumn>> config) {
		for (int i = 0; i < config.size(); i++) {
			if (config.get(i).Identifier == this) return i;
		}
		return -1;
	}
}
