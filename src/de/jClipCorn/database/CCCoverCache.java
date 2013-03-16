package de.jClipCorn.database;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.jClipCorn.Main;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.CachedHashMap;
import de.jClipCorn.util.ImageUtilities;
import de.jClipCorn.util.PathFormatter;

public class CCCoverCache {
	private final static String COVER_DIRECTORY = "\\cover\\"; //$NON-NLS-1$
	private final static int MAX_COVER_CREATE_TRYS = 8;

	private CachedHashMap<String, BufferedImage> cache; //TODO Precaching

	private String databasePath;
	private int biggestCoverId;

	public CCCoverCache(CCMovieList ml) {
		cache = new CachedHashMap<>(CCProperties.getInstance().PROP_DATABASE_COVERCACHESIZE.getValue());
		
		databasePath = PathFormatter.getRealSelfDirectory() + ml.getDatabasePath() + COVER_DIRECTORY;

		tryCreatePath();

		calculateBiggestCID(ml);
	}

	public BufferedImage getCover(String name) {
		return loadCover(name);
	}
	
	public BufferedImage getHalfsizeCover(String name) {
		return loadHalfsizeCover(name);
	}

	public ImageIcon getCoverIcon(String name) {
		return new ImageIcon(loadCover(name));
	}

	private BufferedImage loadCover(String name) {
		if (name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				res = ImageIO.read(new File(databasePath + name));
				cache.put(name, res);
			} catch (IOException e) {
				if (! Main.DEBUG) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", name)); //$NON-NLS-1$
				} else {
					System.out.println("[DBG] Cover not found"); //$NON-NLS-1$
				}
				return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
			}
		}

		return res;
	}
	
	private BufferedImage loadHalfsizeCover(String name) {
		if (name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = cache.get(name + "_halfsize"); //$NON-NLS-1$

		if (res == null) {
			try {
				res = ImageIO.read(new File(databasePath + name));
				res = CachedResourceLoader.resize(res, ImageUtilities.COVER_WIDTH / 2, ImageUtilities.COVER_HEIGHT / 2);
				cache.put(name + "_halfsize", res); //$NON-NLS-1$
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", databasePath + name)); //$NON-NLS-1$
				return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
			}
		}

		return res;
	}
	
	public boolean coverExists(String name) {
		return new File(databasePath + name).exists();
	}

	public void preloadCover(String name) {
		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				res = ImageIO.read(new File(databasePath + name));
				cache.put(name, res);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFoundPreload", databasePath + name)); //$NON-NLS-1$
			}
		}
	}

	private void tryCreatePath() {
		File dbpF = new File(databasePath);

		if (!dbpF.exists() && !dbpF.mkdirs()) { // Only mkdir if not exists
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorMKDIRCovers", databasePath)); //$NON-NLS-1$
		}
	}

	private void calculateBiggestCID(CCMovieList ml) {
		int max = 0;
		
		Pattern p = Pattern.compile(String.format("(?<=%s)[0-9]+(?=\\.%s)", Pattern.quote(CCProperties.getInstance().PROP_COVER_PREFIX.getValue()), Pattern.quote(CCProperties.getInstance().PROP_COVER_TYPE.getValue()))); //$NON-NLS-1$
		
		for (int i = 0; i < ml.getElementCount(); i++) {
			Matcher m = p.matcher(ml.getDatabaseElementBySort(i).getCoverName());
			if (m.find()){
				max = Math.max(max, Integer.parseInt(m.group()));
			}
			
		}

		biggestCoverId = max;
	}

	public String addCover(BufferedImage newCover) {
		return addNewCoverToFolder(newCover, MAX_COVER_CREATE_TRYS); 
	}

	private String addNewCoverToFolder(BufferedImage cov, int tryc) {
		biggestCoverId++;
		
		int id = biggestCoverId;

		String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + id + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();
		String path = databasePath + fname;

		try {
			File f = new File(path);
			if (! f.exists()) {
				ImageIO.write(cov, CCProperties.getInstance().PROP_COVER_TYPE.getValue(), f);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TryOverwriteFile", path)); //$NON-NLS-1$
			}

			return fname;
		} catch (IOException e) {
			if (tryc <= 0) {
				CCLog.addError("LogMessage.ErrorCreatingCoverFile"); //$NON-NLS-1$

				return ""; //$NON-NLS-1$
			} else {
				CCLog.addWarning("LogMessage.InfoCreatingCoverFile"); //$NON-NLS-1$

				return addNewCoverToFolder(cov, tryc - 1); // retry
			}
		}
	}

	public void deleteCover(CCDatabaseElement remMov) {
		deleteCover(remMov.getCoverName());
	}
	
	public void deleteCover(String covername) {
		File f = new File(databasePath + covername);

		if (!f.delete()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DeleteCover", covername)); //$NON-NLS-1$
		}
		
		cache.remove(covername);
	}
}
