package de.jClipCorn.gui.frames.updateMetadataFrame;

import de.jClipCorn.database.databaseElement.CCDatabaseElement;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeason;
import de.jClipCorn.database.databaseElement.ICCDatabaseStructureElement;
import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.database.elementProps.impl.EStringListProp;
import de.jClipCorn.features.online.metadata.OnlineMetadata;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;

import javax.swing.*;

/**
 * Wraps a {@link CCDatabaseElement} (movie/series) or a {@link CCSeason} so the
 * update-metadata table can handle all three structure types uniformly.
 *
 * Capabilities differ per type:
 * <ul>
 *   <li>Score / Genres: movies and series only</li>
 *   <li>Online-References: all types</li>
 *   <li>Anime-Season / Anime-Studio: movies and seasons only (series only aggregate them read-only)</li>
 * </ul>
 */
public class UpdateMetadataTableElement {

	public final ICCDatabaseStructureElement Element;

	public OnlineMetadata OnlineMeta = null;

	public boolean Processed = false;

	public UpdateMetadataTableElement(ICCDatabaseStructureElement el) {
		super();
		Element = el;
	}

	private CCDatabaseElement asDBElement() { return (Element instanceof CCDatabaseElement) ? (CCDatabaseElement) Element : null; }
	private CCSeason         asSeason()    { return (Element instanceof CCSeason)          ? (CCSeason)          Element : null; }

	public boolean isSeason() { return Element instanceof CCSeason; }

	public String getDisplayTitle() {
		if (Element instanceof CCDatabaseElement) return ((CCDatabaseElement) Element).getFullDisplayTitle();
		return Element.getQualifiedTitle();
	}

	// === Online-References (all types) ===

	public CCOnlineReferenceList getOnlineReference() {
		CCDatabaseElement dbe = asDBElement();
		if (dbe != null) return dbe.getOnlineReference();
		return asSeason().getOnlineReference();
	}

	public void setOnlineReference(CCOnlineReferenceList ref) {
		CCDatabaseElement dbe = asDBElement();
		if (dbe != null) { dbe.OnlineReference.set(ref); return; }
		asSeason().OnlineReference.set(ref);
	}

	// === Score (movies + series) ===

	public boolean supportsScore() { return Element instanceof CCDatabaseElement; }

	public CCOnlineScore getLocalScore() { return asDBElement().getOnlinescore(); }

	public void setScore(CCOnlineScore score) { asDBElement().OnlineScore.set(score); }

	// === Genres (movies + series) ===

	public boolean supportsGenres() { return Element instanceof CCDatabaseElement; }

	public CCGenreList getLocalGenres() { return asDBElement().getGenres(); }

	public void setGenres(CCGenreList genres) { asDBElement().Genres.set(genres); }

	public void tryAddGenre(CCGenre genre) { asDBElement().Genres.tryAddGenre(genre); }

	public boolean supportsAnime() {
		return (Element instanceof CCMovie m && m.getGenres().includesAnime()) || (Element instanceof CCSeason s &&  s.getSeries().getGenres().includesAnime());
	}

	private EStringListProp animeSeasonProp() {
		if (Element instanceof CCMovie)  return ((CCMovie)  Element).animeSeason();
		if (Element instanceof CCSeason) return ((CCSeason) Element).animeSeason();
		return null;
	}

	private EStringListProp animeStudioProp() {
		if (Element instanceof CCMovie)  return ((CCMovie)  Element).animeStudio();
		if (Element instanceof CCSeason) return ((CCSeason) Element).animeStudio();
		return null;
	}

	public CCStringList getLocalAnimeSeason() {
		if (Element instanceof CCDatabaseElement) return ((CCDatabaseElement) Element).getAnimeSeason();
		return asSeason().getAnimeSeason();
	}

	public CCStringList getLocalAnimeStudio() {
		if (Element instanceof CCDatabaseElement) return ((CCDatabaseElement) Element).getAnimeStudio();
		return asSeason().getAnimeStudio();
	}

	public void setAnimeSeason(CCStringList value) { animeSeasonProp().set(value); }

	public void setAnimeStudio(CCStringList value) { animeStudioProp().set(value); }

	public void preview(JFrame owner) {
		if (Element instanceof CCDatabaseElement) {
			CCDatabaseElement el = (CCDatabaseElement) Element;
			if (el.isMovie())  PreviewMovieFrame.show(owner,  el.asMovie(),  true);
			if (el.isSeries()) PreviewSeriesFrame.show(owner, el.asSeries(), true);
		} else if (Element instanceof CCSeason) {
			PreviewSeriesFrame.show(owner, ((CCSeason) Element).getSeries(), true);
		}
	}
}
