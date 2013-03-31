package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCMovieTyp;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;

public abstract class CCDatabaseElement {
	private final int localID;				// INTEGER
	private final CCMovieTyp typ;			// TINYINT
	private String title; 					// LEN = 128
	private CCMovieLanguage language;		// TINYINT
	private CCMovieGenreList genres;		// BIGINT - unsigned
	private CCMovieOnlineScore onlinescore;	// TINYINT
	private CCMovieFSK fsk;					// TINYINT
	private CCMovieScore score;				// TINYINT
	private String covername;				// LEN = 256
	private final int seriesID;				// INTEGER
	
	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCMovieTyp typ, int id, int seriesID) {
		this.typ = typ;
		this.localID = id;
		this.seriesID = seriesID;
		this.movielist = ml;
		
		genres = new CCMovieGenreList();
	}
	
	public void setDefaultValues(boolean updateDB) {
		title = ""; //$NON-NLS-1$
		language = CCMovieLanguage.GERMAN;
		genres.clear();
		onlinescore = CCMovieOnlineScore.STARS_0_0;
		fsk = CCMovieFSK.RATING_0;
		score = CCMovieScore.RATING_NO;
		covername = ""; //$NON-NLS-1$
		
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
	
	@SuppressWarnings("nls")
	protected void setXMLAttributes(Element e) {
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

	@SuppressWarnings("nls")
	public Element generateXML(Element el) {
		Element dbelement = null;
		
		switch (typ) {
		case MOVIE:
			dbelement = new Element("movie");
			break;
		case SERIES:
			dbelement = new Element("series");
			break;
		}
		
		setXMLAttributes(dbelement);
		
		el.addContent(dbelement);
		
		return dbelement;
	}
}
