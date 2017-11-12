package de.jClipCorn.table.filter.customFilter.operators;

import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;

public class CustomOrOperator extends CustomOperator {

	public CustomOrOperator() {
		super();
	}
	
	public CustomOrOperator(AbstractCustomFilter c1, AbstractCustomFilter c2) {
		super();
		add(c1);
		add(c2);
	}

	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		boolean result = false;
		
		for (int i = 0; i < list.size(); i++) {
			result |= list.get(i).includes(e);
		}
		
		return result;
	}
	
	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.OP-OR", list.size()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-OR").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_OR;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomOrOperator();
	}
}
