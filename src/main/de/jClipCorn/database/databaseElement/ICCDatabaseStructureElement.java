package de.jClipCorn.database.databaseElement;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.caches.ICalculationCache;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBStructureElementTyp;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileFormat;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import de.jClipCorn.database.elementProps.IEProperty;
import de.jClipCorn.database.elementProps.impl.EEnumProp;
import de.jClipCorn.database.elementProps.impl.EStringProp;
import de.jClipCorn.database.util.ExtendedViewedState;
import de.jClipCorn.util.datetime.CCDate;

import java.awt.image.BufferedImage;

public interface ICCDatabaseStructureElement {
	// Movies, Series, Seasons, Episodes

	EStringProp            title();

	EEnumProp<CCUserScore> score();
	EStringProp            scoreComment();

	CCTagList              getTags();

	CCFileFormat           getFormat();
	CCDate                 getAddDate();

	ExtendedViewedState    getExtendedViewedState();
	String                 getQualifiedTitle();
	int                    getLocalID();
	CCMovieList            getMovieList();

	ICalculationCache      getCache();

	IEProperty[]           getProperties();
	boolean                isDirty();
	void                   resetDirty();
	String[]               getDirty();

	BufferedImage getSelfOrParentCover();

	CCDBStructureElementTyp getStructureType();
}
