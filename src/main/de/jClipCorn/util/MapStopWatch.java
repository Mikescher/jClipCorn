package de.jClipCorn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datatypes.Tuple;

@SuppressWarnings("nls")
public class MapStopWatch {

	private Map<Integer, Tuple<Long, Boolean>> watches = new HashMap<>();
	private BiConsumer<Integer, Long> event_timerStop = (id, time) -> {/**/};
	
	public void start(int id) {
		if (watches.containsKey(id)) {
			CCLog.addDebug("MapStopWatch.start() called for already existing value for " + id);
			return;
		}
		
		watches.put(id, Tuple.Create(System.currentTimeMillis(), false));
	}
	
	public long stop(int id) {
		Tuple<Long, Boolean> old = watches.get(id);

		if (old == null) {
			CCLog.addDebug("MapStopWatch.stop() with no MapStopWatch.start() for " + id);
			return -1;
		}
		
		if (old.Item2) {
			CCLog.addDebug("MapStopWatch.stop() on already stopped ID: " + id);
			return -1;
		}
		
		Tuple<Long, Boolean> curr = Tuple.Create(System.currentTimeMillis() - old.Item1, true);
		
		watches.put(id, curr);
		
		event_timerStop.accept(id, curr.Item1);
		
		return curr.Item1;
	}
	
	public long getMilliseconds(int id) {
		return watches.get(id).Item1;
	}
	
	public long getMillisecondsOrZero(int id) {
		Tuple<Long, Boolean> v = watches.get(id);

		if (v == null) return 0;
		if (!v.Item2) return 0;

		return v.Item1;
	}
	
	public double getSeconds(int id) {
		return watches.get(id).Item1 / 1000.0;
	}
	
	public double getSecondsOrZero(int id) {
		Tuple<Long, Boolean> v = watches.get(id);

		if (v == null) return 0;
		if (!v.Item2) return 0;

		return v.Item1 / 1000.0;
	}
	
	public double getSeconds(int id, int precision) {
		double e = Math.pow(10, precision);
		
		double v = watches.get(id).Item1 / 1000.0;
				
		return ((int)(v * e)) / e;
	}
	
	public double getSecondsOrZero(int id, int precision) {
		Tuple<Long, Boolean> v = watches.get(id);

		if (v == null) return 0;
		if (!v.Item2) return 0;

		double e = Math.pow(10, precision);
		return ((int)Math.round((v.Item1 / 1000.0) * e)) / e;
	}

	public void setOnTimerStop(BiConsumer<Integer, Long> evt) {
		event_timerStop = evt;
	}

	public boolean contains(int id) {
		Tuple<Long, Boolean> v = watches.get(id);
		return v != null && v.Item2;
	}
}
