package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomFilterDialog;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomHistoryFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.listener.FinishListener;

public class CustomHistoryFilter extends AbstractCustomFilter {
	public enum CustomHistoryFilterType { CONTAINS, CONTAINS_NOT, CONTAINS_BETWEEN, CONTAINS_NOT_BETWEEEN }
	
	public CCDate First = CCDate.getMinimumDate();
	public CCDate Second = CCDate.getMinimumDate();
	
	public CustomHistoryFilterType Type = CustomHistoryFilterType.CONTAINS;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		if (! ((CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE)).isMovie()) return false;
		
		CCMovie mov = ((CCMovie)e.getValue(ClipTableModel.COLUMN_TITLE));
		
		switch (Type) {
		case CONTAINS:
			return mov.getViewedHistory().containsDate(First);
		case CONTAINS_BETWEEN:
			return ! mov.getViewedHistory().containsDate(First);
		case CONTAINS_NOT:
			return mov.getViewedHistory().containsDateBetween(First, Second);
		case CONTAINS_NOT_BETWEEEN:
			return ! mov.getViewedHistory().containsDateBetween(First, Second);
		default:
			return false;
		}
	}

	@Override
	public String getName() {
		switch (Type) {
		case CONTAINS:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", First.toStringUIVerbose()); //$NON-NLS-1$
		case CONTAINS_BETWEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", First.toStringUIVerbose() + " - " + Second.toStringUIVerbose()); //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + First.toStringUIVerbose()); //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT_BETWEEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + First.toStringUIVerbose() + " - " + Second.toStringUIVerbose()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		default:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "?"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.History").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_HISTORY;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		switch (Type) {
		case CONTAINS: 
			b.append("0");
			break;
		case CONTAINS_BETWEEN:
			b.append("1");
			break;
		case CONTAINS_NOT:
			b.append("2");
			break;
		case CONTAINS_NOT_BETWEEEN:
			b.append("3");
			break;
		}
		b.append("|");
		b.append(First.toStringSQL());
		b.append("|");
		b.append(Second.toStringSQL());
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		try {
			String params = AbstractCustomFilter.getParameterFromExport(txt);
			if (params == null) return false;
			
			String[] paramsplit = params.split(Pattern.quote(","));
			if (paramsplit.length != 3) return false;
			
			int intval = Integer.parseInt(paramsplit[0]);
			
			if (intval == 0) Type = CustomHistoryFilterType.CONTAINS;
			else if (intval == 1) Type = CustomHistoryFilterType.CONTAINS_BETWEEN;
			else if (intval == 2) Type = CustomHistoryFilterType.CONTAINS_NOT;
			else if (intval == 3) Type = CustomHistoryFilterType.CONTAINS_NOT_BETWEEEN;
			else return false;
			
			First = CCDate.createFromSQL(paramsplit[1]);

			Second = CCDate.createFromSQL(paramsplit[2]);
			
			return true;
		} catch (NumberFormatException | DateFormatException e) {
			return false;
		}
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomHistoryFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomHistoryFilter();
	}
}
