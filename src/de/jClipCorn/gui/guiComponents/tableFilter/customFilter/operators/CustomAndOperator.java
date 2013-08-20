package de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomAndOperator extends CustomOperator {
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		boolean result = true;
		
		for (int i = 0; i < list.size(); i++) {
			result &= list.get(i).include(e);
		}
		
		return result;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.OP-AND", list.size()); //$NON-NLS-1$
	}
}
