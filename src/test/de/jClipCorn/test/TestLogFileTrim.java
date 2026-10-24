package de.jClipCorn.test;

import de.jClipCorn.features.log.CCLogInternal;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestLogFileTrim extends ClipCornBaseTest {

	private File createLogFile(String content) throws Exception {
		var file = SimpleFileUtils.getSystemTempFile("log").toFile();
		CLEANUP.push(() -> Files.deleteIfExists(file.toPath()));
		Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
		return file;
	}

	@Test
	public void testCountLines() throws Exception {
		assertEquals(0, CCLogInternal.countLines(createLogFile("")));
		assertEquals(3, CCLogInternal.countLines(createLogFile("a\nbb\n\n")));

		var missing = SimpleFileUtils.getSystemTempFile("log").toFile();
		assertFalse(missing.exists());
		assertEquals(0, CCLogInternal.countLines(missing));
	}

	@Test
	public void testRemoveFirstLines() throws Exception {
		var file = createLogFile("l1\nl2\nl3\nl4\nl5\n");

		CCLogInternal.removeFirstLines(file, 2);

		assertEquals("l3\nl4\nl5\n", Files.readString(file.toPath(), StandardCharsets.UTF_8));
		assertFalse(new File(file.getPath() + ".tmp").exists());
	}

	@Test
	public void testRemoveNoLinesKeepsFile() throws Exception {
		var file = createLogFile("l1\nl2\n");

		CCLogInternal.removeFirstLines(file, 0);

		assertEquals("l1\nl2\n", Files.readString(file.toPath(), StandardCharsets.UTF_8));
	}

	@Test
	public void testRemoveMoreLinesThanPresent() throws Exception {
		var file = createLogFile("l1\nl2\n");

		CCLogInternal.removeFirstLines(file, 10);

		assertEquals("", Files.readString(file.toPath(), StandardCharsets.UTF_8));
	}
}
