package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.databaseErrors.CCDatabaseValidator;
import de.jClipCorn.features.databaseErrors.DatabaseError;
import de.jClipCorn.features.databaseErrors.DatabaseValidatorOptions;
import de.jClipCorn.util.listener.DoubleProgressCallbackListener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestCCMovieList extends ClipCornBaseTest {

	@Test
	public void testDatabaseCreation() throws Exception {
		CCMovieList ml = createSeededDB();

		assertEquals(1, ml.getElementCount());
	}

	@Test
	public void testDatabaseCheck() throws Exception {
		CCMovieList ml = createSeededDB();

		DatabaseValidatorOptions opt = new DatabaseValidatorOptions(
				true,  // movies
				true,  // series
				true,  // seasons
				true,  // episodes
				true,  // covers
				false, // cover files
				false, // video files
				true,  // groups
				true,  // online-refs
				true,  // internal db
				true); // Additional

		List<DatabaseError> errs = new ArrayList<>();

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);


		assertEmptyErrors(errs);
	}


}
