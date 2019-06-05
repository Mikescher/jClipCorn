package de.jClipCorn.database.covertab;

import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.colorquantizer.ColorQuantizerMethod;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.helper.ImageUtilities;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CCStubCoverCache implements ICoverCache {

	public CCStubCoverCache() {
		//
	}

	@Override
	public void init() {
		//
	}

	@Override
	public void addInternal(CCCoverData elem) {
		//
	}

	@Override
	public int getNewCoverID() {
		return 0;
	}

	@Override
	public void getBackupExclusions(List<String> excludedFolders, List<String> excludedFiles) {
		//
	}

	@Override
	public BufferedImage getCover(int cid) {
		return Resources.IMG_COVER_STANDARD.get();
	}

	@Override
	public BufferedImage getCover(CCCoverData cce) {
		return Resources.IMG_COVER_STANDARD.get();
	}

	@Override
	public int addCover(BufferedImage newCover) {
		return 0;
	}

	@Override
	public void deleteCover(int cid) {
		//
	}

	@Override
	public CCCoverData getInfo(int cid) {
		return new CCCoverData(cid, "", ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT, "", 1024, ColorQuantizerMethod.EMPTY, CCDateTime.getCurrentDateTime()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean coverFileExists(int cid) {
		return true;
	}

	@Override
	public Tuple<Integer, Integer> getDimensions(int cid) {
		return Tuple.Create(ImageUtilities.BASE_COVER_WIDTH, ImageUtilities.BASE_COVER_HEIGHT);
	}

	@Override
	public boolean isCached(int cid) {
		return false;
	}

	@Override
	public void preloadCover(int cid) {
		//
	}

	@Override
	public int getCoverCount() {
		return 0;
	}

	@Override
	public List<CCCoverData> listCovers() {
		return new ArrayList<>();
	}

	@Override
	public String getFilepath(CCCoverData cce) {
		return null;
	}
}
