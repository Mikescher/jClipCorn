package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;

public interface ICCTaggedElement {
	// Movies, Series, Episodes

	CCTagList getTags();

	void setTags(CCTagList t);

	void switchTag(CCSingleTag t);
}
