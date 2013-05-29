package de.jClipCorn.gui.frames.checkDatabaseFrame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import de.jClipCorn.database.databaseErrors.DatabaseError;
import de.jClipCorn.database.databaseErrors.DatabaseErrorType;

public class DatabaseErrorListModel extends DefaultListModel<DatabaseError> {
	private static final long serialVersionUID = -4911426146819969962L;

	private List<DatabaseError> dispList = new ArrayList<>();
	
	public DatabaseErrorListModel() {
		//empf
	}
	
	@Override
	public DatabaseError getElementAt(int index) {
		return dispList.get(index);
	}
	
	@Override
	public int getSize() {
		return dispList.size();
	}
	
	public void updateFilter(DatabaseErrorType type) {
		dispList.clear();
		
		for (int i = 0; i < super.getSize(); i++) {
			if (type == null || super.getElementAt(i).isTypeOf(type)) {
				dispList.add(super.getElementAt(i));
			}
		}
		
		fireContentsChanged(this, 0, getSize());
	}
	
	@Override
	public void addElement(DatabaseError element) {
		super.addElement(element);
		
		updateFilter(null);
	}
}