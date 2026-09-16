package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.properties.types.PathSyntaxVarList;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestPathSyntaxProperties extends ClipCornBaseTest {

	@Test
	public void testNoStoredVariables() {
		var props = CCProperties.createInMemory();

		assertTrue(props.PROP_PATHSYNTAX_VARIABLES.getValue().Values.isEmpty());
	}

	@Test
	public void testRoundtripAndActiveVariables() {
		var props = CCProperties.createInMemory();

		var host = ApplicationHelper.getHostname();

		props.PROP_PATHSYNTAX_VARIABLES.setValue(new PathSyntaxVarList(List.of(
			new PathSyntaxVar("", "a", CCPath.create("/a/")),
			new PathSyntaxVar(host, "b", CCPath.create("/b/")),
			new PathSyntaxVar(host + "_other", "c", CCPath.create("/c/")),
			PathSyntaxVar.EMPTY,
			new PathSyntaxVar("", "d;\"{}", CCPath.create("/d;\"/")))));

		var v = props.PROP_PATHSYNTAX_VARIABLES.getValue().Values;
		assertEquals(4, v.size());
		assertEquals("d;\"{}", v.get(3).Key);
		assertEquals(CCPath.create("/d;\"/"), v.get(3).Value);

		var active = props.getActivePathVariables();
		assertEquals(List.of("a", "b", "d;\"{}"), active.stream().map(p -> p.Key).toList());
	}

	@Test
	public void testAnimeRoots() throws Exception {
		var ml = createSeededDB();
		var props = ml.ccprops();

		var animeMovDir = createAutocleanedDir("anime_mov");
		var animeSerDir = createAutocleanedDir("anime_ser");

		assertEquals(props.PROP_PATHSYNTAX_MOVIEROOT.getValue(), ml.getMoviesRoot(true));
		assertEquals(props.PROP_PATHSYNTAX_SERIESROOT.getValue(), ml.getSeriesRoot(true));

		props.PROP_PATHSYNTAX_ANIMEMOVIEROOT.setValue(CCPath.createFromFSPath(animeMovDir, ml));
		props.PROP_PATHSYNTAX_ANIMESERIESROOT.setValue(CCPath.createFromFSPath(animeSerDir, ml));

		assertEquals(props.PROP_PATHSYNTAX_MOVIEROOT.getValue(), ml.getMoviesRoot(false));
		assertEquals(props.PROP_PATHSYNTAX_SERIESROOT.getValue(), ml.getSeriesRoot(false));
		assertTrue(ml.getMoviesRootDir(true).equalsOnFilesystem(animeMovDir));
		assertTrue(ml.getSeriesRootDir(true).equalsOnFilesystem(animeSerDir));
		assertEquals(4, ml.getAllRootDirs().size());

		var mov = ml.iteratorMovies().firstOrNull();
		var ser = ml.iteratorSeries().firstOrNull();

		mov.Genres.set(new CCGenreList(CCGenre.GENRE_006));
		ser.Genres.set(new CCGenreList(CCGenre.GENRE_006));

		assertTrue(mov.generateExpectedAbsolutePath(0).toString().startsWith(ml.getMoviesRootDir(false).toString()));
		assertTrue(ser.getSeriesRootDir().equalsOnFilesystem(ml.getSeriesRootDir(false)));

		mov.Genres.set(new CCGenreList(CCGenre.GENRE_022));
		ser.Genres.set(new CCGenreList(CCGenre.GENRE_022));

		assertTrue(mov.generateExpectedAbsolutePath(0).toString().startsWith(animeMovDir.toString()));
		assertTrue(ser.getSeriesRootDir().equalsOnFilesystem(animeSerDir));
	}
}
