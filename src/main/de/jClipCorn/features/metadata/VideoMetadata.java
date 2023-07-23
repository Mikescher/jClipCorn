package de.jClipCorn.features.metadata;

import de.jClipCorn.database.databaseElement.columnTypes.*;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.stream.CCStreams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VideoMetadata {

	@NotNull public final MetadataSourceType SourceType;
	@NotNull public final String RawOutput;
	@NotNull public final FSPath InputFile;

	@NotNull public final ErrOpt<Long, MetadataError> CDate;
	@NotNull public final ErrOpt<Long, MetadataError> MDate;

	@NotNull public final ErrOpt<String, MetadataError> Checksum;

	@NotNull public final ErrOpt<String,  MetadataError> Format;
	@NotNull public final ErrOpt<String,  MetadataError> Format_Version;
	@NotNull public final ErrOpt<Long,    MetadataError> FileSize;
	@NotNull public final ErrOpt<Double,  MetadataError> Duration;
	@NotNull public final ErrOpt<Integer, MetadataError> OverallBitRate;
	@NotNull public final ErrOpt<Double,  MetadataError> FrameRate;

	@NotNull public final List<VideoTrackMetadata>    VideoTracks;
	@NotNull public final List<AudioTrackMetadata>    AudioTracks;
	@NotNull public final List<SubtitleTrackMetadata> SubtitleTracks;

	@NotNull public final List<ErrOpt<CCDBLanguage, MetadataError>> AudioLanguages;
	@NotNull public final List<ErrOpt<CCDBLanguage, MetadataError>> SubtitleLanguages;

	public VideoMetadata
	(
		@NotNull MetadataSourceType sourceType,
		@NotNull FSPath inputFile,
		@NotNull String rawOutput,
		@NotNull ErrOpt<Long, MetadataError> cdate,
		@NotNull ErrOpt<Long, MetadataError> mdate,
		@NotNull ErrOpt<String, MetadataError> checksum,
		@NotNull ErrOpt<String, MetadataError> format,
		@NotNull ErrOpt<String, MetadataError> format_Version,
		@NotNull ErrOpt<Long, MetadataError> fileSize,
		@NotNull ErrOpt<Double, MetadataError> duration,
		@NotNull ErrOpt<Integer, MetadataError> overallBitRate,
		@NotNull ErrOpt<Double, MetadataError> frameRate,
		@NotNull List<VideoTrackMetadata> videoTracks,
		@NotNull List<AudioTrackMetadata> audioTracks,
		@NotNull List<SubtitleTrackMetadata> subtitleTracks,
		@NotNull List<ErrOpt<CCDBLanguage, MetadataError>> audioLanguages,
		@NotNull List<ErrOpt<CCDBLanguage, MetadataError>> subtitleLanguages
	)
	{
		SourceType = sourceType;
		InputFile = inputFile;
		RawOutput = rawOutput;
		CDate = cdate;
		MDate = mdate;
		Checksum = checksum;
		Format = format;
		Format_Version = format_Version;
		FileSize = fileSize;
		Duration = duration;
		OverallBitRate = overallBitRate;
		FrameRate = frameRate;
		VideoTracks = videoTracks;
		AudioTracks = audioTracks;
		SubtitleTracks = subtitleTracks;
		AudioLanguages = audioLanguages;
		SubtitleLanguages = subtitleLanguages;
	}

	public Opt<VideoTrackMetadata> getDefaultVideoTrack() {
		return CCStreams.iterate(VideoTracks).first(t -> t.Default.orElse(false)).orElseFlat(CCStreams.iterate(VideoTracks).first());
	}

	public Opt<AudioTrackMetadata> getDefaultAudioTrack() {
		return CCStreams.iterate(AudioTracks).first(t -> t.Default.orElse(false)).orElseFlat(CCStreams.iterate(AudioTracks).first());
	}

	public ErrOpt<Integer, MetadataError> getTotalBitrate() {
		var video = getDefaultVideoTrack();
		if (!video.isPresent()) return ErrOpt.empty();

		var audio = getDefaultAudioTrack();
		if (!audio.isPresent()) return ErrOpt.empty();

		var br_vid = (video.get().BitRateNominal.isPresent()) ? video.get().BitRateNominal : video.get().BitRate;
		var br_aud = (audio.get().BitRateNominal.isPresent()) ? audio.get().BitRateNominal : audio.get().BitRate;

		if (br_vid.isError()) return OverallBitRate;
		if (br_aud.isError()) return OverallBitRate;

		if (br_vid.isEmpty()) return OverallBitRate;
		if (br_aud.isEmpty()) return OverallBitRate;

		if (!OverallBitRate.isEmpty())
		{
			if (OverallBitRate.get() < (br_vid.get() + br_aud.get())) return OverallBitRate;
		}

		return ErrOpt.of(br_vid.get() + br_aud.get());
	}

	public CCDBLanguageSet getValidAudioLanguages() {
		return CCDBLanguageSet.create(CCStreams.iterate(this.AudioLanguages).filter(ErrOpt::isPresent).map(ErrOpt::get).toList());
	}

	public boolean hasEmptyAudioLanguages() {
		return CCStreams.iterate(this.AudioLanguages).any(ErrOpt::isEmpty);
	}

	public boolean hasErrorAudioLanguages() {
		return CCStreams.iterate(this.AudioLanguages).any(ErrOpt::isError);
	}

	public boolean allAudioLanguagesValid() {
		return CCStreams.iterate(this.AudioLanguages).all(ErrOpt::isPresent);
	}

	public CCDBLanguageList getValidSubtitleLanguages() {
		return CCDBLanguageList.createDirect(CCStreams.iterate(this.SubtitleLanguages).filter(ErrOpt::isPresent).map(ErrOpt::get).toList());
	}

	public boolean hasEmptySubtitleLanguages() {
		return CCStreams.iterate(this.SubtitleLanguages).any(ErrOpt::isEmpty);
	}

	public boolean hasErrorSubtitleLanguages() {
		return CCStreams.iterate(this.SubtitleLanguages).any(ErrOpt::isError);
	}

	public boolean allSubtitleLanguagesValid() {
		return CCStreams.iterate(this.SubtitleLanguages).all(ErrOpt::isPresent);
	}

	public CCMediaInfo toMediaInfo()
	{
		var video = getDefaultVideoTrack();
		var audio = getDefaultAudioTrack();

		return CCMediaInfo.create
		(
			CDate.toOpt(),
			MDate.toOpt(),
			FileSize.map(CCFileSize::new).toOpt(),
			Checksum.toOpt(),
			Duration.toOpt(),
			getTotalBitrate().toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.Format).toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.Width).toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.Height).toOpt(),
			FrameRate.toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.BitDepth).toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.FrameCount).toOpt(),
			video.<MetadataError>toErrOpt().mapFlat(p -> p.CodecID).toOpt(),
			audio.<MetadataError>toErrOpt().mapFlat(p -> p.Format).toOpt(),
			audio.<MetadataError>toErrOpt().mapFlat(p -> p.Channels).toOpt(),
			audio.<MetadataError>toErrOpt().mapFlat(p -> p.CodecID).toOpt(),
			audio.<MetadataError>toErrOpt().mapFlat(p -> p.Samplingrate).toOpt()

		);
	}
}
