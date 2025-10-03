package de.jClipCorn.database.covertab;

import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ICoverCache {

	void init();

	void addInternal(CCCoverData elem);
	void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles);

	BufferedImage getCover(int cid);
	BufferedImage getCover(CCCoverData cce);
	int addCover(BufferedImage newCover);
	void deleteCover(int cid);
	CCCoverData getInfoOrNull(int cid);

	boolean coverFileExists(int cid);
	Tuple<Integer, Integer> getDimensions(int cid);
	boolean isCached(int cid);
	void preloadCover(int cid);

	int getCoverCount();

	List<CCCoverData> listCovers();

	FSPath getFilepath(CCCoverData cce);
}
