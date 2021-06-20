package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.elementProps.impl.EIntProp;
import de.jClipCorn.database.elementProps.impl.EStringProp;

public interface ICCDatedElement {
	// Movie, Season

	EStringProp title();
	EIntProp    year();
}
