package de.jClipCorn.online.metadata;

import java.awt.image.BufferedImage;
import java.util.Map;

import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCSingleOnlineReference;

public class OnlineMetadata {

	public final CCSingleOnlineReference Source;
	
	public String Title = null;
	public Integer Year = null;
	public Integer OnlineScore = null;
	public Integer Length = null;
	public CCGenreList Genres = null;
	public String CoverURL = null;
	public BufferedImage Cover = null;
	public Map<String, Integer> FSKList = null;
	public CCFSK FSK = null;
	public CCSingleOnlineReference AltRef = null;
	
	public OnlineMetadata(CCSingleOnlineReference source) {
		super();
		Source = source;
	}

	public void setMissingFields(OnlineMetadata base) {
		if (this.Title == null || this.Title.isEmpty()) this.Title = base.Title;
		if (this.Year == null) this.Year = base.Year;
		if (this.OnlineScore == null) this.OnlineScore = base.OnlineScore;
		if (this.Length == null) this.Length = base.Length;		
		if (this.Genres == null || this.Genres.isEmpty()) this.Genres = base.Genres;
		if (this.CoverURL == null || this.CoverURL.isEmpty()) {this.CoverURL = base.CoverURL; this.Cover = base.Cover; }
		if (this.Cover == null) {this.Cover = base.Cover; this.CoverURL = base.CoverURL; }
		if (this.FSK == null) {this.FSK = base.FSK; this.FSKList = base.FSKList;}		
		if (this.AltRef == null || this.AltRef.isUnset()) this.AltRef = base.AltRef;
	}
	
	public CCOnlineScore getOnlineScore() {
		if (OnlineScore==null) return null;
		return CCOnlineScore.getWrapper().find(OnlineScore);
	}
}
