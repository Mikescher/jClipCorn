package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.database.databaseElement.columnTypes.CCFSK;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGroupList;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineReference;
import de.jClipCorn.database.databaseElement.columnTypes.CCOnlineScore;
import de.jClipCorn.database.databaseElement.columnTypes.CCQuality;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.gui.log.CCLog;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.GroupFormatException;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;

public abstract class CCDatabaseElement implements ICCDatabaseStructureElement, ICCCoveredElement {
	private final int localID;					// INTEGER
	private final CCDBElementTyp typ;				// TINYINT
	private String title; 						// LEN = 128
	private CCDBLanguage language;			// TINYINT
	private CCGenreList genres;			// BIGINT - unsigned
	private CCOnlineScore onlinescore;		// TINYINT
	private CCFSK fsk;						// TINYINT
	private CCUserScore score;					// TINYINT
	private String covername;					// LEN = 256
	private final int seriesID;					// INTEGER
	private CCOnlineReference onlineReference;	// VARCHAR
	private CCGroupList linkedGroups;			// VARCHAR
	
	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCDBElementTyp typ, int id, int seriesID) {
		this.typ = typ;
		this.localID = id;
		this.seriesID = seriesID;
		this.movielist = ml;
		
		onlineReference = CCOnlineReference.createNone();
		linkedGroups = CCGroupList.createEmpty();
		genres = new CCGenreList();
	}
	
	public void setDefaultValues(boolean updateDB) {
		title = ""; //$NON-NLS-1$
		language = CCDBLanguage.GERMAN;
		genres.clear();
		onlinescore = CCOnlineScore.STARS_0_0;
		fsk = CCFSK.RATING_0;
		score = CCUserScore.RATING_NO;
		covername = ""; //$NON-NLS-1$
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

	@Override
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
		
		updateDB();
	}
	
	public CCOnlineScore getOnlinescore() {
		return onlinescore;
	}

	public void setOnlinescore(int onlinescore) {
		this.onlinescore = CCOnlineScore.getWrapper().find(onlinescore);
		
		updateDB();
	}

	public void setOnlinescore(CCOnlineScore onlinescore) {
		this.onlinescore = onlinescore;
		
		updateDB();
	}

	public CCFSK getFSK() {
		return fsk;
	}

	public void setFsk(int fsk) {
		this.fsk = CCFSK.getWrapper().find(fsk);
		
		updateDB();
	}

	public void setFsk(CCFSK fsk) {
		this.fsk = fsk;
		
		updateDB();
	}
	
	public CCDBElementTyp getType() {
		return typ;
	}
	
	public CCUserScore getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = CCUserScore.getWrapper().find(score);
		
		updateDB();
	}
	
	public void setScore(CCUserScore score) {
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
	
	@Override
	public String getCoverName() {
		return covername;
	}

	@Override
	public BufferedImage getCover() {
		return movielist.getCoverCache().getCover(covername);
	}
	
	@Override
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}
		
	public CCDBLanguage getLanguage() {
		return language;
	}

	public void setLanguage(int language) {
		this.language = CCDBLanguage.getWrapper().find(language);
		
		if (this.language == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", language)); //$NON-NLS-1$
		}
		
		updateDB();
	}

	public void setLanguage(CCDBLanguage language) {
		this.language = language;
		
		if (this.language == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", language)); //$NON-NLS-1$
		}
		
		updateDB();
	}

	public CCGenre getGenre(int idx) {
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

	public void setGroups(String data) throws GroupFormatException {
		setGroups(CCGroupList.parse(movielist, data));
	}

	public void setGroups(CCGroupList value) {
		if (linkedGroups.equals(value)) return;
		
		movielist.unlinkElementFromGroups(this, linkedGroups);
		
		setGroupsInternal(value);
		
		updateDB();
		
		movielist.linkElementToGroups(this, value);
	}

	public void setGroupsInternal(CCGroupList value) {
		linkedGroups = value;
	}

	public CCGroupList getGroups() {
		return linkedGroups;
	}

	public boolean hasGroups() {
		return ! linkedGroups.isEmpty();
	}

	public void setGenre(CCGenre genre, int idx) {
		boolean succ = genres.setGenre(idx, genre);
		
		if (! succ) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", idx)); //$NON-NLS-1$
		}
		
		updateDB();
	}
	
	public void setGenres(long grs) {
		genres =  new CCGenreList(grs);
		
		updateDB();
	}
	
	public void setGenres(CCGenreList grs) {
		genres =  new CCGenreList(grs);
		
		updateDB();
	}
	
	public CCGenreList getGenres() {
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
		return getType().equals(CCDBElementTyp.MOVIE);
	}
	
	public boolean isSeries() {
		return getType().equals(CCDBElementTyp.SERIES);
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
		e.setAttribute("seriesid", seriesID + "");
		e.setAttribute("groups", linkedGroups.toSerializationString());
		e.setAttribute("onlinreref", onlineReference.toSerializationString());

		if (! coverData) {
			e.setAttribute("covername", covername);
		}
		
		if (coverHash) {
			e.setAttribute("coverhash", getCoverMD5());
		}
		
		if (coverData) {
			e.setAttribute("coverdata", ByteUtilies.byteArrayToHexString(ImageUtilities.imageToByteArray(getCover())));
		}
	}
	
	/**
	 * @throws CCFormatException  
	 */
	@SuppressWarnings("nls")
	public void parseFromXML(Element e, boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags, boolean ignoreCoverData) throws CCFormatException {
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
			setScore(CCUserScore.RATING_NO);
		
		if (e.getAttributeValue("covername") != null)
			setCover(e.getAttributeValue("covername"));
		
		if (!ignoreCoverData && e.getAttributeValue("coverdata") != null) {
			setCover(""); //Damit er nicht probiert was zu löschen
			setCover(ImageUtilities.byteArrayToImage(ByteUtilies.hexStringToByteArray(e.getAttributeValue("coverdata"))));
		}
		
		if (e.getAttributeValue("groups") != null)
			setGroups(e.getAttributeValue("groups"));
		
		if (e.getAttributeValue("onlinreref") != null)
			setOnlineReference(e.getAttributeValue("onlinreref"));
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

	public CCMovieList getMovieList() {
		return movielist;
	}

	public void forceUpdate() {
		updateDB();
	}

	public abstract CCFileSize getFilesize();
	
	@Override
	public abstract CCDate getAddDate();
	
	@Override
	public abstract CCFileFormat getFormat();
	
	@Override
	public abstract CCQuality getQuality();
	
	@Override
	public abstract CCTagList getTags();
	
	@Override
	public abstract ExtendedViewedState getExtendedViewedState();
	
	public abstract int getFirstYear();

	public abstract String getFullDisplayTitle();
}
