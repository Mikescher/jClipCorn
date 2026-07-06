package de.jClipCorn.test;

import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestEmptyDirectoryValidation extends ClipCornBaseTest {

	private FSPath subdir(FSPath root, String name) throws Exception {
		var d = root.append(name);
		d.mkdirsWithException();
		return d;
	}

	private void touch(FSPath dir, String name) throws Exception {
		dir.append(name).writeAsUTF8TextFile("x");
	}

	// A directory holding only leftover jClipCorn/Jellyfin metadata (an .nfo and/or a generated poster)
	// must be detected as empty and be deletable - the dangling artifacts must not keep it alive.
	@Test
	public void testDanglingNfoAndPosterDoNotPreventDetection() throws Exception {
		var root = createAutocleanedDir("emptydir");

		// leftover movie dir: only "<basename>.nfo" + "<basename>.<ext>" cover remain (see MovieNFOWriter)
		var movieLeftover = subdir(root, "movie_leftover");
		touch(movieLeftover, "The Matrix.nfo");
		touch(movieLeftover, "The Matrix.png");

		// leftover series root: only "tvshow.nfo" + "poster.<ext>" (see SeriesNFOWriter)
		var seriesLeftover = subdir(root, "series_leftover");
		touch(seriesLeftover, "tvshow.nfo");
		touch(seriesLeftover, "poster.png");

		// leftover season folder: only "season.nfo" + "poster.<ext>" (see SeasonNFOWriter)
		var seasonLeftover = subdir(root, "season_leftover");
		touch(seasonLeftover, "season.nfo");
		touch(seasonLeftover, "poster.jpg");

		// classic ignorables (pre-existing behaviour, kept as a regression guard)
		var ignorables = subdir(root, "ignorables");
		touch(ignorables, "Thumbs.db");
		touch(ignorables, "desktop.ini");
		touch(ignorables, "cache.tmp");

		// real content: an actual video file next to its metadata -> must NOT be empty
		var realMovie = subdir(root, "real_movie");
		touch(realMovie, "Film.mkv");
		touch(realMovie, "Film.nfo");
		touch(realMovie, "Film.png");

		// a stray user image without a matching .nfo sibling -> must NOT be treated as empty
		var userImages = subdir(root, "user_images");
		touch(userImages, "vacation.png");

		// --- detection (aggregate walk from the root) ---
		var found = FilesystemUtils.findEmptyDirectories(root, 10, true, null);
		Set<String> foundNames = found.stream().map(FSPath::getLastPathSegment).collect(Collectors.toSet());

		assertTrue("movie_leftover should be detected as empty",  foundNames.contains("movie_leftover"));
		assertTrue("series_leftover should be detected as empty", foundNames.contains("series_leftover"));
		assertTrue("season_leftover should be detected as empty", foundNames.contains("season_leftover"));
		assertTrue("ignorables should be detected as empty",      foundNames.contains("ignorables"));
		assertFalse("real_movie must not be detected as empty",   foundNames.contains("real_movie"));
		assertFalse("user_images must not be detected as empty",  foundNames.contains("user_images"));

		// --- per-directory predicate ---
		assertTrue(FilesystemUtils.isEmptyDirectories(movieLeftover, 10, true));
		assertTrue(FilesystemUtils.isEmptyDirectories(seriesLeftover, 10, true));
		assertTrue(FilesystemUtils.isEmptyDirectories(seasonLeftover, 10, true));
		assertFalse(FilesystemUtils.isEmptyDirectories(realMovie, 10, true));
		assertFalse(FilesystemUtils.isEmptyDirectories(userImages, 10, true));

		// --- deletion: dangling-metadata dirs are removed (incl. their .nfo/poster files) ---
		assertTrue(FilesystemUtils.deleteEmptyDirectory(movieLeftover));
		assertFalse(movieLeftover.exists());

		assertTrue(FilesystemUtils.deleteEmptyDirectory(seriesLeftover));
		assertFalse(seriesLeftover.exists());

		assertTrue(FilesystemUtils.deleteEmptyDirectory(seasonLeftover));
		assertFalse(seasonLeftover.exists());

		// --- deletion: dirs with real content / stray images are left untouched ---
		assertFalse(FilesystemUtils.deleteEmptyDirectory(userImages));
		assertTrue(userImages.exists());
		assertTrue(userImages.append("vacation.png").exists());

		assertFalse(FilesystemUtils.deleteEmptyDirectory(realMovie));
		assertTrue(realMovie.exists());
		assertTrue(realMovie.append("Film.mkv").exists());
	}
}
