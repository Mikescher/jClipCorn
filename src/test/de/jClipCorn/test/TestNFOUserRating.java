package de.jClipCorn.test;

import de.jClipCorn.database.databaseElement.columnTypes.CCUserScore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestNFOUserRating extends ClipCornBaseTest {

	// The enum ids (asInt) are not in rank order (RATING_NO=6, RATING_MID=7), so <userrating> must follow getOrder()

	@Test
	public void testNoRatingIsOmitted() {
		assertEquals(0, CCUserScore.RATING_NO.asNfoUserRating());
	}

	@Test
	public void testRatingsFollowRankOrder() {
		List<Integer> ratings = Arrays.stream(CCUserScore.values())
				.filter(s -> s != CCUserScore.RATING_NO)
				.sorted(Comparator.comparingInt(CCUserScore::getOrder))
				.map(CCUserScore::asNfoUserRating)
				.collect(Collectors.toList());

		assertEquals(List.of(1, 3, 4, 6, 7, 9, 10), ratings);
	}
}
