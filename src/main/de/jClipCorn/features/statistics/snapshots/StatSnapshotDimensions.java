package de.jClipCorn.features.statistics.snapshots;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategoryType;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;

import java.util.*;

import static de.jClipCorn.features.statistics.snapshots.StatClass.*;
import static de.jClipCorn.features.statistics.snapshots.StatSnapshotDimension.*;

/**
 * Every histogram a daily snapshot records, plus the derived indexes the replay and the readers need.
 *
 * Adding a dimension is one entry in {@link #ALL} - no schema migration, because the histograms are
 * stored as JSON (see DatabaseStructure.TAB_STATSNAPSHOTS). Existing rows simply lack the new key until
 * the snapshots are rebuilt.
 *
 * Bucket keys are the raw column text, i.e. the enum id the database itself stores. The ids of a
 * ContinoousEnum are explicitly pinned, so they are stable, and using them means a single-valued
 * dimension needs no decoding at all - the value from the history archive is already the bucket.
 */
@SuppressWarnings("nls")
public final class StatSnapshotDimensions {
	private StatSnapshotDimensions() { throw new InstantiationError(); }

	/** The columns whose totals become typed snapshot columns rather than a histogram. */
	public static final String COL_FILESIZE = "FILESIZE";
	public static final String COL_LENGTH   = "LENGTH";
	public static final String COL_ADDDATE  = "ADDDATE";

	/** Not aggregated, but tracked: only episodes carry an add-date, so a season/series leaves the
	 *  collection when its last child does. */
	public static final String COL_SEASONID = "SEASONID";
	public static final String COL_SERIESID = "SERIESID";

