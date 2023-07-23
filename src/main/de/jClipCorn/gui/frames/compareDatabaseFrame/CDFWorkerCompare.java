package de.jClipCorn.gui.frames.compareDatabaseFrame;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;

public class CDFWorkerCompare
{
	public static CompareState compare(DoubleProgressCallbackListener cb, CompareDatabaseRuleset ruleset, FSPath dbPath, String dbName, CCMovieList mlLoc) throws Exception
	{
		cb.setMaxAndResetValueBoth(3, 1);
		cb.setValueBoth(0, 0, "Connecting", "");

		var mlExt = CCMovieList.loadExtern(null, dbPath, dbName, true, CCProperties.createReadonly(dbPath.append(Main.PROPERTIES_PATH)));

		if (!mlExt.databaseExists()) throw new Exception("Database " + dbPath + " | " + dbName + " not found");

		cb.setValueBoth(1, 0, "Reading", "");

		mlExt.connectExternal(false);
		try
		{
			if (!Str.equals(mlLoc.getDatabaseVersion(), mlExt.getDatabaseVersion())) throw new Exception("Databases have different versions");

			cb.setValueBoth(2, 0, "Comparing", "");
			cb.setSubMax(mlExt.getTotalDatabaseElementCount() + mlLoc.getTotalDatabaseElementCount() + 1);

			var state = new CompareState(mlLoc, mlExt, cb, ruleset);

			compareAndMatchMovies(mlExt, mlLoc, state);
			compareAndMatchSeries(mlExt, mlLoc, state);

			return state;
		}
		finally
		{
			mlExt.disconnectDatabase(true);
		}
	}

