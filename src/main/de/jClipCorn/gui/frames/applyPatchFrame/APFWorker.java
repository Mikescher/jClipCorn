package de.jClipCorn.gui.frames.applyPatchFrame;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.features.serialization.xmlimport.DatabaseXMLImporter;
import de.jClipCorn.features.serialization.xmlimport.ImportOptions;
import de.jClipCorn.features.serialization.xmlimport.ImportState;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.SwingUtils;
import de.jClipCorn.util.lambda.Func2to0;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import javax.imageio.ImageIO;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("nls")
public class APFWorker
{
	public static void applyPatch(List<ActionVM> actlist, CCMovieList ml, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb, Func2to0<ActionVM, ActionVM> lstr) throws Exception
	{
		cb.setMaxAndResetValueBoth(actlist.size() + 1, 1);

		for (int i = 0; i < actlist.size(); i++)
		{
			var act = actlist.get(i);

			cb.stepRootAndResetSub(String.valueOf(act.Ctr), act.Commands.size() + 1);

			if (act.Ctr <= state.LastSuccessfulCtr)
			{
				if (act.State == 2) continue;
				lstr.invoke(act, act.WithState(2));
				continue;
			}

			var actNew1 = act.WithState(1);
			lstr.invoke(act, actNew1);
			act = actNew1;

			executeAction(act, ml, state, opt, cb);

			var actNew2 = act.WithState(2);
			lstr.invoke(act, actNew2);
			act = actNew2;

			state.LastSuccessfulCtr = act.Ctr;

			state.save();
		}
	}

	private static void executeAction(ActionVM act, CCMovieList ml, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		if (act.Ctr <= state.LastSuccessfulCtr) return; // already done

		for (var cmd: act.Commands)
		{
			cb.stepSub(cmd.Command.toUpperCase());

			if      (Str.equals(cmd.Command, "clearvideos"))               executeClearVideos(act, ml, cmd, state, opt, cb);
			else if (Str.equals(cmd.Command, "copyvideo"))                 executeCopyVideo(act, ml, cmd, state, opt, cb);
			else if (Str.equals(cmd.Command, "set"))                       executeSetProp(act, ml, cmd, state, opt, cb);
			else if (Str.equals(cmd.Command, "replacecover"))              executeReplaceCover(act, ml, cmd, state, opt, cb);
			else if (Str.equals(cmd.Command, "insert"))                    executeInsertElement(act, ml, cmd, state, opt, cb);
			else if (Str.equals(cmd.Command, "calc_mediainfo_subjective")) executeCalcMediaInfoSubjective(act, ml, cmd, state, opt, cb);
			else throw new Exception("Unknown command: " + cmd.Command);
		}
	}

	private static void executeClearVideos(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var ielem = getElement(ml, cmd, state);

		if (ielem instanceof CCMovie)
		{
			var elem = (CCMovie)ielem;

			if (opt.Porcelain) return;

			for (int i = 0; i < elem.getPartcount(); i++)
			{
				var src = Path.of(elem.getAbsolutePart(i));
				var dst = Path.of(opt.DestinationTrashMovies, "movie_" + elem.getLocalID() + "_" + i + "_" + Instant.now().getEpochSecond());
				Files.move(src, dst);
			}

			SwingUtils.invokeAndWait(() ->
			{
				elem.Parts.set(Str.Empty, Str.Empty, Str.Empty, Str.Empty, Str.Empty, Str.Empty);
			});
		}
		else if (ielem instanceof CCEpisode)
		{
			var elem = (CCEpisode)ielem;

			if (opt.Porcelain) return;

			var src = Path.of(elem.getAbsolutePart());
			var dst = Path.of(opt.DestinationTrashSeries, "episode_" + elem.getLocalID() + "_" + Instant.now().getEpochSecond());
			Files.move(src, dst);

			elem.Part.set(Str.Empty);
		}
		else
		{
			throw new Exception("Type");
		}
	}

