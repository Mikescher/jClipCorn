package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
