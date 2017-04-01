package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import java.util.function.Function;

import javax.swing.RowFilter;

public class JCCSimpleRowFilter<TData> extends RowFilter<JCCSimpleTableModel<TData>, Integer> {
	private final Function<TData, Boolean> func;
	
	
	public JCCSimpleRowFilter(Function<TData, Boolean> filter) {
		func = filter;
	}
	
	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends JCCSimpleTableModel<TData>, ? extends Integer> entry) {
		TData d = entry.getModel().getElementAtRow(entry.getIdentifier());
		
		return func.apply(d);
	}

}
