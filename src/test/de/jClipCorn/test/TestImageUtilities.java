package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CoverImageSize;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ImageUtilities;

public class TestImageUtilities extends ClipCornBaseTest {

	@Test
	public void testResizeToBounds_1() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(182, 254);
		assertEquals((int)s0.Item1, 182);
		assertEquals((int)s0.Item2, 254);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(182.0 / 254.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_2() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1820, 2540);
		assertEquals((int)s0.Item1, 1820);
		assertEquals((int)s0.Item2, 2540);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(1820.0 / 2540.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_3() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(150, 254);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(150.0 / 254.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_4() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(182, 200);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(182.0 / 200.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_5() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(150, 200);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(150.0 / 200.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_6() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1950, 2540);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(1950.0 / 2540.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_7() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1820, 2950);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(1820.0 / 2950.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_8() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1950, 2950);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
		assertEquals(1950.0 / 2950.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_9() {
		createEmptyDB();

		CCProperties.getInstance().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(300, 500);
		assertEquals((int)s0.Item1, 300);
		assertEquals((int)s0.Item2, 500);
		assertTrue(isSizeOK(s0.Item1, s0.Item2));
	}
	
	private boolean isSizeOK(int w, int h) {
		
		if (w < ImageUtilities.BASE_COVER_WIDTH && h < ImageUtilities.BASE_COVER_HEIGHT) {
			return false; // Cover too small
		}

		if (w > ImageUtilities.getCoverWidth() && h < ImageUtilities.getCoverHeight()) {
			return false; // Cover too big
		}
		
		return true;
	}
}
