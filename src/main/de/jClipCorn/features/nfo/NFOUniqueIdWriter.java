package de.jClipCorn.features.nfo;

import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineRefType;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.util.Str;
import org.jdom2.Element;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("nls")
public class NFOUniqueIdWriter {

	public static void write(Element root, Iterable<CCSingleOnlineReference> refs, String clipcornId) {
		write(root, refs, Collections.emptySet(), clipcornId);
	}

	/**
	 * Jellyfin/Kodi read every {@code <uniqueid>} into a map keyed by its {@code type} attribute, so a repeated
	 * type silently overwrites the earlier value and the last one wins. Only the first reference per type is
	 * written; {@code skippedTypes} suppresses whole providers.
	 */
	public static void write(Element root, Iterable<CCSingleOnlineReference> refs, Set<String> skippedTypes, String clipcornId) {
		Set<String> written = new HashSet<>();

		for (CCSingleOnlineReference ref : refs) {
			if (ref.type == CCOnlineRefType.NONE) continue;

			String typeId = getKodiProviderType(ref.type);
			if (Str.isNullOrEmpty(typeId)) continue;
			if (skippedTypes.contains(typeId)) continue;
			if (!written.add(typeId)) continue;

			Element uniqueid = new Element("uniqueid");
			uniqueid.setAttribute("type", typeId);
			if (written.size() == 1) uniqueid.setAttribute("default", "true");
			uniqueid.setText(ref.getNfoUniqueId());
			root.addContent(uniqueid);
		}

		Element clipcorn = new Element("uniqueid");
		clipcorn.setAttribute("type", "clipcorn");
		clipcorn.setText(clipcornId);
		root.addContent(clipcorn);
	}

	static String getKodiProviderType(CCOnlineRefType type) {
		switch (type) {
			case IMDB:        return "imdb";
			case THEMOVIEDB:  return "tmdb";
			case ANIDB:       return "anidb";
			case MYANIMELIST: return "myanimelist";
			case ANILIST:     return "anilist";
			default:          return null;
		}
	}
}
