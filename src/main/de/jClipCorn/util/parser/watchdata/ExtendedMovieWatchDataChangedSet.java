package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.util.datetime.CCDateTime;

public class ExtendedMovieWatchDataChangedSet extends WatchDataChangeSet {
	private final CCMovie mov;
	private final CCDateTime date;
	private final CCUserScore score;
	
	public ExtendedMovieWatchDataChangedSet(CCDateTime d, CCUserScore s, CCMovie m, boolean newViewed) {
		super(newViewed);
		this.mov = m;
		this.date = d;
		this.score = s;
	}

	@Override
	public String getDate() {
		if (date != null) 
			return date.toStringUINormal();
		else 
			return "-"; //$NON-NLS-1$
	}

	@Override
	public String getName() {
		return mov.getCompleteTitle();
	}

	@Override
	public String getSubInfo() {
		return "-"; //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	@Override
	public String getChange() {
		StringBuilder b = new StringBuilder();

		if (mov.isViewed() ^ newState)
			b.append(String.format("%d -> %d", mov.isViewed()?1:0, newState?1:0));
		
		if (date != null && ! mov.ViewedHistory.get().contains(date)) {
			if (b.length()>0)b.append(" & ");
			
			b.append(String.format("history += %s", date.toStringUINormal()));
		}

		if (score != null && mov.Score.get() != score) {
			if (b.length()>0)b.append(" & ");
			
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
		if (score != null) mov.Score.set(score);

		if (newState && date != null && !mov.ViewedHistory.get().contains(date)) mov.ViewedHistory.add(date);

		if (newState && date == null) mov.ViewedHistory.add(CCDateTime.getUnspecified());

		if (!newState) mov.ViewedHistory.set(CCDateTimeList.createEmpty());
	} 
}
