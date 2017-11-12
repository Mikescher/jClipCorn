package de.jClipCorn.table.filter.customFilter;

import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.util.CCIntArea;
import de.jClipCorn.util.DecimalSearchType;

public class CustomEpisodecountFilter extends AbstractCustomStructureElementFilter {
	private CCIntArea area = new CCIntArea(1, 1, DecimalSearchType.EXACT);
	
	@Override
	public boolean includes(CCMovie m) {
		return false;
	}

	@Override
	public boolean includes(CCSeries s) {
		return area.contains(s.getEpisodeCount());
	}

	@Override
	public boolean includes(CCSeason s) {
		return area.contains(s.getEpisodeCount());
	}

	@Override
	public boolean includes(CCEpisode e) {
		return false;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Episodecount", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Episodecount").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (area.type) {
		case LESSER:
			return "X < " + area.high; //$NON-NLS-1$
		case GREATER:
			return area.low + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return area.low + " < X < " + area.high; //$NON-NLS-1$
		case EXACT:
			return "X == " + area.low; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	public DecimalSearchType getSearchType() {
		return area.type;
	}

	public void setSearchType(DecimalSearchType searchType) {
		this.area.type = searchType;
	}

	public int getHigh() {
		return area.high;
	}

	public void setHigh(int high) {
		this.area.high = high;
	}

	public int getLow() {
		return area.low;
	}

	public void setLow(int low) {
		this.area.low = low;
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_EPISODECOUNT;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(area.low+"");
		b.append(",");
		b.append(area.high+"");
		b.append(",");
		b.append(area.type.asInt() + "");
		b.append("]");
		
		return b.toString();
	}
	
	@SuppressWarnings("nls")
	@Override
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 3) return false;
		
		int intval;
		DecimalSearchType s;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		setLow(intval);
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		setHigh(intval);
		
		try {
			intval = Integer.parseInt(paramsplit[2]);
		} catch (NumberFormatException e) {
			return false;
		}
		s = DecimalSearchType.getWrapper().find(intval);
		if (s == null) return false;
		setSearchType(s);
		
		return true;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomEpisodecountFilter();
	}

	public static CustomEpisodecountFilter create(Integer data) {
		CustomEpisodecountFilter f = new CustomEpisodecountFilter();
		f.setLow(data);
		f.setSearchType(DecimalSearchType.EXACT);
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterIntAreaConfig(() -> area, a -> area = a, 0, null),
		};
	}
}
