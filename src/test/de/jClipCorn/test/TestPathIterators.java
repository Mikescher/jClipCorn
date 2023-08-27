package de.jClipCorn.test;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestPathIterators extends ClipCornBaseTest {

	@Test
	public void testFSPathDepthFirstIterator() throws Exception {
		var tempPath = FilesystemUtils.getTempPath().append("jcc_unittests").append(Str.format("{0}_{1}_{2}", "testFSPathDepthFirstIterator", CCDateTime.getCurrentDateTime().toStringFilesystem(), UUID.randomUUID()));
		tempPath.mkdirsWithException();
		ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Clear dir " + tempPath.getDirectoryName()); tempPath.deleteRecursive(); });

		tempPath.append("A").mkdirsWithException();
		tempPath.append("A", "X").touch();
		tempPath.append("A", "Y").touch();
		tempPath.append("A", "Z").touch();
		tempPath.append("B").mkdirsWithException();
		tempPath.append("C").mkdirsWithException();
		tempPath.append("C", "U").mkdirsWithException();
		tempPath.append("C", "V").mkdirsWithException();
		tempPath.append("C", "W").mkdirsWithException();
		tempPath.append("C", "W", "0").mkdirsWithException();
		tempPath.append("C", "W", "1").mkdirsWithException();
		tempPath.append("C", "W", "2").mkdirsWithException();
		tempPath.append("C", "W", "3").touch();
		tempPath.append("C", "W", "4").touch();
		tempPath.append("C", "W", "5").touch();
		tempPath.append("D").mkdirsWithException();
		tempPath.append("E").mkdirsWithException();

		{
			var r1 = tempPath.listRecursiveDepthFirst().map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(-)", "A:X:Y:Z:B:C:U:V:W:0:1:2:3:4:5:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveDepthFirst(4).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(4)", "A:X:Y:Z:B:C:U:V:W:0:1:2:3:4:5:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveDepthFirst(3).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(3)", "A:X:Y:Z:B:C:U:V:W:0:1:2:3:4:5:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveDepthFirst(2).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(2)", "A:X:Y:Z:B:C:U:V:W:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveDepthFirst(1).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(1)", "A:B:C:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveDepthFirst(0).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(0)", "", r1);
		}

	}

	@Test
	public void testFSPathBreadthFirstIterator() throws Exception {
		var tempPath = FilesystemUtils.getTempPath().append("jcc_unittests").append(Str.format("{0}_{1}_{2}", "testFSPathDepthFirstIterator", CCDateTime.getCurrentDateTime().toStringFilesystem(), UUID.randomUUID()));
		tempPath.mkdirsWithException();
		ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Clear dir " + tempPath.getDirectoryName()); tempPath.deleteRecursive(); });

		tempPath.append("A").mkdirsWithException();
		tempPath.append("A", "X").touch();
		tempPath.append("A", "Y").touch();
		tempPath.append("A", "Z").touch();
		tempPath.append("B").mkdirsWithException();
		tempPath.append("C").mkdirsWithException();
		tempPath.append("C", "U").mkdirsWithException();
		tempPath.append("C", "V").mkdirsWithException();
		tempPath.append("C", "W").mkdirsWithException();
		tempPath.append("C", "W", "0").mkdirsWithException();
		tempPath.append("C", "W", "1").mkdirsWithException();
		tempPath.append("C", "W", "2").mkdirsWithException();
		tempPath.append("C", "W", "3").touch();
		tempPath.append("C", "W", "4").touch();
		tempPath.append("C", "W", "5").touch();
		tempPath.append("D").mkdirsWithException();
		tempPath.append("E").mkdirsWithException();

		{
			var r1 = tempPath.listRecursiveBreadthFirst().map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(-)", "A:B:C:D:E:X:Y:Z:U:V:W:0:1:2:3:4:5", r1);
		}

		{
			var r1 = tempPath.listRecursiveBreadthFirst(4).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(4)", "A:B:C:D:E:X:Y:Z:U:V:W:0:1:2:3:4:5", r1);
		}

		{
			var r1 = tempPath.listRecursiveBreadthFirst(3).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(3)", "A:B:C:D:E:X:Y:Z:U:V:W:0:1:2:3:4:5", r1);
		}

		{
			var r1 = tempPath.listRecursiveBreadthFirst(2).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(2)", "A:B:C:D:E:X:Y:Z:U:V:W", r1);
		}

		{
			var r1 = tempPath.listRecursiveBreadthFirst(1).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(1)", "A:B:C:D:E", r1);
		}

		{
			var r1 = tempPath.listRecursiveBreadthFirst(0).map(FSPath::getDirectoryName).stringjoin(":");
			assertEquals("listRecursiveDepthFirst(0)", "", r1);
		}

	}
}
