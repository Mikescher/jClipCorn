package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomFSKFilter extends AbstractCustomFilter {
	private CCMovieFSK fsk = CCMovieFSK.RATING_0;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return fsk.equals(e.getValue(ClipTableModel.COLUMN_FSK));
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.FSK", fsk.asString()); //$NON-NLS-1$
	}

	public CCMovieFSK getFSK() {
		return fsk;
	}

	public void setFSK(CCMovieFSK fsk) {
		this.fsk = fsk;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_FSK;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(fsk.asInt()+"");
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 1) return false;
		
		int intval;
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		
		CCMovieFSK f = CCMovieFSK.find(intval);
		if (f == null) return false;
		setFSK(f);
		
		return true;
	}
}
