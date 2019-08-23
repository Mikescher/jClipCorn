package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum MainFrameColumn implements ContinoousEnum<MainFrameColumn> {
	USERSCORE(0, ClipTableModel.COLUMN_SCORE),
	TITLE(1, ClipTableModel.COLUMN_TITLE),
	VIEWED(2, ClipTableModel.COLUMN_VIEWED),
	ZYKLUS(3, ClipTableModel.COLUMN_ZYKLUS),
	QUALITY(4, ClipTableModel.COLUMN_MEDIAINFO),
	LANGUAGE(5, ClipTableModel.COLUMN_LANGUAGE),
	GENRES(6, ClipTableModel.COLUMN_GENRE),
	PARTS(7, ClipTableModel.COLUMN_PARTCOUNT),
	LENGTH(8, ClipTableModel.COLUMN_LENGTH),
	ADDDATE(9, ClipTableModel.COLUMN_DATE),
	ONLINESCORE(10, ClipTableModel.COLUMN_ONLINESCORE),
	TAGS(11, ClipTableModel.COLUMN_TAGS),
	FSK(12, ClipTableModel.COLUMN_FSK),
	FORMAT(13, ClipTableModel.COLUMN_FORMAT),
	YEAR(14, ClipTableModel.COLUMN_YEAR),
	FILESIZE(15, ClipTableModel.COLUMN_SIZE);

	@SuppressWarnings("nls")
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
	};

	private int id;

	public final int ColumnIndex;

	private static EnumWrapper<MainFrameColumn> wrapper = new EnumWrapper<>(TITLE);

	MainFrameColumn(int val, int idx) {
		id = val;
		ColumnIndex = idx;
	}
	
	public static EnumWrapper<MainFrameColumn> getWrapper() {
		return wrapper;
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
}
