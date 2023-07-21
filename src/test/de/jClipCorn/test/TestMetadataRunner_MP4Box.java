package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.metadata.MetadataSourceType;
import de.jClipCorn.features.metadata.impl.MP4BoxRunner;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

@SuppressWarnings("nls")
public class TestMetadataRunner_MP4Box extends ClipCornBaseTest
{
	@Test
	public void testMetadataRunner_000_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "000";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_000.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_000.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(1500.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_001_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "001";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_001.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_001.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_002_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "002";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_002.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_002.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_003_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "003";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_003.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_003.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_004_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "004";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_004.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_004.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(5500.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_005_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "005";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_005.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_005.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_006_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "006";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_006.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_006.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_007_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "007";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_007.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_007.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_008_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "008";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_008.mpeg"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_008.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_009_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "009";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_009.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_009.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_010_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "010";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_010.mpeg"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_010.mpeg"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_011_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "011";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_011.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_011.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_012_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "012";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_012.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_012.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(6806.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_013_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "013";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_013.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_013.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_014_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "014";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_014.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_014.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_015_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "015";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_015.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_015.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_016_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "016";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_016.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_016.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(2402.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_017_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "017";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_017.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_017.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_018_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "018";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_018.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_018.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_019_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "019";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_019.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_019.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_020_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "020";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_020.wmv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_020.wmv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_021_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "021";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_021.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_021.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_022_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "022";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_022.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_022.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_023_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "023";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_023.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_023.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_024_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "024";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_024.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_024.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(2604.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_025_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "025";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_025.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_025.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_026_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "026";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_026.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_026.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_027_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "027";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_027.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_027.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_028_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "028";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_028.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_028.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(2717.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_029_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "029";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_029.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_029.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_030_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "030";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_030.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_030.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_031_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "031";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_031.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_031.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_032_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "032";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_032.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_032.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(7088.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_033_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "033";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_033.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_033.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_034_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "034";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_034.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_034.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_035_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "035";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_035.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_035.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_036_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "036";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_036.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_036.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(2007.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_037_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "037";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_037.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_037.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_038_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "038";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_038.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_038.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_039_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "039";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_039.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_039.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_040_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "040";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_040.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_040.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(7508.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_041_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "041";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_041.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_041.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_042_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "042";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_042.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_042.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_043_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "043";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_043.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_043.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_044_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "044";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_044.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_044.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(5690.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_045_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "045";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_045.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_045.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_046_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "046";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_046.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_046.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_047_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "047";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_047.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_047.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_048_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "048";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_048.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_048.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(6480.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_049_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "049";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_049.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_049.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_050_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "050";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_050.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_050.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_051_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "051";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_051.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_051.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_052_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "052";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_052.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_052.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(5452.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_053_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "053";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_053.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_053.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_054_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "054";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_054.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_054.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_055_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "055";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_055.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_055.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_056_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "056";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_056.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_056.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(5632.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_057_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "057";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_057.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_057.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_058_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "058";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_058.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_058.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_059_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "059";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_059.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_059.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_060_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "060";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_060.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_060.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(6042.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_061_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "061";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_061.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_061.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_062_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "062";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_062.avi"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_062.avi"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_063_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "063";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_063.mp4"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_063.mp4"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEquals(6300.0, vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_064_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "064";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/demo_064.mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/demo_064.mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}

	@Test
	public void testMetadataRunner_065_mp4box() throws Exception {
		CCMovieList ml = createEmptyDB();

		var id = "065";

		var out = SimpleFileUtils.readTextResource("/media/demo_"+id+".mp4box.txt", ClipCornBaseTest.class);
		var fvhash = dummyFVH();
		var attr = dummyFileAttr(946681200, 978303600, 1009839600, 1073741824);
		var vmd = new MP4BoxRunner(ml).parse(out, fvhash, attr, FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"));

		assertEquals(MetadataSourceType.MP4BOX, vmd.SourceType);
		assertEquals(FSPath.create("/temp/The Pale Blue Eye [GER+ENG].mkv"), vmd.InputFile);

		assertOptEquals(946681200L, vmd.CDate);
		assertOptEquals(978303600L, vmd.MDate);

		assertOptEquals("[02-0000000000-1-0F:0F:0F:0F:0F:0F:0F:0F]", vmd.Checksum);

		assertOptEmpty(vmd.Format);
		assertOptEmpty(vmd.Format_Version);
		assertOptEmpty(vmd.FileSize);
		assertOptEmpty(vmd.Duration);
		assertOptEmpty(vmd.OverallBitRate);
		assertOptEmpty(vmd.FrameRate);

		assertOptEmpty(vmd.getTotalBitrate());

		assertEquals("", vmd.getValidAudioLanguages().serializeToFilenameString());
		assertEquals("", vmd.getValidSubtitleLanguages().serializeToString());

		assertEquals(false, vmd.hasEmptyAudioLanguages());
		assertEquals(false, vmd.hasErrorAudioLanguages());
		assertEquals(false, vmd.hasEmptySubtitleLanguages());
		assertEquals(false, vmd.hasErrorSubtitleLanguages());

		assertEquals(0, vmd.VideoTracks.size());
		assertEquals(0, vmd.AudioTracks.size());
		assertEquals(0, vmd.SubtitleTracks.size());

		assertOptEmpty(vmd.getDefaultVideoTrack());

		assertOptEmpty(vmd.getDefaultAudioTrack());

	}
}
