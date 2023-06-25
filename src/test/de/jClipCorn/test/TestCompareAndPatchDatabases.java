package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.features.databaseErrors.CCDatabaseValidator;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseValidatorOptions;
import de.jClipCorn.features.serialization.xmlexport.DatabaseXMLExporter;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
import de.jClipCorn.gui.frames.applyPatchFrame.APFWorker;
import de.jClipCorn.gui.frames.applyPatchFrame.PatchExecOptions;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CDFWorkerCompare;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CDFWorkerPatch;
import de.jClipCorn.gui.frames.compareDatabaseFrame.CompareDatabaseRuleset;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.RegExHelper;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.listener.DoubleProgressCallbackProgressBarHelper;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestCompareAndPatchDatabases extends ClipCornBaseTest {

	@Test
	public void testPatch() throws Exception {
		CCMovieList mlBase = createSeededDB();
		CCMovieList mlDiff = createSeededDB();

		// [1] Delete movie 'Stirb Langsam I - Stirb Langsam'
		var m2 = mlDiff.iteratorMovies().singleOrNull(e -> Str.equals(e.OnlineReference.get().Main.id, "movie/562"));
		m2.delete();

		// [2] Remove episode 'S02E02 - My Purest Heart for You'
		var m3 = mlDiff.iteratorEpisodes().singleOrNull(e -> Str.equals(e.Title.get(), "My Purest Heart for You"));
		m3.delete();

		// [3] change meta 'Stirb Langsam II - Die Hard 2'
		var m4 = mlDiff.iteratorMovies().singleOrNull(e -> Str.equals(e.OnlineReference.get().Main.id, "movie/1573"));
		m4.FSK.set(CCFSK.RATING_0);
		m4.Genres.tryAddGenre(CCGenre.GENRE_038);

		// [4] add file 'S01E03 - Ein neuer Schüler'
		// (by removing one episode from base)
		var m5 = mlBase.iteratorEpisodes().singleOrNull(e -> Str.equals(e.getTitle(), "Ein neuer Schüler"));
		m5.delete();
		m5.getPart().toFSPath(mlBase).deleteWithException();

		// =========================================================================================

		mlBase.shutdown();

		var ruleset = CompareDatabaseRuleset.parse(Str.Empty);

		var state = CDFWorkerCompare.compare(DoubleProgressCallbackProgressBarHelper.EMPTY, ruleset, mlBase.getDatabaseDirectory().getParent(), "ClipCornDB", mlDiff);

		var patchPath = createAutocleanedDir("dbpatch");

		CDFWorkerPatch.createPatch(state, patchPath, DoubleProgressCallbackProgressBarHelper.EMPTY, false, false, false, false);

		// =========================================================================================

		mlBase = reloadDBAfterShutdown(mlBase);

		var patchFilePath = patchPath.append("patch.jccpatch");

		var apf_data = APFWorker.readPatch(patchFilePath);

		var trashDir = createAutocleanedDir("trash");

		var opt_patch = new PatchExecOptions(
				patchFilePath,
				FSPath.create(apf_data+".state"),
				mlBase.getCommonMoviesPath().toFSPath(mlBase),
				mlBase.getCommonSeriesPath().toFSPath(mlBase),
				/* autoDestSeries */ true,
				trashDir,
				trashDir,
				patchFilePath.getParent().append("patch_data"),
				/* procelain */ false);

		APFWorker.applyPatch(apf_data.Item1, mlBase, apf_data.Item2, opt_patch, DoubleProgressCallbackProgressBarHelper.EMPTY, (dold, dnew) -> { });

		// =========================================================================================

		mlBase.shutdown();
		mlBase = reloadDBAfterShutdown(mlBase);

		var export_should = DatabaseXMLExporter.export(mlDiff.iteratorElements().enumerate(), new ExportOptions(true, true, false, false));
		var export_actual = DatabaseXMLExporter.export(mlBase.iteratorElements().enumerate(), new ExportOptions(true, true, false, false));

		XMLOutputter xout = new XMLOutputter();
		xout.setFormat(Format.getPrettyFormat());

		var xml_should = xout.outputString(export_should);
		var xml_actual = xout.outputString(export_actual);

		xml_should = RegExHelper.replaceAll("(adddate|mediainfo.mdate|mediainfo.cdate|history)=\"[^\"]*\"", xml_should, "...");
		xml_actual = RegExHelper.replaceAll("(adddate|mediainfo.mdate|mediainfo.cdate|history)=\"[^\"]*\"", xml_actual, "...");

		assertEquals(xml_should, xml_actual);

		// =========================================================================================

		DatabaseValidatorOptions opt_val = new DatabaseValidatorOptions(
				true,  // movies
				true,  // series
				true,  // seasons
				true,  // episodes
				true,  // covers
				true,  // cover files
				true,  // video files
				true,  // groups
				true,  // online-refs
				true,  // internal db
				true,  // Additional
				true,  // Validate Series Structure
				true); // Ignore IFO duplicates

		List<DatabaseError> errs = new ArrayList<>();

		var validator = new CCDatabaseValidator(mlBase);
		validator.validate(errs, opt_val, DoubleProgressCallbackListener.EMPTY);

		assertEmptyErrors(errs);

		// =========================================================================================

		assertEquals(mlDiff.getCoverCount(),   mlBase.getCoverCount());
		assertEquals(mlDiff.getGroupCount(),   mlBase.getGroupCount());
		assertEquals(mlDiff.getMovieCount(),   mlBase.getMovieCount());
		assertEquals(mlDiff.getSeriesCount(),  mlBase.getSeriesCount());
		assertEquals(mlDiff.getSeasonCount(),  mlBase.getSeasonCount());
		assertEquals(mlDiff.getEpisodeCount(), mlBase.getEpisodeCount());
	}

}
