package de.jClipCorn.features.log;

public interface CCLogChangedListener {
	void onChanged();
	void onSQLChanged(CCSQLLogElement cle);
	void onPropsChanged(CCChangeLogElement cle);
}
