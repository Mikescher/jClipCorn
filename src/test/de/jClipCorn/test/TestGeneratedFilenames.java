package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestGeneratedFilenames extends ClipCornBaseTest {

	@Test
	public void testMovieFilename() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals("Super 8.avi", ml.findDatabaseMovie(5).generateFilename(0));
		assertEquals("Forrest Gump [ENG].mpeg", ml.findDatabaseMovie(101).generateFilename(0));
		assertEquals("Kill Bill I - Volume I (Part 1).avi", ml.findDatabaseMovie(8).generateFilename(0));
		assertEquals("Kill Bill I - Volume I (Part 2).avi", ml.findDatabaseMovie(8).generateFilename(1));
		assertEquals("Buddy haut den Lukas [[SpencerHill]].avi", ml.findDatabaseMovie(95).generateFilename(1));
		assertEquals("Der Herr der Ringe III - Die Rückkehr des Königs [GER+ENG].mkv", ml.findDatabaseMovie(10).generateFilename(0));
	}

	@Test
	public void testSeriesFilename() throws Exception {
		CCMovieList ml = createExampleDB();
		
		assertEquals(loc("Steins;Gate/01 - ONA/S01E01 - Prologue of the Beginning and End.mkv"), ml.findDatabaseSeries(11).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals(loc("Steins;Gate/01 - ONA/S01E06 - Divergence of Butterfly Effect.mkv"), ml.findDatabaseSeries(11).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(5).getRelativeFileForCreatedFolderstructure());
		assertEquals(loc("Steins;Gate/02 - OVA/S02E25 - Egoistic Poriomania.mkv"), ml.findDatabaseSeries(11).getSeasonByArrayIndex(1).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals(loc("Steins;Gate/02 - OVA/S02E25 - Egoistic Poriomania.mkv"), ml.findDatabaseSeries(11).getSeasonByArrayIndex(1).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
	
		assertEquals(loc("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E01 - This Self-Proclaimed Goddess and Reincarnation in Another World.mp4"), ml.findDatabaseSeries(102).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals(loc("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E09 - God's Blessing on This Wonderful Shop.mp4"), ml.findDatabaseSeries(102).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(8).getRelativeFileForCreatedFolderstructure());
		assertEquals(loc("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E10 - Final Flame for This Over-the-Top Fortress.mp4"), ml.findDatabaseSeries(102).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(9).getRelativeFileForCreatedFolderstructure());
	}

	private String loc(String s) {
		return s.replace('/', File.separatorChar);
	}

	@Test
	public void testFixStringToFilesystemname() {
		assertEquals("Junketsu - Reinheit",       FilesystemUtils.fixStringToFilesystemname("Junketsu – Reinheit"));
		assertEquals("Departure x And x Friends", FilesystemUtils.fixStringToFilesystemname("Departure × And × Friends"));
		assertEquals(" (Questionmark)",           FilesystemUtils.fixStringToFilesystemname("? (Questionmark)"));
		assertEquals("S.O.S",                     FilesystemUtils.fixStringToFilesystemname("S.O.S."));
		assertEquals("Expose",                    FilesystemUtils.fixStringToFilesystemname("Exposé"));
	}
}
