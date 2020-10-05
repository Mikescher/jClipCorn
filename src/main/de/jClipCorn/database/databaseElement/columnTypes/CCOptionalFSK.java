package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.exceptions.EnumValueNotFoundException;

public enum CCOptionalFSK implements ContinoousEnum<CCOptionalFSK> {
	RATING_0  (0, 1), // keine Alterschbeschräkung
	RATING_I  (1, 2), // ab 6 Jahren
	RATING_II (2, 3), // ab 12 Jahren
	RATING_III(3, 4), // ab 16 Jahren
	RATING_IV (4, 5), // keine Jugendfreigabe
	NULL      (5, 0); // NOT SET

	private final static String[] NAMES =
	{
		LocaleBundle.getString("CCMovieFSK.FSK0"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK1"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK2"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK3"), //$NON-NLS-1$
		LocaleBundle.getString("CCMovieFSK.FSK4"), //$NON-NLS-1$
		" ",                                       //$NON-NLS-1$
	};

	private final int id;
	private final int order;

	private final static EnumWrapper<CCOptionalFSK> wrapper = new EnumWrapper<>(NULL, p -> p.order);

	private CCOptionalFSK(int val, int ord) {
		id    = val;
		order = ord;
	}
	
	public static EnumWrapper<CCOptionalFSK> getWrapper() {
		return wrapper;
	}

	@Override
	public int asInt() {
		return id;
	}

	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public String[] getList() {
		return NAMES;
	}

	@Override
	public CCOptionalFSK[] evalues() {
		return CCOptionalFSK.values();
	}

	public CCFSK asFSK() throws EnumValueNotFoundException {
		if (this == NULL) throw new EnumValueNotFoundException("NULL", CCFSK.class); //$NON-NLS-1$
		return CCFSK.getWrapper().findOrFatalError(id);
	}

	public CCFSK asFSKOrNull() {
		if (this == NULL) return null; else return CCFSK.getWrapper().findOrFatalError(id);
	}
}
