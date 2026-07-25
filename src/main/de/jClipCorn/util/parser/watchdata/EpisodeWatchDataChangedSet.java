package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datetime.CCDateTime;

public class EpisodeWatchDataChangedSet extends WatchDataChangeSet {
	private final CCEpisode eps;
	private final CCDateTime date;
	private final CCUserScore score;

	public EpisodeWatchDataChangedSet(CCDateTime d, CCEpisode e, boolean newViewed) {
		this(d, e, newViewed, null);
	}

	public EpisodeWatchDataChangedSet(CCDateTime d, CCEpisode e, boolean newViewed, CCUserScore newScore) {
		super(newViewed);
		this.eps = e;
		this.date = d;
		this.score = newScore;
	}

	@Override
	public String getDate() {
		return date.toStringUINormal();
	}

	@Override
	public String getName() {
		return eps.getSeries().Title.get();
	}

	@Override
	public String getSubInfo() {
		return eps.getStringIdentifier();
	}

	@SuppressWarnings("nls")
	@Override
	public String getChange() {
		StringBuilder b = new StringBuilder();

		if (eps.isViewed() ^ newState)
			b.append(String.format("%d -> %d", eps.isViewed()?1:0, newState?1:0));
		else if (newState && ! eps.ViewedHistory.get().contains(date))
			b.append(String.format("history += %s", date.toStringUINormal()));

		if (score != null && eps.Score.get() != score) {
			if (b.length() > 0) b.append(" & ");

			if (score == CCUserScore.RATING_NO)
				b.append("score = #");
			else
				b.append(String.format("score = %d", score.asInt()));
		}

		if (b.length() == 0) b.append("#");

		return b.toString();
	}

	@Override
	public void execute() {
		if (score != null) eps.Score.set(score);

		if (newState && !eps.ViewedHistory.get().contains(date))
		{
			eps.addToViewedHistoryFromUI(date);
		}
		else if (!newState)
		{
			eps.ViewedHistory.set(CCDateTimeList.createEmpty());
		}
	}
}
