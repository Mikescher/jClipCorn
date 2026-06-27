package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.elementProps.impl.EOptIntProp;
import de.jClipCorn.database.elementProps.impl.EStringProp;

public interface ICCDatedElement {
	// Movie, Season

	EStringProp title();
	EOptIntProp year();
}
