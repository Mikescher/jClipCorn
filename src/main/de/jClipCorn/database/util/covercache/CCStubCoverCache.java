package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Tuple;

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
	public BufferedImage getHalfsizeCover(String name) {
		return CachedResourceLoader.getImage(Resources.IMG_COVER_STANDARD_SMALL);
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
	public List<Tuple<String, BufferedImage>> listCoversNonCached() throws IOException {
		return new ArrayList<>();
	}

}
