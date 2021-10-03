package de.jClipCorn.properties.enumerations;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.guiComponents.jCCPrimaryTable.JCCPrimaryColumnPrototype;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

import java.util.List;

@SuppressWarnings("nls")
public enum MainFrameColumn implements ContinoousEnum<MainFrameColumn> {
	USERSCORE(    0, 100 ),
	TITLE(        1, 101 ),
	VIEWED(       2, 102 ),
	ZYKLUS(       3, 104 ),
	QUALITY(      4, 105 ),
	LANGUAGE(     5, 106 ),
	GENRES(       6, 108 ),
	PARTS(        7, 109 ),
	LENGTH(       8, 110 ),
	ADDDATE(      9, 111 ),
	ONLINESCORE( 10, 112 ),
	TAGS(        11, 113 ),
	FSK(         12, 114 ),
	FORMAT(      13, 115 ),
	YEAR(        14, 116 ),
	FILESIZE(    15, 117 ),
	LASTVIEWED(  16, 103 ),
	SUBTITLES(   17, 107 );

	private final static String[] NAMES = {
		LocaleBundle.getString("ClipTableModel.Score"),
		LocaleBundle.getString("ClipTableModel.Title"),
		LocaleBundle.getString("ClipTableModel.Viewed"),
		LocaleBundle.getString("ClipTableModel.Zyklus"),
		LocaleBundle.getString("ClipTableModel.Quality"),
		LocaleBundle.getString("ClipTableModel.Language"),
		LocaleBundle.getString("ClipTableModel.Genre"),
		LocaleBundle.getString("ClipTableModel.Parts"),
		LocaleBundle.getString("ClipTableModel.Length"),
		LocaleBundle.getString("ClipTableModel.Added"),
		LocaleBundle.getString("ClipTableModel.OnlineScore"),
		LocaleBundle.getString("ClipTableModel.Tags"),
		LocaleBundle.getString("ClipTableModel.FSK"),
		LocaleBundle.getString("ClipTableModel.Format"),
		LocaleBundle.getString("ClipTableModel.Year"),
		LocaleBundle.getString("ClipTableModel.Size"),
		LocaleBundle.getString("ClipTableModel.LastViewed"),
		LocaleBundle.getString("ClipTableModel.Subtitles"),
	};

	private final int id;
	private final int order;

	private static final EnumWrapper<MainFrameColumn> wrapper = new EnumWrapper<>(TITLE, v -> v.order);

	MainFrameColumn(int val, int order) {
		this.id = val;
		this.order = order;
	}
	
	public static EnumWrapper<MainFrameColumn> getWrapper() {
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

	public static int compare(MainFrameColumn s1, MainFrameColumn s2) {
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
	public MainFrameColumn[] evalues() {
		return MainFrameColumn.values();
	}

	public int getColumnModelIndex(List<JCCPrimaryColumnPrototype<CCDatabaseElement, MainFrameColumn>> config) {
		for (int i = 0; i < config.size(); i++) {
			if (config.get(i).Identifier == this) return i;
		}
		return -1;
	}
}
