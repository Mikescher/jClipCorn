package de.jClipCorn.table.filter.customFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.AbstractCustomStructureElementFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterDateSearchConfig;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateSearchParameter;
import de.jClipCorn.util.datetime.CCDateSearchParameter.DateSearchType;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.DateFormatException;

public class CustomHistoryFilter extends AbstractCustomStructureElementFilter {
	public CCDateSearchParameter Search = new CCDateSearchParameter();
	public boolean Recursive = true;
	
	@Override
	public boolean includes(CCMovie mov) {
		return Search.includes(mov.getViewedHistory());
	}

	@Override
	public boolean includes(CCSeries ser) {
		if (!Recursive) return false;
		
		List<CCDateTime> l = new ArrayList<>();
		for (CCEpisode epi : ser.iteratorEpisodes()) l.addAll(epi.getViewedHistory().iterator().enumerate());

		return Search.includes(CCDateTimeList.create(l));
	}

	@Override
	public boolean includes(CCSeason sea) {
		if (!Recursive) return false;
		
		List<CCDateTime> l = new ArrayList<>();
		for (CCEpisode epi : sea.iteratorEpisodes()) l.addAll(epi.getViewedHistory().iterator().enumerate());

		return Search.includes(CCDateTimeList.create(l));
	}
	
	@Override
	public boolean includes(CCEpisode epi) {
		return Search.includes(epi.getViewedHistory());
	}

	@Override
	public String getName() {
		
		String suffix = ""; //$NON-NLS-1$
		if (Recursive) suffix = " [R]"; //$NON-NLS-1$
		
		switch (Search.Type) {
		case CONTAINS:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", Search.First.toStringUIVerbose()) + suffix; //$NON-NLS-1$
		case CONTAINS_BETWEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + Search.First.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$
		case CONTAINS_NOT_BETWEEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "not " + Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case CONTAINS_ONLY_BETWEEN:
			return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.History", "only " + Search.First.toStringUIVerbose() + " - " + Search.Second.toStringUIVerbose()) + suffix; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		default:
			CCLog.addDefaultSwitchError(this, Search.Type);
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
		switch (Search.Type) {
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
		case CONTAINS_ONLY_BETWEEN:
			b.append("3");
			break;
		}
		b.append(",");
		b.append(Search.First.toStringSQL());
		b.append(",");
		b.append(Search.Second.toStringSQL());
		b.append(",");
		b.append(Recursive ? "1" : "0");
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
			if (paramsplit.length != 4) return false;
			
			int intval = Integer.parseInt(paramsplit[0]);
			
			if (intval == 0)      Search.Type = DateSearchType.CONTAINS;
			else if (intval == 1) Search.Type = DateSearchType.CONTAINS_BETWEEN;
			else if (intval == 2) Search.Type = DateSearchType.CONTAINS_NOT;
			else if (intval == 3) Search.Type = DateSearchType.CONTAINS_NOT_BETWEEEN;
			else if (intval == 4) Search.Type = DateSearchType.CONTAINS_ONLY_BETWEEN;
			else return false;
			
			Search.First = CCDate.createFromSQL(paramsplit[1]);

			Search.Second = CCDate.createFromSQL(paramsplit[2]);
			
			Recursive = Integer.parseInt(paramsplit[3]) != 0;
			
			return true;
		} catch (NumberFormatException | DateFormatException e) {
			return false;
		}
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomHistoryFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterDateSearchConfig(() -> Search, p -> Search = p),
			new CustomFilterBoolConfig(() -> Recursive, p -> Recursive = p, LocaleBundle.getString("FilterTree.Custom.Range.Recursive")), //$NON-NLS-1$
		};
	}
}
