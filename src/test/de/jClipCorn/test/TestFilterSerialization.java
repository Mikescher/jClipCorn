package de.jClipCorn.test;

import de.jClipCorn.database.CCMovieList;
import de.jClipCorn.features.table.filter.AbstractCustomFilter;
import de.jClipCorn.features.table.filter.customFilter.aggregators.CustomAggregator;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomAndOperator;
import de.jClipCorn.features.table.filter.customFilter.operators.CustomOperator;
import de.jClipCorn.features.table.filter.filterConfig.CustomFilterConfig;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("nls")
public class TestFilterSerialization extends ClipCornBaseTest {

	@Test
	public void testFilterSerialization() {
		CCMovieList ml = createEmptyDB();

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
			AbstractCustomFilter f = new CustomAndOperator(ml);
			assertTrue(f.importFromString(s));
			String s2 = f.exportToString();

			AbstractCustomFilter f2 = new CustomAndOperator(ml);
			assertTrue(f2.importFromString(s2));
			String s3 = f2.exportToString();
			
			assertEquals(s2, s3);
		}
	}

	@Test
	public void testFilterSerialization2() {
		CCMovieList ml = createEmptyDB();

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
			AbstractCustomFilter f = new CustomAndOperator(ml);
			assertTrue(f.importFromString(s));
			String s2 = f.exportToString();
			
			assertEquals(s, s2);
		}
	}

	@Test
	public void testFilterSerialization3() {
		CCMovieList ml = createEmptyDB();

		CustomAndOperator parent = new CustomAndOperator(ml);
		
		for (AbstractCustomFilter f : AbstractCustomFilter.getAllSimpleFilter(ml)) {
			parent.add(f);
		}

		for (CustomOperator f : AbstractCustomFilter.getAllOperatorFilter(ml)) {

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllSimpleFilter(ml)) {
				f.add(ff);
			}

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllOperatorFilter(ml)) {
				f.add(ff);
			}

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllAggregatorFilter(ml)) {
				f.add(ff);
			}
			
			parent.add(f);
		}

		for (CustomAggregator fx : AbstractCustomFilter.getAllAggregatorFilter(ml)) {

			for (AbstractCustomFilter ff : AbstractCustomFilter.getAllSimpleFilter(ml)) {
				
				CustomAggregator f = (CustomAggregator)fx.createNew(ml);
				
				f.setProcessorFilter(ff);
				parent.add(f);
			}
			
		}
		
		String s1 = parent.exportToString();
		
		AbstractCustomFilter intermed = new CustomAndOperator(ml);
		assertTrue(intermed.importFromString(s1));

		String s2 = intermed.exportToString();
		
		assertEquals(s1, s2);		
	}

	@Test
	public void testFilterSerialization4() {
		CCMovieList ml = createEmptyDB();

		for (int i = 0; i < 32; i++) {

			Random r = new Random(142857 + i);
			
			for (AbstractCustomFilter f : AbstractCustomFilter.getAllSimpleFilter(ml)) {

				CustomAndOperator op = new CustomAndOperator(ml);
				
				for (CustomFilterConfig cfg : f.createConfig(ml)) cfg.setValueRandom(r);

				op.add(f);
				
				String s1 = op.exportToString();
				AbstractCustomFilter intermed = new CustomAndOperator(ml);
				assertTrue(intermed.importFromString(s1));
				String s2 = intermed.exportToString();
				assertEquals(s1, s2);	
			}
			
			for (CustomOperator f0 : AbstractCustomFilter.getAllOperatorFilter(ml)) {

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter(ml)) {

					CustomAndOperator op = new CustomAndOperator(ml);

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.add(f1);
					
					op.add(f0);
					
					String s1 = op.exportToString();
					AbstractCustomFilter intermed = new CustomAndOperator(ml);
					assertTrue(intermed.importFromString(s1));
					String s2 = intermed.exportToString();
					assertEquals(s1, s2);	
				}
				
			}
			
			for (CustomOperator f0 : AbstractCustomFilter.getAllOperatorFilter(ml)) {

				CustomAndOperator op = new CustomAndOperator(ml);

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter(ml)) {

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.add(f1);
					
					op.add(f0);
				}
				
				String s1 = op.exportToString();
				AbstractCustomFilter intermed = new CustomAndOperator(ml);
				assertTrue(intermed.importFromString(s1));
				String s2 = intermed.exportToString();
				assertEquals(s1, s2);	
				
			}
			
			for (CustomAggregator f0 : AbstractCustomFilter.getAllAggregatorFilter(ml)) {

				for (AbstractCustomFilter f1 : AbstractCustomFilter.getAllSimpleFilter(ml)) {

					CustomAndOperator op = new CustomAndOperator(ml);

					for (CustomFilterConfig cfg : f0.createConfig(ml)) cfg.setValueRandom(r);
					for (CustomFilterConfig cfg : f1.createConfig(ml)) cfg.setValueRandom(r);

					f0.setProcessorFilter(f1);
					
					op.add(f0);
					
					String s1 = op.exportToString();
					AbstractCustomFilter intermed = new CustomAndOperator(ml);
					assertTrue(intermed.importFromString(s1));
					String s2 = intermed.exportToString();
					assertEquals(s1, s2);	
				}
				
			}
		}
	}
}
