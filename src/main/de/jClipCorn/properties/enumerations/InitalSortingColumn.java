package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum InitalSortingColumn implements ContinoousEnum<InitalSortingColumn> {
	LOCALID(0), 
	TITLE(1), 
	ADDDATE(2);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("ClipTableModel.LocalID"),
		LocaleBundle.getString("ClipTableModel.Title"),
		LocaleBundle.getString("ClipTableModel.Added"),
	};
	
	private int id;

	private static final EnumWrapper<InitalSortingColumn> wrapper = new EnumWrapper<>(LOCALID);

	InitalSortingColumn(int val) {
		id = val;
	}
	
	public static EnumWrapper<InitalSortingColumn> getWrapper() {
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

	public static int compare(InitalSortingColumn s1, InitalSortingColumn s2) {
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
	public InitalSortingColumn[] evalues() {
		return InitalSortingColumn.values();
	}
}