	private static void compareAndMatchMovies(CCMovieList mlExt, CCMovieList mlLoc, CompareState state)
	{
		var movsLoc = mlLoc.iteratorMovies().filter(e -> !state.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var movsExt = mlExt.iteratorMovies().filter(e -> !state.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var mloc : new ArrayList<>(movsLoc))
		{
			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return state.Ruleset.IsMatch(mloc.getLocalID(), m.getLocalID());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with the same checksum are matches
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.MediaInfo.get().Checksum.isEmpty()) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m -> m.MediaInfo.get().Checksum.isEqual(mloc.MediaInfo.get().Checksum, Str::equals), null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with (exactly) the same online-refs + same language
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get()) &&
						mloc.Language.get().isEqual(m.Language.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with (exactly) the same online-refs (but evtl different language)
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyOrder(m.OnlineReference.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with which contains at least 1 online ref with the same value
		for (var mloc : new ArrayList<>(movsLoc))
		{
			if (mloc.OnlineReference.get().totalCount() == 0) continue;

			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				return m.OnlineReference.get().totalCount() > 0 &&
						mloc.OnlineReference.get().equalsAnyNonEmptySubset(m.OnlineReference.get());
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		// Movies with the same name + zyklus + lang
		for (var mloc : new ArrayList<>(movsLoc))
		{
			var mext = CCStreams.iterate(movsExt).singleOrDefault(m ->
			{
				if (!Str.equals(mloc.Title.get(), m.Title.get())) return false;
				if (!Str.equals(mloc.Zyklus.get().getTitle(), m.Zyklus.get().getTitle())) return false;
				if (mloc.Zyklus.get().getNumber() != m.Zyklus.get().getNumber()) return false;
				if (!mloc.Language.get().isEqual(m.Language.get())) return false;
				return true;
			}, null, null);
			if (mext == null) continue;

			state.addMovieMatch(mloc, mext);
			movsLoc.remove(mloc);
			movsExt.remove(mext);
		}

		for (var mloc : movsLoc)
		{
			state.addMovieLocalOnly(mloc);
		}
		movsLoc.clear();

		for (var mext : movsExt)
		{
			state.addMovieExternOnly(mext);
		}
		movsExt.clear();
	}

	private static void compareAndMatchSeries(CCMovieList mlExt, CCMovieList mlLoc, CompareState state)
	{
		var serLoc = mlLoc.iteratorSeries().filter(e -> !state.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var serExt = mlExt.iteratorSeries().filter(e -> !state.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var sloc : new ArrayList<>(serLoc))
		{
			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return state.Ruleset.IsMatch(sloc.getLocalID(), s.getLocalID());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with (exactly) the same online-refs + same language
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 &&
						sloc.OnlineReference.get().equalsAnyOrder(s.OnlineReference.get()) &&
						sloc.getAllLanguages().isEqual(s.getAllLanguages()) &&
						sloc.getCommonLanguages().isEqual(s.getCommonLanguages());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with (exactly) the same online-refs (but evtl different language)
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 && sloc.OnlineReference.get().equalsAnyOrder(s.OnlineReference.get());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with which contains at least 1 online ref with the same value
		for (var sloc : new ArrayList<>(serLoc))
		{
			if (sloc.OnlineReference.get().totalCount() == 0) continue;

			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				return s.OnlineReference.get().totalCount() > 0 &&
						sloc.OnlineReference.get().equalsAnyNonEmptySubset(s.OnlineReference.get());
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		// Series with the same title + lang
		for (var sloc : new ArrayList<>(serLoc))
		{
			var sext = CCStreams.iterate(serExt).singleOrDefault(s ->
			{
				if (!Str.equals(sloc.Title.get(), s.Title.get())) return false;
				if (!sloc.getAllLanguages().isEqual(s.getAllLanguages())) return false;
				if (!sloc.getCommonLanguages().isEqual(s.getCommonLanguages())) return false;
				return true;
			}, null, null);
			if (sext == null) continue;

			var match = state.addSeriesMatch(sloc, sext);
			serLoc.remove(sloc);
			serExt.remove(sext);
			compareAndMatchSeasons(match);
		}

		for (var sloc : serLoc)
		{
			var match = state.addSeriesLocalOnly(sloc);
			for (var nloc: sloc.iteratorSeasons())
			{
				var match2 = match.addSeasonLocalOnly(nloc);
				for (var eloc: nloc.iteratorEpisodes())
				{
					match2.addEpisodeLocalOnly(eloc);
				}
			}
		}
		serLoc.clear();

		for (var sext : serExt)
		{
			var match = state.addSeriesExternOnly(sext);
			for (var next: sext.iteratorSeasons())
			{
				var match2 = match.addSeasonDeleteByParent(next);
				for (var eext: next.iteratorEpisodes())
				{
					match2.addEpisodeDeleteByParent(eext);
				}
			}
		}
		serExt.clear();
	}

	private static void compareAndMatchSeasons(SeriesMatch match)
	{
		var seaLoc = match.SeriesLocal .iteratorSeasons().filter(e -> !match.State.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var seaExt = match.SeriesExtern.iteratorSeasons().filter(e -> !match.State.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return match.State.Ruleset.IsMatch(sloc.getLocalID(), s.getLocalID());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Title+Year
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return Str.equals(s.Title.get(), sloc.Title.get()) &&
						s.Year.get().equals(sloc.Year.get());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Title (evtl diff year)
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return Str.equals(s.Title.get(), sloc.Title.get());
			}, null, null);
			if (sext == null) continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		// Seasons with same Year (must be unique) and same index+episodecount
		for (var sloc : new ArrayList<>(seaLoc))
		{
			var sext = CCStreams.iterate(seaExt).singleOrDefault(s ->
			{
				return s.Year.get().equals(sloc.Year.get());
			}, null, null);
			if (sext == null) continue;
			if (sext.getSortedSeasonNumber() != sloc.getSortedSeasonNumber()) continue;
			if (sext.getEpisodeCount()       != sloc.getEpisodeCount())       continue;

			var submatch = match.addSeasonMatch(sloc, sext);
			seaLoc.remove(sloc);
			seaExt.remove(sext);
			compareAndMatchEpisodes(submatch);
		}

		for (var sloc : seaLoc)
		{
			var submatch = match.addSeasonLocalOnly(sloc);
			for (var eloc: sloc.iteratorEpisodes())
			{
				submatch.addEpisodeLocalOnly(eloc);
			}
		}
		seaLoc.clear();

		for (var sext : seaExt)
		{
			var submatch = match.addSeasonExternOnly(sext);
			for (var eext: sext.iteratorEpisodes())
			{
				submatch.addEpisodeDeleteByParent(eext);
			}
		}
		seaExt.clear();
	}

	private static void compareAndMatchEpisodes(SeasonMatch match)
	{
		var episLoc = match.SeasonLocal .iteratorEpisodes().filter(e -> !match.State.Ruleset.ShouldSkipLoc(e.LocalID.get())).toList();
		var episExt = match.SeasonExtern.iteratorEpisodes().filter(e -> !match.State.Ruleset.ShouldSkipExt(e.LocalID.get())).toList();

		// Force matched by Ruleset
		for (var eloc : new ArrayList<>(episLoc))
		{
			var eext = CCStreams.iterate(episExt).singleOrDefault(e ->
			{
				return match.State.Ruleset.IsMatch(eloc.getLocalID(), e.getLocalID());
			}, null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		// Episodes with the same checksum are matches
		for (var eloc : new ArrayList<>(episLoc))
		{
			if (eloc.MediaInfo.get().Checksum.isEmpty()) continue;

			var eext = CCStreams.iterate(episExt).singleOrDefault(e -> e.MediaInfo.get().Checksum.isEqual(eloc.MediaInfo.get().Checksum, Str::equals), null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		// Episodes with the same number
		for (var eloc : new ArrayList<>(episLoc))
		{
			var eext = CCStreams.iterate(episExt).singleOrDefault(e ->
			{
				return e.EpisodeNumber.get().equals(eloc.EpisodeNumber.get());
			}, null, null);
			if (eext == null) continue;

			match.addEpisodeMatch(eloc, eext);
			episLoc.remove(eloc);
			episExt.remove(eext);
		}

		for (var eloc : episLoc)
		{
			match.addEpisodeLocalOnly(eloc);
		}
		episLoc.clear();

		for (var eext : episExt)
		{
			match.addEpisodeExternOnly(eext);
		}
		episExt.clear();
	}

}
