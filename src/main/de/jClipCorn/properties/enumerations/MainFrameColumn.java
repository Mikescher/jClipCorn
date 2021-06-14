package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.mainFrame.table.ClipTableModel;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

@SuppressWarnings("nls")
public enum MainFrameColumn implements ContinoousEnum<MainFrameColumn> {
	USERSCORE(   0, "auto",       ClipTableModel.COLUMN_SCORE),
	TITLE(       1, "*,min=auto", ClipTableModel.COLUMN_TITLE),
	VIEWED(      2, "auto",       ClipTableModel.COLUMN_VIEWED),
	ZYKLUS(      3, "auto",       ClipTableModel.COLUMN_ZYKLUS),
	QUALITY(     4, "auto",       ClipTableModel.COLUMN_MEDIAINFO),
	LANGUAGE(    5, "auto",       ClipTableModel.COLUMN_LANGUAGE),
	GENRES(      6, "auto",       ClipTableModel.COLUMN_GENRE),
	PARTS(       7, "auto",       ClipTableModel.COLUMN_PARTCOUNT),
	LENGTH(      8, "auto",       ClipTableModel.COLUMN_LENGTH),
	ADDDATE(     9, "auto",       ClipTableModel.COLUMN_DATE),
	ONLINESCORE(10, "auto",       ClipTableModel.COLUMN_ONLINESCORE),
	TAGS(       11, "auto",       ClipTableModel.COLUMN_TAGS),
	FSK(        12, "auto",       ClipTableModel.COLUMN_FSK),
	FORMAT(     13, "auto",       ClipTableModel.COLUMN_FORMAT),
	YEAR(       14, "auto",       ClipTableModel.COLUMN_YEAR),
	FILESIZE(   15, "auto",       ClipTableModel.COLUMN_SIZE),
	LASTVIEWED( 16, "auto",       ClipTableModel.COLUMN_LASTVIEWED);

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
	};

	private final int id;
	public  final int ColumnIndex;
	public  final String AdjusterConfig;

	private static final EnumWrapper<MainFrameColumn> wrapper = new EnumWrapper<>(TITLE);

	MainFrameColumn(int val, String adj, int idx) {
		id = val;
		ColumnIndex = idx;
		AdjusterConfig = adj;
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
}
