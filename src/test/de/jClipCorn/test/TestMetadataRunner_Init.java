package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCDBLanguage;
import de.jClipCorn.features.metadata.VideoMetadata;
import de.jClipCorn.features.metadata.impl.FFProbeFastRunner;
import de.jClipCorn.features.metadata.impl.FFProbeFullRunner;
import de.jClipCorn.features.metadata.impl.MP4BoxRunner;
import de.jClipCorn.features.metadata.impl.MediaInfoRunner;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.ErrOpt;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.xml.CCXMLParser;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("nls")
public class TestMetadataRunner_Init extends ClipCornBaseTest {

	private final static int MAX_ID = 65;
	
	private <TData, TError> void printErrOptAssert(ErrOpt<TData, TError> v, String key) {
		if (v.isEmpty()) {
			System.out.println(Str.format("	assertOptEmpty({0});", key));
		} else if (v.isError()) {
			System.out.println(Str.format("	assertOptError({0});", key));
		} else {
			if (v.get() instanceof String) {
				System.out.println(Str.format("	assertOptEquals(\"{0}\", {1});", v.get().toString(), key));
			} else if (v.get() instanceof Long) {
				System.out.println(Str.format("	assertOptEquals({0}L, {1});", v.get().toString(), key));
			} else if (v.get() instanceof Short) {
				System.out.println(Str.format("	assertOptEquals((short){0}, {1});", v.get().toString(), key));
			} else if (v.get() instanceof CCDBLanguage) {
				System.out.println(Str.format("	assertOptEquals(CCDBLanguage.{0}, {1});", v.get().toString(), key));
			} else {
				System.out.println(Str.format("	assertOptEquals({0}, {1});", v.get().toString(), key));
			}
		}
	}

