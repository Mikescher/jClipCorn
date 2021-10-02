package de.jClipCorn.test;

import de.jClipCorn.Main;
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
import de.jClipCorn.util.exceptions.FVHException;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ChecksumHelper;
import de.jClipCorn.util.xml.CCXMLException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("nls")
public class DatabaseSeeder {

	public static CCMovieList init(boolean reloadAfterCreate) throws Exception {

		var tempPath = ClipCornBaseTest.createAutocleanedDir("databaseseeder");

		var dbPath = tempPath.append("db");
		dbPath.mkdirsWithException();

		var props = CCProperties.create(tempPath.append(Main.PROPERTIES_PATH), new String[0]);

		props.PROP_DATABASE_DIR.setValue(dbPath);

		System.out.println("Load DB from: " + tempPath);

		var ml = CCMovieList.createInstanceMovieList(props);
		ml.connectExternal(true);
		{
			ml.getHistory().enableTrigger();

			seed(props, ml, tempPath);
		}

		if (reloadAfterCreate)
		{
			ml.shutdown();

			var mlRet = CCMovieList.createInstanceMovieList(props);
			mlRet.connectExternal(true);

			ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Shutdown ML"); mlRet.shutdown(); });

			return mlRet;
		}
		else
		{
			ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Shutdown ML"); ml.shutdown(); });

			return ml;
		}

	}

