package de.jClipCorn.features.table.filter.filterConfig;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;

import javax.swing.*;
import java.util.Random;

public abstract class CustomFilterConfig {

	protected final CCMovieList movielist;

	public CustomFilterConfig(CCMovieList ml) {
		this.movielist = ml;
	}

	public abstract JComponent getComponent(Runnable onChange);

	public abstract void setValueRandom(Random r);

	public boolean shouldGrow() {
		return false;
	}

	public void onFilterDataChanged(JComponent comp, AbstractCustomFilter filter) {
		// do nothing
	}
}
