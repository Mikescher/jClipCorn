package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ApplicationHelper;

@SuppressWarnings("nls")
public class TestGeneratedFilenames {

	@Test
	public void testMovieFilename() throws Exception {
		CCLog.setUnitTestMode();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals("Super 8.avi", ml.findDatabaseMovie(4).generateFilename(0));
		assertEquals("Forrest Gump [ENG].mpeg", ml.findDatabaseMovie(18).generateFilename(0));
		assertEquals("Kill Bill I - Volume I (Part 1).avi", ml.findDatabaseMovie(7).generateFilename(0));
		assertEquals("Kill Bill I - Volume I (Part 2).avi", ml.findDatabaseMovie(7).generateFilename(1));
		assertEquals("Buddy haut den Lukas [[SpencerHill]].avi", ml.findDatabaseMovie(12).generateFilename(1));
		assertEquals("Der Herr der Ringe III - Die Rückkehr des Königs.mkv", ml.findDatabaseMovie(9).generateFilename(0));
	}

	@Test
	public void testSeriesFilename() throws Exception {
		CCLog.setUnitTestMode();
		ApplicationHelper.SetOverrideModeUnix();
		
		CCMovieList ml = ClipCornTestHelper.createExampleDB();
		
		assertEquals("Steins;Gate/01 - ONA/S01E01 - Prologue of the Beginning and End.mkv", ml.findDatabaseSeries(10).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals("Steins;Gate/01 - ONA/S01E06 - Divergence of Butterfly Effect.mkv", ml.findDatabaseSeries(10).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(5).getRelativeFileForCreatedFolderstructure());
		assertEquals("Steins;Gate/02 - OVA/S02E25 - Egoistic Poriomania.mkv", ml.findDatabaseSeries(10).getSeasonByArrayIndex(1).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals("Steins;Gate/02 - OVA/S02E25 - Egoistic Poriomania.mkv", ml.findDatabaseSeries(10).getSeasonByArrayIndex(1).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
	
		assertEquals("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E01 - This Self-Proclaimed Goddess and Reincarnation in Another World.mp4", ml.findDatabaseSeries(19).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(0).getRelativeFileForCreatedFolderstructure());
		assertEquals("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E09 - God's Blessing on This Wonderful Shop.mp4", ml.findDatabaseSeries(19).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(8).getRelativeFileForCreatedFolderstructure());
		assertEquals("KonoSuba [JAP]/Kono Subarashii Sekai ni Shukufuku wo!/S01E10 - Final Flame for This Over-the-Top Fortress.mp4", ml.findDatabaseSeries(19).getSeasonByArrayIndex(0).getEpisodeByArrayIndex(9).getRelativeFileForCreatedFolderstructure());
	}

	@Test
	public void testFixStringToFilesystemname() throws Exception {
		assertEquals("Junketsu - Reinheit", PathFormatter.fixStringToFilesystemname("Junketsu – Reinheit"));
		assertEquals("Departure x And x Friends", PathFormatter.fixStringToFilesystemname("Departure × And × Friends"));
		assertEquals(" (Questionmark)", PathFormatter.fixStringToFilesystemname("? (Questionmark)"));
		assertEquals("S.O.S", PathFormatter.fixStringToFilesystemname("S.O.S."));
		assertEquals("Expose", PathFormatter.fixStringToFilesystemname("Exposé"));
	}
}
