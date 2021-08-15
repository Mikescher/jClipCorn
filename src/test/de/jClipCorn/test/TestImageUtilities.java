package de.jClipCorn.test;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.CoverImageSize;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.helper.ImageUtilities;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestImageUtilities extends ClipCornBaseTest {

	@Test
	public void testResizeToBounds_1() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(182, 254, ml.ccprops());
		assertEquals((int)s0.Item1, 182);
		assertEquals((int)s0.Item2, 254);
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(182.0 / 254.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_2() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1820, 2540, ml.ccprops());
		assertEquals((int)s0.Item1, 1820);
		assertEquals((int)s0.Item2, 2540);
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(1820.0 / 2540.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_3() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(150, 254, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(150.0 / 254.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_4() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(182, 200, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(182.0 / 200.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_5() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(150, 200, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(150.0 / 200.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_6() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1950, 2540, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(1950.0 / 2540.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_7() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1820, 2950, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(1820.0 / 2950.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_8() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(1950, 2950, ml.ccprops());
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
		assertEquals(1950.0 / 2950.0, s0.Item1 * 1d / s0.Item2, 0.1d);
	}

	@Test
	public void testResizeToBounds_9() {
		var ml = createEmptyDB();

		ml.ccprops().PROP_DATABASE_MAX_COVER_SIZE.setValue(CoverImageSize.DEC_SIZE); // 1820 x 2540

		Tuple<Integer, Integer> s0 = ImageUtilities.calcImageSizeForStorage(300, 500, ml.ccprops());
		assertEquals((int)s0.Item1, 300);
		assertEquals((int)s0.Item2, 500);
		assertTrue(isSizeOK(s0.Item1, s0.Item2, ml.ccprops()));
	}
	
	private boolean isSizeOK(int w, int h, CCProperties ccprops) {
		
		if (w < ImageUtilities.BASE_COVER_WIDTH && h < ImageUtilities.BASE_COVER_HEIGHT) {
			return false; // Cover too small
		}

		if (w > ImageUtilities.getCoverWidth(ccprops) && h < ImageUtilities.getCoverHeight(ccprops)) {
			return false; // Cover too big
		}
		
		return true;
	}
}
