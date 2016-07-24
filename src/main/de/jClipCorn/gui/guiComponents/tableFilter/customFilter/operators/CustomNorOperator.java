package de.jClipCorn.gui.guiComponents.tableFilter.customFilter.operators;

import java.awt.Component;

import javax.swing.RowFilter.Entry;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.frames.mainFrame.clipTable.ClipTableModel;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomFilterDialog;
import de.jClipCorn.gui.frames.mainFrame.filterTree.customFilterDialogs.CustomOperatorFilterDialog;
import de.jClipCorn.gui.guiComponents.tableFilter.customFilter.AbstractCustomFilter;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.listener.FinishListener;

public class CustomNorOperator extends CustomOperator {
	@Override
	public boolean include(Entry<? extends ClipTableModel, ? extends Object> e) {
		boolean result = false;
		
		for (int i = 0; i < list.size(); i++) {
			result |= list.get(i).include(e);
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
