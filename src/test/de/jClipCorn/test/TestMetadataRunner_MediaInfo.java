package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

@SuppressWarnings("nls")
public class TestMetadataRunner_MediaInfo extends ClipCornBaseTest 
{

	@Test
	public void testMetadataRunner_000_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "000";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_000.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_000.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1166926L, vmd.FileSize);
		assertOptEquals(1500.0, vmd.Duration);
		assertOptEquals(6224, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5865, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3771, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(37500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(1500.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3771, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(37500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(1500.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_001_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "001";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_001.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_001.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(62500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2500.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(62500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2500.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_002_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "002";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_002.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_002.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(22391, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(87501, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(3500.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(22391, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(87501, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(3500.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_003_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "003";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_003.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_003.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(112500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4500.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(112500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4500.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_004_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "004";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_004.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_004.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4230401L, vmd.FileSize);
		assertOptEquals(5500.0, vmd.Duration);
		assertOptEquals(6153, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5835, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3741, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(137500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5500.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3741, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(137500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5500.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_005_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "005";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_005.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_005.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(4097053L, vmd.FileSize);
		assertOptEquals(6505.003, vmd.Duration);
		assertOptEquals(5039, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5039, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162625, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6505.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162625, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6505.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_006_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "006";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_006.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_006.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(21899356L, vmd.FileSize);
		assertOptEquals(7505.04, vmd.Duration);
		assertOptEquals(23344, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(23344, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18141, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(187626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7505.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18141, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(187626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7505.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_007_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "007";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_007.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_007.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(212625, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8505.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(212625, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8505.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_008_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "008";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_008.mpeg"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_008.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-PS", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(17879040L, vmd.FileSize);
		assertOptEquals(5704.96, vmd.Duration);
		assertOptEquals(25072, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(25072, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG Video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(142624, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5704.96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(384000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Video", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEmpty(vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(142624, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5704.96, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEmpty(vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(384000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_009_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "009";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_009.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_009.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(180175, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7207.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(180175, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7207.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_010_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "010";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_010.mpeg"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_010.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-PS", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(7712768L, vmd.FileSize);
		assertOptEquals(2402.0, vmd.Duration);
		assertOptEquals(25688, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(25688, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG Video", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(60050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2402.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(384000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Video", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEmpty(vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(60050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2402.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEmpty(vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(384000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_011_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "011";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_011.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_011.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(2799712L, vmd.FileSize);
		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4865, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4865, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(115100, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4604.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(115100, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4604.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_012_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "012";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_012.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_012.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5191928L, vmd.FileSize);
		assertOptEquals(6806.0, vmd.Duration);
		assertOptEquals(6103, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5787, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3693, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(170150, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6806.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3693, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(170150, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6806.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_013_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "013";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_013.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_013.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(5265388L, vmd.FileSize);
		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4951, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4951, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(212700, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8508.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(212700, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8508.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_014_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "014";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_014.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_014.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(22991, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(142626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5705.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(22991, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(142626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5705.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_015_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "015";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_015.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_015.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(180175, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7207.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(180175, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7207.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_016_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "016";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_016.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_016.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1851902L, vmd.FileSize);
		assertOptEquals(2402.0, vmd.Duration);
		assertOptEquals(6168, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5830, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3736, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(60050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2402.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3736, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(60050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2402.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_017_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "017";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_017.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_017.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(2845801L, vmd.FileSize);
		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4945, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4945, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(115100, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4604.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(115100, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4604.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_018_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "018";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_018.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_018.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(19367774L, vmd.FileSize);
		assertOptEquals(6806.041, vmd.Duration);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(17558, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(170151, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6806.041, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(17558, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(170151, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6806.041, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_019_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "019";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_019.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_019.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(5299418L, vmd.FileSize);
		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4983, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4983, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(212700, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8508.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(212700, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8508.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_020_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "020";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_020.wmv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_020.wmv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Windows Media", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(9752591L, vmd.FileSize);
		assertOptEquals(5015.04, vmd.Duration);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("MP43", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(125376, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5015.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("WMA", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("161", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEmpty(vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("MP43", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(125376, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5015.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("WMA", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("161", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_021_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "021";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_021.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_021.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(92500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(3700.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(92500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(3700.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_022_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "022";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_022.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_022.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(22674, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(130176, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5207.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(22674, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(130176, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5207.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_023_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "023";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_023.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_023.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(185050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7402.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(185050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7402.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_024_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "024";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_024.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_024.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2009659L, vmd.FileSize);
		assertOptEquals(2604.0, vmd.Duration);
		assertOptEquals(6174, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5839, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3745, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(65100, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2604.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3745, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(65100, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2604.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_025_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "025";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_025.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_025.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(3060221L, vmd.FileSize);
		assertOptEquals(4856.003, vmd.Duration);
		assertOptEquals(5042, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5042, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(121400, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4856.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(121400, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4856.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_026_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "026";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_026.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_026.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18391, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162701, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6508.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18391, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162701, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6508.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_027_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "027";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_027.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_027.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(218875, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8755.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(218875, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8755.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_028_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "028";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_028.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_028.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(2111657L, vmd.FileSize);
		assertOptEquals(2717.0, vmd.Duration);
		assertOptEquals(6218, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5884, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3790, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(67925, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2717.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3790, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(67925, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2717.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_029_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "029";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_029.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_029.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(105425, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4217.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(105425, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4217.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_030_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "030";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_030.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_030.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(10926078L, vmd.FileSize);
		assertOptEquals(3642.04, vmd.Duration);
		assertOptEquals(24000, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(24000, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18575, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(91051, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(3642.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18575, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(91051, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(3642.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_031_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "031";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_031.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_031.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(146650, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5866.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(146650, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5866.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_032_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "032";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_032.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_032.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5462448L, vmd.FileSize);
		assertOptEquals(7088.0, vmd.Duration);
		assertOptEquals(6165, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5850, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3756, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(177200, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7088.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3756, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(177200, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7088.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_033_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "033";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_033.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_033.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(112500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4500.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(112500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4500.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_034_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "034";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_034.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_034.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(23875, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162626, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6505.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(23875, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162626, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6505.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_035_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "035";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_035.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_035.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(217625, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8705.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(217625, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8705.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_036_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "036";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_036.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_036.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(1564142L, vmd.FileSize);
		assertOptEquals(2007.0, vmd.Duration);
		assertOptEquals(6235, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5890, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3796, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(50175, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2007.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3796, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(50175, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2007.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_037_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "037";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_037.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_037.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(100000, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4000.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(100000, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4000.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_038_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "038";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_038.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_038.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18274, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(90001, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(3600.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18274, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(90001, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(3600.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_039_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "039";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_039.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_039.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(3694402L, vmd.FileSize);
		assertOptEquals(5856.003, vmd.Duration);
		assertOptEquals(5047, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5047, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(146400, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5856.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(146400, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5856.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_040_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "040";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_040.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_040.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(5764442L, vmd.FileSize);
		assertOptEquals(7508.0, vmd.Duration);
		assertOptEquals(6142, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5828, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3734, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(187700, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7508.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3734, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(187700, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7508.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_041_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "041";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_041.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_041.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(1569834L, vmd.FileSize);
		assertOptEquals(2559.003, vmd.Duration);
		assertOptEquals(4908, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(4908, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(63975, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2559.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(63975, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2559.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_042_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "042";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_042.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_042.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(15852724L, vmd.FileSize);
		assertOptEquals(4571.04, vmd.Duration);
		assertOptEquals(27745, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27745, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(22407, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(114276, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4571.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(22407, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(114276, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4571.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_043_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "043";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_043.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_043.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(213675, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8547.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(213675, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8547.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_044_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "044";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_044.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_044.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4337056L, vmd.FileSize);
		assertOptEquals(5690.0, vmd.Duration);
		assertOptEquals(6098, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5780, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3686, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(142250, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5690.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3686, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(142250, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5690.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_045_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "045";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_045.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_045.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(3625309L, vmd.FileSize);
		assertOptEquals(5801.003, vmd.Duration);
		assertOptEquals(5000, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5000, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(145025, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5801.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(145025, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5801.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_046_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "046";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_046.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_046.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18041, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(127326, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5093.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18041, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(127326, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5093.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_047_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "047";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_047.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_047.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(111750, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(4470.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(111750, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(4470.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_048_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "048";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_048.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_048.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4987558L, vmd.FileSize);
		assertOptEquals(6480.0, vmd.Duration);
		assertOptEquals(6157, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5841, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3747, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(162000, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6480.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3747, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(162000, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6480.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_049_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "049";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_049.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_049.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(5314202L, vmd.FileSize);
		assertOptEquals(8496.003, vmd.Duration);
		assertOptEquals(5004, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5004, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(212400, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(8496.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(212400, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(8496.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_050_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "050";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_050.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_050.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(16583294L, vmd.FileSize);
		assertOptEquals(5508.04, vmd.Duration);
		assertOptEquals(24086, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(24086, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18807, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(137701, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5508.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18807, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(137701, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5508.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_051_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "051";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_051.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_051.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(137975, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5519.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(137975, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5519.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_052_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "052";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_052.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_052.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4204652L, vmd.FileSize);
		assertOptEquals(5452.0, vmd.Duration);
		assertOptEquals(6170, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5851, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3757, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(136300, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5452.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3757, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(136300, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5452.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_053_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "053";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_053.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_053.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(138425, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5537.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(138425, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5537.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_054_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "054";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_054.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_054.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(27418084L, vmd.FileSize);
		assertOptEquals(7540.04, vmd.Duration);
		assertOptEquals(29091, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(29091, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(24074, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(188501, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7540.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(24074, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(188501, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7540.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_055_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "055";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_055.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_055.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(1611470L, vmd.FileSize);
		assertOptEquals(2551.003, vmd.Duration);
		assertOptEquals(5054, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5054, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(63775, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(2551.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(63775, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(2551.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_056_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "056";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_056.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_056.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4369026L, vmd.FileSize);
		assertOptEquals(5632.0, vmd.Duration);
		assertOptEquals(6206, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5888, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3794, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(140800, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5632.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3794, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(140800, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5632.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_057_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "057";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_057.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_057.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(143875, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5755.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(143875, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5755.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_058_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "058";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_058.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_058.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
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

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(18607, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(145426, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5817.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(18607, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(145426, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5817.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_059_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "059";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_059.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_059.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(147925, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(5917.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(147925, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(5917.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_060_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "060";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_060.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_060.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4675950L, vmd.FileSize);
		assertOptEquals(6042.0, vmd.Duration);
		assertOptEquals(6191, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5874, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3780, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(151050, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6042.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3780, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(151050, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6042.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_061_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "061";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_061.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_061.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
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

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(154150, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6166.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(154150, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6166.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_062_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "062";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_062.avi"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_062.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("AVI", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(21916794L, vmd.FileSize);
		assertOptEquals(6288.04, vmd.Duration);
		assertOptEquals(27884, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(27884, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("MPEG-4 Visual", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Simple", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("FMP4", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(22641, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(157201, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6288.04, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG Audio", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("55", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(128000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("MPEG-4 Visual", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Simple", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("FMP4", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(22641, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(157201, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6288.04, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("MPEG Audio", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("55", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(128000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEmpty(vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_063_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "063";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_063.mp4"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_063.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("MPEG-4", vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEquals(4866337L, vmd.FileSize);
		assertOptEquals(6300.0, vmd.Duration);
		assertOptEquals(6179, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5863, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("avc1", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(3769, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(157500, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6300.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AAC", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("mp4a-40-2", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(2094, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("avc1", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(3769, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(157500, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6300.0, vmd.VideoTracks.get(0).Duration);
		assertOptEmpty(vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("AAC", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("mp4a-40-2", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(2094, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_064_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "064";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_064.mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_064.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(4008578L, vmd.FileSize);
		assertOptEquals(6405.003, vmd.Duration);
		assertOptEquals(5007, vmd.OverallBitRate);
		assertOptEquals(25.0, vmd.FrameRate);

		assertOptEquals(5007, vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(true, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(1, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEquals("AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("High", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(128, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(96, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(25.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(160125, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)8, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(6405.003, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("Vorbis", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_VORBIS", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)2, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(44100, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(112000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("AVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("High", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(128, vmd.VideoTracks.get(0).Width);
		assertOptEquals(96, vmd.VideoTracks.get(0).Height);
		assertOptEquals(25.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(160125, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)8, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(6405.003, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("Vorbis", vmd.AudioTracks.get(0).Format);
		assertOptEmpty(vmd.AudioTracks.get(0).Title);
		assertOptEmpty(vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_VORBIS", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)2, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(44100, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(112000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

	}

	@Test
	public void testMetadataRunner_065_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "065";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"));

		assertEquals(MetadataSourceType.MEDIAINFO, vmd.SourceType);
		assertEquals(FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEquals("Matroska", vmd.Format);
		assertOptEquals("4", vmd.Format_Version);
		assertOptEquals(3032829820L, vmd.FileSize);
		assertOptEquals(7708.875, vmd.Duration);
		assertOptEquals(3107140, vmd.OverallBitRate);
		assertOptEquals(24.0, vmd.FrameRate);

		assertOptEquals(2353291, vmd.getTotalBitrate());

		assertEquals("GER+ENG", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("0;0;27;44;13;8;25;1;6;6;47;9;22;3;45;26;32;16;23;5;4;20;21;11;12;14;7;7;24;18;10;31;15;15;15;1;34;19;19", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(1, vmd.VideoTracks.size());
		assertEquals(2, vmd.AudioTracks.size());
		assertEquals(39, vmd.SubtitleTracks.size());

		assertOptEquals("HEVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format));
		assertOptEquals("Main 10", vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile));
		assertOptEquals("V_MPEGH/ISO/HEVC", vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals(1585291, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(1920, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width));
		assertOptEquals(1080, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height));
		assertOptEquals(24.0, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate));
		assertOptEquals(185013, vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount));
		assertOptEquals((short)10, vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth));
		assertOptEquals(7708.875, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration));
		assertOptEquals(true, vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default));

		assertOptEquals("E-AC-3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format));
		assertOptEquals("de", vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language));
		assertOptEquals("A_EAC3", vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID));
		assertOptEquals((short)6, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels));
		assertOptEquals(48000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate));
		assertOptEquals(768000, vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate));
		assertOptEmpty(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal));
		assertOptEquals(true, vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default));

		assertOptEquals("HEVC", vmd.VideoTracks.get(0).Format);
		assertOptEquals("Main 10", vmd.VideoTracks.get(0).Format_Profile);
		assertOptEquals("V_MPEGH/ISO/HEVC", vmd.VideoTracks.get(0).CodecID);
		assertOptEquals(1585291, vmd.VideoTracks.get(0).BitRate);
		assertOptEmpty(vmd.VideoTracks.get(0).BitRateNominal);
		assertOptEquals(1920, vmd.VideoTracks.get(0).Width);
		assertOptEquals(1080, vmd.VideoTracks.get(0).Height);
		assertOptEquals(24.0, vmd.VideoTracks.get(0).FrameRate);
		assertOptEquals(185013, vmd.VideoTracks.get(0).FrameCount);
		assertOptEquals((short)10, vmd.VideoTracks.get(0).BitDepth);
		assertOptEquals(7708.875, vmd.VideoTracks.get(0).Duration);
		assertOptEquals(true, vmd.VideoTracks.get(0).Default);
		assertOptEmpty(vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("E-AC-3", vmd.AudioTracks.get(0).Format);
		assertOptEquals("German", vmd.AudioTracks.get(0).Title);
		assertOptEquals("de", vmd.AudioTracks.get(0).Language);
		assertOptEquals("A_EAC3", vmd.AudioTracks.get(0).CodecID);
		assertOptEquals((short)6, vmd.AudioTracks.get(0).Channels);
		assertOptEquals(48000, vmd.AudioTracks.get(0).Samplingrate);
		assertOptEquals(768000, vmd.AudioTracks.get(0).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(0).BitRateNominal);
		assertOptEquals(true, vmd.AudioTracks.get(0).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.AudioTracks.get(0).calcCCDBLanguage());

		assertOptEquals("E-AC-3", vmd.AudioTracks.get(1).Format);
		assertOptEquals("English", vmd.AudioTracks.get(1).Title);
		assertOptEquals("en", vmd.AudioTracks.get(1).Language);
		assertOptEquals("A_EAC3", vmd.AudioTracks.get(1).CodecID);
		assertOptEquals((short)6, vmd.AudioTracks.get(1).Channels);
		assertOptEquals(48000, vmd.AudioTracks.get(1).Samplingrate);
		assertOptEquals(768000, vmd.AudioTracks.get(1).BitRate);
		assertOptEmpty(vmd.AudioTracks.get(1).BitRateNominal);
		assertOptEquals(false, vmd.AudioTracks.get(1).Default);
		assertOptEquals(CCDBLanguage.ENGLISH, vmd.AudioTracks.get(1).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(0).Format);
		assertOptEquals("German (Forced)", vmd.SubtitleTracks.get(0).Title);
		assertOptEquals("de", vmd.SubtitleTracks.get(0).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(0).CodecID);
		assertOptEquals(true, vmd.SubtitleTracks.get(0).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.SubtitleTracks.get(0).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(1).Format);
		assertOptEquals("German", vmd.SubtitleTracks.get(1).Title);
		assertOptEquals("de", vmd.SubtitleTracks.get(1).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(1).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(1).Default);
		assertOptEquals(CCDBLanguage.GERMAN, vmd.SubtitleTracks.get(1).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(2).Format);
		assertOptEquals("Arabic", vmd.SubtitleTracks.get(2).Title);
		assertOptEquals("ar", vmd.SubtitleTracks.get(2).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(2).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(2).Default);
		assertOptEquals(CCDBLanguage.ARABIC, vmd.SubtitleTracks.get(2).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(3).Format);
		assertOptEquals("Catalan", vmd.SubtitleTracks.get(3).Title);
		assertOptEquals("ca", vmd.SubtitleTracks.get(3).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(3).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(3).Default);
		assertOptEquals(CCDBLanguage.CATALAN, vmd.SubtitleTracks.get(3).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(4).Format);
		assertOptEquals("Czech", vmd.SubtitleTracks.get(4).Title);
		assertOptEquals("cs", vmd.SubtitleTracks.get(4).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(4).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(4).Default);
		assertOptEquals(CCDBLanguage.CZECH, vmd.SubtitleTracks.get(4).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(5).Format);
		assertOptEquals("Danish", vmd.SubtitleTracks.get(5).Title);
		assertOptEquals("da", vmd.SubtitleTracks.get(5).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(5).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(5).Default);
		assertOptEquals(CCDBLanguage.DANISH, vmd.SubtitleTracks.get(5).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(6).Format);
		assertOptEquals("Modern Greek (1453-)", vmd.SubtitleTracks.get(6).Title);
		assertOptEquals("el", vmd.SubtitleTracks.get(6).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(6).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(6).Default);
		assertOptEquals(CCDBLanguage.GREEK, vmd.SubtitleTracks.get(6).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(7).Format);
		assertOptEquals("English (SDH)", vmd.SubtitleTracks.get(7).Title);
		assertOptEquals("en", vmd.SubtitleTracks.get(7).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(7).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(7).Default);
		assertOptEquals(CCDBLanguage.ENGLISH, vmd.SubtitleTracks.get(7).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(8).Format);
		assertOptEquals("Spanish", vmd.SubtitleTracks.get(8).Title);
		assertOptEquals("es", vmd.SubtitleTracks.get(8).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(8).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(8).Default);
		assertOptEquals(CCDBLanguage.SPANISH, vmd.SubtitleTracks.get(8).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(9).Format);
		assertOptEquals("Spanish", vmd.SubtitleTracks.get(9).Title);
		assertOptEquals("es", vmd.SubtitleTracks.get(9).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(9).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(9).Default);
		assertOptEquals(CCDBLanguage.SPANISH, vmd.SubtitleTracks.get(9).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(10).Format);
		assertOptEquals("Basque", vmd.SubtitleTracks.get(10).Title);
		assertOptEquals("eu", vmd.SubtitleTracks.get(10).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(10).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(10).Default);
		assertOptEquals(CCDBLanguage.BASQUE, vmd.SubtitleTracks.get(10).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(11).Format);
		assertOptEquals("Finnish", vmd.SubtitleTracks.get(11).Title);
		assertOptEquals("fi", vmd.SubtitleTracks.get(11).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(11).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(11).Default);
		assertOptEquals(CCDBLanguage.FINNISH, vmd.SubtitleTracks.get(11).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(12).Format);
		assertOptEquals("Filipino", vmd.SubtitleTracks.get(12).Title);
		assertOptEquals("fil", vmd.SubtitleTracks.get(12).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(12).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(12).Default);
		assertOptEquals(CCDBLanguage.FILIPINO, vmd.SubtitleTracks.get(12).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(13).Format);
		assertOptEquals("French", vmd.SubtitleTracks.get(13).Title);
		assertOptEquals("fr", vmd.SubtitleTracks.get(13).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(13).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(13).Default);
		assertOptEquals(CCDBLanguage.FRENCH, vmd.SubtitleTracks.get(13).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(14).Format);
		assertOptEquals("Galician", vmd.SubtitleTracks.get(14).Title);
		assertOptEquals("gl", vmd.SubtitleTracks.get(14).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(14).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(14).Default);
		assertOptEquals(CCDBLanguage.GALICIAN, vmd.SubtitleTracks.get(14).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(15).Format);
		assertOptEquals("Hebrew", vmd.SubtitleTracks.get(15).Title);
		assertOptEquals("he", vmd.SubtitleTracks.get(15).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(15).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(15).Default);
		assertOptEquals(CCDBLanguage.HEBREW, vmd.SubtitleTracks.get(15).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(16).Format);
		assertOptEquals("Croatian", vmd.SubtitleTracks.get(16).Title);
		assertOptEquals("hr", vmd.SubtitleTracks.get(16).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(16).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(16).Default);
		assertOptEquals(CCDBLanguage.CROATIAN, vmd.SubtitleTracks.get(16).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(17).Format);
		assertOptEquals("Hungarian", vmd.SubtitleTracks.get(17).Title);
		assertOptEquals("hu", vmd.SubtitleTracks.get(17).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(17).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(17).Default);
		assertOptEquals(CCDBLanguage.HUNGARIAN, vmd.SubtitleTracks.get(17).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(18).Format);
		assertOptEquals("Indonesian", vmd.SubtitleTracks.get(18).Title);
		assertOptEquals("id", vmd.SubtitleTracks.get(18).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(18).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(18).Default);
		assertOptEquals(CCDBLanguage.INDONESIAN, vmd.SubtitleTracks.get(18).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(19).Format);
		assertOptEquals("Italian", vmd.SubtitleTracks.get(19).Title);
		assertOptEquals("it", vmd.SubtitleTracks.get(19).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(19).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(19).Default);
		assertOptEquals(CCDBLanguage.ITALIAN, vmd.SubtitleTracks.get(19).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(20).Format);
		assertOptEquals("Japanese", vmd.SubtitleTracks.get(20).Title);
		assertOptEquals("ja", vmd.SubtitleTracks.get(20).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(20).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(20).Default);
		assertOptEquals(CCDBLanguage.JAPANESE, vmd.SubtitleTracks.get(20).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(21).Format);
		assertOptEquals("Korean", vmd.SubtitleTracks.get(21).Title);
		assertOptEquals("ko", vmd.SubtitleTracks.get(21).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(21).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(21).Default);
		assertOptEquals(CCDBLanguage.KOREAN, vmd.SubtitleTracks.get(21).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(22).Format);
		assertOptEquals("Malay (macrolanguage)", vmd.SubtitleTracks.get(22).Title);
		assertOptEquals("ms", vmd.SubtitleTracks.get(22).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(22).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(22).Default);
		assertOptEquals(CCDBLanguage.MALAY, vmd.SubtitleTracks.get(22).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(23).Format);
		assertOptEquals("Norwegian Bokmål", vmd.SubtitleTracks.get(23).Title);
		assertOptEquals("nb", vmd.SubtitleTracks.get(23).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(23).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(23).Default);
		assertOptEquals(CCDBLanguage.NORWEGIAN, vmd.SubtitleTracks.get(23).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(24).Format);
		assertOptEquals("Dutch", vmd.SubtitleTracks.get(24).Title);
		assertOptEquals("nl", vmd.SubtitleTracks.get(24).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(24).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(24).Default);
		assertOptEquals(CCDBLanguage.DUTCH, vmd.SubtitleTracks.get(24).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(25).Format);
		assertOptEquals("Polish", vmd.SubtitleTracks.get(25).Title);
		assertOptEquals("pl", vmd.SubtitleTracks.get(25).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(25).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(25).Default);
		assertOptEquals(CCDBLanguage.POLISH, vmd.SubtitleTracks.get(25).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(26).Format);
		assertOptEquals("Portuguese", vmd.SubtitleTracks.get(26).Title);
		assertOptEquals("pt", vmd.SubtitleTracks.get(26).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(26).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(26).Default);
		assertOptEquals(CCDBLanguage.PORTUGUESE, vmd.SubtitleTracks.get(26).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(27).Format);
		assertOptEquals("Portuguese", vmd.SubtitleTracks.get(27).Title);
		assertOptEquals("pt", vmd.SubtitleTracks.get(27).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(27).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(27).Default);
		assertOptEquals(CCDBLanguage.PORTUGUESE, vmd.SubtitleTracks.get(27).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(28).Format);
		assertOptEquals("Romanian", vmd.SubtitleTracks.get(28).Title);
		assertOptEquals("ro", vmd.SubtitleTracks.get(28).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(28).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(28).Default);
		assertOptEquals(CCDBLanguage.ROMANIAN, vmd.SubtitleTracks.get(28).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(29).Format);
		assertOptEquals("Russian", vmd.SubtitleTracks.get(29).Title);
		assertOptEquals("ru", vmd.SubtitleTracks.get(29).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(29).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(29).Default);
		assertOptEquals(CCDBLanguage.RUSSIAN, vmd.SubtitleTracks.get(29).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(30).Format);
		assertOptEquals("Swedish", vmd.SubtitleTracks.get(30).Title);
		assertOptEquals("sv", vmd.SubtitleTracks.get(30).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(30).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(30).Default);
		assertOptEquals(CCDBLanguage.SWEDISH, vmd.SubtitleTracks.get(30).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(31).Format);
		assertOptEquals("Thai", vmd.SubtitleTracks.get(31).Title);
		assertOptEquals("th", vmd.SubtitleTracks.get(31).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(31).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(31).Default);
		assertOptEquals(CCDBLanguage.THAI, vmd.SubtitleTracks.get(31).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(32).Format);
		assertOptEquals("Turkish", vmd.SubtitleTracks.get(32).Title);
		assertOptEquals("tr", vmd.SubtitleTracks.get(32).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(32).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(32).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(32).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(33).Format);
		assertOptEquals("Turkish", vmd.SubtitleTracks.get(33).Title);
		assertOptEquals("tr", vmd.SubtitleTracks.get(33).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(33).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(33).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(33).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(34).Format);
		assertOptEquals("Turkish (SDH)", vmd.SubtitleTracks.get(34).Title);
		assertOptEquals("tr", vmd.SubtitleTracks.get(34).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(34).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(34).Default);
		assertOptEquals(CCDBLanguage.TURKISH, vmd.SubtitleTracks.get(34).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(35).Format);
		assertOptEquals("Ukrainian", vmd.SubtitleTracks.get(35).Title);
		assertOptEquals("uk", vmd.SubtitleTracks.get(35).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(35).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(35).Default);
		assertOptEquals(CCDBLanguage.ENGLISH, vmd.SubtitleTracks.get(35).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(36).Format);
		assertOptEquals("Vietnamese", vmd.SubtitleTracks.get(36).Title);
		assertOptEquals("vi", vmd.SubtitleTracks.get(36).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(36).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(36).Default);
		assertOptEquals(CCDBLanguage.VIETNAMESE, vmd.SubtitleTracks.get(36).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(37).Format);
		assertOptEquals("Simplified, Chinese", vmd.SubtitleTracks.get(37).Title);
		assertOptEquals("zh", vmd.SubtitleTracks.get(37).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(37).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(37).Default);
		assertOptEquals(CCDBLanguage.CHINESE, vmd.SubtitleTracks.get(37).calcCCDBLanguage());

		assertOptEquals("UTF-8", vmd.SubtitleTracks.get(38).Format);
		assertOptEquals("Traditional, Chinese", vmd.SubtitleTracks.get(38).Title);
		assertOptEquals("zh", vmd.SubtitleTracks.get(38).Language);
		assertOptEquals("S_TEXT/UTF8", vmd.SubtitleTracks.get(38).CodecID);
		assertOptEquals(false, vmd.SubtitleTracks.get(38).Default);
		assertOptEquals(CCDBLanguage.CHINESE, vmd.SubtitleTracks.get(38).calcCCDBLanguage());

	}
}
