package de.jClipCorn.table.filter.customFilter.operators;

import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.table.filter.AbstractCustomFilter;

public class CustomAndOperator extends CustomOperator {

	public CustomAndOperator() {
		super();
	}
	
	public CustomAndOperator(AbstractCustomFilter inner) {
		super();
		
		add(inner);
	}
	
	@Override
	public boolean includes(ICCDatabaseStructureElement e) {
		boolean result = true;
		
		for (int i = 0; i < list.size(); i++) {
			result &= list.get(i).includes(e);
		}
		
		return result;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.OP-AND", list.size()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.OP-AND").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_AND;
	}

	@Override
	public AbstractCustomFilter createNew() {
		return new CustomAndOperator();
	}

	public void combineWith(AbstractCustomFilter other) {

		for (AbstractCustomFilter child : list) {
			
			if (child instanceof CustomOrOperator) {
				
				CustomOrOperator orChild = (CustomOrOperator)child;
				if (orChild.iterate().any() && orChild.iterate().all(c -> c.getID() == other.getID())) {
					orChild.add(other);
					return;
				}
				
			} else {
				
				if (child.getID() == other.getID()) {
					remove(child);
					add(new CustomOrOperator(child, other));
					return;
				}
				
			}
	
		}

		add(other);
	}
}
