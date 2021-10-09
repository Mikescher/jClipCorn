package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.gui.resources.reftypes.IconRef;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class CCDBLanguageSet implements CCIterable<CCDBLanguage> {
	public static final CCDBLanguageSet GERMAN   = new CCDBLanguageSet(CCDBLanguage.GERMAN);
	public static final CCDBLanguageSet ENGLISH  = new CCDBLanguageSet(CCDBLanguage.ENGLISH);
	public static final CCDBLanguageSet JAPANESE = new CCDBLanguageSet(CCDBLanguage.JAPANESE);
	public static final CCDBLanguageSet EMPTY    = new CCDBLanguageSet();

	private static final HashMap<String, ImageIcon> _fullIconCache = new HashMap<>();

	private final Set<CCDBLanguage> _languages;

	private CCDBLanguageSet(CCDBLanguage... langs) {
		_languages = new HashSet<>(Arrays.asList(langs));
	}

	private CCDBLanguageSet(CCStream<CCDBLanguage> langs) {
		_languages = new HashSet<>(langs.enumerate());
	}

	private CCDBLanguageSet(Set<CCDBLanguage> direct) {
		if (direct == null) throw new NullPointerException();
		_languages = direct;
	}

	public static CCDBLanguageSet parseFromString(String str) throws CCFormatException {
		if (Str.Empty.equals(str)) return CCDBLanguageSet.EMPTY;

		var r = new HashSet<CCDBLanguage>();
		for (var v : str.split(";")) r.add(CCDBLanguage.findByLongString(v));
		return new CCDBLanguageSet(r);
	}

	public static CCDBLanguageSet fromBitmask(long v) {
		HashSet<CCDBLanguage> lang = new HashSet<>();
		for (CCDBLanguage lng : CCDBLanguage.values()) {
			if ((v & lng.asBitMask()) == lng.asBitMask()) lang.add(lng);
		}
		return new CCDBLanguageSet(lang);
	}

	public static CCDBLanguageSet createDirect(Set<CCDBLanguage> v) { // no copy !
		return new CCDBLanguageSet(v);
	}

	public static CCDBLanguageSet single(CCDBLanguage lang) {
		return new CCDBLanguageSet(lang);
	}

	public static CCDBLanguageSet create(CCDBLanguage... langs) {
		return new CCDBLanguageSet(langs);
	}

	public static CCDBLanguageSet create(Collection<CCDBLanguage> allLanguages) {
		return new CCDBLanguageSet(new HashSet<>(allLanguages));
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

	public String toOutputString(String emptyStr) {
		if (isEmpty()) return emptyStr;
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::asString, ", "); //$NON-NLS-1$
	}

	public String toShortOutputString(String emptyStr) {
		if (isEmpty()) return emptyStr;
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::getShortString, ","); //$NON-NLS-1$
	}

	public boolean isExact(CCDBLanguage lang) {
		return _languages.size()==1 && _languages.contains(lang);
	}

	public boolean isExact(int... langs) {
		if (_languages.size() != langs.length) return false;
		for (int lid : langs)
		{
			boolean found = false;
			for (CCDBLanguage lng : _languages)
			{
				if (lng.asInt() == lid) {found=true; break;}
			}
			if (!found) return false;
		}
		return true;
	}

	public boolean isSingle() {
		return _languages.size()==1;
	}

	public String serializeToFilenameString() {
		return CCStreams.iterate(_languages).autosort().stringjoin(CCDBLanguage::getShortString, "+"); //$NON-NLS-1$
	}

	public static int compare(CCDBLanguageSet o1, CCDBLanguageSet o2) {
		return Long.compare(o1.serializeToLong(), o2.serializeToLong());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CCDBLanguageSet that = (CCDBLanguageSet) o;

		return Objects.equals(_languages, that._languages);
	}

	public boolean isEqual(CCDBLanguageSet that) {
		if (_languages.size() != that._languages.size()) return false;

		return _languages.containsAll(that._languages);
	}

	public static boolean equals(CCDBLanguageSet a, CCDBLanguageSet b) {
		if (a != null) return a.isEqual(b);
		return b == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_languages);
	}

	public Collection<CCDBLanguage> getInternalData() {
		return _languages;
	}

	public boolean isEmpty() {
		return _languages.isEmpty();
	}

	@Override
	public Iterator<CCDBLanguage> iterator() {
		return ccstream();
	}

	public CCStream<CCDBLanguage> ccstream() {
		return CCStreams.iterate(_languages);
	}

	public boolean contains(CCDBLanguage ignored) {
		return _languages.contains(ignored);
	}

	public IconRef getIconRef() {
		if (isEmpty()) return Resources.ICN_TABLE_LANGUAGE_NONE;
		if (isSingle()) return _languages.iterator().next().getIconRef();

		if (isExact(0, 1, 4)) return Resources.ICN_TABLE_LANGUAGE_SPECIAL_00_01_04;
		if (isExact(0, 1, 6)) return Resources.ICN_TABLE_LANGUAGE_SPECIAL_00_01_06;

		if (_languages.size() == 2)
		{
			List<CCDBLanguage> langs = CCStreams.iterate(_languages).enumerate();

			return Resources.ICN_TABLE_LANGUAGE_COMBINED.get(Tuple.Create(langs.get(0).asInt(), langs.get(1).asInt()));
		}

		return Resources.ICN_TABLE_LANGUAGE_MORE;
	}

	public ImageIcon getIcon() {
		return getIconRef().get();
	}

	public ImageIcon getFullIcon() {
		if (isEmpty()) return Resources.ICN_TABLE_LANGUAGE_NONE.get();
		if (isSingle()) return _languages.iterator().next().getIcon();

		List<CCDBLanguage> langs = CCStreams.iterate(_languages).autosort().enumerate();

		String key = CCStreams.iterate(langs).stringjoin(CCDBLanguage::getShortString, "|"); //$NON-NLS-1$

		ImageIcon icn = _fullIconCache.get(key);
		if (icn != null) return icn;

		BufferedImage img = new BufferedImage(16 + (langs.size()-1) * (16+2), 16, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = img.getGraphics();
		for (int i = 0; i < langs.size(); i++)
		{
			g.drawImage(langs.get(i).getIconRef().getImage(), i * (16 + 2), 0, null);
		}

		icn = new ImageIcon(img);

		_fullIconCache.put(key, icn);
		return icn;
	}

	public CCDBLanguageSet getRemove(CCDBLanguage lang) {
		HashSet<CCDBLanguage> v = new HashSet<>(_languages);
		v.remove(lang);
		return new CCDBLanguageSet(v);
	}

	public CCDBLanguageSet getAdd(CCDBLanguage lang) {
		HashSet<CCDBLanguage> v = new HashSet<>(_languages);
		v.add(lang);
		return new CCDBLanguageSet(v);
	}

	public boolean isSubsetOf(CCDBLanguageSet other) {
		for (CCDBLanguage lang : _languages) {
			if (!other.contains(lang)) return false;
		}
		return true;
	}

	public List<CCDBLanguageSet> allSubsets()
	{
		if (isEmpty()) return Collections.singletonList(CCDBLanguageSet.EMPTY);
		var count = Math.pow(2, _languages.size());

		var result = new ArrayList<CCDBLanguageSet>();
		for (int i=0; i<count; i++)
		{
			final int mask = i;
			var iter = this
					.ccstream()
					.index()
					.filter(p -> ((1 << p.Index) & mask) != 0)
					.map(p -> p.Value)
					.toList();

			result.add(CCDBLanguageSet.create(iter));
		}
		return result;
	}

	public int size() {
		return _languages.size();
	}

	public static CCDBLanguageSet randomValue(Random r)
	{
		HashSet<CCDBLanguage> v = new HashSet<>();
		for (CCDBLanguage dbl : CCDBLanguage.values())
		{
		    if (r.nextBoolean()) v.add(dbl);
		}
		return new CCDBLanguageSet(v);
	}

	public static CCDBLanguageSet intersection(CCDBLanguageSet a, CCDBLanguageSet b)
	{
		HashSet<CCDBLanguage> v = new HashSet<>(a._languages);
		v.retainAll(b._languages);
		return CCDBLanguageSet.createDirect(v);
	}

	public static CCDBLanguageSet union(CCDBLanguageSet a, CCDBLanguageSet b) {
		HashSet<CCDBLanguage> v = new HashSet<>(a._languages);
		v.addAll(b._languages);
		return CCDBLanguageSet.createDirect(v);
	}

	public static CCDBLanguageSet union(CCStream<CCDBLanguageSet> data) {
		HashSet<CCDBLanguage> v = new HashSet<>();
		for (CCDBLanguageSet ls : data) v.addAll(ls._languages);
		return CCDBLanguageSet.createDirect(v);
	}

	@Override
	public String toString() {
		return "{{" + CCStreams.iterate(_languages).stringjoin(CCDBLanguage::getShortString, "|") + "}}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
