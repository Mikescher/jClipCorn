package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.stream.CCIterable;
import de.jClipCorn.util.stream.CCStream;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class CCDBLanguageList implements CCIterable<CCDBLanguage> {
	public static final CCDBLanguageList EMPTY = new CCDBLanguageList();

	private static final HashMap<String, ImageIcon> _fullIconCache = new HashMap<>();

	private final List<CCDBLanguage> _languages;

	private CCDBLanguageList(CCDBLanguage... langs) {
		_languages = Arrays.asList(langs);
	}

	private CCDBLanguageList(CCStream<CCDBLanguage> langs) {
		_languages = langs.enumerate();
	}

	private CCDBLanguageList(List<CCDBLanguage> direct) {
		if (direct == null) throw new NullPointerException();
		_languages = direct;
	}

	public static CCDBLanguageList single(CCDBLanguage lang) {
		return new CCDBLanguageList(lang);
	}

	public static CCDBLanguageList create(CCDBLanguage... langs) {
		return new CCDBLanguageList(langs);
	}

	public static CCDBLanguageList parseFromString(String str) throws CCFormatException {
		if (Str.Empty.equals(str)) return CCDBLanguageList.EMPTY;

		var r = new ArrayList<CCDBLanguage>();
		for (var v : str.split(";")) {
			var l = CCDBLanguage.findByShortString(v);
			r.add(l);
		}
		return new CCDBLanguageList(r);
	}

	public String serializeToString() {
		return ccstream().map(CCDBLanguage::getShortString).stringjoin(p->p, ";");
	}

	public boolean isExact(CCDBLanguage lang) {
		return _languages.size()==1 && _languages.contains(lang);
	}

	public boolean isSingle() {
		return _languages.size()==1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CCDBLanguageList that = (CCDBLanguageList) o;

		return isEqual(that);
	}

	public boolean isEqual(CCDBLanguageList that) {
		if (_languages.size() != that._languages.size()) return false;

		for (int i = 0; i < _languages.size(); i++) if (_languages.get(i).equals(that._languages.get(i))) return false;

		return true;
	}

	public static boolean equals(CCDBLanguageList a, CCDBLanguageList b) {
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

	public ImageIcon getIcon() {
		if (isEmpty()) return Resources.ICN_TABLE_LANGUAGE_NONE.get();
		if (isSingle()) return _languages.iterator().next().getIcon();

		List<CCDBLanguage> langs = CCStreams.iterate(_languages).autosort().enumerate();

		String key = serializeToString(); //$NON-NLS-1$

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

	public int size() {
		return _languages.size();
	}

	@Override
	public String toString() {
		return "{{" + CCStreams.iterate(_languages).stringjoin(CCDBLanguage::getShortString, "|") + "}}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
