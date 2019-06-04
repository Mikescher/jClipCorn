package de.jClipCorn.database.covertab;

import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func0to1WithIOException;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ICoverCache {

	void init();

	List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached();
	void addInternal(CoverCacheElement elem);
	int getNewCoverID();
	void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles);

	BufferedImage getCover(int cid);
	int addCover(BufferedImage newCover);
	void deleteCover(int cid);
	CoverCacheElement getInfo(int cid);

	boolean coverFileExists(int cid);
	Tuple<Integer, Integer> getDimensions(int cid);
	boolean isCached(int cid);
	void preloadCover(int cid);
}
