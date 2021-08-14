package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.metadata.PartialMediaInfo;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.xml.CCXMLException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

@SuppressWarnings("nls")
public class DatabaseSeeder {

	public static CCMovieList init() throws Exception {

		var tempPath = FilesystemUtils.getTempPath().append("jcc_unittests").append(Str.format("databaseseeder_{0}_{1}", CCDateTime.getCurrentDateTime().toStringFilesystem(), UUID.randomUUID()));
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

	private static void seed(CCProperties prop, CCMovieList ml, FSPath dir) throws Exception
	{
		var pathMov = dir.append("mov"); pathMov.mkdirsWithException();
		var pathSer = dir.append("ser"); pathSer.mkdirsWithException();

		prop.PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.createFromFSPath(pathMov, Opt.False)));
		prop.PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.createFromFSPath(pathSer, Opt.False)));

		{
			var f = pathMov.append("Die Verurteilten.mp4");
			cp("000", "mp4", f);

			var e = ml.createNewEmptyMovie();

			e.beginUpdating();
			{
				e.Title.set("Die Verurteilten");
				e.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/278"), CCSingleOnlineReference.createIMDB("tt0111161")));
				e.Zyklus.set(CCMovieZyklus.EMPTY);
				e.OnlineScore.set(CCOnlineScore.STARS_4_5);
				e.Score.set(CCUserScore.RATING_V);
				e.MediaInfo.set(mi("000", f));
				e.Length.set(25);
				e.Format.set(CCFileFormat.MP4);
				e.FileSize.set(new CCFileSize(1166926));
				e.FSK.set(CCFSK.RATING_II);
				e.ViewedHistory.set(CCDateTimeList.createEmpty());
				e.Language.set(CCDBLanguageList.GERMAN);
				e.Year.set(1994);
				e.Groups.set(CCGroupList.EMPTY);
				e.AddDate.set(CCDate.create(23, 9, 2010));
				e.Genres.set(CCGenreList.create(CCGenre.GENRE_014, CCGenre.GENRE_040));
				e.Tags.set(CCTagList.EMPTY);
				e.setCover(cvr("000"));

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

	private static BufferedImage cvr(String id) throws IOException {
		return SimpleFileUtils.readImageResource("/media/demo_"+id+".cover.png", DatabaseSeeder.class);
	}

	private static void cp(String id, String ext, FSPath dest) throws IOException
	{
		SimpleFileUtils.writeGzippedResource(dest, "/media/demo_"+id+"."+ext+".gz", DatabaseSeeder.class);
	}

	private static PartialMediaInfo mi(String id, FSPath dest) throws InnerMediaQueryException, CCXMLException, IOException
	{
		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", DatabaseSeeder.class);

		var fvhash = ChecksumHelper.fastVideoHash(dest);

		var attr = dest.readFileAttr();

		return MediaQueryRunner.parse(out, fvhash, attr, false).toPartial();
	}
}
