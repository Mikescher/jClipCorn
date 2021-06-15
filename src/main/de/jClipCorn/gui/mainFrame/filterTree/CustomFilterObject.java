package de.jClipCorn.gui.mainFrame.filterTree;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomOperator;

public class CustomFilterObject {
	private String name;
	private CustomOperator filter; 
	
	public CustomFilterObject(String name, CustomOperator filter) {
		this.setName(name);
		this.setFilter(filter);
	}

	public CustomOperator getFilter() {
		return filter;
	}

	public void setFilter(CustomOperator filter) {
		this.filter = filter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String exportToString() {
		return name + "\t" + filter.exportToString(); //$NON-NLS-1$
	}
	
	@Override
	public String toString() {
		return name;
	}

	public CustomFilterObject copy(CCMovieList ml) {
		return new CustomFilterObject(name, (CustomOperator)filter.createCopy(ml));
	}

	public void apply(CustomFilterObject cfo) {
		name = cfo.name;
		filter = cfo.filter;
	}
}