	private static void seed(CCProperties prop, CCMovieList ml, FSPath dir) throws Exception
	{
		var pathMov = dir.append("mov"); pathMov.mkdirsWithException();
		var pathSer = dir.append("ser"); pathSer.mkdirsWithException();

		prop.PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.createFromFSPath(pathMov, Opt.False, ml)));
		prop.PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.createFromFSPath(pathSer, Opt.False, ml)));

		//ml.addGroup(CCGroup.create("DCU Animated",        true,  Str.Empty,           false));
		ml.addGroup(CCGroup.create("BruceWillis",         false, Str.Empty,           false));
		ml.addGroup(CCGroup.create("Animation Studios",   false, Str.Empty,           false));
		ml.addGroup(CCGroup.create("Studio Gainax",       false, "Animation Studios", true));
		//ml.addGroup(CCGroup.create("Studio A-1 Pictures", false, "Animation Studios", true));
		//ml.addGroup(CCGroup.create("Studio Bones",        false, "Animation Studios", true));

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
				e.MediaInfo.set(mi(ml, "000", f));
				e.Length.set(25);
				e.Format.set(CCFileFormat.MP4);
				e.FileSize.set(new CCFileSize(1_166_926));
				e.FSK.set(CCFSK.RATING_II);
				e.ViewedHistory.set(CCDateTimeList.createEmpty());
				e.Language.set(CCDBLanguageSet.GERMAN);
				e.Year.set(1994);
				e.Groups.set(CCGroupList.EMPTY);
				e.AddDate.set(CCDate.create(23, 9, 2010));
				e.Genres.set(CCGenreList.create(CCGenre.GENRE_014, CCGenre.GENRE_040));
				e.Tags.set(CCTagList.EMPTY);
				e.setCover(cvr("000"));

				e.Parts.Part0.set(CCPath.createFromFSPath(f, ml));
				e.Parts.Part1.set(CCPath.Empty);
				e.Parts.Part2.set(CCPath.Empty);
				e.Parts.Part3.set(CCPath.Empty);
				e.Parts.Part4.set(CCPath.Empty);
				e.Parts.Part5.set(CCPath.Empty);
			}
			e.endUpdating();
		}

		{
			var f = pathMov.append("Stirb Langsam I - Stirb Langsam [GER+ENG].mkv");
			cp("001", "mkv", f);

			var e = ml.createNewEmptyMovie();

			e.beginUpdating();
			{
				e.Title.set("Stirb Langsam");
				e.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/562"), CCSingleOnlineReference.createIMDB("tt0095016")));
				e.Zyklus.set(new CCMovieZyklus("Stirb Langsam", 1));
				e.OnlineScore.set(CCOnlineScore.STARS_4_0);
				e.Score.set(CCUserScore.RATING_NO);
				e.MediaInfo.set(mi(ml, "001", f));
				e.Length.set(42);
				e.Format.set(CCFileFormat.MKV);
				e.FileSize.set(new CCFileSize(1_545_782));
				e.FSK.set(CCFSK.RATING_IV);
				e.ViewedHistory.set(CCDateTimeList.create(CCDateTime.createFromSQL("2019-04-01"), CCDateTime.createFromSQL("2020-08-17 20:08:30"), CCDateTime.createFromSQL("2021-01-03 20:08:30")));
				e.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH));
				e.Subtitles.set(CCDBLanguageList.create(CCDBLanguage.ENGLISH, CCDBLanguage.FRENCH));
				e.Year.set(1988);
				e.Groups.set(CCGroupList.create(CCGroup.create("BruceWillis")));
				e.AddDate.set(CCDate.create(6, 8, 2015));
				e.Genres.set(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_019, CCGenre.GENRE_014));
				e.Tags.set(CCTagList.create(CCSingleTag.TAG_WATCH_LATER));
				e.setCover(cvr("001"));

				e.Parts.Part0.set(CCPath.createFromFSPath(f, ml));
				e.Parts.Part1.set(CCPath.Empty);
				e.Parts.Part2.set(CCPath.Empty);
				e.Parts.Part3.set(CCPath.Empty);
				e.Parts.Part4.set(CCPath.Empty);
				e.Parts.Part5.set(CCPath.Empty);
			}
			e.endUpdating();
		}

		{
			var f1 = pathMov.append("Stirb Langsam II - Die Hard 2 (Part 1).avi");
			var f2 = pathMov.append("Stirb Langsam II - Die Hard 2 (Part 2).avi");
			cp("002", "avi", f1);
			cp("006", "avi", f2);

			var e = ml.createNewEmptyMovie();

			e.beginUpdating();
			{
				e.Title.set("Die Hard 2");
				e.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/1573"), Arrays.asList(CCSingleOnlineReference.createIMDB("tt0099423"), CCSingleOnlineReference.createAmazon("B00FYI7D6C"))));
				e.Zyklus.set(new CCMovieZyklus("Stirb Langsam", 2));
				e.OnlineScore.set(CCOnlineScore.STARS_3_5);
				e.Score.set(CCUserScore.RATING_NO);
				e.MediaInfo.set(mi(ml, "002", f1));
				e.Length.set(58+125);
				e.Format.set(CCFileFormat.AVI);
				e.FileSize.set(new CCFileSize(12_170_130+21_899_356));
				e.FSK.set(CCFSK.RATING_IV);
				e.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2021-06-06 07:08:00")));
				e.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN));
				e.Year.set(1990);
				e.Groups.set(CCGroupList.create(CCGroup.create("BruceWillis")));
				e.AddDate.set(CCDate.create(15, 1, 2017));
				e.Genres.set(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_019, CCGenre.GENRE_014, CCGenre.GENRE_010));
				e.Tags.set(CCTagList.create(CCSingleTag.TAG_BAD_QUALITY));
				e.setCover(cvr("002"));

				e.Parts.Part0.set(CCPath.createFromFSPath(f1, ml));
				e.Parts.Part1.set(CCPath.createFromFSPath(f2, ml));
				e.Parts.Part2.set(CCPath.Empty);
				e.Parts.Part3.set(CCPath.Empty);
				e.Parts.Part4.set(CCPath.Empty);
				e.Parts.Part5.set(CCPath.Empty);
			}
			e.endUpdating();
		}

		{
			var ser = ml.createNewEmptySeries();

			ser.beginUpdating();
			{
				ser.Title.set("Neon Genesis Evangelion");
				ser.Groups.set(CCGroupList.create(CCGroup.create("Studio Gainax")));
				ser.Genres.set(CCGenreList.create(CCGenre.GENRE_022, CCGenre.GENRE_019, CCGenre.GENRE_009, CCGenre.GENRE_008, CCGenre.GENRE_040, CCGenre.GENRE_054));
				ser.OnlineScore.set(CCOnlineScore.STARS_4_0);
				ser.FSK.set(CCFSK.RATING_III);
				ser.Score.set(CCUserScore.RATING_IV);
				ser.OnlineReference.set(
						CCOnlineReferenceList.create(CCSingleOnlineReference.createMyAnimeList("30"),
						Arrays.asList(
								CCSingleOnlineReference.createTMDB("tv/890"),
								CCSingleOnlineReference.createIMDB("tt0112159"),
								CCSingleOnlineReference.createAniList("30"),
								CCSingleOnlineReference.createKitsu("neon-genesis-evangelion"),
								CCSingleOnlineReference.createAniDB("22", "Neon Genesis Evangelion"),
								CCSingleOnlineReference.createAniDB("202", "The End of Evangelion"))));
				ser.Tags.set(CCTagList.EMPTY);
				ser.setCover(cvr2("100"));
			}
			ser.endUpdating();

			{
				var sea = ml.createNewEmptySeason(ser);

				sea.beginUpdating();
				{
					sea.Title.set("01 - Neon Genesis Evangelion");
					sea.Year.set(1995);
					sea.setCover(cvr2("101"));
				}
				sea.endUpdating();

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E01 - Angriff der Engel.mkv");
					cp("003", "mkv", f);

					var epis = ml.createNewEmptyEpisode(sea);

					epis.beginUpdating();
					{
						epis.Title.set("Angriff der Engel");
						epis.EpisodeNumber.set(1);
						epis.MediaInfo.set(mi(ml, "003", f));
						epis.Length.set(75);
						epis.Tags.set(CCTagList.EMPTY);
						epis.Format.set(CCFileFormat.MKV);
						epis.FileSize.set(2_826_740);
						epis.Part.set(CCPath.createFromFSPath(f, ml));
						epis.AddDate.set(CCDate.create(14, 8, 2020));
						epis.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 20:21:00")));
						epis.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
					}
					epis.endUpdating();
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E02 - Das Ungeheuer.mp4");
					cp("004", "mp4", f);

					var epis = ml.createNewEmptyEpisode(sea);

					epis.beginUpdating();
					{
						epis.Title.set("Das Ungeheuer");
						epis.EpisodeNumber.set(2);
						epis.MediaInfo.set(mi(ml, "004", f));
						epis.Length.set(91);
						epis.Tags.set(CCTagList.EMPTY);
						epis.Format.set(CCFileFormat.MP4);
						epis.FileSize.set(4_230_401);
						epis.Part.set(CCPath.createFromFSPath(f, ml));
						epis.AddDate.set(CCDate.create(14, 8, 2020));
						epis.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 20:52:00")));
						epis.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
					}
					epis.endUpdating();
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E03 - Ein neuer Schüler.mkv");
					cp("005", "mkv", f);

					var epis = ml.createNewEmptyEpisode(sea);

					epis.beginUpdating();
					{
						epis.Title.set("Ein neuer Schüler");
						epis.EpisodeNumber.set(3);
						epis.MediaInfo.set(mi(ml, "005", f));
						epis.Length.set(108);
						epis.Tags.set(CCTagList.EMPTY);
						epis.Format.set(CCFileFormat.MKV);
						epis.FileSize.set(4_097_053);
						epis.Part.set(CCPath.createFromFSPath(f, ml));
						epis.AddDate.set(CCDate.create(14, 8, 2020));
						epis.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 21:19:00")));
						epis.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
					}
					epis.endUpdating();
				}
			}

			{
				var sea = ml.createNewEmptySeason(ser);

				sea.beginUpdating();
				{
					sea.Title.set("02 - The End of Evangelion");
					sea.Year.set(1997);
					sea.setCover(cvr2("102"));
				}
				sea.endUpdating();

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("02 - The End of Evangelion").append("S02E01 - Air.mkv");
					cp("007", "mkv", f);

					var epis = ml.createNewEmptyEpisode(sea);

					epis.beginUpdating();
					{
						epis.Title.set("Air");
						epis.EpisodeNumber.set(1);
						epis.MediaInfo.set(mi(ml, "007", f));
						epis.Length.set(141);
						epis.Tags.set(CCTagList.EMPTY);
						epis.Format.set(CCFileFormat.MKV);
						epis.FileSize.set(5_310_534);
						epis.Part.set(CCPath.createFromFSPath(f, ml));
						epis.AddDate.set(CCDate.create(1, 9, 2020));
						epis.ViewedHistory.set(CCDateTimeList.createEmpty());
						epis.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE));
					}
					epis.endUpdating();
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("02 - The End of Evangelion").append("S02E02 - My Purest Heart for You.mkv");
					cp("009", "mkv", f);

					var epis = ml.createNewEmptyEpisode(sea);

					epis.beginUpdating();
					{
						epis.Title.set("My Purest Heart for You");
						epis.EpisodeNumber.set(2);
						epis.MediaInfo.set(mi(ml, "009", f));
						epis.Length.set(120);
						epis.Tags.set(CCTagList.EMPTY);
						epis.Format.set(CCFileFormat.MKV);
						epis.FileSize.set(4_538_071);
						epis.Part.set(CCPath.createFromFSPath(f, ml));
						epis.AddDate.set(CCDate.create(1, 9, 2020));
						epis.ViewedHistory.set(CCDateTimeList.createEmpty());
						epis.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE));
					}
					epis.endUpdating();
				}
			}
		}
	}

	private static BufferedImage cvr(String id) throws IOException {
		return SimpleFileUtils.readImageResource("/media/demo_"+id+".cover.png", DatabaseSeeder.class);
	}

	private static BufferedImage cvr2(String id) throws IOException {
		return SimpleFileUtils.readImageResource("/media/additional_"+id+".cover.png", DatabaseSeeder.class);
	}

	private static void cp(String id, String ext, FSPath dest) throws IOException {
		dest.createFolders();
		SimpleFileUtils.writeGzippedResource(dest, "/media/demo_"+id+"."+ext+".gz", DatabaseSeeder.class);
	}

	private static PartialMediaInfo mi(CCMovieList ml, String id, FSPath dest) throws InnerMediaQueryException, CCXMLException, IOException, FVHException {
		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", DatabaseSeeder.class);
		var fvhash = ChecksumHelper.fastVideoHash(Collections.singletonList(dest));
		var attr = dest.readFileAttr();
		return new MediaQueryRunner(ml).parse(out, fvhash, attr, false).toPartial();
	}
}
