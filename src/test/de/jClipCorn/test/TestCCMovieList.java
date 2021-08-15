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

		assertEquals(3, ml.getMovieCount());
		assertEquals(1, ml.getSeriesCount());
		assertEquals(2, ml.getSeasonCount());
		assertEquals(5, ml.getEpisodeCount());
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
				true,  // cover files
				true,  // video files
				true,  // groups
				true,  // online-refs
				true,  // internal db
				true,  // Additional
				true,  // Validate Series Structure
				true); // Ignore IFO duplicates

		List<DatabaseError> errs = new ArrayList<>();

		var validator = new CCDatabaseValidator(ml);
		validator.validate(errs, opt, DoubleProgressCallbackListener.EMPTY);

		assertEmptyErrors(errs);
	}


}
