package de.jClipCorn.features.online.cover.imdb;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class IMDBSecondaryCoverSearch extends IMDBCoverSearchCommon {

	public IMDBSecondaryCoverSearch(CCMovieList ml, FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(ml, fl, uc, pc);
	}

	@Override
	protected String getSearchResultFromHTML(String searchhtml) {
		return helper.getSecondSearchResult(searchhtml);
	}
}
