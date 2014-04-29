package de.jClipCorn.gui.guiComponents.editCoverControl;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieQuality;
import de.jClipCorn.util.listener.ImageCropperResultListener;
import de.jClipCorn.util.parser.ParseResultHandler;


/**
 * Giant NOP Class to keep EditCOerCOntrol.java clean
 */
public class AbstractEditCoverControl extends JPanel implements ParseResultHandler, ImageCropperResultListener {
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
	public void setMovieFormat(CCMovieFormat cmf) {
		// NOP
	}

	@Override
	public void setFilepath(int p, String t) {
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
	public void setFilesize(long size) {
		// NOP
	}

	@Override
	public void setMovieLanguage(CCMovieLanguage lang) {
		// NOP
	}

	@Override
	public void setQuality(CCMovieQuality q) {
		// NOP
	}

	@Override
	public void setYear(int y) {
		// NOP
	}

	@Override
	public void setGenre(int gid, int movGenre) {
		// NOP
	}

	@Override
	public void setFSK(int fsk) {
		// NOP
	}

	@Override
	public void setLength(int l) {
		// NOP
	}

	@Override
	public void setScore(int s) {
		// NOP
	}

	@Override
	public void setCover(BufferedImage nci) {
		// NOP
	}

	@Override
	public void onFinishInserting() {
		// NOP
	}

}
