package de.jClipCorn.online.metadata;

import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.online.OnlineSearchType;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Tuple;

public abstract class Metadataparser {
	
	public static List<Metadataparser> listParser() {
		List<Metadataparser> result = new ArrayList<>();
		for (MetadataParserImplementation impl : CCProperties.getInstance().PROP_METAPARSER_IMPL.getValue()) {
			result.add(impl.getImplementation());
		}
		return result;
	}
	
	public abstract List<Tuple<String, CCOnlineReference>> searchByText(String text, OnlineSearchType type);
	public abstract MetadataParserImplementation getImplType();

	public abstract OnlineMetadata getMetadata(CCOnlineReference ref, boolean downloadCover);
}