	public static final List<StatSnapshotDimension> ALL = List.of(

		// ---- plain enum / numeric columns: the stored value is the bucket ----------------------------

		new StatSnapshotDimension("FORMAT",     classes(MOVIE, EPISODE),                cols("FORMAT"),     r -> one(r.get("FORMAT"))),
		new StatSnapshotDimension("FSK",        classes(MOVIE, SERIES),                 cols("FSK"),        r -> one(r.get("FSK"))),
		new StatSnapshotDimension("SCORE",      classes(MOVIE, SERIES, SEASON, EPISODE), cols("SCORE"),     r -> one(r.get("SCORE"))),
		new StatSnapshotDimension("YEAR",       classes(MOVIE),                         cols("MOVIEYEAR"),  r -> one(r.get("MOVIEYEAR"))),
		new StatSnapshotDimension("YEAR",       classes(SEASON),                        cols("SEASONYEAR"), r -> one(r.get("SEASONYEAR"))),

		new StatSnapshotDimension("VCODEC",     classes(MOVIE, EPISODE), cols("MEDIAINFO.VCODEC"),    r -> one(r.get("MEDIAINFO.VCODEC"))),
		new StatSnapshotDimension("ACODEC",     classes(MOVIE, EPISODE), cols("MEDIAINFO.ACODEC"),    r -> one(r.get("MEDIAINFO.ACODEC"))),
		new StatSnapshotDimension("VFORMAT",    classes(MOVIE, EPISODE), cols("MEDIAINFO.VFORMAT"),   r -> one(r.get("MEDIAINFO.VFORMAT"))),
		new StatSnapshotDimension("AFORMAT",    classes(MOVIE, EPISODE), cols("MEDIAINFO.AFORMAT"),   r -> one(r.get("MEDIAINFO.AFORMAT"))),
		new StatSnapshotDimension("ACHANNELS",  classes(MOVIE, EPISODE), cols("MEDIAINFO.ACHANNELS"), r -> one(r.get("MEDIAINFO.ACHANNELS"))),
		new StatSnapshotDimension("BITDEPTH",   classes(MOVIE, EPISODE), cols("MEDIAINFO.BITDEPTH"),  r -> one(r.get("MEDIAINFO.BITDEPTH"))),

		new StatSnapshotDimension("RESOLUTION", classes(MOVIE, EPISODE), cols("MEDIAINFO.WIDTH", "MEDIAINFO.HEIGHT"),
				r -> {
					String w = r.get("MEDIAINFO.WIDTH"), h = r.get("MEDIAINFO.HEIGHT");
					return (w == null || h == null) ? List.of() : one(w + "x" + h);
				}),

		new StatSnapshotDimension("MI_SET",     classes(MOVIE, EPISODE), cols("MEDIAINFO.CHECKSUM"),
				r -> one(Str.isNullOrWhitespace(r.get("MEDIAINFO.CHECKSUM")) ? "0" : "1")),

		/*
		 * Movies only: an episode's quality category is computed against its *series'* genres (the
		 * "ignore bitrate" exception), which would make every episode's bucket depend on another
		 * element's column. Episodes are covered by RESOLUTION and the codec dimensions instead.
		 */
		new StatSnapshotDimension("QUALITY",    classes(MOVIE),
				cols("MEDIAINFO.WIDTH", "MEDIAINFO.HEIGHT", "MEDIAINFO.BITRATE", "MEDIAINFO.FRAMERATE", "GENRE"),
				r -> one(String.valueOf(qualityCategoryId(r)))),

		// ---- JSON list columns -----------------------------------------------------------------------

		new StatSnapshotDimension("LANGUAGE",   classes(MOVIE, EPISODE), cols("LANGUAGE"),
				r -> ids(CCDBLanguageSet.fromJSONArray(nz(r.get("LANGUAGE"))).ccstream().map(CCDBLanguage::asInt).enumerate())),

		new StatSnapshotDimension("SUBTITLE",   classes(MOVIE, EPISODE), cols("SUBTITLES"),
				r -> ids(CCDBLanguageList.fromJSONArray(nz(r.get("SUBTITLES"))).ccstream().map(CCDBLanguage::asInt).enumerate())),

		new StatSnapshotDimension("GENRE",      classes(MOVIE, SERIES), cols("GENRE"),
				r -> {
					List<String> res = new ArrayList<>();
					for (CCGenre g : CCGenreList.fromJSONArray(nz(r.get("GENRE"))).getGenres()) res.add(String.valueOf(g.asInt()));
					return res;
				}),

		new StatSnapshotDimension("TAG",        classes(MOVIE, SERIES, EPISODE), cols("TAGS"),
				r -> ids(CCTagList.fromJSONArray(nz(r.get("TAGS"))).ccstream().map(CCSingleTag::asInt).enumerate())),

		new StatSnapshotDimension("PROVIDER",   classes(MOVIE, SERIES, SEASON), cols("ONLINEREF"),
				r -> CCOnlineReferenceList.fromJSONArray(nz(r.get("ONLINEREF"))).ccstream().map(p -> p.type.asString()).enumerate()),

		// free-form (user-defined) bucket keys - these are names, not ids
		new StatSnapshotDimension("GROUP",          classes(MOVIE, SERIES), cols("GROUPS"),         r -> jsonStrings(r.get("GROUPS"))),
		new StatSnapshotDimension("SPECIALVERSION", classes(MOVIE, SERIES), cols("SPECIALVERSION"), r -> jsonStrings(r.get("SPECIALVERSION"))),
		new StatSnapshotDimension("ANIMESEASON",    classes(MOVIE, SEASON), cols("ANIMESEASON"),    r -> jsonStrings(r.get("ANIMESEASON"))),
		new StatSnapshotDimension("ANIMESTUDIO",    classes(MOVIE, SEASON), cols("ANIMESTUDIO"),    r -> jsonStrings(r.get("ANIMESTUDIO"))),

		// ---- userdata ---------------------------------------------------------------------------------

		new StatSnapshotDimension("VIEWED",     classes(MOVIE, EPISODE), cols("VIEWED_HISTORY"),
				r -> one(CCDateTimeList.fromJSONArray(nz(r.get("VIEWED_HISTORY"))).count() > 0 ? "1" : "0")),

		new StatSnapshotDimension("WATCHCOUNT", classes(MOVIE, EPISODE), cols("VIEWED_HISTORY"),
				r -> one(String.valueOf(CCDateTimeList.fromJSONArray(nz(r.get("VIEWED_HISTORY"))).count())))
	);

	/** Every column any dimension or scalar of this element kind reads - the single source of truth for
	 *  both the current-state SELECT and the {@code FIELD IN (...)} filter of the history scan. */
	public static final Map<StatClass, Set<String>> TRACKED_COLUMNS = buildTrackedColumns();

