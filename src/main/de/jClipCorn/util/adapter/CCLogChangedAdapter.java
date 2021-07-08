package de.jClipCorn.util.adapter;

import de.jClipCorn.features.log.CCChangeLogElement;
import de.jClipCorn.features.log.CCLogChangedListener;
import de.jClipCorn.features.log.CCSQLLogElement;

public class CCLogChangedAdapter implements CCLogChangedListener
{
	@Override public void onChanged() { }

	@Override public void onSQLChanged(CCSQLLogElement cle) { }

	@Override public void onPropsChanged(CCChangeLogElement cle) { }
}
