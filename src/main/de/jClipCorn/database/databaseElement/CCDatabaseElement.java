package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.gui.localization.LocaleBundle;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDate;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.GroupFormatException;
import de.jClipCorn.util.exceptions.OnlineRefFormatException;

import java.awt.image.BufferedImage;

public abstract class CCDatabaseElement implements ICCDatabaseStructureElement, ICCCoveredElement, IActionSourceObject, ICCTaggedElement {
	private final int localID;
	private final CCDBElementTyp typ;
	private String title;
	private CCGenreList genres;
	private CCOnlineScore onlinescore;
	private CCFSK fsk;
	private CCUserScore score;
	private int coverid;
	private CCOnlineReferenceList onlineReference;
	private CCGroupList linkedGroups;
	private CCTagList tags;
	
	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCDBElementTyp typ, int id) {
		this.typ       = typ;
		this.localID   = id;
		this.movielist = ml;
		
		onlineReference = CCOnlineReferenceList.EMPTY;
		linkedGroups    = CCGroupList.EMPTY;
		genres          = CCGenreList.EMPTY;
		tags            = CCTagList.EMPTY;
	}
	
	public void setDefaultValues(boolean updateDB) {
		title           = ""; //$NON-NLS-1$
		genres          = CCGenreList.EMPTY;
		onlinescore     = CCOnlineScore.STARS_0_0;
		fsk             = CCFSK.RATING_0;
		score           = CCUserScore.RATING_NO;
		coverid         = -1;
		onlineReference = CCOnlineReferenceList.EMPTY;
		linkedGroups    = CCGroupList.EMPTY;
		tags            = CCTagList.EMPTY;
		
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
	
	protected abstract boolean updateDB();

	@Override
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
	
	public void setCover(int cid) {
		this.coverid = cid;
		
		updateDB();
	}
	
	public void setCover(BufferedImage cvr) {
		if (cvr == null) {
			return;
		}

		if (coverid != -1 && cvr.equals(getCover())) {
			return;
		}
		
		if (coverid != -1) {
			movielist.getCoverCache().deleteCover(this.coverid);
		}
		
		this.coverid = movielist.getCoverCache().addCover(cvr);
		
		updateDB();
	}
	
	@Override
	public int getCoverID() {
		return coverid;
	}

	@Override
	public BufferedImage getCover() {
		return movielist.getCoverCache().getCover(coverid);
	}

	@Override
	public Tuple<Integer, Integer> getCoverDimensions() {
		return movielist.getCoverCache().getDimensions(coverid);
	}

	@Override
	public CCCoverData getCoverInfo() {
		return movielist.getCoverCache().getInfoOrNull(coverid);
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

		genres = grs;
		
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

	public boolean isMovie() {
		return getType().equals(CCDBElementTyp.MOVIE);
	}
	
	public boolean isSeries() {
		return getType().equals(CCDBElementTyp.SERIES);
	}

	public int getMovieListPosition() {
		return movielist.getSortByDatabaseElement(this);
	}

	@Override
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
		tags = CCTagList.fromShort(ptags);
	}

	@Override
	public void setTags(CCTagList ptags) {
		if (ptags == null) {CCLog.addUndefinied("Prevented setting CCDBElem.Tags to NULL"); return; } //$NON-NLS-1$
		this.tags = ptags;

		updateDB();
	}

	@Override
	public void switchTag(CCSingleTag t) {
		tags = tags.getSwitchTag(t);

		updateDB();
	}

	public void switchTag(int c) {
		tags = tags.getSwitchTag(c);

		updateDB();
	}

	public void setTag(CCSingleTag t, boolean v) {
		tags = tags.getSetTag(t, v);

		updateDB();
	}

	public void setTag(int c, boolean v) {
		tags = tags.getSetTag(c, v);

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
	public abstract ExtendedViewedState getExtendedViewedState();
	
	public abstract int getFirstYear();

	public abstract String getFullDisplayTitle();

	public abstract CCQualityCategory getMediaInfoCategory();
}
