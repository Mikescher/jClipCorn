package de.jClipCorn.properties.enumerations;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.online.metadata.Metadataparser;
import de.jClipCorn.online.metadata.imdb.IMDBParserCommon;
import de.jClipCorn.online.metadata.mal.MALParser;
import de.jClipCorn.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;

public enum MetadataParserImplementation implements ContinoousEnum<MetadataParserImplementation> {
	IMDB(0), 
	TMDB(1), 
	MAL(2);
	
	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		LocaleBundle.getString("MetadataSearchImplementation.IMDB"),
		LocaleBundle.getString("MetadataSearchImplementation.TMDB"),
		LocaleBundle.getString("MetadataSearchImplementation.MAL"),
	};
	
	private int id;

	private static EnumWrapper<MetadataParserImplementation> wrapper = new EnumWrapper<>(IMDB);

	private MetadataParserImplementation(int val) {
		id = val;
	}
	
	public static EnumWrapper<MetadataParserImplementation> getWrapper() {
		return wrapper;
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
	
	public Metadataparser getImplementation() {
		switch (this) {
		case IMDB:
			return IMDBParserCommon.GetConfiguredParser();
		case TMDB:
			return new TMDBParser();
		case MAL:
			return new MALParser();
		default:
			CCLog.addDefaultSwitchError(this, this);
			return null;
		}
	}
}
