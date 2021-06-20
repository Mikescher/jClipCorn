package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.covertab.CCCoverData;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.databaseElement.datapacks.IDatabaseElementData;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.IPropertyParent;
import de.jClipCorn.database.elementProps.impl.*;
import de.jClipCorn.database.util.CCQualityCategory;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.features.actionTree.IActionSourceObject;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datetime.CCDateTime;

import java.awt.image.BufferedImage;

public abstract class CCDatabaseElement implements ICCDatabaseStructureElement, ICCCoveredElement, IActionSourceObject, ICCTaggedElement, IDatabaseElementData, IPropertyParent {

	public final EIntProp                  LocalID         = new EIntProp(          "LocalID",         -1,                          this, EPropertyType.DATABASE_PRIMARY_ID);
	public final EEnumProp<CCDBElementTyp> Typ             = new EEnumProp<>(       "Typ",             CCDBElementTyp.MOVIE,        this, EPropertyType.DATABASE_READONLY);
	public final EIntProp                  CoverID         = new EIntProp(          "CoverID",         -1,                          this, EPropertyType.DATABASE_REF);
	public final EGroupListProp            Groups          = new EGroupListProp(    "Groups",          CCGroupList.EMPTY,           this, EPropertyType.USER_METADATA, this::onGroupsChanging);
	public final EStringProp               Title           = new EStringProp(       "Title",           Str.Empty,                   this, EPropertyType.OBJECTIVE_METADATA);
	public final EGenreListProp            Genres          = new EGenreListProp(    "Genres",          CCGenreList.EMPTY,           this, EPropertyType.OBJECTIVE_METADATA);
	public final EEnumProp<CCOnlineScore>  OnlineScore     = new EEnumProp<>(       "OnlineScore",     CCOnlineScore.STARS_0_0,     this, EPropertyType.OBJECTIVE_METADATA);
	public final EEnumProp<CCFSK>          FSK             = new EEnumProp<>(       "FSK",             CCFSK.RATING_0,              this, EPropertyType.OBJECTIVE_METADATA);
	public final EEnumProp<CCUserScore>    Score           = new EEnumProp<>(       "Score",           CCUserScore.RATING_NO,       this, EPropertyType.USER_METADATA);
	public final EOnlineRefListProp        OnlineReference = new EOnlineRefListProp("OnlineReference", CCOnlineReferenceList.EMPTY, this, EPropertyType.OBJECTIVE_METADATA);
	public final ETagListProp              Tags            = new ETagListProp(      "Tags",            CCTagList.EMPTY,             this, EPropertyType.USER_METADATA);

	private IEProperty[] _properties = null;

	protected final CCMovieList movielist;
	protected boolean isUpdating = false;

	public CCDatabaseElement(CCMovieList ml, CCDBElementTyp typ, int id) {
		LocalID.setReadonlyPropToInitial(id);
		Typ.setReadonlyPropToInitial(typ);

		this.movielist = ml;
	}

	public IEProperty[] getProperties()
	{
		if (_properties == null) _properties = listProperties();
		return _properties;
	}

	protected IEProperty[] listProperties()
	{
		return new IEProperty[]
		{
			LocalID,
			Typ,
			CoverID,
			Groups,
			Title,
			Genres,
			OnlineScore,
			FSK,
			Score,
			OnlineReference,
			Tags,
		};
	}

	public EStringProp              title()           { return Title;           }
	public EGenreListProp           genres()          { return Genres;          }
	public EEnumProp<CCOnlineScore> onlineScore()     { return OnlineScore;     }
	public EEnumProp<CCFSK>         fsk()             { return FSK;             }
	public EEnumProp<CCUserScore>   score()           { return Score;           }
	public EOnlineRefListProp       onlineReference() { return OnlineReference; }
	public ETagListProp             tags()            { return Tags;            }

	public void setDefaultValues(boolean updateDB) {
		beginUpdating();

		for (IEProperty prop : getProperties()) prop.resetToDefault();

		if (updateDB) endUpdating(); else abortUpdating();
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

	public abstract boolean updateDB();

	@Override
	public int getLocalID() {
		return LocalID.get();
	}

	public CCDBElementTyp getType() {
		return Typ.get();
	}

	public void setCover(int cid) {
		CoverID.set(cid);
	}
	
	public void setCover(BufferedImage cvr) {
		if (cvr == null) {
			return;
		}

		if (CoverID.get() != -1 && cvr.equals(getCover())) {
			return;
		}
		
		if (CoverID.get() != -1) {
			movielist.getCoverCache().deleteCover(CoverID.get());
		}

		CoverID.set(movielist.getCoverCache().addCover(cvr));
	}
	
	@Override
	public int getCoverID() {
		return CoverID.get();
	}

	@Override
	public BufferedImage getCover() {
		return movielist.getCoverCache().getCover(CoverID.get());
	}

	@Override
	public Tuple<Integer, Integer> getCoverDimensions() {
		return movielist.getCoverCache().getDimensions(CoverID.get());
	}

	@Override
	public CCCoverData getCoverInfo() {
		return movielist.getCoverCache().getInfoOrNull(CoverID.get());
	}

	private CCGroupList onGroupsChanging(CCGroupList value)
	{
		return movielist.addMissingGroups(value);
	}

	@Override
	public String getTitle() {
		return Title.get();
	}

	@Override
	public CCGenreList getGenres() {
		return Genres.get();
	}

	@Override
	public CCOnlineScore getOnlinescore() {
		return OnlineScore.get();
	}

	@Override
	public CCFSK getFSK() {
		return FSK.get();
	}

	@Override
	public CCUserScore getScore() {
		return Score.get();
	}

	@Override
	public CCOnlineReferenceList getOnlineReference() {
		return OnlineReference.get();
	}

	public CCGroupList getGroups() {
		return Groups.get();
	}

	public boolean hasGroups() {
		return ! Groups.isEmpty();
	}

	public boolean hasHoleInGenres() {
		return  (Genres.get().getGenre(0).isEmpty() && (!Genres.get().getGenre(1).isEmpty())) ||
				(Genres.get().getGenre(1).isEmpty() && (!Genres.get().getGenre(2).isEmpty())) ||
				(Genres.get().getGenre(2).isEmpty() && (!Genres.get().getGenre(3).isEmpty())) ||
				(Genres.get().getGenre(3).isEmpty() && (!Genres.get().getGenre(4).isEmpty())) ||
				(Genres.get().getGenre(4).isEmpty() && (!Genres.get().getGenre(5).isEmpty())) ||
				(Genres.get().getGenre(5).isEmpty() && (!Genres.get().getGenre(6).isEmpty())) ||
				(Genres.get().getGenre(6).isEmpty() && (!Genres.get().getGenre(7).isEmpty()));
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
		getCache().bust();
	}

	@Override
	public abstract ExtendedViewedState getExtendedViewedState();
	
	public abstract int getFirstYear();

	public abstract String getFullDisplayTitle();

	public abstract CCQualityCategory getMediaInfoCategory();

	public abstract Opt<CCDateTime> getLastViewed();

	public abstract CCFileSize getFilesize();
}
