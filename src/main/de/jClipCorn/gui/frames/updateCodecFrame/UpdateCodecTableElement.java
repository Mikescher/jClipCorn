package de.jClipCorn.gui.frames.updateCodecFrame;

import de.jClipCorn.database.databaseElement.CCEpisode;
import de.jClipCorn.database.databaseElement.CCMovie;
import de.jClipCorn.database.databaseElement.ICCPlayableElement;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguageSet;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.database.databaseElement.columnTypes.CCMediaInfo;
import de.jClipCorn.database.util.CCMediaInfoField;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.metadata.exceptions.MediaQueryException;
import de.jClipCorn.gui.frames.previewMovieFrame.PreviewMovieFrame;
import de.jClipCorn.gui.frames.previewSeriesFrame.PreviewSeriesFrame;
import de.jClipCorn.gui.resources.Resources;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.formatter.TimeIntervallFormatter;
import de.jClipCorn.util.stream.CCStreams;

import javax.swing.*;
import java.util.List;

public class UpdateCodecTableElement {

	public final ICCPlayableElement Element;

	public List<VideoMetadata> MQResult = null;
	public String MQError = null;	
	
	public String MQResultString = null;

	public boolean Processed = false;
	
	public UpdateCodecTableElement(ICCPlayableElement el) {
		super();
		Element = el;
	}

	public void preview(JFrame owner) {
		if (Element instanceof CCMovie) PreviewMovieFrame.show(owner, (CCMovie)Element, true);
		
		if (Element instanceof CCEpisode) PreviewSeriesFrame.show(owner, (CCEpisode)Element, true);
	}

	public ImageIcon getStatusIcon() {
		if (!Processed) return Resources.ICN_TRANSPARENT.get16x16();
		if (MQError != null) return Resources.ICN_GENERIC_ORB_RED.get16x16();

		if (hasDiff(-1))
			return Resources.ICN_GENERIC_ORB_YELLOW.get16x16(); // has diff
		else
			return Resources.ICN_GENERIC_ORB_GRAY.get16x16(); // no diff

	}

	public String getStatusText() {
		if (!Processed) return Str.Empty;
		if (MQError != null) return MQError;

		var err = getDiff(-1);
		if (Str.isNullOrWhitespace(err)) return Str.Empty;
		return err;
	}

	public boolean hasDiff(double maxLenDiff) {
		return getDiff(maxLenDiff) != null;
	}

	@SuppressWarnings("nls")
	public String getDiff(double maxLenDiff) {
		if (!Processed) return null;
		if (MQResult == null) return null;

		if (!CCDBLanguageSet.equals(Element.language().get(), getNewLanguage())) return "Language";
		if (!CCDBLanguageList.equals(Element.subtitles().get(), getNewSubtitles())) return "Subtitles";
		if (maxLenDiff != -1 && hasLenDiff(maxLenDiff)) return "Length";
		if (!Element.mediaInfo().get().equals(getNewMediaInfo())) return "MediaInfo.[" + midiff(Element.mediaInfo().get(), getNewMediaInfo()) + "]";

		return null;
	}

	private String midiff(CCMediaInfo a, CCMediaInfo b) {
		for (CCMediaInfoField f : CCMediaInfoField.getWrapper().allValues())
		{
			var va = f.get(a);
			var vb = f.get(b);

			if (!Str.equals(va, vb)) return f.asString();
		}

		return null;
	}

	public boolean hasLangDiff() {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!CCDBLanguageSet.equals(Element.language().get(), getNewLanguage())) return true;

