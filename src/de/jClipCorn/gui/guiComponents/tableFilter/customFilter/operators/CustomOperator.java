package de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.AbstractCustomFilter;

public abstract class CustomOperator extends AbstractCustomFilter {
	protected List<AbstractCustomFilter> list = new ArrayList<>();

	public List<AbstractCustomFilter> getList() {
		return list;
	}
	
	public AbstractCustomFilter add(AbstractCustomFilter f) {
		list.add(f);
		return f;
	}
	
	public boolean remove(AbstractCustomFilter f) {
		return list.remove(f);
	}
}
