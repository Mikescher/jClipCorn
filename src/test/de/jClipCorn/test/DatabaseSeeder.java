package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@SuppressWarnings("nls")
public class DatabaseSeeder {

	public static CCMovieList init() throws Exception {

		var tempPath = FilesystemUtils.getTempPath().append(Str.format("jcc_unittests_{0}", UUID.randomUUID()));
		tempPath.mkdirsWithException();

		ClipCornBaseTest.CLEANUP.add(() -> { try { tempPath.deleteRecursive(); } catch (IOException e) { e.printStackTrace(); } });

		var dbPath = tempPath.append("db");
		dbPath.mkdirsWithException();

		CCProperties.create(tempPath.append("jClipcorn.properties"), new String[0]);

		CCProperties.getInstance().PROP_DATABASE_DIR.setValue(dbPath);

		System.out.println("Load DB from: " + tempPath);


		var ml = CCMovieList.createInstanceMovieList();
		ml.connectExternal(true);
		{
			ml.getHistory().enableTrigger();

			seed(CCProperties.getInstance(), ml, tempPath);
		}
		ml.shutdown();


		var mlRet = CCMovieList.createInstanceMovieList();
		mlRet.connectExternal(true);

		ClipCornBaseTest.CLEANUP.add(() -> { try { mlRet.shutdown(); } catch (Exception e) { e.printStackTrace(); } });

		return mlRet;
	}

	private static void seed(CCProperties prop, CCMovieList ml, FSPath dir) throws IOException
	{
		var pathMov = dir.append("mov"); pathMov.mkdirsWithException();
		var pathSer = dir.append("ser"); pathMov.mkdirsWithException();

		CCProperties.getInstance().PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.createFromFSPath(pathMov, Opt.False)));
		CCProperties.getInstance().PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.createFromFSPath(pathSer, Opt.False)));

		{
			var f = pathMov.append("Die Verurteilten.mp4");
			cp("000", "mp4", f);

			var e = ml.createNewEmptyMovie();

			e.beginUpdating();
			{
				e.Title.set("Die Verurteilten");
				e.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("278"), CCSingleOnlineReference.createIMDB("tt0111161")));
				e.Zyklus.set(CCMovieZyklus.EMPTY);
				e.OnlineScore.set(CCOnlineScore.STARS_4_5);
				e.Score.set(CCUserScore.RATING_V);
				e.MediaInfo.set();
				e.Length.set();
				e.Format.set();
				e.FileSize.set();
				e.FSK.set();
				e.ViewedHistory.set();
				e.Language.set();
				e.Year.set();
				e.Groups.set();
				e.AddDate.set();
				e.Genres.set();
				e.Tags.set();
				e.setCover();

				e.Parts.Part0.set(CCPath.createFromFSPath(f));
				e.Parts.Part1.set(CCPath.Empty);
				e.Parts.Part2.set(CCPath.Empty);
				e.Parts.Part3.set(CCPath.Empty);
				e.Parts.Part4.set(CCPath.Empty);
				e.Parts.Part5.set(CCPath.Empty);
			}
			e.endUpdating();

		}
	}

}
