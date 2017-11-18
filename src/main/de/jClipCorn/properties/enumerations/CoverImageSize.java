package de.jClipCorn.properties.enumerations;

import de.jClipCorn.util.enumextension.ContinoousEnum;
import de.jClipCorn.util.enumextension.EnumWrapper;
import de.jClipCorn.util.helper.ImageUtilities;

public enum CoverImageSize implements ContinoousEnum<CoverImageSize> {
	BASE_SIZE(0), 
	DOUBLE_SIZE(1), 
	TRIPLE_SIZE(2), 
	QUAD_SIZE(3), 
	QUINT_SIZE(4), 
	SIXT_SIZE(5), 
	SEPT_SIZE(6), 
	OCT_SIZE(7), 
	NONT_SIZE(8), 
	DEC_SIZE(9);

	private final static int WIDTHS[] = {
		1  * ImageUtilities.BASE_COVER_WIDTH,
		2  * ImageUtilities.BASE_COVER_WIDTH,
		3  * ImageUtilities.BASE_COVER_WIDTH,
		4  * ImageUtilities.BASE_COVER_WIDTH,
		5  * ImageUtilities.BASE_COVER_WIDTH,
		6  * ImageUtilities.BASE_COVER_WIDTH,
		7  * ImageUtilities.BASE_COVER_WIDTH,
		8  * ImageUtilities.BASE_COVER_WIDTH,
		9  * ImageUtilities.BASE_COVER_WIDTH,
		10 * ImageUtilities.BASE_COVER_WIDTH,
	};

	private final static int HEIGHTS[] = {
		1  * ImageUtilities.BASE_COVER_HEIGHT,
		2  * ImageUtilities.BASE_COVER_HEIGHT,
		3  * ImageUtilities.BASE_COVER_HEIGHT,
		4  * ImageUtilities.BASE_COVER_HEIGHT,
		5  * ImageUtilities.BASE_COVER_HEIGHT,
		6  * ImageUtilities.BASE_COVER_HEIGHT,
		7  * ImageUtilities.BASE_COVER_HEIGHT,
		8  * ImageUtilities.BASE_COVER_HEIGHT,
		9  * ImageUtilities.BASE_COVER_HEIGHT,
		10 * ImageUtilities.BASE_COVER_HEIGHT,
	};
	
	@SuppressWarnings("nls")
	private final static String NAMES[] = {
		WIDTHS[0]  + "x" + HEIGHTS[0] + " (x1)",
		WIDTHS[1]  + "x" + HEIGHTS[1] + " (x2)",
		WIDTHS[2]  + "x" + HEIGHTS[2] + " (x3)",
		WIDTHS[3]  + "x" + HEIGHTS[3] + " (x4)",
		WIDTHS[4]  + "x" + HEIGHTS[4] + " (x5)",
		WIDTHS[5]  + "x" + HEIGHTS[5] + " (x6)",
		WIDTHS[6]  + "x" + HEIGHTS[6] + " (x7)",
		WIDTHS[7]  + "x" + HEIGHTS[7] + " (x8)",
		WIDTHS[8]  + "x" + HEIGHTS[8] + " (x9)",
		WIDTHS[9]  + "x" + HEIGHTS[9] + " (x10)",
	};
	
	private int id;

	private static EnumWrapper<CoverImageSize> wrapper = new EnumWrapper<>(BASE_SIZE);

	private CoverImageSize(int val) {
		id = val;
	}
	
	public static EnumWrapper<CoverImageSize> getWrapper() {
		return wrapper;
	}
	
	@Override
	public int asInt() {
		return id;
	}
	
	public int getWidth() {
		return WIDTHS[asInt()];
	}
	
	public int getHeight() {
		return HEIGHTS[asInt()];
	}

	public static int compare(CoverImageSize s1, CoverImageSize s2) {
		return Integer.compare(s1.asInt(), s2.asInt());
	}
	
	@Override
	public String[] getList() {
		return NAMES;
	}
	
	@Override
	public String asString() {
		return NAMES[asInt()];
	}

	@Override
	public CoverImageSize[] evalues() {
		return CoverImageSize.values();
	}
}