	private void printVideoMetadataAsserts(String filename, String sourcetype, VideoMetadata vmd) {
		System.out.println(Str.format("	assertEquals({0}, {1});", sourcetype, "vmd.SourceType"));
		System.out.println(Str.format("	assertEquals(FSPath.create(\"{0}\"), {1});", filename, "vmd.InputFile"));

		System.out.println(Str.format(""));

		printErrOptAssert(vmd.CDate, "vmd.CDate");
		printErrOptAssert(vmd.MDate, "vmd.MDate");

		System.out.println(Str.format(""));

		printErrOptAssert(vmd.Checksum, "vmd.Checksum");

		System.out.println(Str.format(""));

		printErrOptAssert(vmd.Format,         "vmd.Format");
		printErrOptAssert(vmd.Format_Version, "vmd.Format_Version");
		printErrOptAssert(vmd.FileSize,       "vmd.FileSize");
		printErrOptAssert(vmd.Duration,       "vmd.Duration");
		printErrOptAssert(vmd.OverallBitRate, "vmd.OverallBitRate");
		printErrOptAssert(vmd.FrameRate,      "vmd.FrameRate");

		System.out.println(Str.format(""));

		printErrOptAssert(vmd.getTotalBitrate(), "vmd.getTotalBitrate()");

		System.out.println(Str.format(""));

		System.out.println(Str.format("	assertEquals(\"{0}\", {1});", vmd.getValidAudioLanguages().serializeToFilenameString(), "vmd.getValidAudioLanguages().serializeToFilenameString()"));
		System.out.println(Str.format("	assertEquals(\"{0}\", {1});", vmd.getValidSubtitleLanguages().serializeToString(),      "vmd.getValidSubtitleLanguages().serializeToString()"));

		System.out.println(Str.format(""));

		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.hasEmptyAudioLanguages(), "vmd.hasEmptyAudioLanguages()"));
		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.hasErrorAudioLanguages(), "vmd.hasErrorAudioLanguages()"));
		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.hasEmptySubtitleLanguages(), "vmd.hasEmptySubtitleLanguages()"));
		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.hasErrorSubtitleLanguages(), "vmd.hasErrorSubtitleLanguages()"));

		System.out.println(Str.format(""));

		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.VideoTracks.size(),    "vmd.VideoTracks.size()"));
		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.AudioTracks.size(),    "vmd.AudioTracks.size()"));
		System.out.println(Str.format("	assertEquals({0}, {1});", vmd.SubtitleTracks.size(), "vmd.SubtitleTracks.size()"));

		System.out.println(Str.format(""));

		if (vmd.getDefaultVideoTrack().isEmpty())
		{
			System.out.println(Str.format("	assertOptEmpty({0});", "vmd.getDefaultVideoTrack()"));
		}
		else
		{
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format),         "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile), "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Format_Profile)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID),        "vmd.getDefaultVideoTrack().flatErrMap(p -> p.CodecID)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate),        "vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRate)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal), "vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitRateNominal)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width),          "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Width)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height),         "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Height)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate),      "vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameRate)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount),     "vmd.getDefaultVideoTrack().flatErrMap(p -> p.FrameCount)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth),       "vmd.getDefaultVideoTrack().flatErrMap(p -> p.BitDepth)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration),       "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Duration)");
			printErrOptAssert(vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default),        "vmd.getDefaultVideoTrack().flatErrMap(p -> p.Default)");
		}

		System.out.println(Str.format(""));

		if (vmd.getDefaultAudioTrack().isEmpty())
		{
			System.out.println(Str.format("	assertOptEmpty({0});", "vmd.getDefaultAudioTrack()"));
		}
		else
		{
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format),         "vmd.getDefaultAudioTrack().flatErrMap(p -> p.Format)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language),       "vmd.getDefaultAudioTrack().flatErrMap(p -> p.Language)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID),        "vmd.getDefaultAudioTrack().flatErrMap(p -> p.CodecID)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels),       "vmd.getDefaultAudioTrack().flatErrMap(p -> p.Channels)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate),   "vmd.getDefaultAudioTrack().flatErrMap(p -> p.Samplingrate)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate),        "vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRate)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal), "vmd.getDefaultAudioTrack().flatErrMap(p -> p.BitRateNominal)");
			printErrOptAssert(vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default),        "vmd.getDefaultAudioTrack().flatErrMap(p -> p.Default)");
		}

		System.out.println(Str.format(""));

		for (int j = 0; j < vmd.VideoTracks.size(); j++)
		{
			printErrOptAssert(vmd.VideoTracks.get(j).Format,             "vmd.VideoTracks.get("+j+").Format");
			printErrOptAssert(vmd.VideoTracks.get(j).Format_Profile,     "vmd.VideoTracks.get("+j+").Format_Profile");
			printErrOptAssert(vmd.VideoTracks.get(j).CodecID,            "vmd.VideoTracks.get("+j+").CodecID");
			printErrOptAssert(vmd.VideoTracks.get(j).BitRate,            "vmd.VideoTracks.get("+j+").BitRate");
			printErrOptAssert(vmd.VideoTracks.get(j).BitRateNominal,     "vmd.VideoTracks.get("+j+").BitRateNominal");
			printErrOptAssert(vmd.VideoTracks.get(j).Width,              "vmd.VideoTracks.get("+j+").Width");
			printErrOptAssert(vmd.VideoTracks.get(j).Height,             "vmd.VideoTracks.get("+j+").Height");
			printErrOptAssert(vmd.VideoTracks.get(j).FrameRate,          "vmd.VideoTracks.get("+j+").FrameRate");
			printErrOptAssert(vmd.VideoTracks.get(j).FrameCount,         "vmd.VideoTracks.get("+j+").FrameCount");
			printErrOptAssert(vmd.VideoTracks.get(j).BitDepth,           "vmd.VideoTracks.get("+j+").BitDepth");
			printErrOptAssert(vmd.VideoTracks.get(j).Duration,           "vmd.VideoTracks.get("+j+").Duration");
			printErrOptAssert(vmd.VideoTracks.get(j).Default,            "vmd.VideoTracks.get("+j+").Default");
			printErrOptAssert(vmd.VideoTracks.get(j).calcCCDBLanguage(), "vmd.AudioTracks.get("+j+").calcCCDBLanguage()");

			System.out.println(Str.format(""));
		}

		for (int j = 0; j < vmd.AudioTracks.size(); j++)
		{
			printErrOptAssert(vmd.AudioTracks.get(j).Format,             "vmd.AudioTracks.get("+j+").Format");
			printErrOptAssert(vmd.AudioTracks.get(j).Title,              "vmd.AudioTracks.get("+j+").Title");
			printErrOptAssert(vmd.AudioTracks.get(j).Language,           "vmd.AudioTracks.get("+j+").Language");
			printErrOptAssert(vmd.AudioTracks.get(j).CodecID,            "vmd.AudioTracks.get("+j+").CodecID");
			printErrOptAssert(vmd.AudioTracks.get(j).Channels,           "vmd.AudioTracks.get("+j+").Channels");
			printErrOptAssert(vmd.AudioTracks.get(j).Samplingrate,       "vmd.AudioTracks.get("+j+").Samplingrate");
			printErrOptAssert(vmd.AudioTracks.get(j).BitRate,            "vmd.AudioTracks.get("+j+").BitRate");
			printErrOptAssert(vmd.AudioTracks.get(j).BitRateNominal,     "vmd.AudioTracks.get("+j+").BitRateNominal");
			printErrOptAssert(vmd.AudioTracks.get(j).Default,            "vmd.AudioTracks.get("+j+").Default");
			printErrOptAssert(vmd.AudioTracks.get(j).calcCCDBLanguage(), "vmd.AudioTracks.get("+j+").calcCCDBLanguage()");

			System.out.println(Str.format(""));
		}

		for (int j = 0; j < vmd.SubtitleTracks.size(); j++)
		{
			printErrOptAssert(vmd.SubtitleTracks.get(j).Format,             "vmd.SubtitleTracks.get("+j+").Format");
			printErrOptAssert(vmd.SubtitleTracks.get(j).Title,              "vmd.SubtitleTracks.get("+j+").Title");
			printErrOptAssert(vmd.SubtitleTracks.get(j).Language,           "vmd.SubtitleTracks.get("+j+").Language");
			printErrOptAssert(vmd.SubtitleTracks.get(j).CodecID,            "vmd.SubtitleTracks.get("+j+").CodecID");
			printErrOptAssert(vmd.SubtitleTracks.get(j).Default,            "vmd.SubtitleTracks.get("+j+").Default");
			printErrOptAssert(vmd.SubtitleTracks.get(j).calcCCDBLanguage(), "vmd.SubtitleTracks.get("+j+").calcCCDBLanguage()");

			System.out.println(Str.format(""));
		}
	}

	//@Test
	public void initMetadataRunner__mediainfo() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i <= MAX_ID; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var filename = "/temp/"+ CCXMLParser.parse(SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class)).getRoot().getFirstChildOrThrow("media").getAttributeValueOrThrow("ref");
			var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create(filename));

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_mediainfo() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".mediainfo.xml\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new MediaInfoRunner(ml).parse(out, fvhash, attr, FSPath.create(\"{0}\"));", filename));
			System.out.println(Str.format(""));

			printVideoMetadataAsserts(filename, "MetadataSourceType.MEDIAINFO", vmd);

			System.out.println("}");

			System.out.println();
		}
	}

	//@Test
	public void initMetadataRunner__ffprobefast() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i <= MAX_ID; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.fast.json", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var filename = "/temp/"+ CCXMLParser.parse(SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class)).getRoot().getFirstChildOrThrow("media").getAttributeValueOrThrow("ref");
			var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create(filename));

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_ffprobefast() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".ffprobe.fast.json\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new FFProbeFastRunner(ml).parse(out, fvhash, attr, FSPath.create(\"{0}\"));", filename));
			System.out.println(Str.format(""));

			printVideoMetadataAsserts(filename, "MetadataSourceType.FFPROBE_FAST", vmd);

			System.out.println("}");

			System.out.println();
		}
	}

	//@Test
	public void initMetadataRunner__ffprobefull() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i <= MAX_ID; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".ffprobe.full.json", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var filename = "/temp/"+ CCXMLParser.parse(SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class)).getRoot().getFirstChildOrThrow("media").getAttributeValueOrThrow("ref");
			var vmd = new FFProbeFullRunner(ml).parse(out, fvhash, attr, FSPath.create(filename));

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_ffprobefull() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".ffprobe.full.json\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new FFProbeFullRunner(ml).parse(out, fvhash, attr, FSPath.create(\"{0}\"));", filename));
			System.out.println(Str.format(""));

			printVideoMetadataAsserts(filename, "MetadataSourceType.FFPROBE_FULL", vmd);

			System.out.println("}");

			System.out.println();
		}
	}

	//@Test
	public void initMetadataRunner__mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i <= MAX_ID; i++) {

			var id = StringUtils.leftPad(String.valueOf(i), 3, '0');

			var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
			var fvhash = dummyFVH();
			var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
			var filename = "/temp/"+ CCXMLParser.parse(SimpleFileUtils.readTextResource("/media/demo_"+id+".mediainfo.xml", TestMetadataRunner_Init.class)).getRoot().getFirstChildOrThrow("media").getAttributeValueOrThrow("ref");
			var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create(filename));

			System.out.println(Str.format("@Test"));
			System.out.println(Str.format("public void testMetadataRunner_{0}_mp4box() throws Exception ", id) + "{");
			System.out.println(Str.format("	CCMovieList ml = createEmptyDB();"));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var id = \"{0}\";", id));
			System.out.println(Str.format(""));
			System.out.println(Str.format("	var out = SimpleFileUtils.readTextResource(\"/media/demo_\"+id+\".mp4box.txt\", ClipCornBaseTest.class);"));
			System.out.println(Str.format("	var fvhash = dummyFVH();"));
			System.out.println(Str.format("	var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);"));
			System.out.println(Str.format("	var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create(\"{0}\"));", filename));
			System.out.println(Str.format(""));

			printVideoMetadataAsserts(filename, "MetadataSourceType.MP4BOX", vmd);

			System.out.println("}");

			System.out.println();
		}
	}
}
