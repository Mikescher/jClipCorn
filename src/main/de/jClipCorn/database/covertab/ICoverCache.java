package de.jClipCorn.database.covertab;

import de.jClipCorn.util.datatypes.CCUUID;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ICoverCache {

	void init();

	void addInternal(CCCoverData elem);
	void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles);

	BufferedImage getCover(CCUUID cid);
	BufferedImage getCover(CCCoverData cce);
	/** empty when the cover could not be written - the error is already logged */
	Opt<CCUUID> addCover(BufferedImage newCover);
	void deleteCover(CCUUID cid);
	CCCoverData getInfoOrNull(CCUUID cid);

	boolean coverFileExists(CCUUID cid);
	Tuple<Integer, Integer> getDimensions(CCUUID cid);
	boolean isCached(CCUUID cid);
	void preloadCover(CCUUID cid);

	int getCoverCount();

	List<CCCoverData> listCovers();

	FSPath getFilepath(CCCoverData cce);
}
