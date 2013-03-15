package de.jClipCorn.database.databaseElement.columnTypes;

import javax.swing.ImageIcon;

public class CombinedMovieQuality {
	private final CCMovieQuality quality;
	private final CCMovieStatus status;
	
	public CombinedMovieQuality(CCMovieQuality q, CCMovieStatus s) {
		this.status = s;
		this.quality = q;
	}

	public String asString() {
		return quality.asString();
	}

	public String getToltipText() {
		if (status == CCMovieStatus.STATUS_OK) {
			return null;
		} else {
			return status.asString();
		}
	}

	public ImageIcon getIcon() {
		if (status == CCMovieStatus.STATUS_OK) {
			return quality.getIcon();
		} else {
			return quality.getIcon();
		}
	}

	public CCMovieQuality getQuality() {
		return quality;
	}

	public static int compare(CombinedMovieQuality o1, CombinedMovieQuality o2) {
		return CCMovieQuality.compare(o1.getQuality(), o2.getQuality());
	}

}
