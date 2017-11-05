package de.jClipCorn.database.util;

import java.util.List;

import org.apache.commons.lang.mutable.MutableInt;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.NextEpisodeHeuristic;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;

public class NextEpisodeHelper {
	
	public static CCEpisode findNextEpisode(CCSeries s) {
		List<CCEpisode> el = s.getEpisodeList();
		
		if (el.size() == 0) return null;

		NextEpisodeHeuristic heuristic = CCProperties.getInstance().PROP_SERIES_NEXT_EPISODE_HEURISTIC.getValue();
		
		switch (heuristic) {
			case AUTOMATIC: return findNextEpisode_Automatic(s, el);
			case FIRST_UNWATCHED: return findNextEpisode_FirstUnwatched(s, el);
			case NEXT_EPISODE: return findNextEpisode_AfterLastViewed(s, el);
			case CONTINUOUS: return findNextEpisode_Continuous(s, el, new MutableInt());
		}
		
		CCLog.addDefaultSwitchError(NextEpisodeHelper.class, heuristic);
		return null;
	}

	private static CCEpisode findNextEpisode_Automatic(CCSeries s, List<CCEpisode> el) {

		if (s.isUnviewed()) {
			
			// [0] Nothing viewed - take first
			
			return el.get(0);
		}
		
		MutableInt episode_continuous_count = new MutableInt(0);
		CCEpisode episode_continuous = findNextEpisode_Continuous(s, el, episode_continuous_count);

		CCEpisode episode_lastWatched_next = findNextEpisode_AfterLastViewed(s, el);
		
		if (episode_continuous_count.intValue() > 2 && episode_lastWatched_next == episode_continuous) {
			
			// [1] Continoous chain from beginning with > 2 elements
			//     and the last in chain is the series-global last-viewed
			//     Return next in chain
			
			return episode_continuous;
		}
		
		CCEpisode next_unwatched_episode = findNextEpisode_FirstUnwatched(s, el);
		
		if (!s.isViewed() && isContinuousViewBlock(el)) {
			
			// [2] A bunch of viewed episodes and then a bunch of not-viewed ones
			//     Return the first not-viewed
			
			return next_unwatched_episode;
		}
		
		{
			// [3] The episode after the last viewed one

			return episode_lastWatched_next;
		}
	}

	private static boolean isContinuousViewBlock(List<CCEpisode> el) {
		if (!el.get(0).isViewed()) return false;
		
		int i = 1;
		
		for(;i<el.size();i++) {
			if (!el.get(i).isViewed()) break;
		}
		
		for(;i<el.size();i++) {
			if (el.get(i).isViewed()) return false;
		}
		
		return true;
	}

	private static CCEpisode findNextEpisode_Continuous(CCSeries s, List<CCEpisode> el, MutableInt continoousCount) {

		CCDate d = CCDate.getMinimumDate();
		
		for (int i = 0; i < el.size(); i++) {
			if (!el.get(i).isViewed() || d.isGreaterThan(el.get(i).getViewedHistoryLast())) {
				continoousCount.setValue(i);
				return el.get(i);
			}
			d = el.get(i).getViewedHistoryLast();
		}

		continoousCount.setValue(0);
		return el.get(0);
	}

	private static CCEpisode findNextEpisode_FirstUnwatched(CCSeries s, List<CCEpisode> el) {

		for(int count = 0;count < 32; count++) {
			for (CCEpisode epis : el) {
				if (epis.getViewedHistory().count() == count) return epis;
			}
		}
		
		return null;
	}

	private static CCEpisode findNextEpisode_AfterLastViewed(CCSeries s, List<CCEpisode> el) {
		if (s.isUnviewed()) return el.get(0);

		int idx = -1;
		CCDateTime t = CCDateTime.getUnspecified();

		for (int i = 0; i < el.size(); i++) {
			if (!el.get(i).isViewed()) continue;
			
			CCDateTime dt = el.get(i).getViewedHistoryLastDateTime();
			
			if (t.isUnspecifiedOrMinimum() || (!dt.isUnspecifiedOrMinimum() && dt.isGreaterEqualsThan(t))) {
				idx = i;
				t = dt;
			}
		}
		
		if (idx+1 < el.size()) return el.get(idx+1);
		return el.get(0);
	}
	
}
