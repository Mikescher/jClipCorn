package de.jClipCorn.table.filter.customFilter.operators;

import java.awt.Component;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.CustomFilterDialog;
import de.jClipCorn.table.filter.customFilterDialogs.CustomOperatorFilterDialog;
import de.jClipCorn.util.listener.FinishListener;

public class CustomNorOperator extends CustomOperator {
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		boolean result = false;
		
		for (int i = 0; i < list.size(); i++) {
			result |= list.get(i).includes(e);
		}
		
		return ! result;
	}
	
	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.OP-NOR", list.size()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-NOR").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_NOR;
	}

	@Override
	public CustomFilterDialog CreateDialog(FinishListener fl, Component parent, CCMovieList ml) {
		return new CustomOperatorFilterDialog(ml, this, fl, parent, false);
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomNorOperator();
	}
}
