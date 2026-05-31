package de.jClipCorn.test;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.features.metadata.exceptions.InnerMediaQueryException;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
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

import org.json.JSONArray;

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
		ml.connectAndLoadExternal(true);
		{
			ml.getHistory().enableTrigger();

			seed(props, ml, tempPath);
		}

		if (reloadAfterCreate)
		{
			ml.shutdown();

			var mlRet = CCMovieList.createInstanceMovieList(props);
			mlRet.connectAndLoadExternal(true);

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

		prop.PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar(Str.Empty, "mov", CCPath.createFromFSPath(pathMov, Opt.False, ml)));
		prop.PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar(Str.Empty, "ser", CCPath.createFromFSPath(pathSer, Opt.False, ml)));

		//ml.addGroup(CCGroup.create("DCU Animated",        true,  Str.Empty,           false));
		ml.addGroup(CCGroup.create("BruceWillis",         false, Str.Empty,           false));
		ml.addGroup(CCGroup.create("Animation Studios",   false, Str.Empty,           false));
		ml.addGroup(CCGroup.create("Studio Gainax",       false, "Animation Studios", true));
		//ml.addGroup(CCGroup.create("Studio A-1 Pictures", false, "Animation Studios", true));
		//ml.addGroup(CCGroup.create("Studio Bones",        false, "Animation Studios", true));

		{
			var f = pathMov.append("1994").append("Die Verurteilten").append("Die Verurteilten.mp4");
			cp("000", "mp4", f);

			var e = ml.createNewMovie(m -> {
				m.Title.set("Die Verurteilten");
				m.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/278"), CCSingleOnlineReference.createIMDB("tt0111161")));
				m.Zyklus.set(CCMovieZyklus.EMPTY);
				m.OnlineScore.set(CCOnlineScore.create((short)9, (short)10));
				m.Score.set(CCUserScore.RATING_V);
				m.MediaInfo.set(mi(ml, "000", f));
				m.Length.set(25);
				m.Format.set(CCFileFormat.MP4);
				m.FileSize.set(new CCFileSize(1_166_926));
				m.FSK.set(CCFSK.RATING_II);
				m.ViewedHistory.set(CCDateTimeList.createEmpty());
				m.Language.set(CCDBLanguageSet.GERMAN);
				m.Year.set(1994);
				m.Groups.set(CCGroupList.EMPTY);
				m.AddDate.set(CCDate.create(23, 9, 2010));
				m.Genres.set(CCGenreList.create(CCGenre.GENRE_014, CCGenre.GENRE_040));
				m.Tags.set(CCTagList.EMPTY);
				m.setCover(cvr("000"));

				m.Parts.set(CCPathList.create(CCPath.createFromFSPath(f, ml)));
				setMovieChecksums(m, f);
			});
		}

		{
			var f = pathMov.append("1988").append("Stirb Langsam").append("Stirb Langsam I - Stirb Langsam [GER+ENG].mkv");
			cp("001", "mkv", f);

			var e = ml.createNewMovie(m -> {
				m.Title.set("Stirb Langsam");
				m.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/562"), CCSingleOnlineReference.createIMDB("tt0095016")));
				m.Zyklus.set(new CCMovieZyklus("Stirb Langsam", 1));
				m.OnlineScore.set(CCOnlineScore.create((short)8, (short)10));
				m.Score.set(CCUserScore.RATING_NO);
				m.MediaInfo.set(mi(ml, "001", f));
				m.Length.set(42);
				m.Format.set(CCFileFormat.MKV);
				m.FileSize.set(new CCFileSize(1_545_782));
				m.FSK.set(CCFSK.RATING_IV);
				m.ViewedHistory.set(CCDateTimeList.create(CCDateTime.createFromSQL("2019-04-01"), CCDateTime.createFromSQL("2020-08-17 20:08:30"), CCDateTime.createFromSQL("2021-01-03 20:08:30")));
				m.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.ENGLISH));
				m.Subtitles.set(CCDBLanguageList.create(CCDBLanguage.ENGLISH, CCDBLanguage.FRENCH));
				m.Year.set(1988);
				m.Groups.set(CCGroupList.create(CCGroup.create("BruceWillis")));
				m.AddDate.set(CCDate.create(6, 8, 2015));
				m.Genres.set(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_019, CCGenre.GENRE_014));
				m.Tags.set(CCTagList.create(CCSingleTag.TAG_WATCH_LATER));
				m.setCover(cvr("001"));

				m.Parts.set(CCPathList.create(CCPath.createFromFSPath(f, ml)));
				setMovieChecksums(m, f);
			});
		}

		{
			var f1 = pathMov.append("1988").append("Stirb Langsam").append("Stirb Langsam II - Die Hard 2 (Part 1).avi");
			var f2 = pathMov.append("1988").append("Stirb Langsam").append("Stirb Langsam II - Die Hard 2 (Part 2).avi");
			cp("002", "avi", f1);
			cp("006", "avi", f2);

			var e = ml.createNewMovie(m -> {
				m.Title.set("Die Hard 2");
				m.OnlineReference.set(CCOnlineReferenceList.create(CCSingleOnlineReference.createTMDB("movie/1573"), Arrays.asList(CCSingleOnlineReference.createIMDB("tt0099423"), CCSingleOnlineReference.createAmazon("B00FYI7D6C"))));
				m.Zyklus.set(new CCMovieZyklus("Stirb Langsam", 2));
				m.OnlineScore.set(CCOnlineScore.create((short)7, (short)10));
				m.Score.set(CCUserScore.RATING_NO);
				m.MediaInfo.set(mi(ml, "002", f1));
				m.Length.set(58+125);
				m.Format.set(CCFileFormat.AVI);
				m.FileSize.set(new CCFileSize(12_170_130+21_899_356));
				m.FSK.set(CCFSK.RATING_IV);
				m.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2021-06-06 07:08:00")));
				m.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN));
				m.Year.set(1990);
				m.Groups.set(CCGroupList.create(CCGroup.create("BruceWillis")));
				m.AddDate.set(CCDate.create(15, 1, 2017));
				m.Genres.set(CCGenreList.create(CCGenre.GENRE_006, CCGenre.GENRE_019, CCGenre.GENRE_014, CCGenre.GENRE_010));
				m.Tags.set(CCTagList.create(CCSingleTag.TAG_BAD_QUALITY));
				m.setCover(cvr("002"));

				m.Parts.set(CCPathList.create(CCPath.createFromFSPath(f1, ml), CCPath.createFromFSPath(f2, ml)));
				setMovieChecksums(m, f1, f2);
			});
		}

		{
			var ser = ml.createNewSeries(s -> {
				s.Title.set("Neon Genesis Evangelion");
				s.Groups.set(CCGroupList.create(CCGroup.create("Studio Gainax")));
				s.Genres.set(CCGenreList.create(CCGenre.GENRE_022, CCGenre.GENRE_019, CCGenre.GENRE_009, CCGenre.GENRE_008, CCGenre.GENRE_040, CCGenre.GENRE_054));
				s.OnlineScore.set(CCOnlineScore.create((short)8, (short)10));
				s.FSK.set(CCFSK.RATING_III);
				s.Score.set(CCUserScore.RATING_IV);
				s.OnlineReference.set(
						CCOnlineReferenceList.create(CCSingleOnlineReference.createMyAnimeList("30"),
						Arrays.asList(
								CCSingleOnlineReference.createTMDB("tv/890"),
								CCSingleOnlineReference.createIMDB("tt0112159"),
								CCSingleOnlineReference.createAniList("30"),
								CCSingleOnlineReference.createKitsu("neon-genesis-evangelion"),
								CCSingleOnlineReference.createAniDB("22", "Neon Genesis Evangelion"),
								CCSingleOnlineReference.createAniDB("202", "The End of Evangelion"))));
				s.Tags.set(CCTagList.EMPTY);
				s.setCover(cvr2("100"));
			});

			{
				var sea = ml.createNewSeason(ser, s -> {
					s.Title.set("01 - Neon Genesis Evangelion");
					s.Year.set(1995);
					s.setCover(cvr2("101"));
				});

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E01 - Angriff der Engel.mkv");
					cp("003", "mkv", f);

					var epis = sea.createNewEpisode(epi -> {
						epi.Title.set("Angriff der Engel");
						epi.EpisodeNumber.set(1);
						epi.MediaInfo.set(mi(ml, "003", f));
						epi.Length.set(75);
						epi.Tags.set(CCTagList.EMPTY);
						epi.Format.set(CCFileFormat.MKV);
						epi.FileSize.set(2_826_740);
						epi.Part.set(CCPath.createFromFSPath(f, ml));
						epi.AddDate.set(CCDate.create(14, 8, 2020));
						epi.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 20:21:00")));
						epi.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
						setEpisodeChecksums(epi, f);
					});
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E02 - Das Ungeheuer.mp4");
					cp("004", "mp4", f);

					var epis = sea.createNewEpisode(epi -> {
						epi.Title.set("Das Ungeheuer");
						epi.EpisodeNumber.set(2);
						epi.MediaInfo.set(mi(ml, "004", f));
						epi.Length.set(91);
						epi.Tags.set(CCTagList.EMPTY);
						epi.Format.set(CCFileFormat.MP4);
						epi.FileSize.set(4_230_401);
						epi.Part.set(CCPath.createFromFSPath(f, ml));
						epi.AddDate.set(CCDate.create(14, 8, 2020));
						epi.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 20:52:00")));
						epi.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
						setEpisodeChecksums(epi, f);
					});
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("01 - Neon Genesis Evangelion").append("S01E03 - Ein neuer Schüler.mkv");
					cp("005", "mkv", f);

					var epis = sea.createNewEpisode(epi -> {
						epi.Title.set("Ein neuer Schüler");
						epi.EpisodeNumber.set(3);
						epi.MediaInfo.set(mi(ml, "005", f));
						epi.Length.set(108);
						epi.Tags.set(CCTagList.EMPTY);
						epi.Format.set(CCFileFormat.MKV);
						epi.FileSize.set(4_097_053);
						epi.Part.set(CCPath.createFromFSPath(f, ml));
						epi.AddDate.set(CCDate.create(14, 8, 2020));
						epi.ViewedHistory.set(CCDateTimeList.create(CCDateTime.getUnspecified(), CCDateTime.createFromSQL("2020-08-14 21:19:00")));
						epi.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE, CCDBLanguage.ENGLISH));
						setEpisodeChecksums(epi, f);
					});
				}
			}

			{
				var sea = ml.createNewSeason(ser, s -> {
					s.Title.set("02 - The End of Evangelion");
					s.Year.set(1997);
					s.setCover(cvr2("102"));
				});

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("02 - The End of Evangelion").append("S02E01 - Air.mkv");
					cp("007", "mkv", f);

					var epis = sea.createNewEpisode(epi -> {
						epi.Title.set("Air");
						epi.EpisodeNumber.set(1);
						epi.MediaInfo.set(mi(ml, "007", f));
						epi.Length.set(141);
						epi.Tags.set(CCTagList.EMPTY);
						epi.Format.set(CCFileFormat.MKV);
						epi.FileSize.set(5_310_534);
						epi.Part.set(CCPath.createFromFSPath(f, ml));
						epi.AddDate.set(CCDate.create(1, 9, 2020));
						epi.ViewedHistory.set(CCDateTimeList.createEmpty());
						epi.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE));
						setEpisodeChecksums(epi, f);
					});
				}

				{
					var f = pathSer.append("Neon Genesis Evangelion [GER+JAP]").append("02 - The End of Evangelion").append("S02E02 - My Purest Heart for You.mkv");
					cp("009", "mkv", f);

					var epis = sea.createNewEpisode(epi -> {
						epi.Title.set("My Purest Heart for You");
						epi.EpisodeNumber.set(2);
						epi.MediaInfo.set(mi(ml, "009", f));
						epi.Length.set(120);
						epi.Tags.set(CCTagList.EMPTY);
						epi.Format.set(CCFileFormat.MKV);
						epi.FileSize.set(4_538_071);
						epi.Part.set(CCPath.createFromFSPath(f, ml));
						epi.AddDate.set(CCDate.create(1, 9, 2020));
						epi.ViewedHistory.set(CCDateTimeList.createEmpty());
						epi.Language.set(CCDBLanguageSet.create(CCDBLanguage.GERMAN, CCDBLanguage.JAPANESE));
						setEpisodeChecksums(epi, f);
					});
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

	private static CCMediaInfo mi(CCMovieList ml, String id, FSPath dest) throws InnerMediaQueryException, CCXMLException, IOException, FVHException {
		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", DatabaseSeeder.class);
		var fvhash = ChecksumHelper.fastVideoHash(Collections.singletonList(dest));
		var attr = dest.readFileAttr();
		return new MediaInfoRunner(ml).parse(out, fvhash, attr, dest).toMediaInfo();
	}

	private static void setMovieChecksums(de.jClipCorn.database.databaseElement.CCMovie mov, FSPath... files) throws IOException {
		var crc32arr  = new JSONArray();
		var md5arr    = new JSONArray();
		var sha256arr = new JSONArray();
		var sha512arr = new JSONArray();
		for (var f : files) {
			var cs = ChecksumHelper.computeAllChecksums(f);
			crc32arr.put(cs.CRC32);
			md5arr.put(cs.MD5);
			sha256arr.put(cs.SHA256);
			sha512arr.put(cs.SHA512);
		}
		mov.ChecksumCRC32.set(Opt.of(crc32arr.toString()));
		mov.ChecksumMD5.set(Opt.of(md5arr.toString()));
		mov.ChecksumSHA256.set(Opt.of(sha256arr.toString()));
		mov.ChecksumSHA512.set(Opt.of(sha512arr.toString()));
	}

	private static void setEpisodeChecksums(de.jClipCorn.database.databaseElement.CCEpisode epis, FSPath f) throws IOException {
		var cs = ChecksumHelper.computeAllChecksums(f);
		epis.ChecksumCRC32.set(Opt.of(cs.CRC32));
		epis.ChecksumMD5.set(Opt.of(cs.MD5));
		epis.ChecksumSHA256.set(Opt.of(cs.SHA256));
		epis.ChecksumSHA512.set(Opt.of(cs.SHA512));
	}
}
