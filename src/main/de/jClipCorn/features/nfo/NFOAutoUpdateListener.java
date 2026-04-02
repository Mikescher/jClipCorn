package de.jClipCorn.features.nfo;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.filesystem.FSPath;

@SuppressWarnings("nls")
public class NFOAutoUpdateListener extends CCDBUpdateAdapter {

	private final CCMovieList movielist;

	public NFOAutoUpdateListener(CCMovieList ml) {
		this.movielist = ml;
	}

	private boolean movieAutoEnabled() {
		return movielist.ccprops().PROP_NFO_CREATE_MOVIES.getValue()
			&& movielist.ccprops().PROP_NFO_AUTO_CREATE_MOVIES.getValue();
	}

	private boolean seriesAutoEnabled() {
		return movielist.ccprops().PROP_NFO_CREATE_SERIES.getValue()
			&& movielist.ccprops().PROP_NFO_AUTO_CREATE_SERIES.getValue();
	}

	private void deleteFileSafe(FSPath path) {
		if (path == null || path.isEmpty()) return;
		if (path.exists()) path.toFile().delete();
	}

	@Override
	public void onChangeDatabaseElement(CCDatabaseElement rootElement, ICCDatabaseStructureElement actualElement, String[] props) {
		if (movielist.isReadonly()) return;

		if (actualElement instanceof CCMovie movie) {
			if (!movieAutoEnabled()) return;

			FSPath oldNfo = movie.NfoPath;
			FSPath oldCover = movie.NfoCoverPath;

			FSPath newNfo = NFOGenerator.generateAndApplyForMovie(movie);
			FSPath newCover = NFOGenerator.applyPosterForMovie(movie);

			if (!oldNfo.isEmpty() && !oldNfo.equals(newNfo)) deleteFileSafe(oldNfo);
			if (!oldCover.isEmpty() && !oldCover.equals(newCover)) deleteFileSafe(oldCover);

			movie.NfoPath = newNfo;
			movie.NfoCoverPath = newCover;

		} else if (actualElement instanceof CCEpisode episode) {
			if (!seriesAutoEnabled()) return;

			FSPath oldNfo = episode.NfoPath;
			FSPath newNfo = NFOGenerator.generateAndApplyForEpisode(episode);

			if (!oldNfo.isEmpty() && !oldNfo.equals(newNfo)) deleteFileSafe(oldNfo);
			episode.NfoPath = newNfo;

			if (containsProp(props, "@EPISODES")) {
				updateSeriesNfo(rootElement.asSeries());
			}

		} else if (actualElement instanceof CCSeason season) {
			if (!seriesAutoEnabled()) return;

			updateSeriesNfo(rootElement.asSeries());

			if (containsProp(props, "CoverID")) {
				FSPath oldCover = season.NfoCoverPath;
				FSPath newCover = NFOGenerator.applyPosterForSeason(rootElement.asSeries(), season);

				if (!oldCover.isEmpty() && !oldCover.equals(newCover)) deleteFileSafe(oldCover);
				season.NfoCoverPath = newCover;
			}

		} else if (actualElement instanceof CCSeries series) {
			if (!seriesAutoEnabled()) return;

			updateSeriesNfo(series);

			if (containsProp(props, "CoverID")) {
				FSPath oldCover = series.NfoCoverPath;
				FSPath newCover = NFOGenerator.applyPosterForSeries(series);

				if (!oldCover.isEmpty() && !oldCover.equals(newCover)) deleteFileSafe(oldCover);
				series.NfoCoverPath = newCover;
			}
		}
	}

	@Override
	public void onRemDatabaseElement(CCDatabaseElement el) {
		if (el.isMovie()) {
			if (!movieAutoEnabled()) return;
			CCMovie movie = el.asMovie();
			deleteFileSafe(movie.NfoPath);
			deleteFileSafe(movie.NfoCoverPath);
		} else if (el.isSeries()) {
			if (!seriesAutoEnabled()) return;
			CCSeries series = el.asSeries();
			deleteFileSafe(series.NfoPath);
			deleteFileSafe(series.NfoCoverPath);
		}
	}

	@Override
	public void onRemSeason(CCSeason season) {
		if (!seriesAutoEnabled()) return;
		deleteFileSafe(season.NfoCoverPath);
	}

	@Override
	public void onRemEpisode(CCEpisode episode) {
		if (!seriesAutoEnabled()) return;
		deleteFileSafe(episode.NfoPath);
	}

	private void updateSeriesNfo(CCSeries series) {
		FSPath oldNfo = series.NfoPath;
		FSPath newNfo = NFOGenerator.generateAndApplyForSeries(series);

		if (!oldNfo.isEmpty() && !oldNfo.equals(newNfo)) deleteFileSafe(oldNfo);
		series.NfoPath = newNfo;
	}

	private static boolean containsProp(String[] props, String name) {
		if (props == null) return false;
		for (String p : props) {
			if (name.equals(p)) return true;
		}
		return false;
	}
}