	private static void executeCopyVideo(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var ielem = getElement(ml, cmd, state);

		if (ielem instanceof CCMovie)
		{
			var elem = (CCMovie)ielem;

			var index        = cmd.XML.getAttributeIntValueOrThrow("index");
			var source       = cmd.XML.getAttributeValueOrThrow("source");
			var sourcehash   = cmd.XML.getAttributeValueOrThrow("sourcehash");
			var destfilename = cmd.XML.getAttributeValueOrThrow("filename");

			var pSource = Path.of(opt.DataDir, source);
			var ptarget = Path.of(opt.DestinationMovies, destfilename);
			if (Files.exists(ptarget)) ptarget = Path.of(opt.DestinationMovies, UUID.randomUUID() + "." + PathFormatter.getExtension(source));
			var finptarget = ptarget;

			if (opt.Porcelain) return;

			Files.move(pSource, ptarget);

			SwingUtils.invokeAndWait(() ->
			{
				elem.Parts.set(index, PathFormatter.getCCPath(finptarget.toAbsolutePath().toString()));
			});
		}
		else if (ielem instanceof CCEpisode)
		{
			var elem = (CCEpisode)ielem;

			var source       = cmd.XML.getAttributeValueOrThrow("source");
			var sourcehash   = cmd.XML.getAttributeValueOrThrow("sourcehash");
			var destfilename = cmd.XML.getAttributeValueOrThrow("filename");

			var serPath = ((CCEpisode) ielem).getFileForCreatedFolderstructure(new File(opt.DestinationSeries));

			var pSource = Path.of(opt.DataDir, source);
			var ptarget = Path.of(serPath.getAbsolutePath(), destfilename);
			if (Files.exists(ptarget)) ptarget = Path.of(opt.DestinationMovies, UUID.randomUUID() + "." + PathFormatter.getExtension(source));
			var finptarget = ptarget;

			if (opt.Porcelain) return;

			Files.move(pSource, ptarget);

			SwingUtils.invokeAndWait(() ->
			{
				elem.Part.set(PathFormatter.getCCPath(finptarget.toAbsolutePath().toString()));
			});
		}
		else
		{
			throw new Exception("Type");
		}
	}

	private static void executeSetProp(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var ielem = getElement(ml, cmd, state);

		var propname = cmd.XML.getAttributeValueOrThrow("prop");
		var valNew   = cmd.XML.getAttributeValueOrThrow("value_new");

		var prop = CCStreams.iterate(ielem.getProperties()).singleOrNull(p -> p.getName().equals(propname));
		if (prop == null) throw new Exception("Unknown prop: " + propname);

		if (cmd.XML.hasAttribute("value_old"))
		{
			var valOld  = cmd.XML.getAttributeValueOrThrow("value_old");
			var valCurr = prop.serializeToString();

			if (!valCurr.equals(valOld))
				throw new Exception(Str.format("Cannot set property {0} of {1} to {2}. Diff on value_old: \"{3}\" <> \"{4}\"",
					propname,
					ielem.getLocalID(),
					valNew,
					valCurr,
					valOld));
		}

		if (opt.Porcelain) return;

		AtomicReference<Exception> _inner = new AtomicReference<>(null);
		SwingUtils.invokeAndWait(() ->
		{
			try {
				prop.deserializeFromString(valNew);
			} catch (CCFormatException e) {
				_inner.set(e);
			}
		});
		if (_inner.get() != null) throw _inner.get();
	}

	private static void executeReplaceCover(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var ielem = getElement(ml, cmd, state);

		var source     = cmd.XML.getAttributeValueOrThrow("source");
		var sourcehash = cmd.XML.getAttributeValueOrThrow("sourcehash");

		var img = ImageIO.read(new File(source));

		if (ielem instanceof CCDatabaseElement)
		{
			var elem = (CCDatabaseElement)ielem;

			SwingUtils.invokeAndWait(() ->
			{
				elem.setCover(img);
			});
		}
		else if (ielem instanceof CCSeason)
		{
			var elem = (CCSeason)ielem;

			SwingUtils.invokeAndWait(() ->
			{
				elem.setCover(img);
			});
		}
		else
		{
			throw new Exception("Type");
		}
	}

