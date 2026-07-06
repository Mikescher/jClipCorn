package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.CCPath;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Verifies that a pure file *move* (path changes, content unchanged) keeps the saved
 * file-checksums (CRC32/MD5/SHA256/SHA512) via {@code setPartKeepChecksums(...)}, while a normal
 * path change (repoint to a possibly different file) still clears them (the safety net).
 */
@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestChecksumPreservation extends ClipCornBaseTest {

	// ---------- helpers ----------

	private void seedEpisodeChecksums(CCEpisode e, String suffix) {
		e.ChecksumCRC32.set( Opt.of("CRC"    + suffix));
		e.ChecksumMD5.set(   Opt.of("MD5"    + suffix));
		e.ChecksumSHA256.set(Opt.of("SHA256" + suffix));
		e.ChecksumSHA512.set(Opt.of("SHA512" + suffix));
	}

	private void assertEpisodeChecksums(CCEpisode e, String suffix) {
		assertEquals(Opt.of("CRC"    + suffix), e.ChecksumCRC32.get());
		assertEquals(Opt.of("MD5"    + suffix), e.ChecksumMD5.get());
		assertEquals(Opt.of("SHA256" + suffix), e.ChecksumSHA256.get());
		assertEquals(Opt.of("SHA512" + suffix), e.ChecksumSHA512.get());
	}

	private void seedMovieChecksums(CCMovie m, String crc, String md5, String sha256, String sha512) {
		m.ChecksumCRC32.set( Opt.of(crc));
		m.ChecksumMD5.set(   Opt.of(md5));
		m.ChecksumSHA256.set(Opt.of(sha256));
		m.ChecksumSHA512.set(Opt.of(sha512));
	}

	private void assertMovieChecksums(CCMovie m, String crc, String md5, String sha256, String sha512) {
		assertEquals(Opt.of(crc),    m.ChecksumCRC32.get());
		assertEquals(Opt.of(md5),    m.ChecksumMD5.get());
		assertEquals(Opt.of(sha256), m.ChecksumSHA256.get());
		assertEquals(Opt.of(sha512), m.ChecksumSHA512.get());
	}

	private void assertEpisodeChecksumsEmpty(CCEpisode e) {
		assertTrue(e.ChecksumCRC32.get().isEmpty());
		assertTrue(e.ChecksumMD5.get().isEmpty());
		assertTrue(e.ChecksumSHA256.get().isEmpty());
		assertTrue(e.ChecksumSHA512.get().isEmpty());
	}

	private void assertMovieChecksumsEmpty(CCMovie m) {
		assertTrue(m.ChecksumCRC32.get().isEmpty());
		assertTrue(m.ChecksumMD5.get().isEmpty());
		assertTrue(m.ChecksumSHA256.get().isEmpty());
		assertTrue(m.ChecksumSHA512.get().isEmpty());
	}

	// ---------- episode: safety net (plain set clears) ----------

	@Test
	@Parameters({ "false", "true" })
	public void testEpisodePlainSetClearsChecksums(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);
		ml.markLoadedForUnitTests(); // activate the loading-gated ChecksumHelper clear-listener (as in the real app)

		var ep = ml.iteratorEpisodes().get(0);
		seedEpisodeChecksums(ep, "_E1");
		assertEpisodeChecksums(ep, "_E1"); // sanity: seeding does not clear

		var newPath = CCPath.create(ep.getPart().toString() + "_moved");
		assertNotEquals(ep.getPart(), newPath);

		ep.Part.set(newPath); // normal repoint -> ChecksumHelper clear-listener fires (synchronous in unit-test mode)

		assertEquals(newPath, ep.getPart());
		assertEpisodeChecksumsEmpty(ep);
	}

	// ---------- episode: move keeps checksums ----------

	@Test
	@Parameters({ "false", "true" })
	public void testEpisodeKeepChecksumsOnMove(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);
		ml.markLoadedForUnitTests(); // clear-listener active - proves the keep-setter still preserves

		var ep = ml.iteratorEpisodes().get(0);
		seedEpisodeChecksums(ep, "_E1");

		var newPath = CCPath.create(ep.getPart().toString() + "_moved");
		assertNotEquals(ep.getPart(), newPath);

		ep.setPartWithoutClearingChecksums(newPath);

		assertEquals(newPath, ep.getPart());
		assertEpisodeChecksums(ep, "_E1"); // preserved
	}

	// ---------- movie: safety net (plain set clears) ----------

	@Test
	@Parameters({ "false", "true" })
	public void testMoviePlainSetClearsChecksums(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);
		ml.markLoadedForUnitTests(); // activate the loading-gated ChecksumHelper clear-listener (as in the real app)

		var mov = ml.iteratorMovies().get(0);
		seedMovieChecksums(mov, "[\"CRC_A\"]", "[\"MD5_A\"]", "[\"SHA256_A\"]", "[\"SHA512_A\"]");

		var newPath = CCPath.create(mov.Parts.get(0).toString() + "_moved");
		assertNotEquals(mov.Parts.get(0), newPath);

		mov.Parts.set(0, newPath); // normal repoint -> clears

		assertEquals(newPath, mov.Parts.get(0));
		assertMovieChecksumsEmpty(mov);
	}

	// ---------- movie: move keeps checksums ----------

	@Test
	@Parameters({ "false", "true" })
	public void testMovieKeepChecksumsOnMove(boolean dbmode) throws Exception {
		CCMovieList ml = createExampleDB(dbmode);
		ml.markLoadedForUnitTests(); // clear-listener active - proves the keep-setter still preserves

		var mov = ml.iteratorMovies().get(0);
		seedMovieChecksums(mov, "[\"CRC_A\"]", "[\"MD5_A\"]", "[\"SHA256_A\"]", "[\"SHA512_A\"]");

		var newPath = CCPath.create(mov.Parts.get(0).toString() + "_moved");
		assertNotEquals(mov.Parts.get(0), newPath);

		mov.setPartWithoutClearingChecksums(0, newPath);

		assertEquals(newPath, mov.Parts.get(0));
		assertMovieChecksums(mov, "[\"CRC_A\"]", "[\"MD5_A\"]", "[\"SHA256_A\"]", "[\"SHA512_A\"]"); // preserved
	}

	// ---------- movie: multi-part index alignment preserved ----------

	@Test
	public void testMovieKeepChecksumsMultiPartAlignment() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests(); // clear-listener active - proves the keep-setter still preserves

		var mov = ml.iteratorMovies().get(0);

		var p0 = mov.Parts.get(0);
		var pathB = CCPath.create(p0.toString() + "_part2");
		mov.Parts.set(1, pathB); // ensure a 2nd part (this clears the still-empty checksums)

		int partcount = mov.getPartcount();
		assertTrue(partcount >= 2);

		// per-part checksum JSON arrays (aligned with the 2 parts)
		seedMovieChecksums(mov, "[\"CRC_A\",\"CRC_B\"]", "[\"MD5_A\",\"MD5_B\"]", "[\"SHA256_A\",\"SHA256_B\"]", "[\"SHA512_A\",\"SHA512_B\"]");

		var newP0 = CCPath.create(p0.toString() + "_moved");
		mov.setPartWithoutClearingChecksums(0, newP0);

		assertEquals(partcount, mov.getPartcount());
		assertEquals(newP0, mov.Parts.get(0));
		assertEquals(pathB, mov.Parts.get(1)); // untouched
		assertMovieChecksums(mov, "[\"CRC_A\",\"CRC_B\"]", "[\"MD5_A\",\"MD5_B\"]", "[\"SHA256_A\",\"SHA256_B\"]", "[\"SHA512_A\",\"SHA512_B\"]");
	}

	// ---------- DB round-trip: preserved checksums are actually persisted ----------

	@Test
	public void testEpisodeKeepChecksumsPersistsToDB() throws Exception {
		CCMovieList ml = createExampleDB(false); // ml is backed by an in-memory SQLite DB
		ml.markLoadedForUnitTests(); // clear-listener active - proves preservation survives to the DB

		var ep = ml.iteratorEpisodes().get(0);
		int id = ep.getLocalID();
		seedEpisodeChecksums(ep, "_DBE");

		var newPath = CCPath.create(ep.getPart().toString() + "_moved");
		ep.setPartWithoutClearingChecksums(newPath); // updateDB=true -> persisted with preserved checksums

		var ml2 = CCMovieList.recreateRawForUnitTests(ml.getDatabaseForUnitTests(), ml.ccprops(), ml.getCoverCache());
		var ep2 = ml2.iteratorEpisodes().singleOrNull(e -> e.getLocalID() == id);

		assertNotNull(ep2);
		assertEquals(newPath, ep2.getPart());
		assertEpisodeChecksums(ep2, "_DBE");
	}

	@Test
	public void testMovieKeepChecksumsPersistsToDB() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests(); // clear-listener active - proves preservation survives to the DB

		var mov = ml.iteratorMovies().get(0);
		int id = mov.getLocalID();
		seedMovieChecksums(mov, "[\"CRC_A\"]", "[\"MD5_A\"]", "[\"SHA256_A\"]", "[\"SHA512_A\"]");

		var newPath = CCPath.create(mov.Parts.get(0).toString() + "_moved");
		mov.setPartWithoutClearingChecksums(0, newPath);

		var ml2 = CCMovieList.recreateRawForUnitTests(ml.getDatabaseForUnitTests(), ml.ccprops(), ml.getCoverCache());
		var mov2 = ml2.findDatabaseMovie(id);

		assertNotNull(mov2);
		assertEquals(newPath, mov2.Parts.get(0));
		assertMovieChecksums(mov2, "[\"CRC_A\"]", "[\"MD5_A\"]", "[\"SHA256_A\"]", "[\"SHA512_A\"]");
	}
}
