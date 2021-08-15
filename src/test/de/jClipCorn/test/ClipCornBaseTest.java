package de.jClipCorn.test;

import de.jClipCorn.Main;
import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.datetime.CCDateTime;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.filesystem.FilesystemUtils;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.lambda.Func0to0;
import de.jClipCorn.util.lambda.Func0to0WithException;
import de.jClipCorn.util.stream.CCStreams;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class ClipCornBaseTest {

	protected static TimeZone GMT_2 = TimeZone.getTimeZone("GMT+2:00");

	public static Stack<Func0to0WithException<Exception>> CLEANUP = new Stack<>();

	@Before
	public void Init() {
		CCLog.initUnitTestMode();
	}

	@After
	public void Finalize() {
		while (!CLEANUP.empty())
		{
			try
			{
				CLEANUP.pop().invoke();
			}
			catch (Throwable e)
			{
				System.out.println();
				System.out.println("[META-ERR]  ( Cleanup failed: " + e.getMessage() + " )");
				System.out.println();
			}
		}

		assertFalse(CCLog.hasErrors());
		assertFalse(CCLog.hasUndefinieds());
	}

	protected CCMovieList createEmptyDB() {
		createInMemoryProperties();
		CCMovieList ml = CCMovieList.createInMemory(CCProperties.createInMemory());
		ml.connectForTests(false);

		return ml;
	}

	protected CCMovieList createSeededDB() throws Exception {
		return DatabaseSeeder.init(true);
	}

	protected CCMovieList createExampleDB() throws IOException { return createExampleDB(false); }

	protected CCMovieList createExampleDB(boolean reloadFromMemory) throws IOException {
		createInMemoryProperties();

		CCMovieList ml1 = CCMovieList.createInMemory(CCProperties.createInMemory());
		ml1.connectForTests(false);

		var filep = SimpleFileUtils.getSystemTempFile("jxmlbkp");
		SimpleFileUtils.writeTextResource(filep, "/example_data_full.jxmlbkp", ClipCornBaseTest.class);

		CCLog.disableChangeEvents();
		ExportHelper.restoreDatabaseFromBackup(filep, ml1);
		CCLog.reenableChangeEvents();

		filep.deleteWithException();

		// create some dummy values for the file checksums

		CCLog.disableChangeEvents();
		{
			for (var p : ml1.iteratorPlayables())
			{
				if (p.mediaInfo().getPartial().Checksum.isPresent()) continue;
				var mi = p.mediaInfo().getPartial();
				mi = mi.WithChecksum(Opt.of("[01-" + StringUtils.leftPad(Long.toHexString(mi.Filesize.orElse(CCFileSize.ZERO).getBytes()).toUpperCase(), 10, '0') + "-00:00:00:00:00:00:00:00]"));
				p.mediaInfo().set(mi.toMediaInfo());
			}
		}
		CCLog.reenableChangeEvents();

		if (reloadFromMemory)
		{
			// With reload the data is loaded again from the (in-mem) database,
			// so ml2 is acting like the data was there all along and not just recently imported

			var ml2 = CCMovieList.createRawForUnitTests(ml1.getDatabaseForUnitTests(), ml1.ccprops());
			ml2.connectForTests(true);
			return ml2;
		}

		return ml1;
	}

	@SuppressWarnings("unchecked")
	protected CCProperties createInMemoryProperties()
	{
		var props = CCProperties.createInMemory();

		if (ApplicationHelper.isWindows())
		{
			props.PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.create("C:/tmpfs/jcc/mov/")));
			props.PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.create("C:/tmpfs/jcc/ser/")));

			props.getDriveMap().initForTests(
				Tuple3.Create('C', "Local Disk",      ""),
				Tuple3.Create('F', "Data",            ""),
				Tuple3.Create('G', "Spine",           ""),
				Tuple3.Create('H', "Network Drive 0", "\\\\server1\\drive0"),
				Tuple3.Create('K', "Network Drive 1", "\\\\server1\\drive1"),
				Tuple3.Create('O', "Network Drive 2", "\\\\server2\\drive2")
			);
		}
		else
		{
			props.PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.create("/tmpfs/jcc/mov/")));
			props.PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.create("/tmpfs/jcc/ser/")));

			props.getDriveMap().initForTests(
					Tuple3.Create('C', "Local Disk",      ""),
					Tuple3.Create('F', "Data",            ""),
					Tuple3.Create('G', "Spine",           ""),
					Tuple3.Create('H', "Network Drive 0", ""),
					Tuple3.Create('K', "Network Drive 1", ""),
					Tuple3.Create('O', "Network Drive 2", "")
			);
		}

		return props;
	}

	protected void assertImageEquals(BufferedImage a, BufferedImage b) {
		assertEquals(a.getWidth(), b.getWidth());
		assertEquals(a.getHeight(), b.getHeight());

		for (int x = 0; x < a.getWidth(); x++) {
			for (int y = 0; y < a.getHeight(); y++) {
				assertEquals(a.getRGB(x, y), b.getRGB(x, y));
			}
		}
	}

	protected void assertEmptyErrors(List<DatabaseError> errs) {
		if (!errs.isEmpty()) {
			for (var e : errs)
			{
				System.err.println("(db-error): " + e.getFullErrorString());
				for (var md: e.Metadata) System.err.println("              - " + md.Item1 + " -> " + md.Item2);
			}
		}

		assertArrayEquals(new Object[0], errs.toArray());
	}

	protected void assertArchiveEquals(FSPath p1, FSPath p2) throws IOException {

		var e1 = new HashMap<String, Tuple<String, byte[]>>();
		try (var fs = new FileInputStream(p1.toFile())) {
			try (var zs = new ZipInputStream(fs)) {
				for (ZipEntry entry = zs.getNextEntry(); entry != null; entry = zs.getNextEntry()) {
					if (entry.getName().toLowerCase().endsWith(".txt") || entry.getName().toLowerCase().endsWith(".xml"))
						e1.put(entry.getName(), Tuple.Create(new String(zs.readAllBytes()), null));
					else
						e1.put(entry.getName(), Tuple.Create(null, zs.readAllBytes()));
				}

			}
		}

		var e2 = new HashMap<String, Tuple<String, byte[]>>();
		try (var fs = new FileInputStream(p2.toFile())) {
			try (var zs = new ZipInputStream(fs)) {
				for (ZipEntry entry = zs.getNextEntry(); entry != null; entry = zs.getNextEntry()) {
					if (entry.getName().toLowerCase().endsWith(".txt") || entry.getName().toLowerCase().endsWith(".xml"))
						e2.put(entry.getName(), Tuple.Create(new String(zs.readAllBytes()), null));
					else
						e2.put(entry.getName(), Tuple.Create(null, zs.readAllBytes()));
				}

			}
		}

		assertEquals(CCStreams.iterate(e1.keySet()).autosort().stringjoin(e->e, "\n"), CCStreams.iterate(e2.keySet()).autosort().stringjoin(e->e, "\n"));

		assertEquals(e1.size(), e2.size());

		for (var k: e1.keySet())
		{
			var v1 = e1.get(k);
			var v2 = e2.get(k);

			assertEquals("ZipEntry::"+k, v1.Item1, v2.Item1);
			assertEquals("ZipEntry::"+k, v1.Item2, v2.Item2);
		}
	}

	protected void assertException(Func0to0 fn)
	{
		try
		{
			fn.invoke();
			Assert.fail("Function did not throw Exception");
		}
		catch (Exception e)
		{
			// Good!
		}
	}

	protected static FSPath createAutocleanedDir(String ident) throws IOException {
		var tempPath = FilesystemUtils.getTempPath().append("jcc_unittests").append(Str.format("{0}_{1}_{2}", ident, CCDateTime.getCurrentDateTime().toStringFilesystem(), UUID.randomUUID()));
		tempPath.mkdirsWithException();

		ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Clear dir " + tempPath.getDirectoryName()); tempPath.deleteRecursive(); });

		System.out.println("[TESTS] Create (autoclean) dir: " + tempPath);

		return tempPath;
	}

	public static CCMovieList reloadDBAfterShutdown(CCMovieList ml) throws Exception {

		var dbPath = ml.getDatabaseDirectory().getParent();

		var props = CCProperties.create(dbPath.getParent().append(Main.PROPERTIES_PATH), new String[0]);
		props.PROP_DATABASE_DIR.setValue(dbPath);
		props.PROP_DATABASE_NAME.setValue(ml.getDatabaseName());

		var mlRet = CCMovieList.createInstanceMovieList(ml.ccprops());
		mlRet.connectExternal(true);

		ClipCornBaseTest.CLEANUP.add(() -> { System.out.println("[CLEANUP] Shutdown ML"); mlRet.shutdown(); });

		return mlRet;
	}

}
