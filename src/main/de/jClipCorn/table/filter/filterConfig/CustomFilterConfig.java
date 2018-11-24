package de.jClipCorn.table.filter.filterConfig;

import java.util.Random;

import javax.swing.JComponent;

public abstract class CustomFilterConfig {

	public abstract JComponent getComponent(Runnable onChange);

	public abstract void setValueRandom(Random r);
	
}
