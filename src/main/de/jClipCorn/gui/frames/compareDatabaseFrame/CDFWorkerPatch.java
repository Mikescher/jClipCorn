package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.elementProps.impl.EPropertyType;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.serialization.xmlexport.ExportOptions;
import de.jClipCorn.features.serialization.xmlexport.impl.DatabaseXMLExporterImpl;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CDFWorkerPatch
{
	@SuppressWarnings("nls")
	public static void createPatch(CompareState state, String dir, DoubleProgressCallbackListener cb, boolean porcelain) throws Exception
	{
		var fdir = new File(dir);

		if (!fdir.exists()) throw new Exception("Target directory not found");
		if (!fdir.isDirectory()) throw new Exception("Target directory not found");

		var datadir = new File(PathFormatter.combine(dir, "patch_data"));
		if (!datadir.exists() && !datadir.mkdirs()) throw new Exception("Target directory could not be created");

		if (new File(PathFormatter.combine(dir, "patch." + ExportHelper.EXTENSION_PATCHFILE)).exists()) throw new Exception("Target directory not empty");

		var subfiles = datadir.listFiles();
		if (subfiles == null || subfiles.length > 0) throw new Exception("Target directory not empty");

		cb.setMaxAndResetValueBoth(5, 1);

		var root = new Element("patch");
		var xml = new Document(root);

		root.setAttribute("version", Main.VERSION);
		root.setAttribute("dbversion", Main.DBVERSION);
		root.setAttribute("date", CCDate.getCurrentDate().toStringSerialize());

		int ctr = 0;

		cb.setValueBoth(1, 0, "Movies", "");
		ctr = exportMovies(state, datadir, root, cb, porcelain, ctr);

		cb.setValueBoth(2, 0, "Series", "");
		ctr = exportSeries(state, datadir, root, cb, porcelain, ctr);

		cb.setValueBoth(3, 0, "Seasons", "");
		ctr = exportSeasons(state, datadir, root, cb, porcelain, ctr);

		cb.setValueBoth(4, 0, "Episodes", "");
		ctr = exportEpisodes(state, datadir, root, cb, porcelain, ctr);


		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
		try(var fw = new FileWriter(PathFormatter.combine(dir, "patch." + ExportHelper.EXTENSION_PATCHFILE))){
			xmlOut.output(xml, fw);
		}
	}

	@SuppressWarnings("nls")
	private static int exportMovies(CompareState state, File datadir, Element xml, DoubleProgressCallbackListener cb, boolean porcelain, int ctr) throws IOException
	{
		var elements = CCStreams.iterate(state.Movies).filter(ComparisonMatch::getNeedsAnything).toList();
		cb.setSubMax(elements.size()+1);

		for (var e: elements)
		{
			cb.stepSub(e.getLocTitle().orElse(e.getExtTitle().orElse(Str.Empty)));

			if (e.getNeedsCreateNew())
			{
				var idvar = "{{created:id:mov:"+e.MovieLocal.LocalID.get()+"}}";

				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "CREATE");
				xaction.setAttribute("description", Str.format("Create movie \"{0}\"", e.MovieLocal.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;

				{
					var cmd1 = new Element("insert");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("output_id", idvar);
					cmd1.setAttribute("type", "MOVIE");
					cmd1.setAttribute("xmlversion", Main.JXMLVER);
					xaction.addContent(cmd1);
					var inner = new Element("movie");
					DatabaseXMLExporterImpl.exportMovie(inner, e.MovieLocal, new ExportOptions(false, false, false, false));
					cmd1.addContent(inner);
				}

				{
					var coverdata = e.MovieLocal.getCoverInfo();

					var source = Paths.get(e.MovieLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.MovieLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", "1");
					cmd1.setAttribute("type", "MOVIE");
					cmd1.setAttribute("id", idvar);
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}

				{
					for (int i = 0; i < e.MovieLocal.getPartcount(); i++)
					{
						var source = Paths.get(e.MovieLocal.getAbsolutePart(i));
						var newfilename = "m_" + e.MovieLocal.LocalID.get() + "_" + i + "." + PathFormatter.getExtension(source.toString());
						var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

						String checksum;
						try (FileInputStream fis = new FileInputStream(source.toFile())) { checksum = porcelain ? "-" : ("sha256://" + DigestUtils.sha256Hex(fis).toLowerCase()); }

						var cmd = new Element("copyvideo");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", idvar);
						cmd.setAttribute("index", String.valueOf(i));
						cmd.setAttribute("source", newfilename);
						cmd.setAttribute("sourcehash", checksum);
						cmd.setAttribute("filename", PathFormatter.getFilenameWithExt(source.toString()));
						xaction.addContent(cmd);

						if (porcelain) {
							Files.createFile(target);
						} else {
							Files.copy(source, target);
						}
					}

					for (var prop: CCStreams.iterate(e.MovieLocal.getProperties()))
					{
						if (prop.getValueType() != EPropertyType.LOCAL_FILE_REF_OBJECTIVE) continue;

						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", idvar);
						cmd.setAttribute("prop", prop.getName());
						cmd.setAttribute("value_new", prop.serializeToString());
						xaction.addContent(cmd);
					}
					{
						var cmd = new Element("calc_mediainfo_subjective");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", idvar);
						xaction.addContent(cmd);
					}
				}

			}
			else if (e.getNeedsDelete())
			{
				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "DELETE");
				xaction.setAttribute("description", Str.format("Delete movie \"{0}\"", e.MovieExtern.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;
				var cmd1 = new Element("delete");
				cmd1.setAttribute("ctr", String.valueOf(innerctr++));
				cmd1.setAttribute("type", "MOVIE");
				cmd1.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
				xaction.addContent(cmd1);
			}
			else
			{
				if (e.getNeedsUpdateMetadata())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "UPDATE");
					xaction.setAttribute("description", Str.format("Update metadata of movie \"{0}\"", e.MovieExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;
					for (var meta: e.MetadataDiff)
					{
						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
						cmd.setAttribute("prop", meta.Item2.getName());
						cmd.setAttribute("value_old", meta.Item2.serializeToString());
						cmd.setAttribute("value_new", meta.Item1.serializeToString());
						xaction.addContent(cmd);
					}

				}
				if (e.getNeedsUpdateCover())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "COVER");
					xaction.setAttribute("description", Str.format("Replace cover of movie \"{0}\"", e.MovieExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var coverdata = e.MovieLocal.getCoverInfo();

					var source = Paths.get(e.MovieLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.MovieLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var innerctr = 1;

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("type", "MOVIE");
					cmd1.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}
				if (e.getNeedsUpdateFile())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "VIDFILES");
					xaction.setAttribute("description", Str.format("Replace video files of movie \"{0}\"", e.MovieExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;

					{
						var cmd1 = new Element("clearvideos");
						cmd1.setAttribute("ctr", String.valueOf(innerctr++));
						cmd1.setAttribute("type", "MOVIE");
						cmd1.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
						xaction.addContent(cmd1);
					}

					for (int i = 0; i < e.MovieLocal.getPartcount(); i++)
					{
						var source = Paths.get(e.MovieLocal.getAbsolutePart(i));
						var newfilename = "m_" + e.MovieLocal.LocalID.get() + "_" + i + "." + PathFormatter.getExtension(source.toString());
						var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

						String checksum;
						try (FileInputStream fis = new FileInputStream(source.toFile())) { checksum = porcelain ? "-" : ("sha256://" + DigestUtils.sha256Hex(fis).toLowerCase()); }

						var cmd = new Element("copyvideo");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
						cmd.setAttribute("index", String.valueOf(i));
						cmd.setAttribute("source", newfilename);
						cmd.setAttribute("sourcehash", checksum);
						cmd.setAttribute("filename", PathFormatter.getFilenameWithExt(source.toString()));
						xaction.addContent(cmd);

						if (porcelain) {
							Files.createFile(target);
						} else {
							Files.copy(source, target);
						}
					}

					for (var prop: CCStreams.zip(CCStreams.iterate(e.MovieLocal.getProperties()), CCStreams.iterate(e.MovieExtern.getProperties())))
					{
						if (prop.Item1.getValueType() != EPropertyType.LOCAL_FILE_REF_OBJECTIVE) continue;
						if (Str.equals(prop.Item1.serializeToString(), prop.Item2.serializeToString())) continue;

						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
						cmd.setAttribute("prop", prop.Item2.getName());
						cmd.setAttribute("value_old", prop.Item2.serializeToString());
						cmd.setAttribute("value_new", prop.Item1.serializeToString());
						xaction.addContent(cmd);
					}
					{
						var cmd = new Element("calc_mediainfo_subjective");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "MOVIE");
						cmd.setAttribute("id", String.valueOf(e.MovieExtern.getLocalID()));
						xaction.addContent(cmd);
					}
				}
			}
		}

		return ctr;
	}

	@SuppressWarnings("nls")
	private static int exportSeries(CompareState state, File datadir, Element xml, DoubleProgressCallbackListener cb, boolean porcelain, int ctr) throws IOException
	{
		var elements = CCStreams.iterate(state.Series).filter(ComparisonMatch::getNeedsAnything).toList();
		cb.setSubMax(elements.size()+1);

		for (var e: elements)
		{
			cb.stepSub(e.getLocTitle().orElse(e.getExtTitle().orElse(Str.Empty)));

			if (e.getNeedsCreateNew())
			{
				var idvar = "{{created:id:ser:"+e.SeriesLocal.LocalID.get()+"}}";

				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "CREATE");
				xaction.setAttribute("description", Str.format("Create series \"{0}\"", e.SeriesLocal.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;

				{
					var cmd1 = new Element("insert");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("output_id", idvar);
					cmd1.setAttribute("type", "SERIES");
					cmd1.setAttribute("xmlversion", Main.JXMLVER);
					xaction.addContent(cmd1);
					var inner = new Element("series");
					DatabaseXMLExporterImpl.exportSeries(inner, e.SeriesLocal, new ExportOptions(false, false, false, false));
					cmd1.addContent(inner);
				}

				{
					var coverdata = e.SeriesLocal.getCoverInfo();

					var source = Paths.get(e.SeriesLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.SeriesLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", "1");
					cmd1.setAttribute("type", "SERIES");
					cmd1.setAttribute("id", idvar);
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}

				{
					for (var prop: CCStreams.iterate(e.SeriesLocal.getProperties()))
					{
						if (prop.getValueType() != EPropertyType.LOCAL_FILE_REF_OBJECTIVE) continue;

						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "SERIES");
						cmd.setAttribute("id", idvar);
						cmd.setAttribute("prop", prop.getName());
						cmd.setAttribute("value_new", prop.serializeToString());
						xaction.addContent(cmd);
					}
				}

			}
			else if (e.getNeedsDelete())
			{
				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "DELETE");
				xaction.setAttribute("description", Str.format("Delete series \"{0}\"", e.SeriesExtern.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;
				var cmd1 = new Element("delete");
				cmd1.setAttribute("ctr", String.valueOf(innerctr++));
				cmd1.setAttribute("type", "SERIES");
				cmd1.setAttribute("id", String.valueOf(e.SeriesExtern.getLocalID()));
				xaction.addContent(cmd1);
			}
			else
			{
				if (e.getNeedsUpdateMetadata())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "UPDATE");
					xaction.setAttribute("description", Str.format("Update metadata of series \"{0}\"", e.SeriesExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;
					for (var meta: e.MetadataDiff)
					{
						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "SERIES");
						cmd.setAttribute("id", String.valueOf(e.SeriesExtern.getLocalID()));
						cmd.setAttribute("prop", meta.Item2.getName());
						cmd.setAttribute("value_old", meta.Item2.serializeToString());
						cmd.setAttribute("value_new", meta.Item1.serializeToString());
						xaction.addContent(cmd);
					}

				}
				if (e.getNeedsUpdateCover())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "COVER");
					xaction.setAttribute("description", Str.format("Replace cover of series \"{0}\"", e.SeriesExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var coverdata = e.SeriesLocal.getCoverInfo();

					var source = Paths.get(e.SeriesLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.SeriesLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var innerctr = 1;

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("type", "SERIES");
					cmd1.setAttribute("id", String.valueOf(e.SeriesExtern.getLocalID()));
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}
			}
		}

		return ctr;
	}

	@SuppressWarnings("nls")
	private static int exportSeasons(CompareState state, File datadir, Element xml, DoubleProgressCallbackListener cb, boolean porcelain, int ctr) throws IOException
	{
		var elements = CCStreams.iterate(state.AllSeasons).filter(ComparisonMatch::getNeedsAnything).toList();
		cb.setSubMax(elements.size()+1);

		for (var e: elements)
		{
			cb.stepSub(e.getLocTitle().orElse(e.getExtTitle().orElse(Str.Empty)));

			if (e.getNeedsCreateNew())
			{
				var idparent = (e.Parent.SeriesExtern != null) ? String.valueOf(e.Parent.SeriesExtern.LocalID.get()) : ("{{created:id:ser:"+e.Parent.SeriesLocal.LocalID.get()+"}}");
				var idvar    = "{{created:id:sea:"+e.SeasonLocal.LocalID.get()+"}}";

				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "CREATE");
				xaction.setAttribute("description", Str.format("Create season \"{0}\" for series {1}", e.SeasonLocal.getQualifiedTitle(), idparent));
				xml.addContent(xaction);

				var innerctr = 1;

				{
					var cmd1 = new Element("insert");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("output_id", idvar);
					cmd1.setAttribute("series_id", idparent);
					cmd1.setAttribute("type", "SEASON");
					cmd1.setAttribute("xmlversion", Main.JXMLVER);
					xaction.addContent(cmd1);
					var inner = new Element("season");
					DatabaseXMLExporterImpl.exportSeason(inner, e.SeasonLocal, new ExportOptions(false, false, false, false));
					cmd1.addContent(inner);
				}

				{
					var coverdata = e.SeasonLocal.getCoverInfo();

					var source = Paths.get(e.SeasonLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.SeasonLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", "1");
					cmd1.setAttribute("type", "SEASON");
					cmd1.setAttribute("id", idvar);
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}
			}
			else if (e.getNeedsDelete())
			{
				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "DELETE");
				xaction.setAttribute("description", Str.format("Delete season \"{0}\"", e.SeasonExtern.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;
				var cmd1 = new Element("delete");
				cmd1.setAttribute("ctr", String.valueOf(innerctr++));
				cmd1.setAttribute("type", "SEASON");
				cmd1.setAttribute("id", String.valueOf(e.SeasonExtern.getLocalID()));
				xaction.addContent(cmd1);
			}
			else
			{
				if (e.getNeedsUpdateMetadata())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "UPDATE");
					xaction.setAttribute("description", Str.format("Update metadata of season \"{0}\"", e.SeasonExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;
					for (var meta: e.MetadataDiff)
					{
						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "SEASON");
						cmd.setAttribute("id", String.valueOf(e.SeasonExtern.getLocalID()));
						cmd.setAttribute("prop", meta.Item2.getName());
						cmd.setAttribute("value_old", meta.Item2.serializeToString());
						cmd.setAttribute("value_new", meta.Item1.serializeToString());
						xaction.addContent(cmd);
					}

				}
				if (e.getNeedsUpdateCover())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "COVER");
					xaction.setAttribute("description", Str.format("Replace cover of season \"{0}\"", e.SeasonExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var coverdata = e.SeasonLocal.getCoverInfo();

					var source = Paths.get(e.SeasonLocal.getMovieList().getCoverCache().getFilepath(coverdata));
					var newfilename = "c_" + e.SeasonLocal.CoverID.get() + "." + PathFormatter.getExtension(source.toString());
					var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

					var innerctr = 1;

					var cmd1 = new Element("replacecover");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("type", "MOVIE");
					cmd1.setAttribute("id", String.valueOf(e.SeasonExtern.getLocalID()));
					cmd1.setAttribute("source", newfilename);
					cmd1.setAttribute("sourcehash", coverdata.Checksum);
					xaction.addContent(cmd1);

					if (porcelain) {
						Files.createFile(target);
					} else {
						Files.copy(source, target);
					}
				}
			}
		}

		return ctr;
	}

	@SuppressWarnings("nls")
	private static int exportEpisodes(CompareState state, File datadir, Element xml, DoubleProgressCallbackListener cb, boolean porcelain, int ctr) throws IOException
	{
		var elements = CCStreams.iterate(state.AllEpisodes).filter(ComparisonMatch::getNeedsAnything).toList();
		cb.setSubMax(elements.size()+1);

		for (var e: elements)
		{
			cb.stepSub(e.getLocTitle().orElse(e.getExtTitle().orElse(Str.Empty)));

			if (e.getNeedsCreateNew())
			{
				var idparent = (e.Parent.SeasonExtern != null) ? String.valueOf(e.Parent.SeasonExtern.LocalID.get()) : ("{{created:id:sea:"+e.Parent.SeasonLocal.LocalID.get()+"}}");
				var idvar = "{{created:id:epi:"+e.EpisodeLocal.LocalID.get()+"}}";

				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "CREATE");
				xaction.setAttribute("description", Str.format("Create episode \"{0}\" for season {1}", e.EpisodeLocal.getQualifiedTitle(), idparent));
				xml.addContent(xaction);

				var innerctr = 1;

				{
					var cmd1 = new Element("insert");
					cmd1.setAttribute("ctr", String.valueOf(innerctr++));
					cmd1.setAttribute("output_id", idvar);
					cmd1.setAttribute("season_id", idparent);
					cmd1.setAttribute("type", "EPISODE");
					cmd1.setAttribute("xmlversion", Main.JXMLVER);
					xaction.addContent(cmd1);
					var inner = new Element("episode");
					DatabaseXMLExporterImpl.exportEpisode(inner, e.EpisodeLocal, new ExportOptions(false, false, false, false));
					cmd1.addContent(inner);
				}

				{
					{
						var source = Paths.get(e.EpisodeLocal.getAbsolutePart());
						var newfilename = "e_" + e.EpisodeLocal.LocalID.get() + "." + PathFormatter.getExtension(source.toString());
						var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

						String checksum;
						try (FileInputStream fis = new FileInputStream(source.toFile())) { checksum = porcelain ? "-" : ("sha256://" + DigestUtils.sha256Hex(fis).toLowerCase()); }

						var cmd = new Element("copyvideo");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", idvar);
						cmd.setAttribute("source", newfilename);
						cmd.setAttribute("sourcehash", checksum);
						cmd.setAttribute("filename", PathFormatter.getFilenameWithExt(source.toString()));
						xaction.addContent(cmd);

						if (porcelain) {
							Files.createFile(target);
						} else {
							Files.copy(source, target);
						}
					}

					for (var prop: CCStreams.iterate(e.EpisodeLocal.getProperties()))
					{
						if (prop.getValueType() != EPropertyType.LOCAL_FILE_REF_OBJECTIVE) continue;

						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", idvar);
						cmd.setAttribute("prop", prop.getName());
						cmd.setAttribute("value_new", prop.serializeToString());
						xaction.addContent(cmd);
					}
					{
						var cmd = new Element("calc_mediainfo_subjective");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", idvar);
						xaction.addContent(cmd);
					}
				}

			}
			else if (e.getNeedsDelete())
			{
				var xaction = new Element("action");
				xaction.setAttribute("ctr", String.valueOf(ctr));
				ctr++;
				xaction.setAttribute("type", "DELETE");
				xaction.setAttribute("description", Str.format("Delete episode \"{0}\"", e.EpisodeExtern.getQualifiedTitle()));
				xml.addContent(xaction);

				var innerctr = 1;
				var cmd1 = new Element("delete");
				cmd1.setAttribute("ctr", String.valueOf(innerctr++));
				cmd1.setAttribute("type", "EPISODE");
				cmd1.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
				xaction.addContent(cmd1);
			}
			else
			{
				if (e.getNeedsUpdateMetadata())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "UPDATE");
					xaction.setAttribute("description", Str.format("Update metadata of episode \"{0}\"", e.EpisodeExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;
					for (var meta: e.MetadataDiff)
					{
						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
						cmd.setAttribute("prop", meta.Item2.getName());
						cmd.setAttribute("value_old", meta.Item2.serializeToString());
						cmd.setAttribute("value_new", meta.Item1.serializeToString());
						xaction.addContent(cmd);
					}

				}
				if (e.getNeedsUpdateFile())
				{
					var xaction = new Element("action");
					xaction.setAttribute("ctr", String.valueOf(ctr));
					ctr++;
					xaction.setAttribute("type", "VIDFILES");
					xaction.setAttribute("description", Str.format("Replace video files of episode \"{0}\"", e.EpisodeExtern.getQualifiedTitle()));
					xml.addContent(xaction);

					var innerctr = 1;

					{
						var cmd1 = new Element("clearvideos");
						cmd1.setAttribute("ctr", String.valueOf(innerctr++));
						cmd1.setAttribute("type", "EPISODE");
						cmd1.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
						xaction.addContent(cmd1);
					}

					{
						var source = Paths.get(e.EpisodeLocal.getAbsolutePart());
						var newfilename = "e_" + e.EpisodeLocal.LocalID.get() + "." + PathFormatter.getExtension(source.toString());
						var target = Paths.get(PathFormatter.combine(datadir.getAbsolutePath(), newfilename));

						String checksum;
						try (FileInputStream fis = new FileInputStream(source.toFile())) { checksum = porcelain ? "-" : ("sha256://" + DigestUtils.sha256Hex(fis).toLowerCase()); }

						var cmd = new Element("copyvideo");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
						cmd.setAttribute("source", newfilename);
						cmd.setAttribute("sourcehash", checksum);
						cmd.setAttribute("filename", PathFormatter.getFilenameWithExt(source.toString()));
						xaction.addContent(cmd);

						if (porcelain) {
							Files.createFile(target);
						} else {
							Files.copy(source, target);
						}
					}

					for (var prop: CCStreams.zip(CCStreams.iterate(e.EpisodeLocal.getProperties()), CCStreams.iterate(e.EpisodeExtern.getProperties())))
					{
						if (prop.Item1.getValueType() != EPropertyType.LOCAL_FILE_REF_OBJECTIVE) continue;
						if (Str.equals(prop.Item1.serializeToString(), prop.Item2.serializeToString())) continue;

						var cmd = new Element("set");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
						cmd.setAttribute("prop", prop.Item2.getName());
						cmd.setAttribute("value_old", prop.Item2.serializeToString());
						cmd.setAttribute("value_new", prop.Item1.serializeToString());
						xaction.addContent(cmd);
					}
					{
						var cmd = new Element("calc_mediainfo_subjective");
						cmd.setAttribute("ctr", String.valueOf(innerctr++));
						cmd.setAttribute("type", "EPISODE");
						cmd.setAttribute("id", String.valueOf(e.EpisodeExtern.getLocalID()));
						xaction.addContent(cmd);
					}
				}
			}
		}

		return ctr;
	}

}
