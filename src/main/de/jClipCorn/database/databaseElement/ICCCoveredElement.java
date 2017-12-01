package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import de.jClipCorn.util.datatypes.Tuple;

public interface ICCCoveredElement {

	public String getTitle();
	
	public String getCoverName();

	public BufferedImage getCover();
	public Tuple<Integer, Integer> getCoverDimensions();
	
	public String getCoverMD5();
	
}
