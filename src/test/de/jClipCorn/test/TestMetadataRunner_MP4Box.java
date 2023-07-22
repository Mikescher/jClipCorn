package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.metadata.mp4box.MP4BoxRunner;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestMetadataRunner_MP4Box extends ClipCornBaseTest {

	@Test
	public void testMetadataRunner_000_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "000";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(1500.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_001_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "001";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_002_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "002";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_003_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "003";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_004_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "004";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5500.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_005_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "005";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_006_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "006";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_007_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "007";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_008_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "008";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_009_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "009";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_010_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "010";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_011_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "011";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_012_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "012";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6806.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_013_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "013";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_014_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "014";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_015_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "015";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_016_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "016";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2402.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_017_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "017";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_018_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "018";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_019_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "019";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_020_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "020";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_021_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "021";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_022_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "022";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_023_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "023";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_024_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "024";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2604.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_025_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "025";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_026_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "026";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_027_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "027";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_028_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "028";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2717.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_029_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "029";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_030_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "030";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_031_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "031";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_032_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "032";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7088.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_033_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "033";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_034_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "034";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_035_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "035";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_036_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "036";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(2007.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_037_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "037";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_038_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "038";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_039_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "039";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_040_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "040";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(7508.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_041_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "041";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_042_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "042";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_043_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "043";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_044_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "044";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5690.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_045_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "045";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_046_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "046";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_047_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "047";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_048_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "048";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6480.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_049_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "049";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_050_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "050";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_051_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "051";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_052_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "052";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5452.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_053_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "053";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_054_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "054";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_055_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "055";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_056_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "056";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(5632.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_057_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "057";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_058_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "058";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_059_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "059";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_060_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "060";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6042.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_061_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "061";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_062_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "062";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEmpty(vmd.Width);
		assertOptEmpty(vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

	@Test
	public void testMetadataRunner_063_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "063";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);
		assertOptEquals(new CCFileSize(1073741824), vmd.Filesize);
		assertOptEquals(fvhash, vmd.Checksum);

		assertOptEquals(6300.0, vmd.Duration);
		assertOptEmpty(vmd.Bitrate);

		assertOptEmpty(vmd.VideoFormat);
		assertOptEquals(128, vmd.Width);
		assertOptEquals(96, vmd.Height);
		assertOptEmpty(vmd.Framerate);
		assertOptEmpty(vmd.Bitdepth);
		assertOptEmpty(vmd.Framecount);
		assertOptEmpty(vmd.VideoCodec);

		assertOptEmpty(vmd.AudioFormat);
		assertOptEmpty(vmd.AudioChannels);
		assertOptEmpty(vmd.AudioCodec);
		assertOptEmpty(vmd.AudioSamplerate);
	}

}