	/** column -> the dimensions of that element kind that have to be re-bucketed when it changes */
	private static final Map<StatClass, Map<String, List<StatSnapshotDimension>>> BY_COLUMN = buildByColumn();

	public static List<StatSnapshotDimension> forClass(StatClass cls) {
		List<StatSnapshotDimension> r = new ArrayList<>();
		for (StatSnapshotDimension d : ALL) if (d.Classes.contains(cls)) r.add(d);
		return r;
	}

	public static List<StatSnapshotDimension> forColumn(StatClass cls, String column) {
		return BY_COLUMN.get(cls).getOrDefault(column, List.of());
	}

	/** All fields the history scan has to fetch, across all element kinds. */
	public static Set<String> allTrackedColumns() {
		Set<String> r = new HashSet<>();
		for (Set<String> s : TRACKED_COLUMNS.values()) r.addAll(s);
		return r;
	}

	private static Map<StatClass, Set<String>> buildTrackedColumns() {
		Map<StatClass, Set<String>> res = new EnumMap<>(StatClass.class);

		for (StatClass cls : StatClass.values()) {
			Set<String> cols = new HashSet<>();
			for (StatSnapshotDimension d : ALL) if (d.Classes.contains(cls)) cols.addAll(d.Columns);

			// the scalars, plus the add-date the pre-history back-projection needs
			if (cls.isPlayable()) { cols.add(COL_FILESIZE); cols.add(COL_LENGTH); cols.add(COL_ADDDATE); }

			if (cls == EPISODE) cols.add(COL_SEASONID);
			if (cls == SEASON)  cols.add(COL_SERIESID);

			res.put(cls, Set.copyOf(cols));
		}

		return Collections.unmodifiableMap(res);
	}

	private static Map<StatClass, Map<String, List<StatSnapshotDimension>>> buildByColumn() {
		Map<StatClass, Map<String, List<StatSnapshotDimension>>> res = new EnumMap<>(StatClass.class);

		for (StatClass cls : StatClass.values()) {
			Map<String, List<StatSnapshotDimension>> m = new HashMap<>();
			for (StatSnapshotDimension d : ALL) {
				if (!d.Classes.contains(cls)) continue;
				for (String c : d.Columns) m.computeIfAbsent(c, k -> new ArrayList<>()).add(d);
			}
			res.put(cls, m);
		}

		return Collections.unmodifiableMap(res);
	}

	private static String nz(String v) {
		return (v == null) ? Str.Empty : v;
	}

	private static List<String> ids(List<Integer> values) {
		List<String> r = new ArrayList<>(values.size());
		for (Integer v : values) r.add(String.valueOf(v));
		return r;
	}

	private static List<String> jsonStrings(String json) {
		try {
			List<String> r = new ArrayList<>();
			for (String s : CCStringList.deserialize(nz(json))) r.add(s);
			return r;
		} catch (Exception e) {
			return List.of();
		}
	}

	private static int qualityCategoryId(StatSnapshotRawRow r) {
		CCMediaInfo mi = CCMediaInfo.create(
				Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty(),
				optDouble(r, "MEDIAINFO.DURATION"), optInt(r, "MEDIAINFO.BITRATE"),
				Opt.empty(), optInt(r, "MEDIAINFO.WIDTH"), optInt(r, "MEDIAINFO.HEIGHT"), optDouble(r, "MEDIAINFO.FRAMERATE"),
				Opt.empty(), Opt.empty(), Opt.empty(),
				Opt.empty(), Opt.empty(), Opt.empty(), Opt.empty());

		return mi.getCategory(CCGenreList.fromJSONArray(nz(r.get("GENRE"))))
				 .map(p -> p.getCategoryType().asInt())
				 .orElse(CCQualityCategoryType.UNKOWN.asInt());
	}

	private static Opt<Integer> optInt(StatSnapshotRawRow r, String col) {
		String v = r.get(col);
		if (v == null) return Opt.empty();
		try { return Opt.of(Integer.parseInt(v.trim())); } catch (NumberFormatException e) { return Opt.empty(); }
	}

	private static Opt<Double> optDouble(StatSnapshotRawRow r, String col) {
		String v = r.get(col);
		if (v == null) return Opt.empty();
		try { return Opt.of(Double.parseDouble(v.trim())); } catch (NumberFormatException e) { return Opt.empty(); }
	}
}
