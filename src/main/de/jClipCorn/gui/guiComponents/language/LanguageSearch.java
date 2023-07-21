package de.jClipCorn.gui.guiComponents.language;

import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.impl.MetadataRunner;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.Str;

public class LanguageSearch {
	public static boolean isSearchMatch(CCDBLanguage lang, String search) {

		if (Str.isNullOrWhitespace(search)) return true;

		search = search.toLowerCase();

		if (lang.getShortString().equalsIgnoreCase(search)) return true;

		if (lang.getLongString().toLowerCase().contains(search)) return true;

		for (var loc : LocaleBundle.listLocales()) {
			if (LocaleBundle.getStringInLocale(loc, lang.getLocalNameRef()).toLowerCase().contains(search)) return true;
		}

		if (MetadataRunner.getLanguageOrNullFromIdent(search) == lang) return true;

		return false;
	}
}
