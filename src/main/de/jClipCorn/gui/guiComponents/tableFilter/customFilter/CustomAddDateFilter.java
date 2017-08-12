package de.jClipCorn.gui.guiComponents.tableFilter.customFilter;

import java.awt.Component;
import java.util.regex.Pattern;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.guiComponents.tableFilter.AbstractCustomFilter;
import de.jClipCorn.gui.guiComponents.tableFilter.CustomFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilterDialogs.CustomAddDateFilterDialog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.DateFormatException;
import de.jClipCorn.util.listener.FinishListener;

public class CustomAddDateFilter extends AbstractCustomFilter {
	private CCDate low = CCDate.getCurrentDate();
	private CCDate high = CCDate.getCurrentDate();
	private DecimalSearchType searchType = DecimalSearchType.EXACT;
	
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		CCDatabaseElement elem = (CCDatabaseElement)e.getValue(ClipTableModel.COLUMN_TITLE);
		
		CCDate d = elem.getAddDate();
		
		switch (searchType) {
		case LESSER:
			return d.isLessEqualsThan(high);
		case GREATER:
			return d.isGreaterEqualsThan(low);
		case IN_RANGE:
			return d.isGreaterEqualsThan(low) && d.isLessEqualsThan(high);
		case EXACT:
			return d.isEqual(low);
		}

		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.AddDate", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.AddDate").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (searchType) {
		case LESSER:
			return "X < " + high.toStringUIShort(); //$NON-NLS-1$
		case GREATER:
			return low.toStringUIShort() + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return low.toStringUIShort() + " < X < " + high.toStringUIShort(); //$NON-NLS-1$
		case EXACT:
			return "X == " + low.toStringUIShort(); //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	public DecimalSearchType getSearchType() {
		return searchType;
	}

	public void setSearchType(DecimalSearchType searchType) {
		this.searchType = searchType;
	}

	public CCDate getHigh() {
		return high;
	}

	public void setHigh(CCDate high) {
		this.high = high;
	}

	public CCDate getLow() {
		return low;
	}

	public void setLow(CCDate low) {
		this.low = low;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ADDDATE;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(low.toStringSQL()+"");
		b.append(",");
		b.append(high.toStringSQL()+"");
		b.append(",");
		b.append(searchType.asInt() + "");
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
			
			setLow(CCDate.createFromSQL(paramsplit[0]));
			
			setHigh(CCDate.createFromSQL(paramsplit[1]));

			try {
				setSearchType(DecimalSearchType.getWrapper().find(Integer.parseInt(paramsplit[2])));
			} catch (NumberFormatException e) {
				return false;
			}
			
			return true;
		} catch (NumberFormatException | DateFormatException e) {
			return false;
		}
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomAddDateFilterDialog(this, fl, parent);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomAddDateFilter();
	}

	public static AbstractCustomFilter create(CCDate data) {
		CustomAddDateFilter f = new CustomAddDateFilter();
		f.setLow(data);
		f.setSearchType(DecimalSearchType.EXACT);
		return f;
	}
}
