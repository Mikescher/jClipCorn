package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.nfo.NFOUniqueIdWriter;
import org.jdom2.Element;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SuppressWarnings("nls")
public class TestNFOUniqueId extends ClipCornBaseTest {

	// TMDB ids are stored internally with a "movie/" or "tv/" type-prefix (it is part of the api url),
	// but the NFO <uniqueid> must contain only the bare numeric id - otherwise Jellyfin/Emby throw
	// `System.FormatException: The input string 'tv/63174' was not in a correct format`.

	@Test
	public void testTmdbSeriesPrefixIsStripped() {
		assertEquals("63174", CCSingleOnlineReference.createTMDB("tv/63174").getNfoUniqueId());
	}

	@Test
	public void testTmdbMoviePrefixIsStripped() {
		assertEquals("550", CCSingleOnlineReference.createTMDB("movie/550").getNfoUniqueId());
	}

	@Test
	public void testNonTmdbIdsAreUnchanged() {
		assertEquals("tt0137523", CCSingleOnlineReference.createIMDB("tt0137523").getNfoUniqueId());
		assertEquals("12345", CCSingleOnlineReference.createAniList("12345").getNfoUniqueId());
		assertEquals("987", CCSingleOnlineReference.createAniDB("987").getNfoUniqueId());
	}

	@Test
	public void testTmdbWithoutPrefixIsUnchanged() {
		// defensive: a bare numeric tmdb id (no "movie/"/"tv/") must not be mangled
		assertEquals("63174", CCSingleOnlineReference.createTMDB("63174").getNfoUniqueId());
	}

	// Jellyfin reads <uniqueid> into a map keyed by @type - a repeated type overwrites the earlier value,
	// so a series holding two tmdb references would silently be identified as the second one.

	@Test
	public void testRepeatedProviderTypeIsWrittenOnlyOnce() {
		Element root = new Element("tvshow");
		NFOUniqueIdWriter.write(root, List.of(
				CCSingleOnlineReference.createMyAnimeList("28121"),
				CCSingleOnlineReference.createTMDB("tv/62745"),
				CCSingleOnlineReference.createTMDB("tv/70590"),
				CCSingleOnlineReference.createIMDB("tt4728568"),
				CCSingleOnlineReference.createIMDB("tt6768600")), "42201");

		assertEquals(List.of("myanimelist:28121", "tmdb:62745", "imdb:tt4728568", "clipcorn:42201"), dump(root));
	}

	@Test
	public void testOnlyTheFirstReferenceIsMarkedDefault() {
		Element root = new Element("tvshow");
		NFOUniqueIdWriter.write(root, List.of(
				CCSingleOnlineReference.createMyAnimeList("39468"),
				CCSingleOnlineReference.createTMDB("tv/91768")), "32936");

		assertEquals("true", root.getChildren("uniqueid").get(0).getAttributeValue("default"));
		assertNull(root.getChildren("uniqueid").get(1).getAttributeValue("default"));
	}

	@Test
	public void testSkippedTypesAreOmitted() {
		Element root = new Element("tvshow");
		NFOUniqueIdWriter.write(root, List.of(
				CCSingleOnlineReference.createMyAnimeList("39468"),
				CCSingleOnlineReference.createTMDB("tv/91768"),
				CCSingleOnlineReference.createIMDB("tt10885406")), Set.of("tmdb", "imdb"), "32936");

		assertEquals(List.of("myanimelist:39468", "clipcorn:32936"), dump(root));
	}

	private static List<String> dump(Element root) {
		return root.getChildren("uniqueid").stream()
				.map(e -> e.getAttributeValue("type") + ":" + e.getText())
				.collect(Collectors.toList());
	}
}
