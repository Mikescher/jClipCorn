package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTags;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.localization.LocaleBundle;

public class CustomTagFilter extends AbstractCustomFilter {
	private int tag = 0;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		return ((CCMovieTags)e.getValue(ClipTableModel.COLUMN_TAGS)).getTag(tag);
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Tag", CCMovieTags.getName(tag)); //$NON-NLS-1$
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_TAG;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(tag+"");
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
		
		if (! CCMovieTags.isTagActive(intval)) return false;
		setTag(intval);
		
		return true;
	}
}
