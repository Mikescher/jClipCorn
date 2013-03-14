package de.jClipCorn.gui.localization.util;

import java.util.Collection;
import java.util.Vector;

import de.jClipCorn.gui.localization.LocaleBundle;

public class LocalizedVector extends Vector<String> {
	private static final long serialVersionUID = -2433360580848994877L;

	public LocalizedVector() {
		super();
	}

	public LocalizedVector(int initialCapacity) {
		super(initialCapacity);
	}

	public LocalizedVector(Collection<? extends String> c) {
		super(c);
	}

	public LocalizedVector(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	@Override
	public String elementAt(int i) {
		return LocaleBundle.getString(super.elementAt(i));
	}
}
