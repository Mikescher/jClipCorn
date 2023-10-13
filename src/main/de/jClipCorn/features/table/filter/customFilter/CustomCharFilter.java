package de.jClipCorn.features.table.filter.customFilter;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.AbstractCustomMovieOrSeriesFilter;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterBoolConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterEnumChooserConfig;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterStringConfig;
import de.jClipCorn.features.table.filter.filterSerialization.FilterSerializationConfig;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CharListMatchType;
import de.jClipCorn.util.datatypes.ElemFieldMatchType;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.regex.Pattern;

public class CustomCharFilter extends AbstractCustomMovieOrSeriesFilter {
	@SuppressWarnings("nls")
	private final static String[] EXCLUSIONS = {"der", "die", "das", "the", "den", "le"};

	private static final Pattern REX_CHARS = Pattern.compile("[^A-Za-z0-9 ]");

	private String[] charset = new String[0]; //$NON-NLS-1$
	private CharListMatchType matchMode = CharListMatchType.STRING_START;
	private ElemFieldMatchType fieldSelector = ElemFieldMatchType.TITLE_AND_ZYKLUS;
	private boolean excludeCommonWordStarts = true;
	private boolean ignoreNonFilterableCharacters = true;

	public CustomCharFilter(CCMovieList ml) {
		super(ml);
	}

	public void collectNextChars(CCDatabaseElement e, HashSet<Character> next) {

		if (this.fieldSelector == ElemFieldMatchType.TITLE || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) {
			var str = this.cleanString(e.title().get());
			this.doMatch(str, next);
		}

		if (e.isMovie() && (this.fieldSelector == ElemFieldMatchType.ZYKLUS || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) && e.asMovie().hasZyklus()) {
			var str = this.cleanString(e.asMovie().zyklus().get().getTitle());
			this.doMatch(str, next);
		}
	}

	public boolean isMatch(CCDatabaseElement e) {

		if (this.fieldSelector == ElemFieldMatchType.TITLE || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) {
			var str = this.cleanString(e.title().get());
			if (this.doMatch(str, new HashSet<>())) return true;
		}

		if (e.isMovie() && (this.fieldSelector == ElemFieldMatchType.ZYKLUS || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) && e.asMovie().hasZyklus()) {
			var str = this.cleanString(e.asMovie().zyklus().get().getTitle());
			if (this.doMatch(str, new HashSet<>())) return true;
		}

		return false;
	}

	@SuppressWarnings("nls")
	private String cleanString(String v) {
		v = v.toLowerCase();

		v = v.replace("\t", "");

		v = v.replace("ä", "a");
		v = v.replace("ö", "ü");
		v = v.replace("ü", "u");
		v = v.replace("ß", "s");

		if (excludeCommonWordStarts && matchMode == CharListMatchType.STRING_START) {
			for (String s : EXCLUSIONS) {
				if (v.startsWith(s + Str.SingleSpace)) {
					v = v.substring(s.length() + 1);
				}
			}
		}

		if (ignoreNonFilterableCharacters) {
			v = REX_CHARS.matcher(v).replaceAll("");
		}

		if (matchMode != CharListMatchType.WORD_START) v = v.replaceAll(" ", "");

		return v;
	}

	private boolean doMatch(String v, HashSet<Character> next) {

		if (this.matchMode == CharListMatchType.STRING_START) {

			if (this.startsWithCharSet(v)) {
				if (this.charset.length < v.length()) next.add(v.charAt(this.charset.length));
				return true;
			}

			return false;
		}

		if (this.matchMode == CharListMatchType.WORD_START) {

			var match = false;

			var idx=0;
			while (true) {

				var split = v.substring(idx).replace(" ", "");

				if (this.startsWithCharSet(split)) {
					if (this.charset.length < split.length()) next.add(split.charAt(this.charset.length));
					match = true;
				}

				var nidx = v.indexOf(' ', idx+1);
				if (nidx == -1) {
					return match;
				}
				idx = nidx;
			}

		}

		if (this.matchMode == CharListMatchType.ANYWHERE) {

			var idx = 0;

			var match = false;

			while (true) {

				var nidx = this.indexOfCharSet(v, idx);
				if (nidx == -1) {
					return match;
				} else {
					match = true;
					if (nidx + charset.length < v.length()) next.add(v.charAt(nidx + charset.length));
					idx = nidx+1;
				}

			}

		}

		CCLog.addDefaultSwitchError(this , this.matchMode);
		return false;

	}

	private boolean startsWithCharSet(String v) {
		if (v.length() < this.charset.length) {
			return false;
		}

		for (int i = 0; i < this.charset.length; i++) {
			if (!StringUtils.containsIgnoreCase(charset[i], v.substring(i, i+1))) {
				return false;
			}
		}

		return true;
	}

	private int indexOfCharSet(String v, int fromIndex) {
		if (v.length() - fromIndex < this.charset.length) {
			return -1;
		}

		for (int start = fromIndex; start <= v.length() - this.charset.length; start++) {

			var broken = false;
			for (int i = 0; i < this.charset.length; i++) {
				if (!StringUtils.containsIgnoreCase(charset[i], v.substring(start + i, start + i+1))) {
					broken = true;
					break;
				}
			}

			if (!broken) return start;

		}


		return -1;
	}

