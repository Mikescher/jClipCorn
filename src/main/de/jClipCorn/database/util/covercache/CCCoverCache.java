package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.ImageIcon;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.util.Tuple;

public abstract class CCCoverCache {
	public abstract void connect();
	
	public abstract BufferedImage getCover(String name);
	public abstract BufferedImage getHalfsizeCover(String name);
	public abstract void preloadCover(String name);

	public abstract boolean coverExists(String name);

	public abstract int getNewCoverID();

	public abstract String addCover(BufferedImage newCover);
	public abstract void addCover(String name, InputStream stream) throws Exception;
	
	public abstract void deleteCover(String covername);

	public abstract List<Tuple<String, BufferedImage>> listCoversNonCached() throws IOException;
	
	public void deleteCover(CCDatabaseElement remMov) {
		deleteCover(remMov.getCoverName());
	}

	public ImageIcon getCoverIcon(String name) {
		return new ImageIcon(getCover(name));
	}
}
