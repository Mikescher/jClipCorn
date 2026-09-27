package de.jClipCorn.test;

import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestMoveWithProgress extends ClipCornBaseTest {

	@Test
	public void testMoveCreatesFoldersAndKeepsContent() throws Exception {
		var dir = createAutocleanedDir("move_progress");
		var src = dir.append("source.mkv");
		var dst = dir.append("a").append("b").append("target.mkv");

		Files.write(src.toPath(), new byte[] { 1, 2, 3, 4 });
		Files.setLastModifiedTime(src.toPath(), FileTime.fromMillis(1_000_000_000_000L));

		var progress = new long[] { -1, -1 };
		SimpleFileUtils.moveWithProgress(src, dst, (v, m) -> { progress[0] = v; progress[1] = m; });

		assertFalse(src.exists());
		assertTrue(dst.fileExists());
		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, Files.readAllBytes(dst.toPath()));
		assertEquals(1_000_000_000_000L, Files.getLastModifiedTime(dst.toPath()).toMillis());
		assertEquals(4, progress[0]);
		assertEquals(4, progress[1]);
	}

	@Test
	public void testMoveNeverOverwritesTarget() throws Exception {
		var dir = createAutocleanedDir("move_progress");
		var src = dir.append("source.mkv");
		var dst = dir.append("target.mkv");

		Files.write(src.toPath(), new byte[] { 1, 2, 3, 4 });
		Files.write(dst.toPath(), new byte[] { 9 });

		assertThrows(FileAlreadyExistsException.class, () -> SimpleFileUtils.moveWithProgress(src, dst, (v, m) -> {}));

		assertArrayEquals(new byte[] { 1, 2, 3, 4 }, Files.readAllBytes(src.toPath()));
		assertArrayEquals(new byte[] { 9 }, Files.readAllBytes(dst.toPath()));
	}

	@Test
	public void testMoveMissingSource() throws Exception {
		var dir = createAutocleanedDir("move_progress");
		var src = dir.append("source.mkv");
		var dst = dir.append("target.mkv");

		assertThrows(FileNotFoundException.class, () -> SimpleFileUtils.moveWithProgress(src, dst, (v, m) -> {}));

		assertFalse(dst.exists());
	}
}
