package de.jClipCorn.online.cover.imdb;

import de.jClipCorn.util.listener.FinishListener;
import de.jClipCorn.util.listener.ProgressCallbackListener;
import de.jClipCorn.util.listener.UpdateCallbackListener;

public class IMDBSecondaryCoverSearch extends IMDBCoverSearchCommon {

	public IMDBSecondaryCoverSearch(FinishListener fl, UpdateCallbackListener uc, ProgressCallbackListener pc) {
		super(fl, uc, pc);
	}

	@Override
	protected String getSearchResultFromHTML(String searchhtml) {
		return helper.getSecondSearchResult(searchhtml);
	}
}
