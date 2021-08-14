package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.databaseElement.columnTypes.CCFileSize;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.types.PathSyntaxVar;
import de.jClipCorn.util.DriveMap;
import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple3;
import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.filesystem.SimpleFileUtils;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.lambda.Func0to0;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.TimeZone;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class ClipCornBaseTest {

	protected static TimeZone GMT_2 = TimeZone.getTimeZone("GMT+2:00");

	public static Stack<Func0to0> CLEANUP = new Stack<>();

	@Before
	public void Init() {
		CCLog.initUnitTestMode();
	}

	@After
	public void Finalize() {
		while (!CLEANUP.empty()) CLEANUP.pop().invoke();

		assertFalse(CCLog.hasErrors());
		assertFalse(CCLog.hasUndefinieds());
	}

	protected CCMovieList createEmptyDB() {
		createInMemoryProperties();
		CCProperties.createInMemory();
		CCMovieList ml = CCMovieList.createInMemory();
		ml.connectForTests(false);

		return ml;
	}

	protected CCMovieList createSeededDB() throws Exception {
		return DatabaseSeeder.init();
	}

	protected CCMovieList createExampleDB() throws IOException { return createExampleDB(false); }

	protected CCMovieList createExampleDB(boolean reloadFromMemory) throws IOException {
		createInMemoryProperties();

		CCMovieList ml1 = CCMovieList.createInMemory();
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

			var ml2 = CCMovieList.createRawForUnitTests(ml1.getDatabaseForUnitTests());
			ml2.connectForTests(true);
			return ml2;
		}

		return ml1;
	}

	@SuppressWarnings("unchecked")
	protected void createInMemoryProperties()
	{
		CCProperties.createInMemory();

		if (ApplicationHelper.isWindows())
		{
			CCProperties.getInstance().PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.create("C:/tmpfs/jcc/mov/")));
			CCProperties.getInstance().PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.create("C:/tmpfs/jcc/ser/")));

			DriveMap.initForTests(
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
			CCProperties.getInstance().PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", CCPath.create("/tmpfs/jcc/mov/")));
			CCProperties.getInstance().PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", CCPath.create("/tmpfs/jcc/ser/")));

			DriveMap.initForTests(
					Tuple3.Create('C', "Local Disk",      ""),
					Tuple3.Create('F', "Data",            ""),
					Tuple3.Create('G', "Spine",           ""),
					Tuple3.Create('H', "Network Drive 0", ""),
					Tuple3.Create('K', "Network Drive 1", ""),
					Tuple3.Create('O', "Network Drive 2", "")
			);
		}
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
			for (var e : errs) System.err.println("Additional db-error: " + e.getErrorString());
		}

		assertArrayEquals(new Object[0], errs.toArray());
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
}
