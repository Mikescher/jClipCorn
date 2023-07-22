package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.ffprobe.FFProbeFastRunner;
import de.jClipCorn.features.metadata.ffprobe.FFProbeFullRunner;
import de.jClipCorn.features.metadata.mediaquery.MediaQueryRunner;
import de.jClipCorn.features.metadata.mp4box.MP4BoxRunner;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestMetadataRunner_Init extends ClipCornBaseTest {

	//@Test
	public void initMetadataRunner__mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 64; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var vmd = new MediaQueryRunner(ml).parse(out, fvhash, attr, false).toPartial();

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_mediainfo() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".mediainfo.xml\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new MediaQueryRunner(ml).parse(out, fvhash, attr, false).toPartial();"));
			System.out.println(Str.format(""));

			System.out.println(Str.format("	assertOptEquals({0}L, vmd.CDate);", vmd.CDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals({0}L, vmd.MDate);", vmd.MDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals(new CCFileSize({0}), vmd.Filesize);", Long.toString(vmd.Filesize.get().getBytes())));
			System.out.println(Str.format("	assertOptEquals(fvhash, vmd.Checksum);"));

			System.out.println(Str.format(""));

			if (vmd.Duration.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Duration);", vmd.Duration.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Duration);"));

			if (vmd.Bitrate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Bitrate);", vmd.Bitrate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitrate);"));

			System.out.println(Str.format(""));

			if (vmd.VideoFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoFormat);", vmd.VideoFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoFormat);"));

			if (vmd.Width.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Width);", vmd.Width.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Width);"));

			if (vmd.Height.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Height);", vmd.Height.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Height);"));

			if (vmd.Framerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framerate);", vmd.Framerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framerate);"));

			if (vmd.Bitdepth.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.Bitdepth);", vmd.Bitdepth.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitdepth);"));

			if (vmd.Framecount.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framecount);", vmd.Framecount.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framecount);"));

			if (vmd.VideoCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoCodec);", vmd.VideoCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoCodec);"));

			System.out.println(Str.format(""));

			if (vmd.AudioFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioFormat);", vmd.AudioFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioFormat);"));

			if (vmd.AudioChannels.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.AudioChannels);", vmd.AudioChannels.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioChannels);"));

			if (vmd.AudioCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioCodec);", vmd.AudioCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioCodec);"));

			if (vmd.AudioSamplerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.AudioSamplerate);", vmd.AudioSamplerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioSamplerate);"));

			System.out.println("}");

			System.out.println("");
		}
	}

	//@Test
	public void initMetadataRunner__ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 64; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr).toPartial();

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_ffprobefast() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".ffprobe.fast.json\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr).toPartial();"));
			System.out.println(Str.format(""));

			System.out.println(Str.format("	assertOptEquals({0}L, vmd.CDate);", vmd.CDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals({0}L, vmd.MDate);", vmd.MDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals(new CCFileSize({0}), vmd.Filesize);", Long.toString(vmd.Filesize.get().getBytes())));
			System.out.println(Str.format("	assertOptEquals(fvhash, vmd.Checksum);"));

			System.out.println(Str.format(""));

			if (vmd.Duration.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Duration);", vmd.Duration.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Duration);"));

			if (vmd.Bitrate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Bitrate);", vmd.Bitrate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitrate);"));

			System.out.println(Str.format(""));

			if (vmd.VideoFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoFormat);", vmd.VideoFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoFormat);"));

			if (vmd.Width.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Width);", vmd.Width.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Width);"));

			if (vmd.Height.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Height);", vmd.Height.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Height);"));

			if (vmd.Framerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framerate);", vmd.Framerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framerate);"));

			if (vmd.Bitdepth.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.Bitdepth);", vmd.Bitdepth.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitdepth);"));

			if (vmd.Framecount.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framecount);", vmd.Framecount.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framecount);"));

			if (vmd.VideoCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoCodec);", vmd.VideoCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoCodec);"));

			System.out.println(Str.format(""));

			if (vmd.AudioFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioFormat);", vmd.AudioFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioFormat);"));

			if (vmd.AudioChannels.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.AudioChannels);", vmd.AudioChannels.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioChannels);"));

			if (vmd.AudioCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioCodec);", vmd.AudioCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioCodec);"));

			if (vmd.AudioSamplerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.AudioSamplerate);", vmd.AudioSamplerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioSamplerate);"));

			System.out.println("}");

			System.out.println("");
		}
	}

	//@Test
	public void initMetadataRunner__ffprobefull() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 64; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.full.json", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var vmd = new FFProbeFullRunner(ml).parse(out, fvhash, attr).toPartial();

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_ffprobefast() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".ffprobe.full.json\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new FFProbeFullRunner(ml).parse(out, fvhash, attr).toPartial();"));
			System.out.println(Str.format(""));

			System.out.println(Str.format("	assertOptEquals({0}L, vmd.CDate);", vmd.CDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals({0}L, vmd.MDate);", vmd.MDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals(new CCFileSize({0}), vmd.Filesize);", Long.toString(vmd.Filesize.get().getBytes())));
			System.out.println(Str.format("	assertOptEquals(fvhash, vmd.Checksum);"));

			System.out.println(Str.format(""));

			if (vmd.Duration.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Duration);", vmd.Duration.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Duration);"));

			if (vmd.Bitrate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Bitrate);", vmd.Bitrate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitrate);"));

			System.out.println(Str.format(""));

			if (vmd.VideoFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoFormat);", vmd.VideoFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoFormat);"));

			if (vmd.Width.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Width);", vmd.Width.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Width);"));

			if (vmd.Height.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Height);", vmd.Height.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Height);"));

			if (vmd.Framerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framerate);", vmd.Framerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framerate);"));

			if (vmd.Bitdepth.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.Bitdepth);", vmd.Bitdepth.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitdepth);"));

			if (vmd.Framecount.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framecount);", vmd.Framecount.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framecount);"));

			if (vmd.VideoCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoCodec);", vmd.VideoCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoCodec);"));

			System.out.println(Str.format(""));

			if (vmd.AudioFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioFormat);", vmd.AudioFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioFormat);"));

			if (vmd.AudioChannels.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.AudioChannels);", vmd.AudioChannels.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioChannels);"));

			if (vmd.AudioCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioCodec);", vmd.AudioCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioCodec);"));

			if (vmd.AudioSamplerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.AudioSamplerate);", vmd.AudioSamplerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioSamplerate);"));

			System.out.println("}");

			System.out.println("");
		}
	}

	@Test
	public void initMetadataRunner__mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 64; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_mp4box() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".mp4box.txt\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr);"));
			System.out.println(Str.format(""));

			System.out.println(Str.format("	assertOptEquals({0}L, vmd.CDate);", vmd.CDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals({0}L, vmd.MDate);", vmd.MDate.get().toString()));
			System.out.println(Str.format("	assertOptEquals(new CCFileSize({0}), vmd.Filesize);", Long.toString(vmd.Filesize.get().getBytes())));
			System.out.println(Str.format("	assertOptEquals(fvhash, vmd.Checksum);"));

			System.out.println(Str.format(""));

			if (vmd.Duration.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Duration);", vmd.Duration.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Duration);"));

			if (vmd.Bitrate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Bitrate);", vmd.Bitrate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitrate);"));

			System.out.println(Str.format(""));

			if (vmd.VideoFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoFormat);", vmd.VideoFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoFormat);"));

			if (vmd.Width.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Width);", vmd.Width.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Width);"));

			if (vmd.Height.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Height);", vmd.Height.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Height);"));

			if (vmd.Framerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framerate);", vmd.Framerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framerate);"));

			if (vmd.Bitdepth.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.Bitdepth);", vmd.Bitdepth.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Bitdepth);"));

			if (vmd.Framecount.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.Framecount);", vmd.Framecount.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.Framecount);"));

			if (vmd.VideoCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.VideoCodec);", vmd.VideoCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.VideoCodec);"));

			System.out.println(Str.format(""));

			if (vmd.AudioFormat.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioFormat);", vmd.AudioFormat.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioFormat);"));

			if (vmd.AudioChannels.isPresent())
				System.out.println(Str.format("	assertOptEquals((short){0}, vmd.AudioChannels);", vmd.AudioChannels.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioChannels);"));

			if (vmd.AudioCodec.isPresent())
				System.out.println(Str.format("	assertOptEquals(\"{0}\", vmd.AudioCodec);", vmd.AudioCodec.get()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioCodec);"));

			if (vmd.AudioSamplerate.isPresent())
				System.out.println(Str.format("	assertOptEquals({0}, vmd.AudioSamplerate);", vmd.AudioSamplerate.get().toString()));
			else
				System.out.println(Str.format("	assertOptEmpty(vmd.AudioSamplerate);"));

			System.out.println("}");

			System.out.println("");
		}
	}

}
