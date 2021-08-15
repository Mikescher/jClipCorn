package de.jClipCorn.properties.enumerations;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.anilist.AniListParser;
import de.jClipCorn.features.online.metadata.imdb.IMDBParserCommon;
import de.jClipCorn.features.online.metadata.mal.MALParser;
import de.jClipCorn.features.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.enumextension.IEnumWrapper;

public enum MetadataParserImplementation implements ContinoousEnum<MetadataParserImplementation> {
	IMDB(0), 
	TMDB(1),
	MAL(2),
	ANILIST(3);

	@SuppressWarnings("nls")
	private final static String[] NAMES = {
		LocaleBundle.getString("MetadataSearchImplementation.IMDB"),
		LocaleBundle.getString("MetadataSearchImplementation.TMDB"),
		LocaleBundle.getString("MetadataSearchImplementation.MAL"),
		LocaleBundle.getString("MetadataSearchImplementation.ANILIST"),
	};
	
	private int id;

	private static final EnumWrapper<MetadataParserImplementation> wrapper = new EnumWrapper<>(IMDB);

	MetadataParserImplementation(int val) {
		id = val;
	}
	
	public static EnumWrapper<MetadataParserImplementation> getWrapper() {
		return wrapper;
	}

	@Override
	public IEnumWrapper wrapper() {
		return getWrapper();
	}
	
	@Override
	public int asInt() {
		return id;
	}

	public static int compare(MetadataParserImplementation s1, MetadataParserImplementation s2) {
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
	public MetadataParserImplementation[] evalues() {
		return MetadataParserImplementation.values();
	}
	
	public Metadataparser getImplementation(CCMovieList ml) {
		switch (this) {
		case IMDB:
			return IMDBParserCommon.GetConfiguredParser(ml);
		case TMDB:
			return new TMDBParser(ml);
		case MAL:
			return new MALParser(ml);
		case ANILIST:
			return new AniListParser(ml);
		default:
			CCLog.addDefaultSwitchError(this, this);
			return null;
		}
	}
}
