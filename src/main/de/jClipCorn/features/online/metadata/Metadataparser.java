package de.jClipCorn.features.online.metadata;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.datatypes.Tuple;

import java.util.ArrayList;
import java.util.List;

public abstract class Metadataparser {

	protected final CCMovieList movielist;

	protected Metadataparser(CCMovieList ml) {
		movielist = ml;
	}

	public CCProperties ccprops() {
		return movielist.ccprops();
	}

	public static List<Metadataparser> listParser(CCMovieList ml) {
		List<Metadataparser> result = new ArrayList<>();
		for (MetadataParserImplementation impl : ml.ccprops().PROP_METAPARSER_IMPL.getValue()) {
			result.add(impl.getImplementation(ml));
		}
		return result;
	}
	
	public abstract List<Tuple<String, CCSingleOnlineReference>> searchByText(String text, OnlineSearchType type);
	public abstract MetadataParserImplementation getImplType();

	public abstract OnlineMetadata getMetadata(CCSingleOnlineReference ref, boolean downloadCover);
}
