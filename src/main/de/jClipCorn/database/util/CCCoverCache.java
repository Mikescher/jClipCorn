package de.jClipCorn.database.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.gui.CachedResourceLoader;
import de.jClipCorn.gui.Resources;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.CachedHashMap;
import de.jClipCorn.util.formatter.PathFormatter;
import de.jClipCorn.util.helper.ImageUtilities;

public class CCCoverCache {
	private final static String COVER_DIRECTORY = PathFormatter.appendAndPrependSeparator("cover"); //$NON-NLS-1$

	private Map<String, BufferedImage> cache; //TODO Precaching

	private String coverPath;
	
	private Vector<Integer> usedCoverIDs;

	private final boolean isInMemory;
	
	public CCCoverCache(CCMovieList ml) {
		cache = new CachedHashMap<>(CCProperties.getInstance().PROP_DATABASE_COVERCACHESIZE.getValue());
		
		coverPath = PathFormatter.getRealSelfDirectory() + ml.getDatabasePath() + COVER_DIRECTORY;

		isInMemory = ml.isInMemory();
		
		tryCreatePath();

		calculateBiggestCID();
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
		if ((name == null) || name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				res = ImageIO.read(new File(coverPath + name));
				if (res != null) {
					cache.put(name, res);
				} else {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", name)); //$NON-NLS-1$
					return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
				}
			} catch (IOException e) {
				if (! Main.DEBUG) {
					CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", name)); //$NON-NLS-1$
				} else {
					System.out.println(String.format("[DBG] Cover not found (%s)", name)); //$NON-NLS-1$
				}
				return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
			}
		}

		return res;
	}
	
	private BufferedImage loadHalfsizeCover(String name) {
		if ((name == null) || name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = cache.get(name + "_halfsize"); //$NON-NLS-1$

		if (res == null) {
			try {
				res = ImageIO.read(new File(coverPath + name));
				res = CachedResourceLoader.resize(res, ImageUtilities.COVER_WIDTH / 2, ImageUtilities.COVER_HEIGHT / 2);
				cache.put(name + "_halfsize", res); //$NON-NLS-1$
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFound", coverPath + name)); //$NON-NLS-1$
				return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
			}
		}

		return res;
	}
	
	public boolean coverExists(String name) {
		if (isInMemory) return cache.containsKey(name);
		
		return new File(coverPath + name).exists();
	}

	public void preloadCover(String name) {
		if (isInMemory) return;
		
		BufferedImage res = cache.get(name);

		if (res == null) {
			try {
				res = ImageIO.read(new File(coverPath + name));
				cache.put(name, res);
			} catch (IOException e) {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverNotFoundPreload", coverPath + name)); //$NON-NLS-1$
			}
		}
	}

	private void tryCreatePath() {
		if (isInMemory) return;
		
		File dbpF = new File(coverPath);

		if (!dbpF.exists() && !dbpF.mkdirs()) { // Only mkdir if not exists
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErrorMKDIRCovers", coverPath)); //$NON-NLS-1$
		}
	}

	private void calculateBiggestCID() {
		if (isInMemory) return;
		
		usedCoverIDs = new Vector<>();
		
		String[] files = getCoverDirectory().list();
		
		Pattern pattern = Pattern.compile(String.format("(?<=%s)[0-9]+(?=\\.%s)", Pattern.quote(CCProperties.getInstance().PROP_COVER_PREFIX.getValue().toLowerCase()), Pattern.quote(CCProperties.getInstance().PROP_COVER_TYPE.getValue().toLowerCase()))); //$NON-NLS-1$
		
		for (String f : files) {
			Matcher matcher = pattern.matcher(PathFormatter.getFilenameWithExt(f).toLowerCase());
			if (matcher.find()){
				usedCoverIDs.add(Integer.parseInt(matcher.group()));
			}
		}
		
		Collections.sort(usedCoverIDs);
	}
	
	public int getNewCoverID() {
		if (isInMemory) return cache.size() + 1;
		
		int i = 0;
		while(usedCoverIDs.size() > i && usedCoverIDs.get(i).equals(i)) {
			i++;
		}
		usedCoverIDs.add(i, i);
		
		return i;
	}

	public String addCover(BufferedImage newCover) {
		return addNewCoverToFolder(newCover); 
	}

	private String addNewCoverToFolder(BufferedImage cov) {
		int id = getNewCoverID();
		
		if (Main.DEBUG) {
			System.out.println("addingCoverToFolder: " + id); //$NON-NLS-1$
		}

		String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + id + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();
		String path = coverPath + fname;

		if (isInMemory) {
			cache.put(fname, cov);
			return fname;
		}
		
		try {
			File f = new File(path);
			if (! f.exists()) {
				ImageIO.write(cov, CCProperties.getInstance().PROP_COVER_TYPE.getValue(), f);
			} else {
				CCLog.addError(LocaleBundle.getFormattedString("LogMessage.TryOverwriteFile", path)); //$NON-NLS-1$
			}

			return fname;
		} catch (IOException e) {
			CCLog.addError("LogMessage.ErrorCreatingCoverFile"); //$NON-NLS-1$

			return ""; //$NON-NLS-1$
		}
	}

	public void deleteCover(CCDatabaseElement remMov) {
		deleteCover(remMov.getCoverName());
	}
	
	public void deleteCover(String covername) {
		cache.remove(covername);
		
		if (isInMemory) return;
		
		File f = new File(coverPath + covername);

		if (!f.delete()) {
			CCLog.addWarning(LocaleBundle.getFormattedString("LogMessage.DeleteCover", covername)); //$NON-NLS-1$
		}
		
		if (Main.DEBUG) {
			System.out.println("removing Cover from Folder: " + covername); //$NON-NLS-1$
		}
	}
	
	public String getCoverPath() {
		return coverPath;
	}

	public File getCoverDirectory() {
		return new File(getCoverPath());
	}

	public void addCover(String name, InputStream stream) throws Exception {
		if (isInMemory) {
			BufferedImage bi = ImageIO.read(stream);
			cache.put(name, bi);
		} else {
			byte[] buffer = new byte[2048];
			
			String outpath = getCoverPath() + name;
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(outpath);
				int len = 0;
				while ((len = stream.read(buffer)) > 0) {
					output.write(buffer, 0, len);
				}
			} finally {
				if (output != null) {
					output.close();
				}
			}
		}
		
	}
}
