package de.jClipCorn.gui.mainFrame.charSelector;

import de.jClipCorn.features.table.filter.customFilter.CustomCharFilter;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;
import de.jClipCorn.properties.CCProperties;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractClipCharSortSelector extends JToolBar {
	private static final long serialVersionUID = -8270219279263812975L;
	
	protected final MainFrame owner;	
	
	public AbstractClipCharSortSelector(MainFrame mf) {
		super();
		this.owner = mf;
	}

	public CCProperties ccprops() {
		return this.owner.ccprops();
	}

	protected void onClick(String search) {

		CustomCharFilter filter;

		if (search == null) {

			// clear filter
			owner.getClipTable().setRowFilter(filter = null, RowFilterSource.CHARSELECTOR, true);

		} else {

			var oldFilter = owner.getClipTable().getRowFilter();

			var ccf = CustomCharFilter.createSingle(
					owner.getMovielist(),
					search,
					ccprops().PROP_CHARSELECTOR_MATCHMODE.getValue(),
					ccprops().PROP_CHARSELECTOR_SELMODE.getValue(),
					ccprops().PROP_CHARSELECTOR_EXCLUSIONS.getValue(),
					ccprops().PROP_CHARSELECTOR_IGNORENONCHARS.getValue());

			if (oldFilter == null) {

				// simply set filter (no old filter)
				owner.getClipTable().setRowFilter(filter = ccf, RowFilterSource.CHARSELECTOR, false);

			} else if (oldFilter.getFilter() instanceof CustomCharFilter && ((CustomCharFilter)oldFilter.getFilter()).sameMetaSettings(ccf)) {

				// append
				var fltr = (CustomCharFilter)oldFilter.getFilter();
				owner.getClipTable().setRowFilter(filter = fltr.appendCharset(search), RowFilterSource.CHARSELECTOR, false);

			} else {

				//override existing filter
				owner.getClipTable().setRowFilter(filter = ccf, RowFilterSource.CHARSELECTOR, false);

			}
		}

		updateButtonOpacity(filter);
	}

	private void updateButtonOpacity(@Nullable CustomCharFilter filter) {
		if (!ccprops().PROP_CHARSELECTOR_DYNAMIC_OPACTITY.getValue()) return;

		if (!owner.getMovielist().isLoaded()) return;

		if (filter == null) {
			filter = CustomCharFilter.createSingle(
					owner.getMovielist(),
					"",
					ccprops().PROP_CHARSELECTOR_MATCHMODE.getValue(),
					ccprops().PROP_CHARSELECTOR_SELMODE.getValue(),
					ccprops().PROP_CHARSELECTOR_EXCLUSIONS.getValue(),
					ccprops().PROP_CHARSELECTOR_IGNORENONCHARS.getValue());
		}

		var hs = new HashSet<Character>();

		for (var elem : owner.getMovielist().iteratorElements()) {
			filter.collectNextChars(elem, hs);
		}

		updateButtonInactive(hs);
	}

	protected abstract void updateButtonInactive(Set<Character> chars);

	public void reset() {
		updateButtonOpacity(null);
	}
}
