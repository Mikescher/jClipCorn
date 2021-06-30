package de.jClipCorn.gui.guiComponents.editCoverControl;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.features.online.metadata.ParseResultHandler;
import de.jClipCorn.util.listener.ImageCropperResultListener;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Giant NOP Class to keep EditCoverControl.java clean
 */
public class AbstractEditCoverControl extends JLayeredPane implements ParseResultHandler, ImageCropperResultListener {
	private static final long serialVersionUID = 2645958195252886548L;

	public AbstractEditCoverControl() {
		// NOP
	}

	@Override
	public void editingFinished(BufferedImage i) {
		// NOP
	}

	@Override
	public void editingCanceled() {
		// NOP
	}

	@Override
	public String getFullTitle() {
		// NOP
		return ""; //$NON-NLS-1$
	}

	@Override
	public String getTitleForParser() {
		// NOP
		return ""; //$NON-NLS-1$
	}

	@Override
	public void setMovieFormat(CCFileFormat cmf) {
		// NOP
	}

	@Override
	public void setMovieName(String name) {
		// NOP
	}

	@Override
	public void setZyklus(String mZyklusTitle) {
		// NOP
	}

	@Override
	public void setZyklusNumber(int iRoman) {
		// NOP
	}

	@Override
	public void setFilesize(CCFileSize size) {
		// NOP
	}

	@Override
	public void setYear(int y) {
		// NOP
	}

	@Override
	public void setGenre(int gid, CCGenre movGenre) {
		// NOP
	}

	@Override
	public void setFSK(CCFSK fsk) {
		// NOP
	}

	@Override
	public void setLength(int l) {
		// NOP
	}

	@Override
	public void setScore(CCOnlineScore s) {
		// NOP
	}

	@Override
	public void setCover(BufferedImage nci) {
		// NOP
	}
	
	@Override
	public void setOnlineReference(CCOnlineReferenceList ref) {
		// NOP
	}

	@Override
	public void onFinishInserting() {
		// NOP
	}

	@Override
	public CCOnlineReferenceList getSearchReference() {
		return CCOnlineReferenceList.EMPTY;
	}

}
