package de.jClipCorn.gui.mainFrame.charSelector;

import de.jClipCorn.features.table.filter.customFilter.CustomCharFilter;
import de.jClipCorn.gui.mainFrame.MainFrame;
import de.jClipCorn.gui.mainFrame.table.RowFilterSource;
import de.jClipCorn.properties.CCProperties;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;
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
			owner.getClipTable().setRowFilter(filter = null, RowFilterSource.CHARSELECTOR, true);
		} else {

			var oldFilter = owner.getClipTable().getRowFilter();

			if (oldFilter == null) {

				// simply set filter (no old filter)
				owner.getClipTable().setRowFilter(filter = CustomCharFilter.createSingle(owner.getMovielist(), search), RowFilterSource.CHARSELECTOR, false);

			} else if (oldFilter.getFilter() instanceof CustomCharFilter) {

				// append
				var fltr = (CustomCharFilter)oldFilter.getFilter();
				owner.getClipTable().setRowFilter(filter = fltr.appendCharset(search), RowFilterSource.CHARSELECTOR, false);

			} else {

				//override existing filter
				owner.getClipTable().setRowFilter(filter = CustomCharFilter.createSingle(owner.getMovielist(), search), RowFilterSource.CHARSELECTOR, false);

			}
		}

		updateButtonOpacity(filter);
	}

	private void updateButtonOpacity(@Nullable  CustomCharFilter filter) {
		if (!ccprops().PROP_CHARSELECTOR_DYNAMIC_OPACTITY.getValue()) return;

		if (!owner.getMovielist().isLoaded()) return;

		var chars = owner
				.getMovielist()
				.iteratorElements()
				.filter(elem -> filter == null || filter.includes(elem))
				.map(p -> CustomCharFilter.getRelevantCharacter(p, filter == null ? 0 : filter.getLength()))
				.filter(Objects::nonNull)
				.map(Character::toUpperCase)
				.unique()
				.toSet();

		updateButtonInactive(chars);
	}

	protected abstract void updateButtonInactive(Set<Character> chars);

	public void reset() {
		updateButtonOpacity(null);
	}
}
