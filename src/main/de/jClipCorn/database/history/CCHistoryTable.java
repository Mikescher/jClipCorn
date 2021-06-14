package de.jClipCorn.database.history;

import de.jClipCorn.database.driver.DatabaseStructure;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;
import de.jClipCorn.util.sqlwrapper.CCSQLTableDef;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;

public enum CCHistoryTable implements ContinoousEnum<CCHistoryTable>
{
	COVERS(0,   "COVERS",   DatabaseStructure.TAB_COVERS,   Resources.ICN_TABLE_COVERS),   //$NON-NLS-1$
	ELEMENTS(1, "ELEMENTS", DatabaseStructure.TAB_MAIN,     Resources.ICN_TABLE_MOVIE),    //$NON-NLS-1$
	EPISODES(2, "EPISODES", DatabaseStructure.TAB_EPISODES, Resources.ICN_TABLE_EPISODES), //$NON-NLS-1$
	GROUPS(3,   "GROUPS",   DatabaseStructure.TAB_GROUPS,   Resources.ICN_TABLE_GROUPS),   //$NON-NLS-1$
	INFO(4,     "INFO",     DatabaseStructure.TAB_INFO,     Resources.ICN_TABLE_INFO),     //$NON-NLS-1$
	SEASONS(5,  "SEASONS",  DatabaseStructure.TAB_SEASONS,  Resources.ICN_TABLE_SEASONS);  //$NON-NLS-1$

	private static final String[] _list = CCStreams.iterate(values()).map(CCHistoryTable::asString).toArray(new String[0]);

	public final int ID;
	public final String Name;
	public final IconRef Icon;
	public final CCSQLTableDef Definition;

	private static final EnumWrapper<CCHistoryTable> wrapper = new EnumWrapper<>(COVERS);

	CCHistoryTable(int id, String name, CCSQLTableDef def, IconRef icn) {
		ID = id;
		Name = name;
		Icon = icn;
		Definition = def;
	}

	public static EnumWrapper<CCHistoryTable> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}

	@Override
	public int asInt() {
		return ID;
	}

	@Override
	public String asString() {
		return Name;
	}

	public Icon getIcon() {
		return Icon.get();
	}

	@Override
	public String[] getList() {
		return _list;
	}

	@Override
	public CCHistoryTable[] evalues() {
		return values();
	}
}
