package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAggregator;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.table.filter.customFilter.operators.CustomOperator;

@SuppressWarnings("nls")
public class TestFilterSerialization extends ClipCornBaseTest {

	@Test
	public void testFilterSerialization() throws Exception {
		String[] T1 = new String[]
		{
			"[0|[30|0,0,3,0],[6|0],[0|],[3|[24|[0|[0|[0|[0|[3|]],[1|[2|]]]]]]],[1|[32|1,1,3,[0|[22|2018-11-23,2018-11-23,3]]]],[2|[33|1,1,3,[0|[16|090,2,1]]]],[6|6]]",
			"[0|[6|0]]",
			"[0|]",
			"[0|[17|I am a group,1]]",
			"[0|[15|1900,1900,3]]",
			"[0|[12|ÄÖÜß,1,1]]",
			"[0|[12|\"&|&,&&\\\"\\\\_\\&&\\\",1,1]]",
		};
		
		for (String s : T1) {
			AbstractCustomFilter f = new CustomAndOperator();
			f.importFromString(s);
			String s2 = f.exportToString();
			
			assertEquals(s, s2);
		}
	}

	@Test
	public void testFilterSerialization2() throws Exception {
		CustomAndOperator parent = new CustomAndOperator();
		
		for (AbstractCustomFilter f : AbstractCustomFilter.getAllSimpleFilter()) {
			parent.add(f);
		}

		for (CustomOperator f : AbstractCustomFilter.getAllOperatorFilter()) {

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllSimpleFilter()) {
				f.add(ff);
			}
			
			parent.add(f);
		}

		for (CustomAggregator fx : AbstractCustomFilter.getAllAggregatorFilter()) {

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllSimpleFilter()) {
				
				CustomAggregator f = (CustomAggregator)fx.createNew();
				
				f.setProcessorFilter(ff);
				parent.add(f);
			}
			
		}
		
		String s1 = parent.exportToString();
		
		AbstractCustomFilter intermed = new CustomAndOperator();
		intermed.importFromString(s1);

		String s2 = intermed.exportToString();
		
		assertEquals(s1, s2);		
	}
}
