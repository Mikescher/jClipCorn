package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.util.*;

public class CCDBLanguageList implements Iterable<CCDBLanguage> {
	public static final CCDBLanguageList GERMAN = new CCDBLanguageList(CCDBLanguage.GERMAN);
	public static final CCDBLanguageList ENGLISH = new CCDBLanguageList(CCDBLanguage.ENGLISH);
	public static final CCDBLanguageList JAPANESE = new CCDBLanguageList(CCDBLanguage.JAPANESE);
	public static final CCDBLanguageList EMPTY = new CCDBLanguageList();

	private final Set<CCDBLanguage> _languages;

	public CCDBLanguageList(CCDBLanguage... langs) {
		_languages = new HashSet<>(Arrays.asList(langs));
	}

	public CCDBLanguageList(CCStream<CCDBLanguage> langs) {
		_languages = new HashSet<>(langs.enumerate());
	}

	private CCDBLanguageList(Set<CCDBLanguage> direct) {
		_languages = direct;
	}

	public static CCDBLanguageList parseFromString(String str) {
		return new CCDBLanguageList(CCStreams.iterate(str.split(";")).map(CCDBLanguage::findByLongString)); //$NON-NLS-1$
	}

	public static CCDBLanguageList fromBitmask(long v) {
		HashSet<CCDBLanguage> lang = new HashSet<>();
		for (CCDBLanguage lng : CCDBLanguage.values()) {
			if ((v & lng.asBitMask()) == lng.asBitMask()) lang.add(lng);
		}
		return new CCDBLanguageList(lang);
	}

	public static CCDBLanguageList createDirect(Set<CCDBLanguage> v) { // no copy !
		return new CCDBLanguageList(v);
	}

	public long serializeToLong() {
		long v = 0x0;

		for (CCDBLanguage lng : _languages) v = v | lng.asBitMask();

		return v;
	}

	public String serializeToString() {
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::getLongString, ";"); //$NON-NLS-1$
	}

	public String toOutputString() {
		if (isEmpty()) return LocaleBundle.getString("CCMovieLanguageList.Empty"); //$NON-NLS-1$
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::asString, ", "); //$NON-NLS-1$
	}

	public boolean isExact(CCDBLanguage lang) {
		return _languages.size()==1 && _languages.contains(lang);
	}

	public boolean isSingle() {
		return _languages.size()==1;
	}

	public String serializeToFilenameString() {
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::getShortString, "+"); //$NON-NLS-1$
	}

	public static int compare(CCDBLanguageList o1, CCDBLanguageList o2) {
		return Long.compare(o1.serializeToLong(), o2.serializeToLong());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CCDBLanguageList that = (CCDBLanguageList) o;

		return Objects.equals(_languages, that._languages);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_languages);
	}

	public Collection<CCDBLanguage> getInternalData() {
		return _languages;
	}

	public CCStream<CCDBLanguage> iterate() {
		return CCStreams.iterate(_languages);
	}

	public boolean isEmpty() {
		return _languages.isEmpty();
	}

	@Override
	public Iterator<CCDBLanguage> iterator() {
		return iterate();
	}

	public boolean contains(CCDBLanguage ignored) {
		return _languages.contains(ignored);
	}

	public ImageIcon getIcon() {
		if (isEmpty()) return Resources.ICN_TABLE_LANGUAGE_NONE.get();
		if (isSingle()) return _languages.iterator().next().getIcon();
		if (_languages.size() == 2)
		{
			List<CCDBLanguage> langs = CCStreams.iterate(_languages).enumerate();

			return Resources.ICN_TABLE_LANGUAGE[langs.get(0).asInt()][langs.get(1).asInt()].get();
		}

		return Resources.ICN_TABLE_LANGUAGE_MORE.get();
	}

	public CCDBLanguageList getRemove(CCDBLanguage lang) {
		HashSet<CCDBLanguage> v = new HashSet<>(_languages);
		v.remove(lang);
		return new CCDBLanguageList(v);
	}

	public boolean isSubsetOf(CCDBLanguageList other) {
		for (CCDBLanguage lang : _languages) {
			if (!other.contains(lang)) return false;
		}
		return true;
	}

	public static CCDBLanguageList randomValue(Random r)
	{
		HashSet<CCDBLanguage> v = new HashSet<>();
		for (CCDBLanguage dbl : CCDBLanguage.values())
		{
		    if (r.nextBoolean()) v.add(dbl);
		}
		return new CCDBLanguageList(v);
	}
}
