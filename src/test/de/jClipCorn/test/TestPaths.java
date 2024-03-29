package de.jClipCorn.test;

import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.helper.ApplicationHelper;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestPaths extends ClipCornBaseTest {

	@Test
	public void testFSPath() {
		var ccprops = createInMemoryProperties();

		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).toString());
		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).toAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).toNormalizedAndAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2/file.txt/"), FSPath.create(loc("F:/dir1/dir2/file.txt")).toStringWithTraillingSeparator());

		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2/")).toString());
		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2/")).toAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2/")).toNormalizedAndAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2/"), FSPath.create(loc("F:/dir1/dir2/")).toStringWithTraillingSeparator());

		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2")).toString());
		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2")).toAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2")).toNormalizedAndAbsolutePathString());
		assertEquals(loc("F:/dir1/dir2/"), FSPath.create(loc("F:/dir1/dir2")).toStringWithTraillingSeparator());

		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).toAbsolutePathString());
		assertEquals(loc("F:/dir1/file.txt"), FSPath.create(loc("F:/dir1/dir2/../file.txt")).toNormalizedAndAbsolutePathString());

		assertTrue(FSPath.isEqual(FSPath.create(loc("F:/dir1/dir2/file.txt")), FSPath.create(loc("F:/dir1/dir2/file.txt"))));
		assertFalse(FSPath.isEqual(FSPath.create(loc("F:/dir1/dir2/file1.txt")), FSPath.create(loc("F:/dir1/dir2/file2.txt"))));

		assertTrue(FSPath.create(loc("F:/dir1/dir2/file.txt")).equalsOnFilesystem(FSPath.create(loc("F:/dir1/dir2/file.txt"))));
		assertFalse(FSPath.create(loc("F:/dir1/dir2/file1.txt")).equalsOnFilesystem(FSPath.create(loc("F:/dir1/dir2/file2.txt"))));

		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).forceExtension("txt").toString());
		assertEquals(loc("F:/dir1/dir2/file.txt.mp4"), FSPath.create(loc("F:/dir1/dir2/file.txt")).forceExtension("mp4").toString());

		assertEquals("file.txt", FSPath.create(loc("F:/dir1/dir2/file.txt")).getDirectoryName());
		assertEquals("dir2", FSPath.create(loc("F:/dir1/dir2/")).getDirectoryName());
		assertEquals("txt", FSPath.create(loc("F:/dir1/dir2/file.txt")).getExtension());
		assertEquals("file.txt", FSPath.create(loc("F:/dir1/dir2/file.txt")).getFilenameWithExt());
		assertEquals("file", FSPath.create(loc("F:/dir1/dir2/file.txt")).getFilenameWithoutExt());

		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2/file.txt")).getParent().toString());
		assertEquals(loc("F:/dir1/dir2"), FSPath.create(loc("F:/dir1/dir2/file.txt")).getParent(1).toString());
		assertEquals(loc("F:/dir1"), FSPath.create(loc("F:/dir1/dir2/file.txt")).getParent(2).toString());

		if (ApplicationHelper.isWindows()) // all paths are valid under Linux
		{
			assertTrue(FSPath.create(loc("F:/dir1/dir2/file.txt")).isValidPath());
			assertFalse(FSPath.create(loc("F:|dir1|dir2|file.txt")).isValidPath());
		}

		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).replaceExtension("txt").toString());
		assertEquals(loc("F:/dir1/dir2/file.mp4"), FSPath.create(loc("F:/dir1/dir2/file.txt")).replaceExtension("mp4").toString());
		assertEquals(loc("F:/dir1/dir2/other.avi"), FSPath.create(loc("F:/dir1/dir2/file.txt")).replaceFilename("other.avi").toString());
		assertEquals(loc("F:/dir1/dir2/file.txt"), FSPath.create(loc("F:/dir1/dir2/file.txt")).replaceFilename("file.txt").toString());

		assertTrue(FSPath.create(loc("")).isEmpty());
		assertTrue(FSPath.Empty.isEmpty());
		assertFalse(FSPath.create(loc("F:/dir1/dir2/file.txt")).isEmpty());
	}

	private String loc(String s) {
		s = s.replace('/', File.separatorChar);

		if (!ApplicationHelper.isWindows() && s.length() >= 2 && s.charAt(1) == ':') s = s.substring(2);

		return s;
	}

	@Test
	public void testCCPathNormal() {
		var ccprops = createInMemoryProperties();

		assertEquals("F:/dir1/dir2/file.txt", CCPath.create("F:/dir1/dir2/file.txt").toString());
		assertEquals("F:/dir1/dir2", CCPath.create("F:/dir1/dir2/").toString());
		assertEquals("F:/dir1/dir2", CCPath.create("F:/dir1/dir2").toString());

		assertEquals("F:/dir1/dir2/file.txt/", CCPath.create("F:/dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("F:/dir1/dir2/", CCPath.create("F:/dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("F:/dir1/dir2/", CCPath.create("F:/dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("F:/dir1/dir2/file.txt").getExtension());
		assertEquals("F:/dir1/dir2", CCPath.create("F:/dir1/dir2/file.txt").getParent().toString());
		assertEquals("F:/dir1/dir2", CCPath.create("F:/dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("F:/dir1", CCPath.create("F:/dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("F:/dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("F:/dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("F:/dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("F:/dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("F:/dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathNormalWithVar() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?[mov]>dir1/dir2/file.txt", CCPath.create("<?[mov]>dir1/dir2/file.txt").toString());
		assertEquals("<?[mov]>dir1/dir2", CCPath.create("<?[mov]>dir1/dir2/").toString());
		assertEquals("<?[mov]>dir1/dir2", CCPath.create("<?[mov]>dir1/dir2").toString());

		assertEquals("<?[mov]>dir1/dir2/file.txt/", CCPath.create("<?[mov]>dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("<?[mov]>dir1/dir2/", CCPath.create("<?[mov]>dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("<?[mov]>dir1/dir2/", CCPath.create("<?[mov]>dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("<?[mov]>dir1/dir2/file.txt").getExtension());
		assertEquals("<?[mov]>dir1/dir2", CCPath.create("<?[mov]>dir1/dir2/file.txt").getParent().toString());
		assertEquals("<?[mov]>dir1/dir2", CCPath.create("<?[mov]>dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("<?[mov]>dir1", CCPath.create("<?[mov]>dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("<?[mov]>dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?[mov]>dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("<?[mov]>dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("<?[mov]>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?[mov]>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathNormalWithSelf() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?self>dir1/dir2/file.txt", CCPath.create("<?self>dir1/dir2/file.txt").toString());
		assertEquals("<?self>dir1/dir2", CCPath.create("<?self>dir1/dir2/").toString());
		assertEquals("<?self>dir1/dir2", CCPath.create("<?self>dir1/dir2").toString());

		assertEquals("<?self>dir1/dir2/file.txt/", CCPath.create("<?self>dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("<?self>dir1/dir2/", CCPath.create("<?self>dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("<?self>dir1/dir2/", CCPath.create("<?self>dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("<?self>dir1/dir2/file.txt").getExtension());
		assertEquals("<?self>dir1/dir2", CCPath.create("<?self>dir1/dir2/file.txt").getParent().toString());
		assertEquals("<?self>dir1/dir2", CCPath.create("<?self>dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("<?self>dir1", CCPath.create("<?self>dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("<?self>dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?self>dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("<?self>dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("<?self>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?self>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathNormalWithDriveLabel() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").toString());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/").toString());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2").toString());

		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt/", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2/", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2/", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("<?vLabel=\"dl0\">dir1/dir2/file.txt").getExtension());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").getParent().toString());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1/dir2", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("<?vLabel=\"Network Drive 0\">dir1", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?vLabel=\"Network Drive 0\">dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathNormalWithDriveLetter() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?vLetter=\"C\">dir1/dir2/file.txt", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").toString());
		assertEquals("<?vLetter=\"C\">dir1/dir2", CCPath.create("<?vLetter=\"C\">dir1/dir2/").toString());
		assertEquals("<?vLetter=\"C\">dir1/dir2", CCPath.create("<?vLetter=\"C\">dir1/dir2").toString());

		assertEquals("<?vLetter=\"C\">dir1/dir2/file.txt/", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("<?vLetter=\"C\">dir1/dir2/", CCPath.create("<?vLetter=\"C\">dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("<?vLetter=\"C\">dir1/dir2/", CCPath.create("<?vLetter=\"C\">dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getExtension());
		assertEquals("<?vLetter=\"C\">dir1/dir2", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getParent().toString());
		assertEquals("<?vLetter=\"C\">dir1/dir2", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("<?vLetter=\"C\">dir1", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?vLetter=\"C\">dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathNormalWithSelfDrive() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?self[dir]>dir1/dir2/file.txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toString());
		assertEquals("<?self[dir]>dir1/dir2", CCPath.create("<?self[dir]>dir1/dir2/").toString());
		assertEquals("<?self[dir]>dir1/dir2", CCPath.create("<?self[dir]>dir1/dir2").toString());

		assertEquals("<?self[dir]>dir1/dir2/file.txt/", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toStringWithTraillingSeparator());
		assertEquals("<?self[dir]>dir1/dir2/", CCPath.create("<?self[dir]>dir1/dir2/").toStringWithTraillingSeparator());
		assertEquals("<?self[dir]>dir1/dir2/", CCPath.create("<?self[dir]>dir1/dir2").toStringWithTraillingSeparator());

		assertEquals("txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getExtension());
		assertEquals("<?self[dir]>dir1/dir2", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getParent().toString());
		assertEquals("<?self[dir]>dir1/dir2", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getParent(1).toString());
		assertEquals("<?self[dir]>dir1", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getParent(2).toString());
		assertEquals("file", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").getFilenameWithExt());

		assertEquals("txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toFSPath(ccprops).getExtension());
		assertEquals("file", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithoutExt());
		assertEquals("file.txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toFSPath(ccprops).getFilenameWithExt());
	}

	@Test
	public void testCCPathtoFSPath() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?self[dir]>dir1/dir2/file.txt", CCPath.create("<?self[dir]>dir1/dir2/file.txt").toString());

		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("dir").append("file.txt").toString(),                          CCPath.create("<?self>dir/file.txt").toFSPath(ccprops).toString());
		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("dir").append("file.txt").toAbsolutePathString(),              CCPath.create("<?self>dir/file.txt").toFSPath(ccprops).toAbsolutePathString());
		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("dir").append("file.txt").toNormalizedAndAbsolutePathString(), CCPath.create("<?self>dir/file.txt").toFSPath(ccprops).toNormalizedAndAbsolutePathString());

		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("file.txt").toString(),                          CCPath.create("<?self>file.txt").toFSPath(ccprops).toString());
		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("file.txt").toAbsolutePathString(),              CCPath.create("<?self>file.txt").toFSPath(ccprops).toAbsolutePathString());
		assertEquals(FilesystemUtils.getAbsoluteSelfDirectory(ccprops).append("file.txt").toNormalizedAndAbsolutePathString(), CCPath.create("<?self>file.txt").toFSPath(ccprops).toNormalizedAndAbsolutePathString());

		var selfdir = FSPath.create(ApplicationHelper.isWindows() ? (FilesystemUtils.getRealSelfDirectory().toString().charAt(0)+":\\") : ("/"));

		assertEquals(selfdir.append("file.txt").toString(),                          CCPath.create("<?self[dir]>file.txt").toFSPath(ccprops).toString());
		assertEquals(selfdir.append("file.txt").toAbsolutePathString(),              CCPath.create("<?self[dir]>file.txt").toFSPath(ccprops).toAbsolutePathString());
		assertEquals(selfdir.append("file.txt").toNormalizedAndAbsolutePathString(), CCPath.create("<?self[dir]>file.txt").toFSPath(ccprops).toNormalizedAndAbsolutePathString());

		assertEquals(selfdir.append("dir").append("file.txt").toString(),                          CCPath.create("<?self[dir]>dir/file.txt").toFSPath(ccprops).toString());
		assertEquals(selfdir.append("dir").append("file.txt").toAbsolutePathString(),              CCPath.create("<?self[dir]>dir/file.txt").toFSPath(ccprops).toAbsolutePathString());
		assertEquals(selfdir.append("dir").append("file.txt").toNormalizedAndAbsolutePathString(), CCPath.create("<?self[dir]>dir/file.txt").toFSPath(ccprops).toNormalizedAndAbsolutePathString());

		var vdir = ApplicationHelper.isWindows() ? CCPath.create("C:/tmpfs/jcc/mov/").toFSPath(ccprops) : CCPath.create("/tmpfs/jcc/mov/").toFSPath(ccprops);

		assertEquals(vdir.append("file.txt").toString(),                          CCPath.create("<?[mov]>file.txt").toFSPath(ccprops).toString());
		assertEquals(vdir.append("file.txt").toAbsolutePathString(),              CCPath.create("<?[mov]>file.txt").toFSPath(ccprops).toAbsolutePathString());
		assertEquals(vdir.append("file.txt").toNormalizedAndAbsolutePathString(), CCPath.create("<?[mov]>file.txt").toFSPath(ccprops).toNormalizedAndAbsolutePathString());
	}

	@Test
	public void testFSPathToCCPath() {
		var ccprops = createInMemoryProperties();

		assertEquals("<?[mov]>fname.ext",          CCPath.createFromFSPath(FSPath.create(loc("C:/tmpfs/jcc/mov/fname.ext")), ccprops).toString());
		assertEquals("<?[mov]>fname.ext",          CCPath.createFromFSPath(FSPath.create(loc("C:/tmpfs/jcc/mov/fname.ext")), Opt.True, ccprops).toString());

		if (ApplicationHelper.isWindows())
		{
			assertEquals("<?vNetwork=\"\\\\server2\\drive2\">fname.ext", CCPath.createFromFSPath(FSPath.create(loc("O:/fname.ext")), ccprops).toString());
			assertEquals("<?vNetwork=\"\\\\server2\\drive2\">fname.ext", CCPath.createFromFSPath(FSPath.create(loc("O:/fname.ext")), Opt.True, ccprops).toString());
			assertEquals("O:/fname.ext",                                 CCPath.createFromFSPath(FSPath.create(loc("O:/fname.ext")), Opt.False, ccprops).toString());

			assertEquals("C:/tmpfs/jcc/mov/fname.ext", CCPath.createFromFSPath(FSPath.create("C:\\tmpfs\\jcc\\mov\\fname.ext"), Opt.False, ccprops).toString());
		}
		else
		{
			assertEquals("/tmpfs/jcc/mov/fname.ext", CCPath.createFromFSPath(FSPath.create("/tmpfs/jcc/mov/fname.ext"), Opt.False, ccprops).toString());
		}
	}

	@Test
	public void testCommonPathStart() {

		var p1 = CCPath.create("<?[ser]>Gundam Build Divers [JAP]/01 - Gundam Build Divers/S01E02 - Chaotic Ogre.mkv");
		var p2 = CCPath.create("<?[ser]>Gundam Build Divers [JAP]/01 - Gundam Build Divers/S01E10 - Coalition of Volunteers.mkv");
		var p3 = CCPath.create("<?[ser]>Gundam Build Divers [JAP]/02 - Gundam Build Divers ReRise/S02E07 - Battered Crown.mkv");
		var p4 = CCPath.create("<?[ser]>Gundam Build Divers [JAP]/03 - Gundam Build Divers ReRise 2nd Season/S03E08 - To Fly Once More.mkv");
		var p5 = CCPath.create("<?[ser]>Mobile Suit Gundam Iron Blooded Orphans [JAP]/1 - Kidou Senshi Gundam Tekketsu no Orphans/S01E01 - Iron and Blood....mkv");
		var p6 = CCPath.create("<?[mov]>Mobile Suit Gundam I - Cucuruz Doan's Island [JAP].mkv");

		assertEquals("<?[ser]>Gundam Build Divers [JAP]/01 - Gundam Build Divers",  											CCPath.getCommonPath(List.of(p1)).toString());
		assertEquals("<?[ser]>Mobile Suit Gundam Iron Blooded Orphans [JAP]/1 - Kidou Senshi Gundam Tekketsu no Orphans",    	CCPath.getCommonPath(List.of(p5)).toString());
		assertEquals("<?[mov]>",           																						CCPath.getCommonPath(List.of(p6)).toString());
		assertEquals("<?[ser]>Gundam Build Divers [JAP]/01 - Gundam Build Divers",  											CCPath.getCommonPath(List.of(p1, p2)).toString());
		assertEquals("<?[ser]>Gundam Build Divers [JAP]",          																CCPath.getCommonPath(List.of(p1, p2, p3)).toString());
		assertEquals("<?[ser]>",          																						CCPath.getCommonPath(List.of(p1, p2, p5)).toString());
		assertEquals("",        		  																						CCPath.getCommonPath(List.of(p1, p6)).toString());
		assertEquals("",        		  																						CCPath.getCommonPath(List.of(p1, p2, p3, p4, p5, p6)).toString());
	}
}
