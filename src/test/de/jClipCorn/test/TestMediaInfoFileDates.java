package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.helper.MediaInfoHelper;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import static org.junit.Assert.*;

/**
 * Verifies that {@link MediaInfoHelper#refreshMediaInfoFileDates} re-reads cdate/mdate from the file a moved
 * element now points at, and that it leaves elements without any MediaInfo alone.
 */
@SuppressWarnings("nls")
public class TestMediaInfoFileDates extends ClipCornBaseTest {

	@Test
	public void testRefreshReadsDatesFromNewFile() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests();

		var dir  = createAutocleanedDir("mediainfo_dates");
		var file = dir.append("moved.mkv");
		Files.write(file.toPath(), new byte[] { 1, 2, 3, 4 });

		var mov = ml.iteratorMovies().get(0);
		mov.setPartWithoutClearingChecksums(0, CCPath.createFromFSPath(file, Opt.of(false), ml));

		mov.MediaInfo.CDate.set(Opt.of(1L));
		mov.MediaInfo.MDate.set(Opt.of(2L));

		assertTrue(MediaInfoHelper.refreshMediaInfoFileDates(ml, mov));

		var attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

		assertEquals(Opt.of(attr.creationTime().toMillis()),     mov.MediaInfo.CDate.get());
		assertEquals(Opt.of(attr.lastModifiedTime().toMillis()), mov.MediaInfo.MDate.get());
	}

	@Test
	public void testRefreshKeepsChecksums() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests();

		var dir  = createAutocleanedDir("mediainfo_dates");
		var file = dir.append("moved.mkv");
		Files.write(file.toPath(), new byte[] { 1, 2, 3, 4 });

		var mov = ml.iteratorMovies().get(0);
		mov.setPartWithoutClearingChecksums(0, CCPath.createFromFSPath(file, Opt.of(false), ml));

		mov.ChecksumCRC32.set(Opt.of("[\"CRC_A\"]"));
		mov.MediaInfo.CDate.set(Opt.of(1L));
		mov.MediaInfo.MDate.set(Opt.of(2L));

		assertTrue(MediaInfoHelper.refreshMediaInfoFileDates(ml, mov));

		assertEquals(Opt.of("[\"CRC_A\"]"), mov.ChecksumCRC32.get());
	}

	@Test
	public void testRefreshDoesNotCreatePartialMediaInfo() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests();

		var dir  = createAutocleanedDir("mediainfo_dates");
		var file = dir.append("moved.mkv");
		Files.write(file.toPath(), new byte[] { 1, 2, 3, 4 });

		var mov = ml.iteratorMovies().get(0);
		mov.setPartWithoutClearingChecksums(0, CCPath.createFromFSPath(file, Opt.of(false), ml));
		mov.MediaInfo.set(CCMediaInfo.EMPTY);

		assertFalse(MediaInfoHelper.refreshMediaInfoFileDates(ml, mov));

		assertTrue(mov.MediaInfo.get().isFullyEmpty());
	}

	@Test
	public void testRefreshOnMissingFileDoesNothing() throws Exception {
		CCMovieList ml = createExampleDB(false);
		ml.markLoadedForUnitTests();

		var dir  = createAutocleanedDir("mediainfo_dates");
		var file = dir.append("does_not_exist.mkv");

		var mov = ml.iteratorMovies().get(0);
		mov.setPartWithoutClearingChecksums(0, CCPath.createFromFSPath(file, Opt.of(false), ml));

		mov.MediaInfo.CDate.set(Opt.of(1L));
		mov.MediaInfo.MDate.set(Opt.of(2L));

		assertFalse(MediaInfoHelper.refreshMediaInfoFileDates(ml, mov));

		assertEquals(Opt.of(1L), mov.MediaInfo.CDate.get());
		assertEquals(Opt.of(2L), mov.MediaInfo.MDate.get());
	}
}
