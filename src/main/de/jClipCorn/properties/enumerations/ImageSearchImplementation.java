package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.cover.AbstractImageSearch;
import de.jClipCorn.features.online.cover.google.GoogleCoverSearch;
import de.jClipCorn.features.online.cover.google.GooglePosterSearch;
import de.jClipCorn.features.online.cover.imdb.IMDBPrimaryCoverSearch;
import de.jClipCorn.features.online.cover.imdb.IMDBSecondaryCoverSearch;
import de.jClipCorn.features.online.cover.tmdb.TMDBPosterSearch;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public enum ImageSearchImplementation implements ContinoousEnum<ImageSearchImplementation> {
	GOOGLE_COVER(0), 
	GOOGLE_POSTER(1), 
	IMDB_COVER(2),
	IMDB_SEC_COVER(3), 
	TMDP_POSTER(4);
	
	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("ImageSearchImplementations.GOOGLE_COVER"),
		LocaleBundle.getString("ImageSearchImplementations.GOOGLE_POSTER"),
		LocaleBundle.getString("ImageSearchImplementations.IMDB_COVER"),
		LocaleBundle.getString("ImageSearchImplementations.IMDB_SEC_COVER"),
		LocaleBundle.getString("ImageSearchImplementations.TMDP_POSTER"),
	};
	
	private int id;

	private static EnumWrapper<ImageSearchImplementation> wrapper = new EnumWrapper<>(GOOGLE_COVER);

	private ImageSearchImplementation(int val) {
		id = val;
	}
	
	public static EnumWrapper<ImageSearchImplementation> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(ImageSearchImplementation s1, ImageSearchImplementation s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public ImageSearchImplementation[] evalues() {
		return ImageSearchImplementation.values();
	}
	
	public AbstractImageSearch getImplementation(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		switch (this) {
		case GOOGLE_COVER:
			return new GoogleCoverSearch(fl, uc, pc);
		case GOOGLE_POSTER:
			return new GooglePosterSearch(fl, uc, pc);
		case IMDB_COVER:
			return new IMDBPrimaryCoverSearch(fl, uc, pc);
		case IMDB_SEC_COVER:
			return new IMDBSecondaryCoverSearch(fl, uc, pc);
		case TMDP_POSTER:
			return new TMDBPosterSearch(fl, uc, pc);
		default:
			CCLog.addDefaultSwitchError(this, this);
			return null;
		}
	}
}
