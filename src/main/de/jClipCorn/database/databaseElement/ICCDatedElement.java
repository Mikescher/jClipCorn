package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.elementValues.EIntProp;
import de.jClipCorn.database.elementValues.EStringProp;

public interface ICCDatedElement {
	// Movie, Season

	EStringProp title();
	EIntProp    year();
}
