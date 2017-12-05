package de.jClipCorn.gui.guiComponents.jCCSimpleTable;

import javax.swing.RowFilter;

import de.jClipCorn.util.lambda.Func1to1;

public class JCCSimpleRowFilter<TData> extends RowFilter<JCCSimpleTableModel<TData>, Integer> {
	private final Func1to1<TData, Boolean> func;
	
	public JCCSimpleRowFilter(Func1to1<TData, Boolean> filter) {
		func = filter;
	}
	
	@Override
	public boolean include(javax.swing.RowFilter.Entry<? extends JCCSimpleTableModel<TData>, ? extends Integer> entry) {
		TData d = entry.getModel().getElementAtRow(entry.getIdentifier());
		
		return func.invoke(d);
	}
}