	private static void executeCalcMediaInfoSubjective(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var ielem = getElement(ml, cmd, state);

		if (ielem instanceof CCMovie)
		{
			var elem = (CCMovie)ielem;

			BasicFileAttributes attr = Files.readAttributes(new File(PathFormatter.fromCCPath(elem.getParts().get(0))).toPath(), BasicFileAttributes.class);

			if (opt.Porcelain) return;

			SwingUtils.invokeAndWait(() ->
			{
				var cdate = attr.lastModifiedTime().toMillis();
				elem.mediaInfo().MDate.set(cdate);
			});
		}
		else if (ielem instanceof CCEpisode)
		{
			var elem = (CCEpisode)ielem;

			BasicFileAttributes attr = Files.readAttributes(new File(PathFormatter.fromCCPath(elem.getParts().get(0))).toPath(), BasicFileAttributes.class);

			if (opt.Porcelain) return;

			SwingUtils.invokeAndWait(() ->
			{
				var cdate = attr.creationTime().toMillis();
				elem.mediaInfo().CDate.set(cdate);
			});
		}
		else
		{
			throw new Exception("Type");
		}
	}

	private static void executeInsertElement(ActionVM act, CCMovieList ml, ActionCommandVM cmd, PatchExecState state, PatchExecOptions opt, DoubleProgressCallbackListener cb) throws Exception
	{
		var type  = cmd.XML.getAttributeValueOrThrow("type");
		var idOut = cmd.XML.getAttributeValueOrDefault("output_id", null);

		var xmlvers = cmd.XML.getAttributeIntValueOrThrow("xmlversion");

		if (Str.equals(type.toUpperCase(), "MOVIE"))
		{
			var dat = act.XML.getFirstChildOrNull("movie");
			if (dat == null) throw new Exception("Missing data");

			if (opt.Porcelain) { if (idOut != null) state.Variables.put(idOut, "(porcelain)"); return; }

			AtomicReference<Exception> _inner = new AtomicReference<>(null);
			SwingUtils.invokeAndWait(() ->
			{
				try
				{
					var dbelem = ml.createNewEmptyMovie();
					DatabaseXMLImporter.parseSingleMovie(dbelem, dat, fn->null, new ImportState(xmlvers, new ImportOptions
					(
						true, // resetAddDate
						true, // resetViewed
						true, // resetScore
						true, // resetTags
						true  // ignoreCoverData
					)));

					if (idOut != null) state.Variables.put(idOut, String.valueOf(dbelem.getLocalID()));
				}
				catch (Exception e)
				{
					_inner.set(e);
				}
			});
			if (_inner.get() != null) throw _inner.get();
		}
		else if (Str.equals(type.toUpperCase(), "SERIES"))
		{
			var dat = act.XML.getFirstChildOrNull("series");
			if (dat == null) throw new Exception("Missing data");

			if (opt.Porcelain) { if (idOut != null) state.Variables.put(idOut, "(porcelain)"); return; }

			AtomicReference<Exception> _inner = new AtomicReference<>(null);
			SwingUtils.invokeAndWait(() ->
			{
				try
				{
					var dbelem = ml.createNewEmptySeries();
					DatabaseXMLImporter.parseSingleSeries(dbelem, dat, fn->null, new ImportState(xmlvers, new ImportOptions
					(
						true, // resetAddDate
						true, // resetViewed
						true, // resetScore
						true, // resetTags
						true  // ignoreCoverData
					)));

					if (idOut != null) state.Variables.put(idOut, String.valueOf(dbelem.getLocalID()));
				}
				catch (Exception e)
				{
					_inner.set(e);
				}
			});
			if (_inner.get() != null) throw _inner.get();
		}
		else if (Str.equals(type.toUpperCase(), "SEASON"))
		{
			var dat = act.XML.getFirstChildOrNull("series");
			if (dat == null) throw new Exception("Missing data");

			var parent = (CCSeries)getElement(ml, cmd, state, "series_id", "type");

			if (opt.Porcelain) { if (idOut != null) state.Variables.put(idOut, "(porcelain)"); return; }

			AtomicReference<Exception> _inner = new AtomicReference<>(null);
			SwingUtils.invokeAndWait(() ->
			{
				try
				{
					var dbelem = ml.createNewEmptySeason(parent);
					DatabaseXMLImporter.parseSingleSeason(dbelem, dat, fn->null, new ImportState(xmlvers, new ImportOptions
					(
						true, // resetAddDate
						true, // resetViewed
						true, // resetScore
						true, // resetTags
						true  // ignoreCoverData
					)));

					if (idOut != null) state.Variables.put(idOut, String.valueOf(dbelem.getLocalID()));
				}
				catch (Exception e)
				{
					_inner.set(e);
				}
			});
			if (_inner.get() != null) throw _inner.get();
		}
		else if (Str.equals(type.toUpperCase(), "EPISODE"))
		{
			var dat = act.XML.getFirstChildOrNull("series");
			if (dat == null) throw new Exception("Missing data");

			var parent = (CCSeason)getElement(ml, cmd, state, "season_id", "type");

			if (opt.Porcelain) { if (idOut != null) state.Variables.put(idOut, "(porcelain)"); return; }

			AtomicReference<Exception> _inner = new AtomicReference<>(null);
			SwingUtils.invokeAndWait(() ->
			{
				try
				{
					var dbelem = ml.createNewEmptyEpisode(parent);
					DatabaseXMLImporter.parseSingleEpisode(dbelem, dat, fn->null, new ImportState(xmlvers, new ImportOptions
					(
						true, // resetAddDate
						true, // resetViewed
						true, // resetScore
						true, // resetTags
						true  // ignoreCoverData
					)));

					if (idOut != null) state.Variables.put(idOut, String.valueOf(dbelem.getLocalID()));
				}
				catch (Exception e)
				{
					_inner.set(e);
				}
			});
			if (_inner.get() != null) throw _inner.get();
		}
		else
		{
			throw new Exception("Type");
		}

	}

