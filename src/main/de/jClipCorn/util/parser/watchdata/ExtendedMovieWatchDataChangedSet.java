package de.jClipCorn.util.parser.watchdata;

import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.util.datetime.CCDateTime;

public class ExtendedMovieWatchDataChangedSet extends WatchDataChangeSet {
	private CCMovie mov;
	private CCDateTime date;
	private CCMovieScore score;
	
	public ExtendedMovieWatchDataChangedSet(CCDateTime d, CCMovieScore s, CCMovie m, boolean newViewed) {
		super(newViewed);
		this.mov = m;
		this.date = d;
		this.score = s;
	}

	@Override
	public String getDate() {
		if (date != null) 
			return date.getSimpleStringRepresentation();
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
		
		if (date != null && ! mov.getViewedHistory().contains(date)) {
			if (b.length()>0)b.append(" & ");
			
			b.append(String.format("history += %s", date.getSimpleStringRepresentation()));
		}

		if (score != null && mov.getScore() != score) {
			if (b.length()>0)b.append(" & ");
			
			if (score == CCMovieScore.RATING_NO) 
				b.append("score = #");
			else 
				b.append(String.format("score = %d", score.asInt()));
		}

		if (b.length() == 0) b.append("#");
		
		return b.toString();
	}

	@Override
	public void execute() {
		mov.setViewed(newState);
		
		if (score != null) 
			mov.setScore(score);
		
		if (date != null && !mov.getViewedHistory().contains(date))
			mov.addToViewedHistory(date);
	} 
}
