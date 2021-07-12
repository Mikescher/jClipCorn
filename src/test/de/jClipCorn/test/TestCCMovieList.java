package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import org.junit.Test;

@SuppressWarnings("nls")
public class TestCCMovieList extends ClipCornBaseTest {


	@Test
	public void testDatabaseCreation() throws Exception {
		CCMovieList ml = createSeededDB();


	}


}
