package de.jClipCorn.features.online.metadata.anilist;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.online.OnlineSearchType;
import de.jClipCorn.features.online.metadata.Metadataparser;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.properties.enumerations.AniListTitleLang;
import de.jClipCorn.properties.enumerations.MetadataParserImplementation;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.CollectionHelper;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.http.HTTPUtilities;
import de.jClipCorn.util.stream.CCStreams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("nls")
public class AniListParser extends Metadataparser {

	//
	// https://github.com/AniList/ApiV2-GraphQL-Docs
	// https://anilist.gitbook.io/anilist-apiv2-docs
	// https://anilist.github.io/ApiV2-GraphQL-Docs/
	// https://anilist.co/graphiql
	//

	private final static String GRAPHQL_URL = "https://graphql.anilist.co"; //$NON-NLS-1$

	private String gql_select     = Str.Empty;
	private String gql_search_tv  = Str.Empty;
	private String gql_search_mov = Str.Empty;
	private String gql_search_any = Str.Empty;

	public AniListParser(CCMovieList ml) {
		super(ml);
		try {
			gql_select     = SimpleFileUtils.readTextResource("/scripts/graphql/anilist_select.graphql",     this.getClass());
			gql_search_tv  = SimpleFileUtils.readTextResource("/scripts/graphql/anilist_search_tv.graphql",  this.getClass());
			gql_search_mov = SimpleFileUtils.readTextResource("/scripts/graphql/anilist_search_mov.graphql", this.getClass());
			gql_search_any = SimpleFileUtils.readTextResource("/scripts/graphql/anilist_search_any.graphql", this.getClass());
		} catch (IOException e) {
			CCLog.addError(e);
		}
	}

	@Override
	public List<Tuple<String, CCSingleOnlineReference>> searchByText(String text, OnlineSearchType type) {

		String cmd = "";
		switch (type) {

			case MOVIES:
				cmd = gql_search_mov;
				break;
			case SERIES:
				cmd = gql_search_tv;
				break;
			case BOTH:
				cmd = gql_search_any;
				break;
			default:
				CCLog.addDefaultSwitchError(this, type);
				break;
		}

		JSONObject result = HTTPUtilities.getGraphQL(GRAPHQL_URL, cmd, CollectionHelper.createHashMap("stxt", text));
		if (result == null) return null;

		List<Tuple<String, CCSingleOnlineReference>> rdat = new ArrayList<>();

		JSONArray med = result.getJSONObject("data").getJSONObject("Page").getJSONArray("media");

		for (int i = 0; i < med.length(); i++) {
			rdat.add(Tuple.Create(getTitle(med.getJSONObject(i)), CCSingleOnlineReference.createAniList(med.getJSONObject(i).getInt("id"))));
		}

		return rdat;
	}

	@Override
	public MetadataParserImplementation getImplType() {
		return MetadataParserImplementation.ANILIST;
	}

	@Override
	public OnlineMetadata getMetadata(CCSingleOnlineReference ref, boolean downloadCover) {

		JSONObject result = HTTPUtilities.getGraphQL(GRAPHQL_URL, gql_select, CollectionHelper.createHashMap("id", ref.id));
		if (result == null) return null;

		JSONObject med = result.getJSONObject("data").getJSONObject("Media");

		OnlineMetadata om = new OnlineMetadata(ref);

		if (med.has("idMal")) om.AltRef = CCSingleOnlineReference.createMyAnimeList(med.getInt("idMal"));

		om.Title       = getTitle(med);
		om.CoverURL    = med.getJSONObject("coverImage").getString("extraLarge");
		if (!med.isNull("averageScore")) om.OnlineScore = (int)Math.round(med.getInt("averageScore")/10.0);
		om.Length      = med.getInt("duration");
		om.Year        = med.getJSONObject("startDate").getInt("year");
		om.Genres      = new CCGenreList(CCStreams.iterate(med.getJSONArray("genres")).<String>cast().map(CCGenre::parseFromAniList).flatten(CCStreams::iterate).unique().enumerate());

		if (downloadCover && om.CoverURL != null) om.Cover = HTTPUtilities.getImage(om.CoverURL);

		return om;
	}

	private String getTitle(JSONObject media) {
		AniListTitleLang v = movielist.ccprops().PROP_ANILIST_PREFERRED_TITLE_LANG.getValue();

		switch (v) {
			case NATIVE:    return media.getJSONObject("title").getString("native");
			case ENGLISH:   return media.getJSONObject("title").getString("english");
			case PREFERRED: return media.getJSONObject("title").getString("userPreferred");

			default: CCLog.addDefaultSwitchError(this, v); return Str.Empty;
		}
	}
}
