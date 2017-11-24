package de.jClipCorn.database.util.covercache;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.gui.resources.CachedResourceLoader;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.datatypes.Tuple;

public class CCMemoryCoverCache extends CCCoverCache {
	private Map<String, BufferedImage> data;
	
	private int nextCoverID;
	
	@Override
	public void connect() {
		data = new HashMap<>();
	}

	@Override
	public BufferedImage getCover(String name) {
		if ((name == null) || name.isEmpty()) {
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		BufferedImage res = data.get(name);

		if (res == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.CoverFileBroken", name)); //$NON-NLS-1$
			return CachedResourceLoader.getImage(Resources.IMG_COVER_NOTFOUND);
		}

		return res;
	}

	@Override
	public void preloadCover(String name) {
		getCover(name);
	}

	@Override
	public boolean coverExists(String name) {
		return data.containsKey(name);
	}

	@Override
	public int getNewCoverID() {
		return nextCoverID++;
	}

	@Override
	public String addCover(BufferedImage newCover) {
		int id = getNewCoverID();
		String fname = CCProperties.getInstance().PROP_COVER_PREFIX.getValue() + id + '.' + CCProperties.getInstance().PROP_COVER_TYPE.getValue();

		data.put(fname, newCover);
		
		return fname;
	}

	@Override
	public void addCover(String name, InputStream stream) throws Exception {
		Pattern pattern = Pattern.compile(String.format("(?<=%s)[0-9]+(?=\\.%s)", Pattern.quote(CCProperties.getInstance().PROP_COVER_PREFIX.getValue().toLowerCase()), Pattern.quote(CCProperties.getInstance().PROP_COVER_TYPE.getValue().toLowerCase()))); //$NON-NLS-1$

		Matcher matcher = pattern.matcher(name);
		if (matcher.find()){
			int id = Integer.parseInt(matcher.group());
			if (id >= nextCoverID) nextCoverID = id+1;
		}
		
		BufferedImage bi = ImageIO.read(stream);
		data.put(name, bi);
	}

	@Override
	public void deleteCover(String covername) {
		data.remove(covername);
	}

	@Override
	public List<Tuple<String, BufferedImage>> listCoversNonCached() throws IOException {
		List<Tuple<String, BufferedImage>> r = new ArrayList<>();
		for (Entry<String, BufferedImage> datum : data.entrySet()) {
			r.add(Tuple.Create(datum.getKey(), datum.getValue()));
		}
		return r;
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		// do nothing
	}
}
