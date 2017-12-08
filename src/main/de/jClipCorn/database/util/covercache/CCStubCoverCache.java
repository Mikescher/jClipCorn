package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.lambda.Func0to1WithIOException;

public class CCStubCoverCache extends CCCoverCache {
	@Override
	public void connect() {
		// NOP
	}

	@Override
	public BufferedImage getCover(String name) {
		return CachedResourceLoader.getImage(Resources.IMG_COVER_STANDARD);
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(String covername) {
		BufferedImage bi = getCover(covername);
		return Tuple.Create(bi.getWidth(), bi.getHeight());
	}

	@Override
	public void preloadCover(String name) {
		// NOP
	}

	@Override
	public boolean coverExists(String name) {
		return true;
	}

	@Override
	public boolean isCached(String coverName) {
		return true;
	}

	@Override
	public int getNewCoverID() {
		return 80085;
	}

	@Override
	public String addCover(BufferedImage newCover) {
		return ""; //$NON-NLS-1$
	}

	@Override
	public void addCover(String name, InputStream stream) throws Exception {
		// NOP
	}

	@Override
	public void deleteCover(String covername) {
		// NOP
	}

	@Override
	public List<Tuple<String, Func0to1WithIOException<BufferedImage>>> listCoversNonCached() {
		return new ArrayList<>();
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		// do nothing
	}
}
