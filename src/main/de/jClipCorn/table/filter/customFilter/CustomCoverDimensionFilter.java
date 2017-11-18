package de.jClipCorn.table.filter.customFilter;

import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCCoveredElement;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.table.filter.filterConfig.CustomFilterIntAreaConfig;
import de.jClipCorn.util.DecimalSearchType;
import de.jClipCorn.util.datatypes.CCIntArea;
import de.jClipCorn.util.datatypes.DimensionAxisType;

public class CustomCoverDimensionFilter extends AbstractCustomFilter {
	private DimensionAxisType axis = DimensionAxisType.WIDTH;
	private CCIntArea range = new CCIntArea();

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		if (e instanceof ICCCoveredElement) {
			BufferedImage cvr = ((ICCCoveredElement)e).getCover();

			if (axis == DimensionAxisType.WIDTH) return range.contains(cvr.getWidth());
			if (axis == DimensionAxisType.HEIGHT) return range.contains(cvr.getHeight());
			
		}
		return false;
	}
	
	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.CoverDimension", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.CoverDimension").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		switch (range.type) {
		case LESSER:
			return axis.asString() + " < " + range.high; //$NON-NLS-1$
		case GREATER:
			return range.low + " < " + axis.asString(); //$NON-NLS-1$
		case IN_RANGE:
			return range.low + " < " + axis.asString() + " < " + range.high; //$NON-NLS-1$ //$NON-NLS-2$
		case EXACT:
			return axis.asString() + " == " + range.low; //$NON-NLS-1$
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_COVERDIMENSION;
	}
	
	@SuppressWarnings("nls")
	@Override
	public String exportToString() {
		StringBuilder b = new StringBuilder();
		b.append("[");
		b.append(getID() + "");
		b.append("|");
		b.append(range.low+"");
		b.append(",");
		b.append(range.high+"");
		b.append(",");
		b.append(range.type.asInt() + "");
		b.append(",");
		b.append(axis.asInt()+"");
		b.append("]");
		
		return b.toString();
	}
	
	@Override
	@SuppressWarnings("nls")
	public boolean importFromString(String txt) {
		String params = AbstractCustomFilter.getParameterFromExport(txt);
		if (params == null) return false;
		
		String[] paramsplit = params.split(Pattern.quote(","));
		if (paramsplit.length != 4) return false;
		
		int intval;
		DecimalSearchType s;
		DimensionAxisType a;
		
		try {
			intval = Integer.parseInt(paramsplit[0]);
		} catch (NumberFormatException e) {
			return false;
		}
		range.low = intval;
		
		try {
			intval = Integer.parseInt(paramsplit[1]);
		} catch (NumberFormatException e) {
			return false;
		}
		range.high = intval;
		
		try {
			intval = Integer.parseInt(paramsplit[2]);
		} catch (NumberFormatException e) {
			return false;
		}
		s = DecimalSearchType.getWrapper().find(intval);
		if (s == null) return false;
		range.type = s;
		
		try {
			intval = Integer.parseInt(paramsplit[3]);
		} catch (NumberFormatException e) {
			return false;
		}
		a = DimensionAxisType.getWrapper().find(intval);
		if (a == null) return false;
		axis = a;
		
		return true;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomCoverDimensionFilter();
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterEnumChooserConfig<>(() -> axis, p -> axis = p, DimensionAxisType.getWrapper()),
			new CustomFilterIntAreaConfig(() -> range, a -> range = a, 0, null),
		};
	}
}
