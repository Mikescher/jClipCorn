package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineStars;
import de.jClipCorn.features.table.filter.AbstractCustomDatabaseElementFilter;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datatypes.DecimalSearchType;

public class CustomOnlinescoreFilter extends AbstractCustomDatabaseElementFilter {
	private CCOnlineStars low = CCOnlineStars.STARS_0_0;
	private CCOnlineStars high = CCOnlineStars.STARS_0_0;
	private DecimalSearchType searchType = DecimalSearchType.EXACT;

	public CustomOnlinescoreFilter(CCMovieList ml) {
		super(ml);
	}

	@Override
	public boolean includes(CCDatabaseElement e) {
		CCOnlineScore osco = e.getOnlinescore();
		
		switch (searchType) {
		case LESSER:
			return osco.getStars().asInt() < high.asInt();
		case GREATER:
			return low.asInt() < osco.getStars().asInt();
		case IN_RANGE:
			return low.asInt() < osco.getStars().asInt() && osco.getStars().asInt() < high.asInt();
		case EXACT:
			return low == osco.getStars();
		default:
			return false;
		}
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Onlinescore", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Onlinescore").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (searchType) {
		case LESSER:
			return "X < " + high.asInt()/2.0; //$NON-NLS-1$
		case GREATER:
			return low.asInt()/2.0 + " < X"; //$NON-NLS-1$
		case IN_RANGE:
			return low.asInt()/2.0 + " < X < " + high.asInt()/2.0; //$NON-NLS-1$
		case EXACT:
			return "X == " + low.asInt()/2.0; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_ONLINESCORE;
	}
		
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addCCEnum("low", CCOnlineStars.getWrapper(), (d) -> this.low = d,  () -> this.low);
		cfg.addCCEnum("high", CCOnlineStars.getWrapper(), (d) -> this.high = d,  () -> this.high);
		cfg.addCCEnum("searchtype", DecimalSearchType.getWrapper(), (d) -> this.searchType = d,  () -> this.searchType);
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomOnlinescoreFilter(ml);
	}

	public static CustomOnlinescoreFilter create(CCMovieList ml, CCOnlineStars data) {
		CustomOnlinescoreFilter f = new CustomOnlinescoreFilter(ml);
		f.searchType = DecimalSearchType.EXACT;
		f.low = data;
		return f;
	}

	private CCIntArea getAsIntArea() {
		return new CCIntArea(low.asInt(), high.asInt(), searchType);
	}

	private void setAsIntArea(CCIntArea a) {
		low = CCOnlineStars.getWrapper().findOrNull(a.low);
		if (low==null)low=CCOnlineStars.STARS_0_0;
		high = CCOnlineStars.getWrapper().findOrNull(a.high);
		if (high==null)high=CCOnlineStars.STARS_5_0;
		searchType = a.type;
	}
	
	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterIntAreaConfig(ml, this::getAsIntArea, this::setAsIntArea, 0, 10),
		};
	}
}
