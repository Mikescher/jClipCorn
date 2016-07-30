package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum CCMovieTyp implements ContinoousEnum<CCMovieTyp> {
	MOVIE(0),
	SERIES(1);
	
	private final static String NAMES[] = {
		LocaleBundle.getString("CCMovieTyp.Movie"),  //$NON-NLS-1$
		LocaleBundle.getString("CCMovieTyp.Series") //$NON-NLS-1$
	};
	
	private int id;

	private static EnumWrapper<CCMovieTyp> wrapper = new EnumWrapper<>(MOVIE);

	private CCMovieTyp(int val) {
		id = val;
	}
	
	public static EnumWrapper<CCMovieTyp> getWrapper() {
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
	
	public ImageIcon getIcon() {
		switch (this) {
		case MOVIE:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_MOVIE );
		case SERIES:
			return CachedResourceLoader.getImageIcon(Resources.ICN_TABLE_SERIES);
		default:
			return null;
		}
		
	}

	@Override
	public CCMovieTyp[] evalues() {
		return CCMovieTyp.values();
	}
}
