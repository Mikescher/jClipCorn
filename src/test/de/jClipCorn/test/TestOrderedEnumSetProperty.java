package de.jClipCorn.test;

import de.jClipCorn.properties.CCProperties;
import de.jClipCorn.properties.enumerations.MainFrameColumn;
import de.jClipCorn.properties.enumerations.SeriesFrameColumn;
import de.jClipCorn.properties.types.OrderedEnumSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public class TestOrderedEnumSetProperty extends ClipCornBaseTest {

	@Test
	public void testDefault() {
		var props = CCProperties.createInMemory();

		var v = props.PROP_MAINFRAME_VISIBLE_COLUMNS.getValue();

		assertEquals(MainFrameColumn.getWrapper().allDisplayValuesSorted(), v.Order);
		assertTrue(v.isEnabled(MainFrameColumn.TITLE));
		assertFalse(v.isEnabled(MainFrameColumn.GENRES));
	}

	@Test
	public void testSeriesFrameDefault() {
		var props = CCProperties.createInMemory();

		var v = props.PROP_SERIESFRAME_VISIBLE_COLUMNS.getValue();

		assertEquals(SeriesFrameColumn.getWrapper().allDisplayValuesSorted(), v.Order);
		assertEquals(v.Order, v.getEnabledInOrder());
	}

	@Test
	public void testRoundtrip() {
		var props = CCProperties.createInMemory();

		var order = new ArrayList<>(MainFrameColumn.getWrapper().allDisplayValuesSorted());
		order.remove(MainFrameColumn.YEAR);
		order.add(0, MainFrameColumn.YEAR);

		var value = new OrderedEnumSet<>(order, Set.of(MainFrameColumn.YEAR, MainFrameColumn.TITLE));
		props.PROP_MAINFRAME_VISIBLE_COLUMNS.setValue(value);

		var v = props.PROP_MAINFRAME_VISIBLE_COLUMNS.getValue();
		assertEquals(value, v);
		assertEquals(List.of(MainFrameColumn.YEAR, MainFrameColumn.TITLE), v.getEnabledInOrder());
		assertTrue(props.PROP_MAINFRAME_VISIBLE_COLUMNS.isValue(value));
	}

	@Test
	public void testLegacyFormat() {
		var props = CCProperties.createInMemory();

		props.setProperty("PROP_MAINFRAME_VISIBLE_COLUMNS", "14;1;0");

		var v = props.PROP_MAINFRAME_VISIBLE_COLUMNS.getValue();
		assertEquals(MainFrameColumn.getWrapper().allDisplayValuesSorted(), v.Order);
		assertEquals(List.of(MainFrameColumn.USERSCORE, MainFrameColumn.TITLE, MainFrameColumn.YEAR), v.getEnabledInOrder());
	}

	@Test
	public void testMissingValuesAreInsertedAtDefaultPosition() {
		var props = CCProperties.createInMemory();

		var stored = new ArrayList<>(MainFrameColumn.getWrapper().allDisplayValuesSorted());
		stored.remove(MainFrameColumn.TITLE);
		props.setProperty("PROP_MAINFRAME_VISIBLE_COLUMNS", new OrderedEnumSet<>(stored, List.of()).serialize());

		var v = props.PROP_MAINFRAME_VISIBLE_COLUMNS.getValue();
		assertEquals(MainFrameColumn.getWrapper().allDisplayValuesSorted(), v.Order);
		assertEquals(List.of(MainFrameColumn.TITLE), v.getEnabledInOrder());
	}

	@Test
	public void testInvalidFormatResetsToDefault() {
		var props = CCProperties.createInMemory();

		props.setProperty("PROP_MAINFRAME_VISIBLE_COLUMNS", "+1;2");

		assertEquals(props.PROP_MAINFRAME_VISIBLE_COLUMNS.getDefault(), props.PROP_MAINFRAME_VISIBLE_COLUMNS.getValue());
	}
}