	@Override
	public boolean includes(CCMovie e) {

		if (this.fieldSelector == ElemFieldMatchType.TITLE || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) {
			var str = this.cleanString(e.title().get());
			if (this.doMatch(str, new HashSet<>())) return true;
		}

		if (this.fieldSelector == ElemFieldMatchType.ZYKLUS || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) {
			var str = this.cleanString(e.zyklus().get().getTitle());
			if (this.doMatch(str, new HashSet<>())) return true;
		}

		return false;
	}

	@Override
	public boolean includes(CCSeries e) {

		if (this.fieldSelector == ElemFieldMatchType.TITLE || this.fieldSelector == ElemFieldMatchType.TITLE_AND_ZYKLUS) {
			var str = this.cleanString(e.title().get());
			if (this.doMatch(str, new HashSet<>())) return true;
		}

		return false;
	}

	public int getLength() {
		return charset.length;
	}

	@Override
	public String getName() {
		return LocaleBundle.getFormattedString("FilterTree.Custom.CustomFilterNames.Char", asString()); //$NON-NLS-1$
	}

	@Override
	public String getPrecreateName() {
		return LocaleBundle.getDeformattedString("FilterTree.Custom.CustomFilterNames.Char").replace("()", "").trim(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public String asString() {
		return CCStreams.iterate(charset).stringjoin(p->p, " -> ");
	}

	@Override
	public int getID() {
		return AbstractCustomFilter.CUSTOMFILTERID_CHAR;
	}
	
	@Override
	@SuppressWarnings("nls")
	protected void initSerialization(FilterSerializationConfig cfg) {
		cfg.addString("charset",  (d) -> this.charset = deserializeCharset(d),  () -> serializeCharset(this.charset));
		cfg.addCCEnum("matchMode", CharListMatchType.getWrapper(), (d) -> this.matchMode = d,  () -> this.matchMode);
		cfg.addCCEnum("fieldSelector", ElemFieldMatchType.getWrapper(), (d) -> this.fieldSelector = d,  () -> this.fieldSelector);
		cfg.addBool("excludeCommonWordStarts",  (d) -> this.excludeCommonWordStarts = d,  () -> this.excludeCommonWordStarts);
		cfg.addBool("ignoreNonFilterableCharacters",  (d) -> this.ignoreNonFilterableCharacters = d,  () -> this.ignoreNonFilterableCharacters);
	}

	@Override
	public AbstractCustomFilter createNew(CCMovieList ml) {
		return new CustomCharFilter(ml);
	}

	public static CustomCharFilter createSingle(CCMovieList ml, String data, CharListMatchType mm, ElemFieldMatchType fs, boolean ex, boolean ign) {
		CustomCharFilter f = new CustomCharFilter(ml);
		f.charset = Str.isNullOrEmpty(data) ? new String[]{} : new String[]{data};
		f.matchMode = mm;
		f.fieldSelector = fs;
		f.excludeCommonWordStarts = ex;
		f.ignoreNonFilterableCharacters = ign;
		return f;
	}

	public static CustomCharFilter create(CCMovieList ml, String[] data, CharListMatchType mm, ElemFieldMatchType fs, boolean ex, boolean ign) {
		CustomCharFilter f = new CustomCharFilter(ml);
		f.charset = data;
		f.matchMode = mm;
		f.fieldSelector = fs;
		f.excludeCommonWordStarts = ex;
		f.ignoreNonFilterableCharacters = ign;
		return f;
	}

	@Override
	public CustomFilterConfig[] createConfig(CCMovieList ml) {
		return new CustomFilterConfig[]
		{
			new CustomFilterStringConfig(ml,        () -> serializeCharset(charset),     a -> charset = deserializeCharset(a)),
			new CustomFilterEnumChooserConfig<>(ml, () -> matchMode,                     a -> matchMode = a, CharListMatchType.getWrapper()),
			new CustomFilterEnumChooserConfig<>(ml, () -> fieldSelector,                 a -> fieldSelector = a, ElemFieldMatchType.getWrapper()),
			new CustomFilterBoolConfig(ml,          () -> excludeCommonWordStarts,       a -> excludeCommonWordStarts = a, LocaleBundle.getString("FilterTree.Custom.ExcludeCommonWordStarts")),
			new CustomFilterBoolConfig(ml,          () -> ignoreNonFilterableCharacters, a -> ignoreNonFilterableCharacters = a, LocaleBundle.getString("FilterTree.Custom.IgnoreNonFilterableCharacters")),
		};
	}

	public CustomCharFilter appendCharset(String cs) {
		CustomCharFilter f = new CustomCharFilter(this.movielist);
		f.charset                       = CCStreams.iterate(this.charset).append(cs).toArray(new String[0]);
		f.matchMode                     = this.matchMode;
		f.fieldSelector                 = this.fieldSelector;
		f.excludeCommonWordStarts       = this.excludeCommonWordStarts;
		f.ignoreNonFilterableCharacters = this.ignoreNonFilterableCharacters;
		return f;
	}

	private static String serializeCharset(String[] v) {
		return CCStreams.iterate(v).stringjoin(p->p, "|");
	}

	private static String[] deserializeCharset(String v) {
		return v.split("\\|");
	}

	public boolean sameMetaSettings(CustomCharFilter other) {
		if (this.matchMode                     != other.matchMode)                     return false;
		if (this.fieldSelector                 != other.fieldSelector)                 return false;
		if (this.excludeCommonWordStarts       != other.excludeCommonWordStarts)       return false;
		if (this.ignoreNonFilterableCharacters != other.ignoreNonFilterableCharacters) return false;

		return true;
	}
}
