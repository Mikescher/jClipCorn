package de.jClipCorn.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

import de.jClipCorn.properties.types.PathSyntaxVar;
import org.junit.After;
import org.junit.Before;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SuppressWarnings("nls")
public class ClipCornBaseTest {

	protected static TimeZone GMT_2 = TimeZone.getTimeZone("GMT+2:00");

	@Before
	public void Init() {
		CCLog.initUnitTestMode();
		ApplicationHelper.SetOverrideModeUnix();
	}

	@After
	public void Finalize() {
		assertFalse(CCLog.hasErrors());
		assertFalse(CCLog.hasUndefinieds());
	}
	
	protected CCMovieList createEmptyDB() {
		CCProperties.createInMemory();
		CCMovieList ml = CCMovieList.createInMemory();
		ml.connectForTests();
		
		return ml;
	}

	protected CCMovieList createExampleDB() throws IOException {
		CCProperties.createInMemory();
		CCMovieList ml = CCMovieList.createInMemory();
		ml.connectForTests();
		File filep = new File(SimpleFileUtils.getSystemTempFile("jxmlbkp"));
		SimpleFileUtils.writeTextResource(filep, "/example_data_full.jxmlbkp", ClipCornBaseTest.class);
		ExportHelper.restoreDatabaseFromBackup(filep, ml);
		filep.delete();

		CCProperties.getInstance().PROP_PATHSYNTAX_VAR1.setValue(new PathSyntaxVar("mov", "/tmpfs/jcc/mov/"));
		CCProperties.getInstance().PROP_PATHSYNTAX_VAR2.setValue(new PathSyntaxVar("ser", "/tmpfs/jcc/ser/"));

		return ml;
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
}
