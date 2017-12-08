package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func0to1WithIOException;

public abstract class CCCoverCache {
	public abstract void connect();
	
	public abstract BufferedImage getCover(String name);
	public abstract void preloadCover(String name);
	public abstract Tuple<Integer, Integer> getDimensions(String covername);
	public abstract boolean isCached(String coverName);

	public abstract boolean coverExists(String name);

	public abstract int getNewCoverID();

	public abstract String addCover(BufferedImage newCover);
	public abstract void addCover(String name, InputStream stream) throws Exception;
	
	public abstract void deleteCover(String covername);

	public abstract List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached();
	
	public void deleteCover(CCDatabaseElement remMov) {
		deleteCover(remMov.getCoverName());
	}

	public abstract void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles);


}
