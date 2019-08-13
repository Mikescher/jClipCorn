package de.jClipCorn.gui.frames.updateCodecFrame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.CCSeries;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.MediaQueryException;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.mediaquery.MediaQueryResult;
import de.jClipCorn.util.stream.CCStreams;

import java.util.List;

public class UpdateCodecTableElement {

	public final ICCPlayableElement Element;

	public List<MediaQueryResult> MQResult = null;
	public String MQError = null;	
	
	public String MQResultString = null;

	public boolean Processed = false;
	
	public UpdateCodecTableElement(ICCPlayableElement el) {
		super();
		Element = el;
	}

	public void preview(JFrame owner) {
		if (Element instanceof CCMovie) PreviewMovieFrame.show(owner, (CCMovie)Element);
		
		if (Element instanceof CCEpisode) PreviewSeriesFrame.show(owner, (CCEpisode)Element);
	}

	public ImageIcon getStatusIcon() {
		if (!Processed) return Resources.ICN_TRANSPARENT.get16x16();
		if (MQError != null) return Resources.ICN_GENERIC_ORB_RED.get16x16();

		if (hasDiff(-1))
			return Resources.ICN_GENERIC_ORB_YELLOW.get16x16(); // has diff
		else
			return Resources.ICN_GENERIC_ORB_GRAY.get16x16(); // no diff

	}

	public boolean hasDiff(double maxLenDiff) {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!CCDBLanguageList.equals(Element.getLanguage(), getNewLanguage())) return true;
		if (maxLenDiff != -1 && hasLenDiff(maxLenDiff)) return true;
		if (!Element.getMediaInfo().equals(getNewMediaInfo())) return true;

		return false;
	}

	public boolean hasLangDiff() {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!CCDBLanguageList.equals(Element.getLanguage(), getNewLanguage())) return true;

		return false;
	}

	public boolean hasMediaInfoDiff() {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!Element.getMediaInfo().equals(getNewMediaInfo())) return true;

		return false;
	}

	public boolean hasLenDiff(double maxLenDiff) {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (getNewDuration() != -1 && Math.abs(Element.getLength() - getNewDuration()) > Element.getLength()*maxLenDiff) return true;

		return false;
	}

	public String getFullDisplayTitle() {
		if (Element instanceof CCMovie) return ((CCMovie)Element).getFullDisplayTitle();
		if (Element instanceof CCEpisode) return Str.format("[{0}] {1} - {2}", ((CCEpisode)Element).getSeries().getTitle(), ((CCEpisode)Element).getStringIdentifier(), Element.getTitle()); //$NON-NLS-1$
		
		return "{{??}}"; //$NON-NLS-1$
	}

	public CCDBLanguageList getOldLanguage() {
		return Element.getLanguage();
	}

	public CCDBLanguageList getNewLanguage() {
		if (MQResult == null) return CCDBLanguageList.EMPTY;
		if (MQResult.size() == 0) return CCDBLanguageList.EMPTY;

		if (CCStreams.iterate(MQResult).all(r -> r.AudioLanguages == null)) {
			if (getOldLanguage().isSingle()) return getOldLanguage();
			return CCDBLanguageList.EMPTY;
		}

		CCDBLanguageList dbll = MQResult.get(0).AudioLanguages;
		for (int i = 1; i < MQResult.size(); i++) dbll = CCDBLanguageList.intersection(dbll, MQResult.get(i).AudioLanguages);
		return dbll;
	}

	public String getOldLengthStr() {
		return TimeIntervallFormatter.format(getOldDuration());
	}

	public String getNewLengthStr() {
		if (MQResult == null) return Str.Empty;
		int nd = getNewDuration();
		if (nd == -1) return "/None/"; //$NON-NLS-1$
		return TimeIntervallFormatter.format(nd);
	}

	public int getOldDuration() {
		return Element.getLength();
	}

	public int getNewDuration() {
		if (MQResult == null) return -1;

		for (MediaQueryResult mqr : MQResult) if (mqr.Duration == -1) return -1;

		double d = 0;
		for (MediaQueryResult mqr : MQResult) d += mqr.Duration;

		return (int)(d/60);
	}

	public String getNewResolution() {
		if (!Processed) return Str.Empty;
		if (MQResult == null) return Str.Empty;

		int w = CCStreams.iterate(MQResult).map(p -> p.Video.Width).minOrDefault(Integer::compare, 0);
		int h = CCStreams.iterate(MQResult).map(p -> p.Video.Height).minOrDefault(Integer::compare, 0);

		return Str.format("{0} x {1}", w, h); //$NON-NLS-1$
	}

	public void check() throws MediaQueryException {
		if (CCStreams.iterate(MQResult).any(r -> r.AudioLanguages == null) && !CCStreams.iterate(MQResult).all(r -> r.AudioLanguages == null)) {
			throw new MediaQueryException("Some parts have no language specified"); //$NON-NLS-1$
		}
		if (CCStreams.iterate(MQResult).any(r -> r.AudioLanguages == null) && !getOldLanguage().isSingle()) {
			throw new MediaQueryException("Some parts have no language specified"); //$NON-NLS-1$
		}
	}

	public CCMediaInfo getOldMediaInfo() {
		return Element.getMediaInfo();
	}

	public CCMediaInfo getNewMediaInfo() {
		if (!Processed) return CCMediaInfo.EMPTY;
		if (MQResult == null) return CCMediaInfo.EMPTY;
		if (MQResult.isEmpty()) return CCMediaInfo.EMPTY;
		return MQResult.get(0).toMediaInfo();
	}

	public CCGenreList getSourceGenres() {
		return Element.getGenresFromSelfOrParent();
	}
}
