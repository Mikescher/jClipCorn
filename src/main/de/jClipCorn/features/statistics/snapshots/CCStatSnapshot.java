package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.util.datetime.CCDate;
import org.json.JSONObject;

import java.util.*;

/**
 * The state of the collection at the end of one day.
 *
 * Rows are stored sparsely - a day without a row has the values of the newest row before it - so two
 * consecutive snapshots are only both written when {@link #valuesEqual} says they differ.
 */
public class CCStatSnapshot {

	/** Envelope version of the histogram JSON; a reader that meets a higher one ignores the histograms. */
	private static final int JSON_VERSION = 1;
	private static final String JSON_VERSION_KEY = "v"; //$NON-NLS-1$

	public final CCDate  Date;
	/** false when this day was back-projected from add-dates because the change archive does not reach it */
	public final boolean Exact;

	public final Map<StatClass, Integer> Count;
	public final Map<StatClass, Long>    Bytes;
	public final Map<StatClass, Integer> Minutes;

	/** class -> dimension -> bucket -> count; zero buckets are absent */
	public final Map<StatClass, Map<String, Map<String, Integer>>> Histograms;

	public CCStatSnapshot(CCDate date, boolean exact,
	                      Map<StatClass, Integer> count, Map<StatClass, Long> bytes, Map<StatClass, Integer> minutes,
	                      Map<StatClass, Map<String, Map<String, Integer>>> histograms) {
		Date       = date;
		Exact      = exact;
		Count      = count;
		Bytes      = bytes;
		Minutes    = minutes;
		Histograms = histograms;
	}

	public int  count(StatClass c)   { return Count.getOrDefault(c, 0); }
	public long bytes(StatClass c)   { return Bytes.getOrDefault(c, 0L); }
	public int  minutes(StatClass c) { return Minutes.getOrDefault(c, 0); }

	public Map<String, Integer> histogram(StatClass c, String dimension) {
		Map<String, Map<String, Integer>> byDim = Histograms.get(c);
		if (byDim == null) return Map.of();
		return byDim.getOrDefault(dimension, Map.of());
	}

	/** Everything except the date - the {@code Exact} flag counts, so the boundary day is always written. */
	public boolean valuesEqual(CCStatSnapshot other) {
		if (other == null) return false;
		if (Exact != other.Exact) return false;

		return Count.equals(other.Count)
			&& Bytes.equals(other.Bytes)
			&& Minutes.equals(other.Minutes)
			&& Histograms.equals(other.Histograms);
	}

	public String histogramsToJson(StatClass cls) {
		JSONObject obj = new JSONObject();
		obj.put(JSON_VERSION_KEY, JSON_VERSION);

		Map<String, Map<String, Integer>> byDim = Histograms.get(cls);
		if (byDim != null) {
			for (Map.Entry<String, Map<String, Integer>> e : byDim.entrySet()) {
				if (e.getValue().isEmpty()) continue;
				obj.put(e.getKey(), new JSONObject(e.getValue()));
			}
		}

		return obj.toString();
	}

	public static Map<String, Map<String, Integer>> histogramsFromJson(String json) {
		Map<String, Map<String, Integer>> res = new HashMap<>();
		if (json == null || json.isBlank()) return res;

		try {
			JSONObject obj = new JSONObject(json);
			if (obj.optInt(JSON_VERSION_KEY, JSON_VERSION) > JSON_VERSION) return res;

			for (String dim : obj.keySet()) {
				if (dim.equals(JSON_VERSION_KEY)) continue;

				JSONObject buckets = obj.optJSONObject(dim);
				if (buckets == null) continue;

				Map<String, Integer> m = new HashMap<>();
				for (String b : buckets.keySet()) m.put(b, buckets.optInt(b, 0));
				res.put(dim, m);
			}
		} catch (Exception e) {
			return new HashMap<>();
		}

		return res;
	}
}
