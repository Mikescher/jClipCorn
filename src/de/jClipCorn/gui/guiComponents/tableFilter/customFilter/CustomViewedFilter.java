package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomViewedFilter extends AbstractCustomFilter {
	private boolean viewed = false;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ! viewed ^ ((Boolean)e.getValue(ClipTableModel.COLUMN_VIEWED));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Viewed", viewed); //$NON-NLS-1$
	}

	public boolean getViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}
	
	@Override
	public int getID() {
		return 14;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append((viewed)?("1"):("0"));
		b.append("]");
		
		return b.toString();
	}
	
	@Override
	public boolean importFromString(String txt) {
		
	}
}
