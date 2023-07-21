package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestMetadataRunner_PartialMediaInfo extends ClipCornBaseTest
{
	@Test
	public void testMetadataRunner_000_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "000";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1166926), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(1500.0, vmd.Duration);
		assertOptEquals(5865, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(37500, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_001_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "001";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1545782), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2500.003, vmd.Duration);
		assertOptEquals(4946, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(62500, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_002_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "002";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(12170130), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(3500.04, vmd.Duration);
		assertOptEquals(27817, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(87501, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_003_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "003";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2826740), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4500.003, vmd.Duration);
		assertOptEquals(5025, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(112500, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_004_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "004";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4230401), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5500.0, vmd.Duration);
		assertOptEquals(5835, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(137500, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_005_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "005";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4097053), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6505.003, vmd.Duration);
		assertOptEquals(5039, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(162625, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_006_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "006";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(21899356), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7505.04, vmd.Duration);
		assertOptEquals(23344, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(187626, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_007_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "007";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5310534), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8505.003, vmd.Duration);
		assertOptEquals(4995, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(212625, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_008_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "008";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(17879040), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5704.96, vmd.Duration);
		assertOptEquals(25072, vmd.Bitrate);

		assertOptEquals("MPEG Video", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(142624, vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_009_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "009";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4538071), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7207.003, vmd.Duration);
		assertOptEquals(5037, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(180175, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_010_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "010";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(7712768), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2402.0, vmd.Duration);
		assertOptEquals(25688, vmd.Bitrate);

		assertOptEquals("MPEG Video", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(60050, vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_011_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "011";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2799712), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4865, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(115100, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_012_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "012";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5191928), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6806.0, vmd.Duration);
		assertOptEquals(5787, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(170150, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_013_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "013";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5265388), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4951, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(212700, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_014_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "014";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(20032670), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5705.04, vmd.Duration);
		assertOptEquals(28091, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(142626, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_015_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "015";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4476805), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7207.003, vmd.Duration);
		assertOptEquals(4969, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(180175, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_016_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "016";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1851902), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2402.0, vmd.Duration);
		assertOptEquals(5830, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(60050, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_017_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "017";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2845801), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4604.003, vmd.Duration);
		assertOptEquals(4945, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(115100, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_018_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "018";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(19367774), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6806.041, vmd.Duration);
		assertOptEquals(22765, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(170151, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_019_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "019";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5299418), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8508.003, vmd.Duration);
		assertOptEquals(4983, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(212700, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_020_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "020";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(9752591), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5015.04, vmd.Duration);
		assertOptEquals(15557, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(125376, vmd.Framecount);
		assertOptEquals("MP43", vmd.VideoCodec);

		assertOptEquals("WMA", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("161", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_021_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "021";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2286180), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(3700.003, vmd.Duration);
		assertOptEquals(4943, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(92500, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_022_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "022";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(18204592), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5207.04, vmd.Duration);
		assertOptEquals(27969, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(130176, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_023_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "023";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4643238), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7402.003, vmd.Duration);
		assertOptEquals(5018, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(185050, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_024_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "024";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2009659), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2604.0, vmd.Duration);
		assertOptEquals(5839, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(65100, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_025_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "025";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3060221), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4856.003, vmd.Duration);
		assertOptEquals(5042, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(121400, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_026_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "026";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(19206248), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6508.04, vmd.Duration);
		assertOptEquals(23609, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(162701, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_027_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "027";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5478633), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8755.003, vmd.Duration);
		assertOptEquals(5006, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(218875, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_028_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "028";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2111657), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2717.0, vmd.Duration);
		assertOptEquals(5884, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(67925, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_029_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "029";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2666207), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4217.003, vmd.Duration);
		assertOptEquals(5058, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(105425, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_030_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "030";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(10926078), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(3642.04, vmd.Duration);
		assertOptEquals(24000, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(91051, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_031_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "031";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3620277), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5866.003, vmd.Duration);
		assertOptEquals(4937, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(146650, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_032_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "032";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5462448), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7088.0, vmd.Duration);
		assertOptEquals(5850, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(177200, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_033_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "033";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2827187), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4500.003, vmd.Duration);
		assertOptEquals(5026, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(112500, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_034_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "034";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(23519088), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6505.04, vmd.Duration);
		assertOptEquals(28924, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(162626, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_035_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "035";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5478741), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8705.003, vmd.Duration);
		assertOptEquals(5035, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(217625, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_036_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "036";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1564142), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2007.0, vmd.Duration);
		assertOptEquals(5890, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(50175, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_037_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "037";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2507615), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4000.003, vmd.Duration);
		assertOptEquals(5015, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(100000, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_038_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "038";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(10666994), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(3600.04, vmd.Duration);
		assertOptEquals(23704, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(90001, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_039_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "039";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3694402), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5856.003, vmd.Duration);
		assertOptEquals(5047, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(146400, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_040_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "040";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5764442), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7508.0, vmd.Duration);
		assertOptEquals(5828, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(187700, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_041_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "041";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1569834), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2559.003, vmd.Duration);
		assertOptEquals(4908, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(63975, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_042_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "042";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(15852724), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4571.04, vmd.Duration);
		assertOptEquals(27745, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(114276, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_043_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "043";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5318606), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8547.003, vmd.Duration);
		assertOptEquals(4978, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(213675, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_044_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "044";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4337056), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5690.0, vmd.Duration);
		assertOptEquals(5780, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(142250, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_045_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "045";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3625309), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5801.003, vmd.Duration);
		assertOptEquals(5000, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(145025, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_046_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "046";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(14860990), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5093.04, vmd.Duration);
		assertOptEquals(23343, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(127326, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_047_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "047";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(2777136), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(4470.003, vmd.Duration);
		assertOptEquals(4970, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(111750, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_048_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "048";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4987558), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6480.0, vmd.Duration);
		assertOptEquals(5841, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(162000, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_049_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "049";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(5314202), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(8496.003, vmd.Duration);
		assertOptEquals(5004, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(212400, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_050_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "050";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(16583294), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5508.04, vmd.Duration);
		assertOptEquals(24086, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(137701, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_051_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "051";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3404795), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5519.003, vmd.Duration);
		assertOptEquals(4935, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(137975, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_052_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "052";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4204652), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5452.0, vmd.Duration);
		assertOptEquals(5851, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(136300, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_053_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "053";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3475277), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5537.003, vmd.Duration);
		assertOptEquals(5021, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(138425, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_054_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "054";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(27418084), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7540.04, vmd.Duration);
		assertOptEquals(29091, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(188501, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_055_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "055";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1611470), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2551.003, vmd.Duration);
		assertOptEquals(5054, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(63775, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_056_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "056";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4369026), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5632.0, vmd.Duration);
		assertOptEquals(5888, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(140800, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_057_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "057";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3604423), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5755.003, vmd.Duration);
		assertOptEquals(5010, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(143875, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_058_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "058";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(17357324), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5817.04, vmd.Duration);
		assertOptEquals(23871, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(145426, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_059_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "059";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3733892), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5917.003, vmd.Duration);
		assertOptEquals(5048, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(147925, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_060_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "060";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4675950), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6042.0, vmd.Duration);
		assertOptEquals(5874, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(151050, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_061_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "061";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(3809307), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6166.003, vmd.Duration);
		assertOptEquals(4942, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(154150, vmd.Framecount);
		assertOptEquals("V_MPEG4/ISO/AVC", vmd.VideoCodec);

		assertOptEquals("Vorbis", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("A_VORBIS", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_062_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "062";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(21916794), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6288.04, vmd.Duration);
		assertOptEquals(27884, vmd.Bitrate);

		assertOptEquals("MPEG-4 Visual", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(157201, vmd.Framecount);
		assertOptEquals("FMP4", vmd.VideoCodec);

		assertOptEquals("MPEG Audio", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("55", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_063_mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "063";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create("/tmp")).toPartialMediaInfo();

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(4866337), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6300.0, vmd.Duration);
		assertOptEquals(5863, vmd.Bitrate);

		assertOptEquals("AVC", vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEquals(25.0, vmd.Framerate);
		assertOptEquals((short)8, vmd.Bitdepth);
		assertOptEquals(157500, vmd.Framecount);
		assertOptEquals("avc1", vmd.VideoCodec);

		assertOptEquals("AAC", vmd.AudioFormat);
		assertOptEquals((short)2, vmd.AudioChannels);
		assertOptEquals("mp4a-40-2", vmd.AudioCodec);
		assertOptEquals(44100, vmd.AudioSamplerate);
	}
}
