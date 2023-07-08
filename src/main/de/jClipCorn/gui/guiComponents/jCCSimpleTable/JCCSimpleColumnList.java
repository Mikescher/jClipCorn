package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.util.ArrayList;
import java.util.List;

public final class JCCSimpleColumnList<TData> {

	private final ArrayList<JCCSimpleColumnPrototype<TData>> data = new ArrayList<>();

	public final IJCCSimpleTable table;

	public JCCSimpleColumnList(IJCCSimpleTable o) {
		this.table = o;
	}

	public JCCSimpleColumnPrototype<TData> add(String _captionIdent) {
		var v = new JCCSimpleColumnPrototype<TData>(this.table, _captionIdent);
		this.data.add(v);
		return v;
	}

	public List<JCCSimpleColumnPrototype<TData>> get() {
		return new ArrayList<>(data);
	}
}
