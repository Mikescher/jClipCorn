package de.jClipCorn.features.transactionLog;

import de.jClipCorn.Globals;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.*;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.adapter.CCDBUpdateAdapter;
import de.jClipCorn.util.filesystem.FSPath;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("nls")
public class CCTransactionLog {

	private final FSPath transactionDir;
	private final String sessionFilename;
	private final ExecutorService executor;

	public CCTransactionLog(CCMovieList movielist) {
		this.transactionDir = movielist.getDatabaseDirectory().getParent().append("ClipCornTransactions");
		this.sessionFilename = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date(Globals.MILLIS_MAIN)) + ".jsonl";
		this.executor = Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r, "THREAD_TRANSACTION_LOG");
			t.setDaemon(true);
			return t;
		});
	}

	public void register(CCMovieList ml) {
		ml.addChangeListener(new CCDBUpdateAdapter() {
			@Override
			public void onAddDatabaseElement(CCDatabaseElement el) {
				if (el instanceof CCMovie movie) {
					logEntry(CCTransactionAction.MOVIE_ADDED, "MOVIE", movie.getLocalID(), movie.getQualifiedTitle(), null, null);
				} else if (el instanceof CCSeries series) {
					logEntry(CCTransactionAction.SERIES_ADDED, "SERIES", series.getLocalID(), series.getQualifiedTitle(), null, null);
				}
			}

			@Override
			public void onAddSeason(CCSeason el) {
				logEntry(CCTransactionAction.SEASON_ADDED, "SEASON", el.getLocalID(), el.getQualifiedTitle(), null, null);
			}

			@Override
			public void onAddEpisode(CCEpisode el) {
				logEntry(CCTransactionAction.EPISODE_ADDED, "EPISODE", el.getLocalID(), el.getQualifiedTitle(), null, null);
			}

			@Override
			public void onRemDatabaseElement(CCDatabaseElement el) {
				if (el instanceof CCMovie movie) {
					logEntry(CCTransactionAction.MOVIE_DELETED, "MOVIE", movie.getLocalID(), movie.getQualifiedTitle(), null, null);
				} else if (el instanceof CCSeries series) {
					logEntry(CCTransactionAction.SERIES_DELETED, "SERIES", series.getLocalID(), series.getQualifiedTitle(), null, null);
				}
			}

			@Override
			public void onRemSeason(CCSeason el) {
				logEntry(CCTransactionAction.SEASON_DELETED, "SEASON", el.getLocalID(), el.getQualifiedTitle(), null, null);
			}

			@Override
			public void onRemEpisode(CCEpisode el) {
				logEntry(CCTransactionAction.EPISODE_DELETED, "EPISODE", el.getLocalID(), el.getQualifiedTitle(), null, null);
			}
		});

		ml.addMoviePropChangeListener((prop, movie, oldVal, newVal) -> {
			if ("ViewedHistory".equals(prop.getName())) {
				logEntry(CCTransactionAction.MOVIE_CHANGE_VIEWED, "MOVIE", movie.getLocalID(), movie.getQualifiedTitle(), oldVal, newVal);
			} else if ("Score".equals(prop.getName())) {
				logEntry(CCTransactionAction.MOVIE_USERRATING, "MOVIE", movie.getLocalID(), movie.getQualifiedTitle(), oldVal, newVal);
			} else if ("ScoreComment".equals(prop.getName())) {
				logEntry(CCTransactionAction.MOVIE_USERCOMMENT, "MOVIE", movie.getLocalID(), movie.getQualifiedTitle(), oldVal, newVal);
			}
		});

		ml.addSeriesPropChangeListener((prop, series, oldVal, newVal) -> {
			if ("Score".equals(prop.getName())) {
				logEntry(CCTransactionAction.SERIES_USERRATING, "SERIES", series.getLocalID(), series.getQualifiedTitle(), oldVal, newVal);
			} else if ("ScoreComment".equals(prop.getName())) {
				logEntry(CCTransactionAction.SERIES_USERCOMMENT, "MOVIE", series.getLocalID(), series.getQualifiedTitle(), oldVal, newVal);
			}
		});

		ml.addSeasonPropChangeListener((prop, season, oldVal, newVal) -> {
			if ("Score".equals(prop.getName())) {
				logEntry(CCTransactionAction.SEASON_USERRATING, "SEASON", season.getLocalID(), season.getQualifiedTitle(), oldVal, newVal);
			} else if ("ScoreComment".equals(prop.getName())) {
				logEntry(CCTransactionAction.SEASON_USERCOMMENT, "MOVIE", season.getLocalID(), season.getQualifiedTitle(), oldVal, newVal);
			}
		});

		ml.addEpisodePropChangeListener((prop, episode, oldVal, newVal) -> {
			if ("ViewedHistory".equals(prop.getName())) {
				logEntry(CCTransactionAction.EPISODE_CHANGE_VIEWED, "EPISODE", episode.getLocalID(), episode.getQualifiedTitle(), oldVal, newVal);
			} else if ("Score".equals(prop.getName())) {
				logEntry(CCTransactionAction.EPISODE_USERRATING, "EPISODE", episode.getLocalID(), episode.getQualifiedTitle(), oldVal, newVal);
			} else if ("ScoreComment".equals(prop.getName())) {
				logEntry(CCTransactionAction.EPISODE_USERCOMMENT, "MOVIE", episode.getLocalID(), episode.getQualifiedTitle(), oldVal, newVal);
			}
		});
	}

	private void logEntry(CCTransactionAction action, String type, int localId, String name, Object oldVal, Object newVal) {
		String ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ").format(new Date());
		String oldStr = objToStr(oldVal);
		String newStr = objToStr(newVal);

		String line = "{"
			+ "\"ts\":" + JSONObject.quote(ts)
			+ ",\"action\":" + JSONObject.quote(action.name())
			+ ",\"type\":" + JSONObject.quote(type)
			+ ",\"localid\":" + localId
			+ ",\"name\":" + JSONObject.quote(name)
			+ ",\"old\":" + (oldStr != null ? JSONObject.quote(oldStr) : "null")
			+ ",\"new\":" + (newStr != null ? JSONObject.quote(newStr) : "null")
			+ "}";

		executor.submit(() -> {
			try {
				if (!transactionDir.exists()) {
					transactionDir.mkdirsWithException();
				}

				FSPath filePath = transactionDir.append(sessionFilename);

				try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
					writer.write(line);
					writer.newLine();
				}
			} catch (Exception e) {
				CCLog.addError(e);
			}
		});
	}

	private String objToStr(Object o) {
		if (o == null) return null;

		if (o instanceof String s) return s;

		if (o instanceof CCDateTimeList v) return v.toSerializationString();
		if (o instanceof CCDBLanguageList v) return v.serializeToLongString();
		if (o instanceof CCDBLanguageSet v) return v.serializeToString();
		if (o instanceof CCFileSize v) return v.getFormatted();
		if (o instanceof CCGenreList v) return v.toString();
		if (o instanceof CCGroup v) return v.toString();
		if (o instanceof CCGroupList v) return v.toSerializationString();
		if (o instanceof CCHexColor v) return v.getHex();
		if (o instanceof CCMediaInfo v) return v.toString();
		if (o instanceof CCMovieZyklus v) return v.getFormatted();
		if (o instanceof CCOnlineReferenceList v) return v.toSerializationString();
		if (o instanceof CCOnlineScore v) return v.getDisplayString();
		if (o instanceof CCPathList v) return v.toString();
		if (o instanceof CCSingleOnlineReference v) return v.toSerializationString();
		if (o instanceof CCStringList v) return v.toString();
		if (o instanceof CCTagList v) return v.getAsString();

		return String.valueOf(o);
	}
}
