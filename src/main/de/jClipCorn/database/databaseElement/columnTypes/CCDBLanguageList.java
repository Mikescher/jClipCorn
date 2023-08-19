package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.IntParseException;
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

	public static CCDBLanguageList createDirect(List<CCDBLanguage> v) { // no copy !
		return new CCDBLanguageList(v);
	}

	public static CCDBLanguageList parseFromString(String str) throws CCFormatException {
		if (Str.Empty.equals(str)) return CCDBLanguageList.EMPTY;

		var r = new ArrayList<CCDBLanguage>();
		for (var v : str.split(";")) {
			try	{
				var l = CCDBLanguage.getWrapper().findOrException(Integer.parseInt(v));
				r.add(l);
			} catch (NumberFormatException e) {
				throw new IntParseException(v);
			}
		}
		return new CCDBLanguageList(r);
	}

	public static CCDBLanguageList parseFromLongString(String str) throws CCFormatException {
		if (Str.Empty.equals(str)) return CCDBLanguageList.EMPTY;

		var r = new ArrayList<CCDBLanguage>();
		for (var v : str.split(";")) r.add(CCDBLanguage.findByLongString(v));
		return new CCDBLanguageList(r);
	}

	public static int compare(CCDBLanguageList o1, CCDBLanguageList o2) {
		var s1 = o1.ccstream().map(p -> String.valueOf(p.asInt())).autosort().stringjoin(p->p, ";");
		var s2 = o2.ccstream().map(p -> String.valueOf(p.asInt())).autosort().stringjoin(p->p, ";");
		return s1.compareTo(s2);
	}

	public String serializeToString() {
		return ccstream().map(p -> String.valueOf(p.asInt())).stringjoin(p->p, ";");
	}

	public String serializeToLongString() {
		return ccstream().map(CCDBLanguage::getLongString).stringjoin(p->p, ";");
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

		for (int i = 0; i < _languages.size(); i++) if (!_languages.get(i).equals(that._languages.get(i))) return false;

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

	public ImageIcon getIcon(int maxCount) {
		if (isEmpty()) return Resources.ICN_TRANSPARENT.get16x16();
		if (isSingle()) return _languages.iterator().next().getIcon();

		String key = "@CCDBLanguageList::" + serializeToString() + "::" + maxCount; //$NON-NLS-1$

		ImageIcon icn = _fullIconCache.get(key);
		if (icn != null) return icn;

		var showMoreIcon = false;

		var languages = _languages;
		if (maxCount != -1 && languages.size() > maxCount)
		{
			showMoreIcon = true;

			languages = new ArrayList<>(_languages.subList(0, (int)Math.ceil(maxCount / 2.0)));
			var remaining = new ArrayList<>(_languages.subList(languages.size(), _languages.size()));

			var prim = new HashSet<>(CCStreams.iterate(CCDBLanguage.PRIMARY_LANGUAGES).toList());

			while (languages.size() < maxCount && !remaining.isEmpty()) {
				var match = false;
				for (int i = 0; i < remaining.size(); i++) {
					if (prim.contains(remaining.get(i))) {
						match = true;
						languages.add(remaining.get(i));
						remaining.remove(i);
						break;
					}
				}
				if (!match) break;
			}

			while (languages.size() < maxCount && !remaining.isEmpty()) {
				languages.add(remaining.get(0));
				remaining.remove(0);
			}
		}

		BufferedImage img = new BufferedImage(16 + (languages.size()-1 + (showMoreIcon?1:0)) * (16+2), 16, BufferedImage.TYPE_4BYTE_ABGR);

		Graphics g = img.getGraphics();
		for (int i = 0; i < languages.size(); i++)
		{
			g.drawImage(languages.get(i).getIconRef().getImage(), i * (16 + 2), 0, null);
		}
		if (showMoreIcon)
		{
			g.drawImage(Resources.ICN_TABLE_LANGUAGE_DOT3.getImage(), languages.size() * (16 + 2), 0, null);
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

	public CCDBLanguageList getAdd(CCDBLanguage v) {
		return new CCDBLanguageList(ccstream().append(v).enumerate());
	}

	public CCDBLanguageList getRemove(int idx) {
		return new CCDBLanguageList(ccstream().index().filter(p -> p.Index != idx).map(p -> p.Value).enumerate());
	}

	public CCDBLanguageList getRemoveLast(CCDBLanguage v) {
		var idx = _languages.lastIndexOf(v);
		if (idx < 0) return this;

		var llnew = new ArrayList<>(_languages);
		llnew.remove(idx);
		return new CCDBLanguageList(llnew);
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

	public String toTooltipString() {
		if (isEmpty()) return LocaleBundle.getString("CCMovieLanguageList.Empty"); //$NON-NLS-1$

		StringBuilder b = new StringBuilder();

		b.append("<html>");
		for (var lng : _languages) b.append(lng.asString()).append("<br/>");
		b.append("</html>");

		return b.toString();
	}
}
