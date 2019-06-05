package de.jClipCorn.database.covertab;

import de.jClipCorn.util.datatypes.Tuple;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ICoverCache {

	void init();

	void addInternal(CCCoverData elem);
	int getNewCoverID();
	void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles);

	BufferedImage getCover(int cid);
	BufferedImage getCover(CCCoverData cce);
	int addCover(BufferedImage newCover);
	void deleteCover(int cid);
	CCCoverData getInfo(int cid);

	boolean coverFileExists(int cid);
	Tuple<Integer, Integer> getDimensions(int cid);
	boolean isCached(int cid);
	void preloadCover(int cid);

	int getCoverCount();

	List<CCCoverData> listCovers();

	String getFilepath(CCCoverData cce);
}
