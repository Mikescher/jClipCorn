package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.features.table.filter.AbstractCustomFilter;

import java.util.Random;

import javax.swing.JComponent;

public abstract class CustomFilterConfig {

	public abstract JComponent getComponent(Runnable onChange);

	public abstract void setValueRandom(Random r);

	public boolean shouldGrow() {
		return false;
	}

	public void onFilterDataChanged(JComponent comp, AbstractCustomFilter filter) {
		// do nothing
	}
}
