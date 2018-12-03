package de.jClipCorn.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.table.filter.AbstractCustomFilter;
import de.jClipCorn.table.filter.customFilter.aggregators.CustomAggregator;
import de.jClipCorn.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.table.filter.filterConfig.CustomFilterConfig;

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
			"\n [0|[31|6] , [2|[31\t|\t\t7]]] \n\n",
		};
		
		for (String s : T1) {
			AbstractCustomFilter f = new CustomAndOperator();
			assertTrue(f.importFromString(s));
			String s2 = f.exportToString();

			AbstractCustomFilter f2 = new CustomAndOperator();
			assertTrue(f2.importFromString(s2));
			String s3 = f2.exportToString();
			
			assertEquals(s2, s3);
		}
	}

	@Test
	public void testFilterSerialization2() throws Exception {
		String[] T1 = new String[]
		{
			"[0|[30|0,0,3,0],[6|0],[0],[3|[24|[0|[0|[0|[0|[3]],[1|[2]]]]]]],[1|[32|1,1,3,[0|[22|20181123,20181123,3]]]],[2|[33|1,1,3,[0|[16|090,2,1]]]],[6|6]]",
			"[0|[6|0]]",
			"[0]",
			"[0|[17|073032097109032097032103114111117112,1]]",
			"[0|[15|1900,1900,3]]",
			"[0|[12|195132195150195156195159,1,1]]",
			"[0|[12|034124044038092034092092095092038092034,1,1]]",
			"[0|[12|034124044038092034092092095092038092034,1,1],[12|0,1,1],[12|195132195150195156126123125,2,1]]"
		};
		
		for (String s : T1) {
			AbstractCustomFilter f = new CustomAndOperator();
			assertTrue(f.importFromString(s));
			String s2 = f.exportToString();
			
			assertEquals(s, s2);
		}
	}

	@Test
	public void testFilterSerialization3() throws Exception {
		CustomAndOperator parent = new CustomAndOperator();
		
		for (AbstractCustomFilter f : AbstractCustomFilter.getAllSimpleFilter()) {
			parent.add(f);
		}

		for (CustomOperator f : AbstractCustomFilter.getAllOperatorFilter()) {

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllSimpleFilter()) {
				f.add(ff);
			}

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllOperatorFilter()) {
				f.add(ff);
			}

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllAggregatorFilter()) {
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
		assertTrue(intermed.importFromString(s1));

		String s2 = intermed.exportToString();
		
		assertEquals(s1, s2);		
	}

	@Test
	public void testFilterSerialization4() throws Exception {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 32; i++) {

			Random r = new Random(142857 + i);
			
			for (AbstractCustomFilter f : AbstractCustomFilter.getAllSimpleFilter()) {

				CustomAndOperator op = new CustomAndOperator();
				
				for (CustomFilterConfig cfg : f.createConfig(ml)) cfg.setValueRandom(r);

				op.add(f);
				
				String s1 = op.exportToString();
				AbstractCustomFilter intermed = new CustomAndOperator();
				assertTrue(intermed.importFromString(s1));
				String s2 = intermed.exportToString();
				assertEquals(s1, s2);	
			}
			
			for (CustomOperator f0 : AbstractCustomFilter.getAllOperatorFilter()) {

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter()) {

					CustomAndOperator op = new CustomAndOperator();

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.add(f1);
					
					op.add(f0);
					
					String s1 = op.exportToString();
					AbstractCustomFilter intermed = new CustomAndOperator();
					assertTrue(intermed.importFromString(s1));
					String s2 = intermed.exportToString();
					assertEquals(s1, s2);	
				}
				
			}
			
			for (CustomOperator f0 : AbstractCustomFilter.getAllOperatorFilter()) {

				CustomAndOperator op = new CustomAndOperator();

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter()) {

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.add(f1);
					
					op.add(f0);
				}
				
				String s1 = op.exportToString();
				AbstractCustomFilter intermed = new CustomAndOperator();
				assertTrue(intermed.importFromString(s1));
				String s2 = intermed.exportToString();
				assertEquals(s1, s2);	
				
			}
			
			for (CustomAggregator f0 : AbstractCustomFilter.getAllAggregatorFilter()) {

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter()) {

					CustomAndOperator op = new CustomAndOperator();

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.setProcessorFilter(f1);
					
					op.add(f0);
					
					String s1 = op.exportToString();
					AbstractCustomFilter intermed = new CustomAndOperator();
					assertTrue(intermed.importFromString(s1));
					String s2 = intermed.exportToString();
					assertEquals(s1, s2);	
				}
				
			}
		}
	}
}
