package de.jClipCorn.test;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.serialization.ExportHelper;
import de.jClipCorn.features.log.CCLog;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.helper.SimpleFileUtils;

import static org.junit.Assert.assertFalse;

@SuppressWarnings("nls")
public class ClipCornBaseTest {
	
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
		
		return ml;
	}
}
