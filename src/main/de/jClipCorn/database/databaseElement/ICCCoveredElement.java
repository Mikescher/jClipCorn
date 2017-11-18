package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

public interface ICCCoveredElement {

	public String getTitle();
	
	public String getCoverName();
	
	public BufferedImage getCover();
	
	public String getCoverMD5();
	
}
