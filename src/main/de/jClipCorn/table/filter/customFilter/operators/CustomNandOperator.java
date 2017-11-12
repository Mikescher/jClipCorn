package de.jClipCorn.table.filter.customFilter.operators;

import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;

public class CustomNandOperator extends CustomOperator {
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		boolean result = true;
		
		for (int i = 0; i < list.size(); i++) {
			result &= list.get(i).includes(e);
		}
		
		return ! result;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.OP-NAND", list.size()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-NAND").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_NAND;
	}
	
	@Override
	public AbstractCustomFilter createNew() {
		return new CustomNandOperator();
	}
}