	private static ICCDatabaseStructureElement getElement(CCMovieList ml, ActionCommandVM cmd, PatchExecState state) throws Exception
	{
		return getElement(ml, cmd, state, "id", "type");
	}

	private static ICCDatabaseStructureElement getElement(CCMovieList ml, ActionCommandVM cmd, PatchExecState state, String keyID, String keyType) throws Exception
	{
		var type  = cmd.XML.getAttributeValueOrThrow(keyType);
		var idraw = cmd.XML.getAttributeValueOrThrow(keyID);

		int id = Integer.parseInt(state.Variables.getOrDefault(idraw, idraw));

		if (Str.equals(type.toUpperCase(), "MOVIE"))
		{
			var v = ml.iteratorMovies().singleOrNull(e -> e.LocalID.get().equals(id));
			if (v == null) throw new Exception("Could not find [MOVIE] with id " + idraw + " ( = " + id + ")");
			return v;
		}

		if (Str.equals(type.toUpperCase(), "SERIES"))
		{
			var v = ml.iteratorSeries().singleOrNull(e -> e.LocalID.get().equals(id));
			if (v == null) throw new Exception("Could not find [MOVIE] with id " + idraw + " ( = " + id + ")");
			return v;
		}

		if (Str.equals(type.toUpperCase(), "SEASON"))
		{
			var v = ml.iteratorSeasons().singleOrNull(e -> e.LocalID.get().equals(id));
			if (v == null) throw new Exception("Could not find [MOVIE] with id " + idraw + " ( = " + id + ")");
			return v;
		}

		if (Str.equals(type.toUpperCase(), "EPISODE"))
		{
			var v = ml.iteratorEpisodes().singleOrNull(e -> e.LocalID.get().equals(id));
			if (v == null) throw new Exception("Could not find [MOVIE] with id " + idraw + " ( = " + id + ")");
			return v;
		}

		throw new Exception("Unknown element type: " + type);
	}
}
