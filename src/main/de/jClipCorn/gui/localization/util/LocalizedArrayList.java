package de.jClipCorn.gui.localization.util;

import java.util.ArrayList;
import java.util.Collection;

import de.jClipCorn.gui.localization.LocaleBundle;

public class LocalizedArrayList extends ArrayList<String> {
	private static final long serialVersionUID = -1806199300527142676L;

	public LocalizedArrayList() {
		super();
	}

	public LocalizedArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	public LocalizedArrayList(Collection<? extends String> c) {
		super(c);
	}

	@Override
	public boolean add(String e) {
		return super.add(LocaleBundle.getString(e));
	}
}
