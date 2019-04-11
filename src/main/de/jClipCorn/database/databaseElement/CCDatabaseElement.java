package de.jClipCorn.database.databaseElement;

import java.awt.image.BufferedImage;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.exceptions.EnumFormatException;
import org.jdom2.Element;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.util.LargeMD5Calculator;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.CCFormatException;
import de.jClipCorn.util.exceptions.GroupFormatException;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;
import de.jClipCorn.util.helper.ByteUtilies;
import de.jClipCorn.util.helper.ImageUtilities;

public abstract class CCDatabaseElement implements ICCDatabaseStructureElement, ICCCoveredElement {
	private final int localID;
	private final CCDBElementTyp typ;
	private String title;
	private CCGenreList genres;
	private CCOnlineScore onlinescore;
	private CCFSK fsk;
	private CCUserScore score;
	private String covername;
	private final int seriesID;
	private CCOnlineReferenceList onlineReference;
	private CCGroupList linkedGroups;
	private CCTagList tags;
	
	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCDBElementTyp typ, int id, int seriesID) {
		this.typ = typ;
		this.localID = id;
		this.seriesID = seriesID;
		this.movielist = ml;
		
		onlineReference = CCOnlineReferenceList.createEmpty();
		linkedGroups    = CCGroupList.createEmpty();
		genres          = CCGenreList.createEmpty();
		tags            = CCTagList.createEmpty();
	}
	
	public void setDefaultValues(boolean updateDB) {
		title           = ""; //$NON-NLS-1$
		genres          = CCGenreList.createEmpty();
		onlinescore     = CCOnlineScore.STARS_0_0;
		fsk             = CCFSK.RATING_0;
		score           = CCUserScore.RATING_NO;
		covername       = ""; //$NON-NLS-1$
		onlineReference = CCOnlineReferenceList.createEmpty();
		linkedGroups    = CCGroupList.createEmpty();

		tags.clear();
		
		if (updateDB) updateDB();
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

	public void setOnlinescoreSafe(int onlinescore) {
		this.onlinescore = CCOnlineScore.getWrapper().findOrNull(onlinescore);

		if (this.onlinescore == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", score)); //$NON-NLS-1$
			this.onlinescore = CCOnlineScore.STARS_0_0;
		}

		updateDB();
	}

	public void setOnlinescore(int onlinescore) throws EnumFormatException {
		this.onlinescore = CCOnlineScore.getWrapper().findOrException(onlinescore);
		
		updateDB();
	}

	public void setOnlinescore(CCOnlineScore onlinescore) {
		if (onlinescore == null) {CCLog.addUndefinied("Prevented setting CCDBElem.Onlinescore to NULL"); return; } //$NON-NLS-1$

		this.onlinescore = onlinescore;
		
		updateDB();
	}

	public CCFSK getFSK() {
		return fsk;
	}

	public void setFskSafe(int fsk) {
		this.fsk = CCFSK.getWrapper().findOrNull(fsk);

		if (this.fsk == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", fsk)); //$NON-NLS-1$
			this.fsk = CCFSK.RATING_0;
		}

		updateDB();
	}

	public void setFsk(int fsk) throws EnumFormatException {
		this.fsk = CCFSK.getWrapper().findOrException(fsk);
		
		updateDB();
	}

	public void setFsk(CCFSK fsk) {
		if (fsk == null) {CCLog.addUndefinied("Prevented setting CCDBElem.FSK to NULL"); return; } //$NON-NLS-1$

		this.fsk = fsk;
		
		updateDB();
	}
	
	public CCDBElementTyp getType() {
		return typ;
	}
	
	public CCUserScore getScore() {
		return score;
	}

	public void setScoreSafe(int score) {
		this.score = CCUserScore.getWrapper().findOrNull(score);

		if (this.score == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", score)); //$NON-NLS-1$
			this.score = CCUserScore.RATING_NO;
		}

		updateDB();
	}

	public void setScore(int score) throws EnumFormatException {
		this.score = CCUserScore.getWrapper().findOrException(score);
		
		updateDB();
	}
	
	public void setScore(CCUserScore score) {
		if (score == null) {CCLog.addUndefinied("Prevented setting CCDBElem.Score to NULL"); return; } //$NON-NLS-1$

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
	public Tuple<Integer, Integer> getCoverDimensions() {
		return movielist.getCoverCache().getDimensions(covername);
	}
	
	@Override
	public String getCoverMD5() {
		return LargeMD5Calculator.calcMD5(getCover());
	}

	public CCGenre getGenre(int idx) {
		return genres.getGenre(idx);
	}
	
	public int getGenreCount() {
		return genres.getGenreCount();
	}

	public void setOnlineReference(String data) throws OnlineRefFormatException {
		onlineReference = CCOnlineReferenceList.parse(data);
		
		updateDB();
	}

	public CCOnlineReferenceList getOnlineReference() {
		return onlineReference;
	}

	public void setOnlineReference(CCOnlineReferenceList value) {
		if (value == null) {CCLog.addUndefinied("Prevented setting CCDBElem.OnlineReference to NULL"); return; } //$NON-NLS-1$

		onlineReference = value;
		
		updateDB();
	}

	public void setGroups(String data) throws GroupFormatException {
		setGroups(CCGroupList.parseWithoutAddingNewGroups(movielist, data));
	}

	public void setGroups(CCGroupList value) {
		if (value == null) {CCLog.addUndefinied("Prevented setting CCDBElem.Groups to NULL"); return; } //$NON-NLS-1$

		if (linkedGroups.equals(value)) return;
		
		value = movielist.addMissingGroups(value);
		
		setGroupsInternal(value);
		
		updateDB();
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
		CCGenreList glnew = genres.getSetGenre(idx, genre);
		
		if (glnew == null) {
			CCLog.addError(LocaleBundle.getFormattedString("LogMessage.ErroneousDatabaseValues", idx)); //$NON-NLS-1$
			return;
		}
		
		genres = glnew;
		
		updateDB();
	}
	
	public void setGenres(long grs) {
		genres =  new CCGenreList(grs);
		
		updateDB();
	}
	
	public void setGenres(CCGenreList grs) {
		if (grs == null) { CCLog.addUndefinied("Prevented setting CCDBElem.Genres to NULL"); return; } //$NON-NLS-1$

		genres =  new CCGenreList(grs);
		
		updateDB();
	}
	
	public CCGenreList getGenres() {
		return genres;
	}
	
	public boolean tryAddGenre(CCGenre g) {
		CCGenreList l = genres.getAddGenre(g);
		if (l == null) return false;
		
		setGenres(l);
		return true;
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

	@SuppressWarnings("nls")
	public void parseFromXML(Element e, int xmlver, boolean resetAddDate, boolean resetViewed, boolean resetScore, boolean resetTags, boolean ignoreCoverData) throws CCFormatException {
		if (e.getAttributeValue("title") != null)
			setTitle(e.getAttributeValue("title"));
		
		if (e.getAttributeValue("genres") != null)
			setGenres(Long.parseLong(e.getAttributeValue("genres")));
		
		if (e.getAttributeValue("onlinescore") != null)
			setOnlinescore(Integer.parseInt(e.getAttributeValue("onlinescore")));
		
		if (e.getAttributeValue("fsk") != null)
			setFsk(Integer.parseInt(e.getAttributeValue("fsk")));
		
		if (e.getAttributeValue("score") != null)
			setScore(Integer.parseInt(e.getAttributeValue("score")));

		if (e.getAttributeValue("tags") != null)
			setTags(Short.parseShort(e.getAttributeValue("tags")));

		if (resetTags)
			setTags(new CCTagList());
		
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

	public int getMovieListPosition() {
		return movielist.getSortByDatabaseElement(this);
	}

	public CCMovieList getMovieList() {
		return movielist;
	}

	public void forceUpdate() {
		updateDB();
	}

	@Override
	public CCTagList getTags() {
		return tags;
	}

	public void setTags(short ptags) {
		tags.parseFromShort(ptags);
	}

	public void setTags(CCTagList ptags) {
		if (tags == null) {CCLog.addUndefinied("Prevented setting CCDBElem.Tags to NULL"); return; } //$NON-NLS-1$
		this.tags = ptags;

		updateDB();
	}

	public void switchTag(CCSingleTag t) {
		tags.switchTag(t);

		updateDB();
	}

	public void switchTag(int c) {
		tags.switchTag(c);

		updateDB();
	}

	public void setTag(CCSingleTag t, boolean v) {
		tags.setTag(t, v);

		updateDB();
	}

	public void setTag(int c, boolean v) {
		tags.setTag(c, v);

		updateDB();
	}

	public boolean getTag(CCSingleTag t) {
		return tags.getTag(t);
	}

	public boolean getTag(int c) {
		return tags.getTag(c);
	}

	public abstract CCFileSize getFilesize();
	
	@Override
	public abstract CCDate getAddDate();
	
	@Override
	public abstract CCFileFormat getFormat();
	
	@Override
	public abstract CCQuality getQuality();

	@Override
	public abstract ExtendedViewedState getExtendedViewedState();
	
	public abstract int getFirstYear();

	public abstract String getFullDisplayTitle();
}
