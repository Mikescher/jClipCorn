package de.jClipCorn.test;

import java.io.File;
import java.io.IOException;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.database.util.ExportHelper;
import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.util.helper.SimpleFileUtils;

@SuppressWarnings("nls")
public class ClipCornTestHelper {
	public static CCMovieList createEmptyDB() {
		CCProperties.createInMemory();
		CCMovieList ml = CCMovieList.createInMemory();
		ml.connectForTests();
		
		return ml;
	}

	public static CCMovieList createExampleDB() throws IOException {
		CCProperties.createInMemory();
		CCMovieList ml = CCMovieList.createInMemory();
		ml.connectForTests();
		File filep = new File(SimpleFileUtils.getTempFile("jxmlbkp"));
		SimpleFileUtils.writeTextResource(filep, "/example_data_full.jxmlbkp", new ClipCornTestHelper().getClass());
		ExportHelper.restoreDatabaseFromBackup(filep, ml);
		filep.delete();
		
		return ml;
	}
}
