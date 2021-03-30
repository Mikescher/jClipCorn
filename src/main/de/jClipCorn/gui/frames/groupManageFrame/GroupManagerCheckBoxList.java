package de.jClipCorn.gui.frames.groupManageFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.guiComponents.jCheckBoxList.JCheckBoxList;

public class GroupManagerCheckBoxList extends JCheckBoxList<CCDatabaseElement> {
	public GroupManagerCheckBoxList() {
		super(CCDatabaseElement::getFullDisplayTitle);
	}
}
