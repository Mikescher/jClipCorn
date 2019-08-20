package de.jClipCorn.database.history;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.stream.CCStreams;

public enum CCHistoryAction implements ContinoousEnum<CCHistoryAction>
{
	INSERT(0, "ADD"),    //$NON-NLS-1$
	UPDATE(1, "UPDATE"), //$NON-NLS-1$
	REMOVE(2, "DELETE"); //$NON-NLS-1$

	private static String[] _list = CCStreams.iterate(values()).map(CCHistoryAction::asString).toArray(new String[0]);

	public final int ID;
	public final String Name;

	private static EnumWrapper<CCHistoryAction> wrapper = new EnumWrapper<>(INSERT);

	CCHistoryAction(int id, String name) {
		ID = id;
		Name = name;
	}

	public static EnumWrapper<CCHistoryAction> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return ID;
	}

	@Override
	public String asString() {
		return Name;
	}

	@Override
	public String[] getList() {
		return _list;
	}

	@Override
	public CCHistoryAction[] evalues() {
		return values();
	}
}
