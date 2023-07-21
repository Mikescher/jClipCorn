package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.impl.FFProbeFastRunner;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

@SuppressWarnings("nls")
public class TestMetadataRunner_FFProbeFast extends ClipCornBaseTest
{
	@Test
	public void testMetadataRunner_000_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "000";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_000.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_000.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1166926L, vmd.FileSize);
		assertOptEquals(1500.0, vmd.Duration);
		assertOptEquals(6223, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6223, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(37500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(37500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_001_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "001";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_001.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_001.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1545782L, vmd.FileSize);
		assertOptEquals(2500.003, vmd.Duration);
		assertOptEquals(4946, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4946, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_002_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "002";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_002.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_002.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(12170130L, vmd.FileSize);
		assertOptEquals(3500.04, vmd.Duration);
		assertOptEquals(27817, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27817, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(87501, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(87501, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_003_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "003";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_003.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_003.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2826740L, vmd.FileSize);
		assertOptEquals(4500.003, vmd.Duration);
		assertOptEquals(5025, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5025, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_004_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "004";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_004.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_004.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4230401L, vmd.FileSize);
		assertOptEquals(5500.0, vmd.Duration);
		assertOptEquals(6153, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6153, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(137500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(137500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_005_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "005";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_005.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_005.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4097053L, vmd.FileSize);
		assertOptEquals(6505.003, vmd.Duration);
		assertOptEquals(5038, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5038, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_006_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "006";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_006.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_006.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(21899356L, vmd.FileSize);
		assertOptEquals(7505.04, vmd.Duration);
		assertOptEquals(23343, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23343, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(187626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(187626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_007_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "007";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_007.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_007.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5310534L, vmd.FileSize);
		assertOptEquals(8505.003, vmd.Duration);
		assertOptEquals(4995, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4995, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_008_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "008";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_008.mpeg"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_008.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(17879040L, vmd.FileSize);
		assertOptEquals(5704.930911, vmd.Duration);
		assertOptEquals(25071, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(25071, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg1video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-1 video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP2 (MPEG audio layer 2)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(384000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg1video", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-1 video", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp2", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP2 (MPEG audio layer 2)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(384000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_009_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "009";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_009.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_009.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4538071L, vmd.FileSize);
		assertOptEquals(7207.003, vmd.Duration);
		assertOptEquals(5037, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5037, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_010_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "010";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_010.mpeg"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_010.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(7712768L, vmd.FileSize);
		assertOptEquals(2402.010911, vmd.Duration);
		assertOptEquals(25687, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(25687, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg1video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-1 video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP2 (MPEG audio layer 2)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(384000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg1video", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-1 video", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp2", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP2 (MPEG audio layer 2)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(384000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_011_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "011";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_011.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_011.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2799712L, vmd.FileSize);
		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4864, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4864, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_012_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "012";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_012.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_012.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5191928L, vmd.FileSize);
		assertOptEquals(6806.0, vmd.Duration);
		assertOptEquals(6102, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6102, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(170150, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(170150, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_013_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "013";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_013.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_013.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5265388L, vmd.FileSize);
		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4950, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4950, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_014_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "014";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_014.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_014.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(20032670L, vmd.FileSize);
		assertOptEquals(5705.04, vmd.Duration);
		assertOptEquals(28091, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(28091, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(142626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(142626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_015_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "015";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_015.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_015.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4476805L, vmd.FileSize);
		assertOptEquals(7207.003, vmd.Duration);
		assertOptEquals(4969, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4969, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_016_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "016";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_016.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_016.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1851902L, vmd.FileSize);
		assertOptEquals(2402.0, vmd.Duration);
		assertOptEquals(6167, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6167, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(60050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(60050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_017_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "017";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_017.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_017.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2845801L, vmd.FileSize);
		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4944, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4944, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_018_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "018";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_018.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_018.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(19367774L, vmd.FileSize);
		assertOptEquals(6806.04, vmd.Duration);
		assertOptEquals(22765, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(22765, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(170151, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(170151, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_019_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "019";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_019.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_019.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5299418L, vmd.FileSize);
		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4982, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4982, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_020_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "020";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_020.wmv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_020.wmv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(9752591L, vmd.FileSize);
		assertOptEquals(5015.092, vmd.Duration);
		assertOptEquals(15557, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(15557, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("msmpeg4v3", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2 Microsoft variant version 3", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("wmav2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Windows Media Audio 2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("msmpeg4v3", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2 Microsoft variant version 3", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("wmav2", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Windows Media Audio 2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_021_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "021";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_021.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_021.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2286180L, vmd.FileSize);
		assertOptEquals(3700.003, vmd.Duration);
		assertOptEquals(4943, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4943, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_022_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "022";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_022.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_022.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(18204592L, vmd.FileSize);
		assertOptEquals(5207.04, vmd.Duration);
		assertOptEquals(27969, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27969, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(130176, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(130176, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_023_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "023";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_023.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_023.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4643238L, vmd.FileSize);
		assertOptEquals(7402.003, vmd.Duration);
		assertOptEquals(5018, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5018, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_024_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "024";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_024.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_024.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2009659L, vmd.FileSize);
		assertOptEquals(2604.0, vmd.Duration);
		assertOptEquals(6174, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6174, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(65100, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(65100, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_025_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "025";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_025.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_025.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3060221L, vmd.FileSize);
		assertOptEquals(4856.003, vmd.Duration);
		assertOptEquals(5041, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5041, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_026_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "026";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_026.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_026.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(19206248L, vmd.FileSize);
		assertOptEquals(6508.04, vmd.Duration);
		assertOptEquals(23609, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23609, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162701, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162701, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_027_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "027";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_027.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_027.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5478633L, vmd.FileSize);
		assertOptEquals(8755.003, vmd.Duration);
		assertOptEquals(5006, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5006, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_028_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "028";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_028.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_028.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2111657L, vmd.FileSize);
		assertOptEquals(2717.0, vmd.Duration);
		assertOptEquals(6217, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6217, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(67925, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(67925, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_029_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "029";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_029.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_029.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2666207L, vmd.FileSize);
		assertOptEquals(4217.003, vmd.Duration);
		assertOptEquals(5058, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5058, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_030_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "030";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_030.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_030.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(10926078L, vmd.FileSize);
		assertOptEquals(3642.04, vmd.Duration);
		assertOptEquals(23999, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23999, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(91051, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(91051, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_031_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "031";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_031.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_031.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3620277L, vmd.FileSize);
		assertOptEquals(5866.003, vmd.Duration);
		assertOptEquals(4937, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4937, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_032_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "032";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_032.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_032.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5462448L, vmd.FileSize);
		assertOptEquals(7088.0, vmd.Duration);
		assertOptEquals(6165, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6165, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(177200, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(177200, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_033_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "033";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_033.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_033.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2827187L, vmd.FileSize);
		assertOptEquals(4500.003, vmd.Duration);
		assertOptEquals(5026, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5026, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_034_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "034";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_034.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_034.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(23519088L, vmd.FileSize);
		assertOptEquals(6505.04, vmd.Duration);
		assertOptEquals(28924, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(28924, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_035_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "035";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_035.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_035.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5478741L, vmd.FileSize);
		assertOptEquals(8705.003, vmd.Duration);
		assertOptEquals(5035, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5035, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_036_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "036";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_036.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_036.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1564142L, vmd.FileSize);
		assertOptEquals(2007.0, vmd.Duration);
		assertOptEquals(6234, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6234, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(50175, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(50175, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_037_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "037";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_037.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_037.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2507615L, vmd.FileSize);
		assertOptEquals(4000.003, vmd.Duration);
		assertOptEquals(5015, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5015, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_038_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "038";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_038.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_038.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(10666994L, vmd.FileSize);
		assertOptEquals(3600.04, vmd.Duration);
		assertOptEquals(23704, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23704, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(90001, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(90001, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_039_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "039";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_039.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_039.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3694402L, vmd.FileSize);
		assertOptEquals(5856.003, vmd.Duration);
		assertOptEquals(5046, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5046, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_040_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "040";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_040.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_040.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5764442L, vmd.FileSize);
		assertOptEquals(7508.0, vmd.Duration);
		assertOptEquals(6142, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6142, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(187700, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(187700, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_041_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "041";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_041.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_041.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1569834L, vmd.FileSize);
		assertOptEquals(2559.003, vmd.Duration);
		assertOptEquals(4907, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4907, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_042_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "042";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_042.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_042.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(15852724L, vmd.FileSize);
		assertOptEquals(4571.04, vmd.Duration);
		assertOptEquals(27744, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27744, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(114276, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(114276, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_043_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "043";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_043.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_043.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5318606L, vmd.FileSize);
		assertOptEquals(8547.003, vmd.Duration);
		assertOptEquals(4978, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4978, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_044_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "044";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_044.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_044.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4337056L, vmd.FileSize);
		assertOptEquals(5690.0, vmd.Duration);
		assertOptEquals(6097, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6097, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(142250, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(142250, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_045_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "045";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_045.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_045.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3625309L, vmd.FileSize);
		assertOptEquals(5801.003, vmd.Duration);
		assertOptEquals(4999, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4999, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_046_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "046";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_046.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_046.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(14860990L, vmd.FileSize);
		assertOptEquals(5093.04, vmd.Duration);
		assertOptEquals(23343, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23343, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(127326, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(127326, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_047_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "047";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_047.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_047.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2777136L, vmd.FileSize);
		assertOptEquals(4470.003, vmd.Duration);
		assertOptEquals(4970, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4970, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_048_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "048";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_048.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_048.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4987558L, vmd.FileSize);
		assertOptEquals(6480.0, vmd.Duration);
		assertOptEquals(6157, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6157, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162000, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162000, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_049_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "049";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_049.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_049.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5314202L, vmd.FileSize);
		assertOptEquals(8496.003, vmd.Duration);
		assertOptEquals(5003, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5003, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_050_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "050";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_050.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_050.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(16583294L, vmd.FileSize);
		assertOptEquals(5508.04, vmd.Duration);
		assertOptEquals(24085, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(24085, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(137701, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(137701, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_051_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "051";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_051.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_051.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3404795L, vmd.FileSize);
		assertOptEquals(5519.003, vmd.Duration);
		assertOptEquals(4935, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4935, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_052_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "052";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_052.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_052.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4204652L, vmd.FileSize);
		assertOptEquals(5452.0, vmd.Duration);
		assertOptEquals(6169, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6169, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(136300, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(136300, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_053_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "053";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_053.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_053.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3475277L, vmd.FileSize);
		assertOptEquals(5537.003, vmd.Duration);
		assertOptEquals(5021, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5021, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_054_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "054";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_054.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_054.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(27418084L, vmd.FileSize);
		assertOptEquals(7540.04, vmd.Duration);
		assertOptEquals(29090, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(29090, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(188501, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(188501, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_055_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "055";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_055.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_055.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1611470L, vmd.FileSize);
		assertOptEquals(2551.003, vmd.Duration);
		assertOptEquals(5053, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5053, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_056_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "056";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_056.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_056.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4369026L, vmd.FileSize);
		assertOptEquals(5632.0, vmd.Duration);
		assertOptEquals(6206, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6206, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(140800, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(140800, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_057_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "057";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_057.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_057.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3604423L, vmd.FileSize);
		assertOptEquals(5755.003, vmd.Duration);
		assertOptEquals(5010, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5010, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_058_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "058";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_058.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_058.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(17357324L, vmd.FileSize);
		assertOptEquals(5817.04, vmd.Duration);
		assertOptEquals(23871, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23871, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(145426, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(145426, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_059_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "059";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_059.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_059.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3733892L, vmd.FileSize);
		assertOptEquals(5917.003, vmd.Duration);
		assertOptEquals(5048, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5048, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_060_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "060";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_060.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_060.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4675950L, vmd.FileSize);
		assertOptEquals(6042.0, vmd.Duration);
		assertOptEquals(6191, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6191, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(151050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(151050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_061_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "061";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_061.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_061.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3809307L, vmd.FileSize);
		assertOptEquals(6166.003, vmd.Duration);
		assertOptEquals(4942, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4942, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_062_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "062";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_062.avi"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_062.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(21916794L, vmd.FileSize);
		assertOptEquals(6288.04, vmd.Duration);
		assertOptEquals(27883, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27883, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("mpeg4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple Profile", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MPEG-4 part 2", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(157201, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(false, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mp3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(false, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("mpeg4", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple Profile", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MPEG-4 part 2", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(157201, vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(false, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("mp3", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("MP3 (MPEG audio layer 3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_063_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "063";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_063.mp4"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_063.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4866337L, vmd.FileSize);
		assertOptEquals(6300.0, vmd.Duration);
		assertOptEquals(6179, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(6179, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(true, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(157500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("aac", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("und", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2098, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(157500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("aac", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEquals("und", vmd.AudioTracks.get(0).Language);
		assertOptEquals("AAC (Advanced Audio Coding)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2098, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptError(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_064_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "064";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_064.mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_064.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4008578L, vmd.FileSize);
		assertOptEquals(6405.003, vmd.Duration);
		assertOptEquals(5006, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5006, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("h264", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("h264", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_065_ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "065";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"));

		assertEquals(MetadataSourceType.FFPROBE_FAST, vmd.SourceType);
		assertEquals(FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(3032829820L, vmd.FileSize);
		assertOptEquals(7808.672, vmd.Duration);
		assertOptEquals(3107140, vmd.OverallBitRate);
		assertOptEquals(24.0, vmd.FrameRate);

		assertOptEquals(3107140, vmd.getTotalBitrate());

		assertEquals("GER+ENG", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("0;0;27;44;13;8;1;6;6;47;9;22;3;45;26;32;16;23;5;4;20;21;12;14;7;7;24;18;10;31;15;15;15;46;34", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(true, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(2, vmd.AudioTracks.size());
		assertEquals(39, vmd.SubtitleTracks.size());

		assertOptEquals("hevc", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Main 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("H.265 / HEVC (High Efficiency Video Coding)", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(1920, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(1080, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(24.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("eac3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("ger", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("ATSC A/52B (AC-3, E-AC-3)", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)6, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(48000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(768000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("hevc", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Main 10", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("H.265 / HEVC (High Efficiency Video Coding)", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(1920, vmd.VideoTracks.get(0).Width);
		assertOptEquals(1080, vmd.VideoTracks.get(0).Height);
		assertOptEquals(24.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEmpty(vmd.VideoTracks.get(0).FrameCount);
		assertOptEmpty(vmd.VideoTracks.get(0).BitDepth);
		assertOptEmpty(vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("eac3", vmd.AudioTracks.get(0).Format);
		assertOptEquals("German", vmd.AudioTracks.get(0).Title);
		assertOptEquals("ger", vmd.AudioTracks.get(0).Language);
		assertOptEquals("ATSC A/52B (AC-3, E-AC-3)", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)6, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(48000, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(768000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("eac3", vmd.AudioTracks.get(1).Format);
		assertOptEquals("English", vmd.AudioTracks.get(1).Title);
		assertOptEquals("eng", vmd.AudioTracks.get(1).Language);
		assertOptEquals("ATSC A/52B (AC-3, E-AC-3)", vmd.AudioTracks.get(1).CodecID);
		assertOptEquals((short)6, vmd.AudioTracks.get(1).Channels);
		assertOptEquals(48000, vmd.AudioTracks.get(1).Samplingrate);
		assertOptEquals(768000, vmd.AudioTracks.get(1).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(1).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(1).Default);
		assertOptEquals(CCDBLanguage.ENGLISH, vmd.AudioTracks.get(1).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(0).Format);
		assertOptEquals("German (Forced)", vmd.SubtitleTracks.get(0).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(0).Language);
		assertOptEquals("ger", vmd.SubtitleTracks.get(0).CodecID);
		assertOptEquals(true, vmd.SubtitleTracks.get(0).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.SubtitleTracks.get(0).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(1).Format);
		assertOptEquals("German", vmd.SubtitleTracks.get(1).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(1).Language);
		assertOptEquals("ger", vmd.SubtitleTracks.get(1).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(1).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.SubtitleTracks.get(1).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(2).Format);
		assertOptEquals("Arabic", vmd.SubtitleTracks.get(2).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(2).Language);
		assertOptEquals("ara", vmd.SubtitleTracks.get(2).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(2).Default);
		assertOptEquals(CCDBLanguage.ARABIC, vmd.SubtitleTracks.get(2).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(3).Format);
		assertOptEquals("Catalan", vmd.SubtitleTracks.get(3).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(3).Language);
		assertOptEquals("cat", vmd.SubtitleTracks.get(3).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(3).Default);
		assertOptEquals(CCDBLanguage.CATALAN, vmd.SubtitleTracks.get(3).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(4).Format);
		assertOptEquals("Czech", vmd.SubtitleTracks.get(4).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(4).Language);
		assertOptEquals("cze", vmd.SubtitleTracks.get(4).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(4).Default);
		assertOptEquals(CCDBLanguage.CZECH, vmd.SubtitleTracks.get(4).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(5).Format);
		assertOptEquals("Danish", vmd.SubtitleTracks.get(5).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(5).Language);
		assertOptEquals("dan", vmd.SubtitleTracks.get(5).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(5).Default);
		assertOptEquals(CCDBLanguage.DANISH, vmd.SubtitleTracks.get(5).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(6).Format);
		assertOptEquals("Modern Greek (1453-)", vmd.SubtitleTracks.get(6).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(6).Language);
		assertOptEquals("gre", vmd.SubtitleTracks.get(6).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(6).Default);
		assertOptError(vmd.SubtitleTracks.get(6).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(7).Format);
		assertOptEquals("English (SDH)", vmd.SubtitleTracks.get(7).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(7).Language);
		assertOptEquals("eng", vmd.SubtitleTracks.get(7).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(7).Default);
		assertOptEquals(CCDBLanguage.ENGLISH, vmd.SubtitleTracks.get(7).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(8).Format);
		assertOptEquals("Spanish", vmd.SubtitleTracks.get(8).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(8).Language);
		assertOptEquals("spa", vmd.SubtitleTracks.get(8).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(8).Default);
		assertOptEquals(CCDBLanguage.SPANISH, vmd.SubtitleTracks.get(8).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(9).Format);
		assertOptEquals("Spanish", vmd.SubtitleTracks.get(9).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(9).Language);
		assertOptEquals("spa", vmd.SubtitleTracks.get(9).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(9).Default);
		assertOptEquals(CCDBLanguage.SPANISH, vmd.SubtitleTracks.get(9).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(10).Format);
		assertOptEquals("Basque", vmd.SubtitleTracks.get(10).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(10).Language);
		assertOptEquals("baq", vmd.SubtitleTracks.get(10).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(10).Default);
		assertOptEquals(CCDBLanguage.BASQUE, vmd.SubtitleTracks.get(10).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(11).Format);
		assertOptEquals("Finnish", vmd.SubtitleTracks.get(11).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(11).Language);
		assertOptEquals("fin", vmd.SubtitleTracks.get(11).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(11).Default);
		assertOptEquals(CCDBLanguage.FINNISH, vmd.SubtitleTracks.get(11).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(12).Format);
		assertOptEquals("Filipino", vmd.SubtitleTracks.get(12).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(12).Language);
		assertOptEquals("fil", vmd.SubtitleTracks.get(12).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(12).Default);
		assertOptEquals(CCDBLanguage.FILIPINO, vmd.SubtitleTracks.get(12).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(13).Format);
		assertOptEquals("French", vmd.SubtitleTracks.get(13).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(13).Language);
		assertOptEquals("fre", vmd.SubtitleTracks.get(13).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(13).Default);
		assertOptEquals(CCDBLanguage.FRENCH, vmd.SubtitleTracks.get(13).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(14).Format);
		assertOptEquals("Galician", vmd.SubtitleTracks.get(14).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(14).Language);
		assertOptEquals("glg", vmd.SubtitleTracks.get(14).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(14).Default);
		assertOptEquals(CCDBLanguage.GALICIAN, vmd.SubtitleTracks.get(14).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(15).Format);
		assertOptEquals("Hebrew", vmd.SubtitleTracks.get(15).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(15).Language);
		assertOptEquals("heb", vmd.SubtitleTracks.get(15).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(15).Default);
		assertOptEquals(CCDBLanguage.HEBREW, vmd.SubtitleTracks.get(15).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(16).Format);
		assertOptEquals("Croatian", vmd.SubtitleTracks.get(16).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(16).Language);
		assertOptEquals("hrv", vmd.SubtitleTracks.get(16).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(16).Default);
		assertOptEquals(CCDBLanguage.CROATIAN, vmd.SubtitleTracks.get(16).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(17).Format);
		assertOptEquals("Hungarian", vmd.SubtitleTracks.get(17).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(17).Language);
		assertOptEquals("hun", vmd.SubtitleTracks.get(17).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(17).Default);
		assertOptEquals(CCDBLanguage.HUNGARIAN, vmd.SubtitleTracks.get(17).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(18).Format);
		assertOptEquals("Indonesian", vmd.SubtitleTracks.get(18).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(18).Language);
		assertOptEquals("ind", vmd.SubtitleTracks.get(18).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(18).Default);
		assertOptEquals(CCDBLanguage.INDONESIAN, vmd.SubtitleTracks.get(18).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(19).Format);
		assertOptEquals("Italian", vmd.SubtitleTracks.get(19).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(19).Language);
		assertOptEquals("ita", vmd.SubtitleTracks.get(19).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(19).Default);
		assertOptEquals(CCDBLanguage.ITALIAN, vmd.SubtitleTracks.get(19).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(20).Format);
		assertOptEquals("Japanese", vmd.SubtitleTracks.get(20).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(20).Language);
		assertOptEquals("jpn", vmd.SubtitleTracks.get(20).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(20).Default);
		assertOptEquals(CCDBLanguage.JAPANESE, vmd.SubtitleTracks.get(20).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(21).Format);
		assertOptEquals("Korean", vmd.SubtitleTracks.get(21).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(21).Language);
		assertOptEquals("kor", vmd.SubtitleTracks.get(21).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(21).Default);
		assertOptEquals(CCDBLanguage.KOREAN, vmd.SubtitleTracks.get(21).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(22).Format);
		assertOptEquals("Malay (macrolanguage)", vmd.SubtitleTracks.get(22).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(22).Language);
		assertOptEquals("may", vmd.SubtitleTracks.get(22).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(22).Default);
		assertOptEquals(CCDBLanguage.MALAY, vmd.SubtitleTracks.get(22).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(23).Format);
		assertOptEquals("Norwegian Bokmål", vmd.SubtitleTracks.get(23).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(23).Language);
		assertOptEquals("nob", vmd.SubtitleTracks.get(23).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(23).Default);
		assertOptError(vmd.SubtitleTracks.get(23).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(24).Format);
		assertOptEquals("Dutch", vmd.SubtitleTracks.get(24).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(24).Language);
		assertOptEquals("dut", vmd.SubtitleTracks.get(24).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(24).Default);
		assertOptEquals(CCDBLanguage.DUTCH, vmd.SubtitleTracks.get(24).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(25).Format);
		assertOptEquals("Polish", vmd.SubtitleTracks.get(25).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(25).Language);
		assertOptEquals("pol", vmd.SubtitleTracks.get(25).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(25).Default);
		assertOptEquals(CCDBLanguage.POLISH, vmd.SubtitleTracks.get(25).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(26).Format);
		assertOptEquals("Portuguese", vmd.SubtitleTracks.get(26).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(26).Language);
		assertOptEquals("por", vmd.SubtitleTracks.get(26).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(26).Default);
		assertOptEquals(CCDBLanguage.PORTUGUESE, vmd.SubtitleTracks.get(26).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(27).Format);
		assertOptEquals("Portuguese", vmd.SubtitleTracks.get(27).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(27).Language);
		assertOptEquals("por", vmd.SubtitleTracks.get(27).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(27).Default);
		assertOptEquals(CCDBLanguage.PORTUGUESE, vmd.SubtitleTracks.get(27).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(28).Format);
		assertOptEquals("Romanian", vmd.SubtitleTracks.get(28).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(28).Language);
		assertOptEquals("rum", vmd.SubtitleTracks.get(28).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(28).Default);
		assertOptEquals(CCDBLanguage.ROMANIAN, vmd.SubtitleTracks.get(28).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(29).Format);
		assertOptEquals("Russian", vmd.SubtitleTracks.get(29).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(29).Language);
		assertOptEquals("rus", vmd.SubtitleTracks.get(29).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(29).Default);
		assertOptEquals(CCDBLanguage.RUSSIAN, vmd.SubtitleTracks.get(29).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(30).Format);
		assertOptEquals("Swedish", vmd.SubtitleTracks.get(30).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(30).Language);
		assertOptEquals("swe", vmd.SubtitleTracks.get(30).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(30).Default);
		assertOptEquals(CCDBLanguage.SWEDISH, vmd.SubtitleTracks.get(30).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(31).Format);
		assertOptEquals("Thai", vmd.SubtitleTracks.get(31).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(31).Language);
		assertOptEquals("tha", vmd.SubtitleTracks.get(31).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(31).Default);
		assertOptEquals(CCDBLanguage.THAI, vmd.SubtitleTracks.get(31).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(32).Format);
		assertOptEquals("Turkish", vmd.SubtitleTracks.get(32).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(32).Language);
		assertOptEquals("tur", vmd.SubtitleTracks.get(32).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(32).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(32).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(33).Format);
		assertOptEquals("Turkish", vmd.SubtitleTracks.get(33).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(33).Language);
		assertOptEquals("tur", vmd.SubtitleTracks.get(33).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(33).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(33).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(34).Format);
		assertOptEquals("Turkish (SDH)", vmd.SubtitleTracks.get(34).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(34).Language);
		assertOptEquals("tur", vmd.SubtitleTracks.get(34).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(34).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(34).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(35).Format);
		assertOptEquals("Ukrainian", vmd.SubtitleTracks.get(35).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(35).Language);
		assertOptEquals("ukr", vmd.SubtitleTracks.get(35).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(35).Default);
		assertOptEquals(CCDBLanguage.UKRAINIAN, vmd.SubtitleTracks.get(35).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(36).Format);
		assertOptEquals("Vietnamese", vmd.SubtitleTracks.get(36).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(36).Language);
		assertOptEquals("vie", vmd.SubtitleTracks.get(36).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(36).Default);
		assertOptEquals(CCDBLanguage.VIETNAMESE, vmd.SubtitleTracks.get(36).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(37).Format);
		assertOptEquals("Simplified, Chinese", vmd.SubtitleTracks.get(37).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(37).Language);
		assertOptEquals("chi", vmd.SubtitleTracks.get(37).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(37).Default);
		assertOptError(vmd.SubtitleTracks.get(37).calcCCDBLanguage());

		assertOptEquals("subrip", vmd.SubtitleTracks.get(38).Format);
		assertOptEquals("Traditional, Chinese", vmd.SubtitleTracks.get(38).Title);
		assertOptEquals("SubRip subtitle", vmd.SubtitleTracks.get(38).Language);
		assertOptEquals("chi", vmd.SubtitleTracks.get(38).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(38).Default);
		assertOptError(vmd.SubtitleTracks.get(38).calcCCDBLanguage());

	}
}
