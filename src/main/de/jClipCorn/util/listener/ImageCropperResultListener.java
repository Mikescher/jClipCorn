package de.jClipCorn.util.listener;

import java.awt.image.BufferedImage;

public interface ImageCropperResultListener {
	void editingFinished(BufferedImage i);
	void editingCanceled();
}
