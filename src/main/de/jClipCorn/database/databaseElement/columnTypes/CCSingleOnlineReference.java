package de.jClipCorn.database.databaseElement.columnTypes;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.util.CCOnlineRefTypeHelper;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.anilist.AniListParser;
import de.jClipCorn.features.online.metadata.imdb.IMDBParserCommon;
import de.jClipCorn.features.online.metadata.mal.MALParser;
import de.jClipCorn.features.online.metadata.tmdb.TMDBParser;
import de.jClipCorn.gui.resources.MultiSizeIconRef;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.http.HTTPUtilities;

import javax.swing.*;

public class CCSingleOnlineReference {
	public final static CCSingleOnlineReference EMPTY = new CCSingleOnlineReference(CCOnlineRefType.NONE, Str.Empty, Str.Empty);

	public final CCOnlineRefType type;
	public final String id;
	public final String description;
	
	public CCSingleOnlineReference(CCOnlineRefType type, String id, String desc) {
		this.type        = type;
		this.id          = id;
		this.description = desc;
	}

	public static CCSingleOnlineReference createIMDB(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.IMDB, id, Str.Empty);
	}

	public static CCSingleOnlineReference createAmazon(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.AMAZON, id, Str.Empty);
	}

	public static CCSingleOnlineReference createMoviepilot(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.MOVIEPILOT, id, Str.Empty);
	}

	public static CCSingleOnlineReference createTMDB(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.THEMOVIEDB, id, Str.Empty);
	}

	public static CCSingleOnlineReference createProxer(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.PROXERME, id, Str.Empty);
	}

	public static CCSingleOnlineReference createMyAnimeList(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.MYANIMELIST, id, Str.Empty);
	}

	public static CCSingleOnlineReference createMyAnimeList(int id) {
		return new CCSingleOnlineReference(CCOnlineRefType.MYANIMELIST, String.valueOf(id), Str.Empty);
	}

	public static CCSingleOnlineReference createAniList(String id) {
		return new CCSingleOnlineReference(CCOnlineRefType.ANILIST, id, Str.Empty);
	}

	public static CCSingleOnlineReference createAniList(int id) {
		return new CCSingleOnlineReference(CCOnlineRefType.ANILIST, String.valueOf(id), Str.Empty);
	}

	public String toSerializationString() {
		if (type == CCOnlineRefType.NONE) return ""; //$NON-NLS-1$
		if (!hasDescription()) return type.getIdentifier() + ":" + id; //$NON-NLS-1$
		return type.getIdentifier() + ":" + id + ":" + Str.toBase64(description); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static CCSingleOnlineReference parse(String data) throws OnlineRefFormatException {
		if (data.isEmpty()) return EMPTY;

		int idx1 = data.indexOf(':');
		int idx2 = data.lastIndexOf(':');

		if (idx1 == idx2) {

			String strtype = data.substring(0, idx1);
			String strid = data.substring(idx1+1);
			
			CCOnlineRefType type = CCOnlineRefType.parse(strtype);
			
			return new CCSingleOnlineReference(type, strid, Str.Empty);
			
		} else {

			String strtype = data.substring(0, idx1);
			String strid   = data.substring(idx1+1, idx2);
			String strdesc = data.substring(idx2+1);
			
			CCOnlineRefType type = CCOnlineRefType.parse(strtype);
			
			return new CCSingleOnlineReference(type, strid, Str.fromBase64(strdesc));
		}
	}

	public boolean isUnset() {
		return !isSet();
	}

	public boolean isSet() {
		return type != CCOnlineRefType.NONE & !id.trim().isEmpty();
	}

	public String getURL() {
		return CCOnlineRefTypeHelper.getURL(this);
	}

	public Icon getIcon() {
		return type.getIcon();
	}

	public MultiSizeIconRef getIconRef() {
		return type.getIconRef();
	}

	public Icon getIcon16x16() {
		return type.getIcon16x16();
	}

	public Icon getIconButton() {
		return type.getIconButton();
	}

	public boolean isInvalid() {
		return !isValid();
	}

	public boolean isValid() {
		return type.isValid(id);
	}

	@Override
	public int hashCode() {
		return id.hashCode() ^ 13 * type.asInt() ^ 17 * description.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CCSingleOnlineReference))
			return false;
		if (obj == this)
			return true;

		return equals((CCSingleOnlineReference)obj);
	}

	public boolean equals(CCSingleOnlineReference obj) {
		return obj.id.equals(id) && obj.type == type && Str.equals(obj.description, description);
	}

	public Metadataparser getMetadataParser() {
		switch (type) {
		case NONE:
			return null;
		case IMDB:
			return IMDBParserCommon.GetConfiguredParser();
		case AMAZON:
			return null;
		case MOVIEPILOT:
			return null;
		case THEMOVIEDB:
			return new TMDBParser();
		case PROXERME:
			return null;
		case MYANIMELIST:
			return new MALParser();
		case ANILIST:
			return new AniListParser();
		default:
			CCLog.addDefaultSwitchError(this, this);
			return null;
		}
	}

	public boolean hasDescription() {
		return !Str.isNullOrWhitespace(description);
	}
	
	public void openInBrowser(CCDatabaseElement src) {
		if (isUnset()) {
			if (src.isMovie()) {
				HTTPUtilities.searchInBrowser(((CCMovie)src).getCompleteTitle());
			} else if (src.isSeries()) {
				HTTPUtilities.searchInBrowser(((CCSeries)src).Title.get());
			}
		} else {
			HTTPUtilities.openInBrowser(getURL());
		}
	}
}
