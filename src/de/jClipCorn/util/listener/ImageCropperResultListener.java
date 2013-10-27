package de.jClipCorn.util.listener;

import java.awt.image.BufferedImage;

public interface ImageCropperResultListener {
	public void editingFinished(BufferedImage i);
	public void editingCanceled();
}
