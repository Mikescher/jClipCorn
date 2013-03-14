package de.jClipCorn.gui.guiComponents.tableFilter;

import javax.swing.RowFilter;

import org.apache.commons.lang.StringUtils;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;

public class TableCharFilter extends RowFilter<ClipTableModel, Object> {
	@SuppressWarnings("nls")
	private final static String[] exclusions = {"Der", "Die", "Das", "The", "Den"};
	
	private String charset;
	
	public TableCharFilter(String charset) {
		super();
		this.charset = charset;
	}

	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		String first = ((CCDatabaseElement)e.getValue(1)).getTitle();
		
		for (String s : exclusions) {
			if (first.startsWith(s+" ")) { //$NON-NLS-1$
				first = first.substring(s.length() + 1);
			}
		}
		
		if (first.length() < 1) {
			return false;
		}
		
		String fchar = first.substring(0, 1);
		return StringUtils.containsIgnoreCase(charset, fchar);
	}
}