		return false;
	}

	public boolean hasSubsDiff() {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!CCDBLanguageList.equals(Element.subtitles().get(), getNewSubtitles())) return true;

		return false;
	}

	public boolean hasMediaInfoDiff() {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (!Element.mediaInfo().get().equals(getNewMediaInfo())) return true;

		return false;
	}

	public boolean hasLenDiff(double maxLenDiff) {
		if (!Processed) return false;
		if (MQResult == null) return false;

		if (getNewDuration().isPresent() && Math.abs(Element.length().get() - getNewDuration().get()) > Element.length().get()*maxLenDiff) return true;

		return false;
	}

	public String getFullDisplayTitle() {
		if (Element instanceof CCMovie) return ((CCMovie)Element).getFullDisplayTitle();
		if (Element instanceof CCEpisode) return Str.format("[{0}] {1} - {2}", ((CCEpisode)Element).getSeries().getTitle(), ((CCEpisode)Element).getStringIdentifier(), Element.title().get()); //$NON-NLS-1$
		
		return "{{??}}"; //$NON-NLS-1$
	}

	public CCDBLanguageSet getOldLanguage() {
		return Element.language().get();
	}

	public CCDBLanguageSet getNewLanguage() {
		if (MQResult == null) return CCDBLanguageSet.EMPTY;
		if (MQResult.size() == 0) return CCDBLanguageSet.EMPTY;

		if (CCStreams.iterate(MQResult).all(r -> r.AudioLanguages == null)) {
			if (getOldLanguage().isSingle()) return getOldLanguage();
			return CCDBLanguageSet.EMPTY;
		}

		CCDBLanguageSet dbll = MQResult.get(0).getValidAudioLanguages();
		for (int i = 1; i < MQResult.size(); i++) dbll = CCDBLanguageSet.intersection(dbll, MQResult.get(i).getValidAudioLanguages());
		return dbll;
	}

	public CCDBLanguageList getOldSubtitles() {
		return Element.subtitles().get();
	}

	public CCDBLanguageList getNewSubtitles() {
		if (MQResult == null) return CCDBLanguageList.EMPTY;
		if (MQResult.size() == 0) return CCDBLanguageList.EMPTY;

		return MQResult.get(0).getValidSubtitleLanguages();
	}

	public String getOldLengthStr() {
		return TimeIntervallFormatter.format(getOldDuration());
	}

	public String getNewLengthStr() {
		if (MQResult == null) return Str.Empty;

		var nd = getNewDuration();
		if (nd.isEmpty()) return "/None/"; //$NON-NLS-1$

		return TimeIntervallFormatter.format(nd.get());
	}

	public int getOldDuration() {
		return Element.length().get();
	}

	public Opt<Integer> getNewDuration() {
		if (MQResult == null) return Opt.empty();

		for (var mqr : MQResult) if (!mqr.Duration.isPresent()) return Opt.empty();

		double d = 0;
		for (var mqr : MQResult) d += mqr.Duration.orElse(0.0);

		return Opt.of((int)(d/60));
	}

	public String getNewResolution() {
		if (!Processed) return Str.Empty;
		if (MQResult == null) return Str.Empty;

		int w = CCStreams.iterate(MQResult).map(p -> p.getDefaultVideoTrack().flatErrMap(q -> q.Width).orElse(-1)).minOrDefault(Integer::compare, -1);
		int h = CCStreams.iterate(MQResult).map(p -> p.getDefaultVideoTrack().flatErrMap(q -> q.Height).orElse(-1)).minOrDefault(Integer::compare, -1);

		if (w < 0 || h < 0) return "N/A"; //$NON-NLS-1$

		return Str.format("{0} x {1}", w, h); //$NON-NLS-1$
	}

	public void check() throws MediaQueryException {
		if ( CCStreams.iterate(MQResult).any(r -> !r.allAudioLanguagesValid())) {
			throw new MediaQueryException("Some parts have no audio-language specified"); //$NON-NLS-1$
		}
		if (CCStreams.iterate(MQResult).any(r -> !r.allAudioLanguagesValid()) && !getOldLanguage().isSingle()) {
			throw new MediaQueryException("Some parts have no audio-language specified"); //$NON-NLS-1$
		}
		if (CCStreams.iterate(MQResult).any(r -> !r.allSubtitleLanguagesValid())) {
			throw new MediaQueryException("Some parts have no subtitle-language specified"); //$NON-NLS-1$
		}
	}

	public CCMediaInfo getOldMediaInfo() {
		return Element.mediaInfo().get();
	}

	public CCMediaInfo getNewMediaInfo() {
		if (!Processed) return CCMediaInfo.EMPTY;
		if (MQResult == null) return CCMediaInfo.EMPTY;
		if (MQResult.isEmpty()) return CCMediaInfo.EMPTY;
		return MQResult.get(0).toPartialMediaInfo().toMediaInfo();
	}

	public CCGenreList getSourceGenres() {
		return Element.getGenresFromSelfOrParent();
	}
}
