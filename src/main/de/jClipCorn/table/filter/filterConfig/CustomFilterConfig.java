package de.jClipCorn.table.filter.filterConfig;

import javax.swing.JComponent;

public abstract class CustomFilterConfig {

	public abstract JComponent getComponent(Runnable onChange);
	
}
