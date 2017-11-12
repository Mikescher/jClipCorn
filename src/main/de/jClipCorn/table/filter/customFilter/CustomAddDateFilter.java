package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterDateAreaConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateArea;
import de.jClipCorn.util.exceptions.DateFormatException;

public class CustomAddDateFilter extends AbstractCustomFilter {
	private CCDateArea area = new CCDateArea();
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		return area.contains(e.getAddDate());
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
		switch (area.type) {
		case LESSER:
			return "X < " + area.high.toStringUIShort(); //$NON-NLS-1$
		case GREATER:
			return area.low.toStringUIShort() + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return area.low.toStringUIShort() + " < X < " + area.high.toStringUIShort(); //$NON-NLS-1$
		case EXACT:
			return "X == " + area.low.toStringUIShort(); //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	public DecimalSearchType getSearchType() {
		return area.type;
	}

	public void setSearchType(DecimalSearchType searchType) {
		area.type = searchType;
	}

	public CCDate getHigh() {
		return area.high;
	}

	public void setHigh(CCDate high) {
		this.area.high = high;
	}

	public CCDate getLow() {
		return area.low;
	}

	public void setLow(CCDate low) {
		this.area.low = low;
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
		b.append(area.low.toStringSQL()+"");
		b.append(",");
		b.append(area.high.toStringSQL()+"");
		b.append(",");
		b.append(area.type.asInt() + "");
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
	public AbstractCustomFilter createNew() {
		return new CustomAddDateFilter();
	}

	public static CustomAddDateFilter create(CCDate data) {
		CustomAddDateFilter f = new CustomAddDateFilter();
		f.setLow(data);
		f.setSearchType(DecimalSearchType.EXACT);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterDateAreaConfig(() -> area, a -> area = a),
		};
	}
}
