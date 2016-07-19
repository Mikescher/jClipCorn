package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDateTimeList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;

public abstract class CCDatabaseElement {
	private final int localID;					// INTEGER
	private final CCMovieTyp typ;				// TINYINT
	private String title; 						// LEN = 128
	private CCMovieLanguage language;			// TINYINT
	private CCMovieGenreList genres;			// BIGINT - unsigned
	private CCMovieOnlineScore onlinescore;		// TINYINT
	private CCMovieFSK fsk;						// TINYINT
	private CCMovieScore score;					// TINYINT
	private String covername;					// LEN = 256
	private final int seriesID;					// INTEGER
	private CCOnlineReference onlineReference;	// VARCHAR
	private CCGroupList linkedGroups;			// VARCHAR
	private CCDateTimeList viewedHistory;       // VARCHAR
	
	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCMovieTyp typ, int id, int seriesID) {
		this.typ = typ;
		this.localID = id;
		this.seriesID = seriesID;
		this.movielist = ml;
		
		onlineReference = CCOnlineReference.createNone();
		linkedGroups = CCGroupList.createEmpty();
		genres = new CCMovieGenreList();
		viewedHistory = CCDateTimeList.createEmpty();
	}
	
	public void setDefaultValues(boolean updateDB) {
		title = ""; //$NON-NLS-1$
		language = CCMovieLanguage.GERMAN;
		genres.clear();
		onlinescore = CCMovieOnlineScore.STARS_0_0;
		fsk = CCMovieFSK.RATING_0;
		score = CCMovieScore.RATING_NO;
		covername = ""; //$NON-NLS-1$
		viewedHistory = CCDateTimeList.createEmpty();
		onlineReference = CCOnlineReference.createNone();
		linkedGroups = CCGroupList.createEmpty();
		
		if (updateDB) {
			updateDB();
		}
	}
	
	public void beginUpdating() {
		isUpdating = true;
	}
	
	public void endUpdating() {
		isUpdating = false;
		
		updateDB();
	}
	
	public void abortUpdating() {
		isUpdating = false;
	}
	
	protected abstract void updateDB();
	
	public int getLocalID() {
		return localID;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		
		updateDB();
	}
	
	public CCMovieOnlineScore getOnlinescore() {
		return onlinescore;
	}

	public void setOnlinescore(int onlinescore) {
		this.onlinescore = CCMovieOnlineScore.find(onlinescore);
		
		updateDB();
	}

	public CCMovieFSK getFSK() {
		return fsk;
	}

	public void setFsk(int fsk) {
		this.fsk = CCMovieFSK.find(fsk);
		
		updateDB();
	}
	
	public CCMovieTyp getType() {
		return typ;
	}
	
	public CCMovieScore getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = CCMovieScore.find(score);
		
		updateDB();
	}
	
	public void setScore(CCMovieScore score) {
		this.score = score;
		
		updateDB();
	}
	
	public void setCover(String name) {
		this.covername = name;
		
		updateDB();
	}
	
	public void setCover(BufferedImage name) {
		if (name == null) {
			return;
		}
			
		
		if (! covername.isEmpty() && name.equals(getCover())) {
			return;
		}
		
		if (! covername.isEmpty()) {
			movielist.getCoverCache().deleteCover(this);
		}
		
		this.covername = movielist.getCoverCache().addCover(name);
		
		updateDB();
	}
	
	public String getCoverName() {
		return covername;
	}

	public BufferedImage getCover() {
		return movielist.getCoverCache().getCover(covername);
	}
	
	public BufferedImage getHalfsizeCover() {
		return movielist.getCoverCache().getHalfsizeCover(covername);
	}
	
	public ImageIcon getCoverIcon() {
		return movielist.getCoverCache().getCoverIcon(covername);
	}
	
	public CCMovieLanguage getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = CCMovieLanguage.find(language);
		
		if (this.language == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", language)); //$NON-NLS-1$
		}
		
		updateDB();
	}

	public CCMovieGenre getGenre(int idx) {
		return genres.getGenre(idx);
	}
	
	public int getGenreCount() {
		return genres.getGenreCount();
	}

	public void setOnlineReference(String data) throws OnlineRefFormatException {
		onlineReference = CCOnlineReference.parse(data);
		
		updateDB();
	}

	public CCOnlineReference getOnlineReference() {
		return onlineReference;
	}

	public void setOnlineReference(CCOnlineReference value) {
		onlineReference = value;
		
		updateDB();
	}

	public void setGroups(String data) {
		setGroups(CCGroupList.parse(movielist, data));
	}

	public void setGroups(CCGroupList value) {
		if (linkedGroups.equals(value)) return;
		
		movielist.unlinkElementFromGroups(this, linkedGroups);
		
		linkedGroups = value;
		
		updateDB();
		
		movielist.linkElementToGroups(this, value);
	}

	public CCGroupList getGroups() {
		return linkedGroups;
	}

	public void setViewedHistory(String data) throws CCFormatException {
		viewedHistory = CCDateTimeList.parse(data);
		
		updateDB();
	}

	public void setViewedHistory(CCDateTimeList value) {
		viewedHistory = value;
		
		updateDB();
	}

	public CCDateTimeList getViewedHistory() {
		return viewedHistory;
	}
	
	public void addToViewedHistory(CCDateTime datetime) {
		this.viewedHistory = this.viewedHistory.add(datetime);
		
		updateDB();
	}
	
	public void fullResetViewedHistory() {
		this.viewedHistory = CCDateTimeList.createEmpty();
		
		updateDB();
	}

	public void setGenre(CCMovieGenre genre, int idx) {
		boolean succ = genres.setGenre(idx, genre);
		
		if (! succ) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", idx)); //$NON-NLS-1$
		}
		
		updateDB();
	}
	
	public void setGenres(long grs) {
		genres =  new CCMovieGenreList(grs);
		
		updateDB();
	}
	
	public void setGenres(CCMovieGenreList grs) {
		genres =  new CCMovieGenreList(grs);
		
		updateDB();
	}
	
	public CCMovieGenreList getGenres() {
		return genres;
	}
	
	public boolean hasHoleInGenres() {
		return  (genres.getGenre(0).isEmpty() && (!genres.getGenre(1).isEmpty())) ||
				(genres.getGenre(1).isEmpty() && (!genres.getGenre(2).isEmpty())) ||
				(genres.getGenre(2).isEmpty() && (!genres.getGenre(3).isEmpty())) ||
				(genres.getGenre(3).isEmpty() && (!genres.getGenre(4).isEmpty())) ||
				(genres.getGenre(4).isEmpty() && (!genres.getGenre(5).isEmpty())) ||
				(genres.getGenre(5).isEmpty() && (!genres.getGenre(6).isEmpty())) ||
				(genres.getGenre(6).isEmpty() && (!genres.getGenre(7).isEmpty()));
	}

	public int getSeriesID() {
		return seriesID;
	}

	public boolean isMovie() {
		return getType().equals(CCMovieTyp.MOVIE);
	}
	
	public boolean isSeries() {
		return getType().equals(CCMovieTyp.SERIES);
	}
	
	@SuppressWarnings({ "nls"})
	protected void setXMLAttributes(Element e, boolean fileHash, boolean coverHash, boolean coverData) {
		e.setAttribute("localid", localID + "");
		e.setAttribute("typ", typ.asInt() + "");
		e.setAttribute("title", title);
		e.setAttribute("language", language.asInt() + "");
		e.setAttribute("genres", genres.getAllGenres() + "");
		e.setAttribute("onlinescore", onlinescore.asInt() + "");
		e.setAttribute("fsk", fsk.asInt() + "");
		e.setAttribute("score", score.asInt() + "");
		e.setAttribute("covername", covername);
		e.setAttribute("seriesid", seriesID + "");
	}
	
	/**
	 * @throws CCFormatException  
	 */
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags) throws CCFormatException {
		if (e.getAttributeValue("title") != null)
			setTitle(e.getAttributeValue("title"));
		
		if (e.getAttributeValue("language") != null)
			setLanguage(Integer.parseInt(e.getAttributeValue("language")));
		
		if (e.getAttributeValue("genres") != null)
			setGenres(Long.parseLong(e.getAttributeValue("genres")));
		
		if (e.getAttributeValue("onlinescore") != null)
			setOnlinescore(Integer.parseInt(e.getAttributeValue("onlinescore")));
		
		if (e.getAttributeValue("fsk") != null)
			setFsk(Integer.parseInt(e.getAttributeValue("fsk")));
		
		if (e.getAttributeValue("score") != null)
			setScore(Integer.parseInt(e.getAttributeValue("score")));
		
		if (resetScore)
			setScore(CCMovieScore.RATING_NO);
		
		if (e.getAttributeValue("covername") != null)
			setCover(e.getAttributeValue("covername"));
		
		if (e.getAttributeValue("coverdata") != null) {
			setCover(""); //Damit er nicht probiert was zu löschen
			setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValue("coverdata"))));
		}
	}

	@SuppressWarnings("nls")
	public Element generateXML(Element el, boolean fileHash, boolean coverHash, boolean coverData) {
		Element dbelement = null;
		
		switch (typ) {
		case MOVIE:
			dbelement = new Element("movie");
			break;
		case SERIES:
			dbelement = new Element("series");
			break;
		}
		
		setXMLAttributes(dbelement, fileHash, coverHash, coverData);
		
		el.addContent(dbelement);
		
		return dbelement;
	}
	
	public int getMovieListPosition() {
		return movielist.getSortByDatabaseElement(this);
	}

	public void forceUpdate() {
		updateDB();
	}

	public abstract CCMovieSize getFilesize();
	
	public abstract CCDate getAddDate();
	
	public abstract int getFirstYear();
}
