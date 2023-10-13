package de.jClipCorn.test;

import de.jClipCorn.features.table.filter.customFilter.CustomCharFilter;
import de.jClipCorn.util.datatypes.CharListMatchType;
import de.jClipCorn.util.datatypes.ElemFieldMatchType;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCStreams;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
@RunWith(JUnitParamsRunner.class)
public class TestCustomCharFilter extends ClipCornBaseTest {

	public static class TestMoviesData{
		private final String             Charset;
		private final CharListMatchType  MatchType;
		private final ElemFieldMatchType Fields;
		private final boolean ExcludeCommonWordStart;
		private final boolean            IgnoreNonFilterable;
		private final String             Title;
		private final String             Zyklus;
		private final int                ZyklusNumber;
		private final boolean            Match;
		private final String             NextChars;

		private TestMoviesData(String CS, CharListMatchType m, ElemFieldMatchType f, boolean EX, boolean IGN, String title, String zyklus, int zyklNum, boolean mtch, String nc) {
			Charset                = CS;
			MatchType              = m;
			Fields                 = f;
			ExcludeCommonWordStart = EX;
			IgnoreNonFilterable    = IGN;
			Title                  = title;
			Zyklus                 = zyklus;
			ZyklusNumber           = zyklNum;
			Match                  = mtch;
			NextChars              = nc;
		}
	}

	public static class TestSeriesData{
		private final String             Charset;
		private final CharListMatchType  MatchType;
		private final boolean ExcludeCommonWordStart;
		private final boolean            IgnoreNonFilterable;
		private final String             Title;
		private final boolean            Match;
		private final String             NextChars;

		private TestSeriesData(String CS, CharListMatchType m, boolean EX, boolean IGN, String title, boolean mtch, String nc) {
			Charset                = CS;
			MatchType              = m;
			ExcludeCommonWordStart = EX;
			IgnoreNonFilterable    = IGN;
			Title                  = title;
			Match                  = mtch;
			NextChars              = nc;
		}

		public TestSeriesData cloneNoMatch() {
			return new TestSeriesData(Charset, MatchType, ExcludeCommonWordStart, IgnoreNonFilterable, Title, false, "");
		}
	}

	private Collection<Object[]> testMoviesDataSupplier()
	{
		var data = new TestMoviesData[]{
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   false, false, "The Shawshank Redemption", "", -1, /**/ true,  "T"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   false, false, "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  false, "The Shawshank Redemption", "", -1, /**/ true,  "S"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  false, "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   false, true,  "The Shawshank Redemption", "", -1, /**/ true,  "T"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   false, true,  "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  true,  "The Shawshank Redemption", "", -1, /**/ true,  "S"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE_AND_ZYKLUS,   true,  true,  "The Shawshank Redemption", "", -1, /**/ true,  "H"),

				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              false, false, "The Shawshank Redemption", "", -1, /**/ true,  "T"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              false, false, "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  false, "The Shawshank Redemption", "", -1, /**/ true,  "S"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  false, "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              false, true,  "The Shawshank Redemption", "", -1, /**/ true,  "T"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              false, true,  "The Shawshank Redemption", "", -1, /**/ true,  "H"),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Redemption", "", -1, /**/ true,  "S"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Redemption", "", -1, /**/ true,  "H"),

				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             false, false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             false, false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  false, "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             false, true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             false, true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("",       CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  true,  "The Shawshank Redemption", "", -1, /**/ false, ""),
				new TestMoviesData("S",      CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,             true,  true,  "The Shawshank Redemption", "", -1, /**/ false, ""),

				new TestMoviesData("",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Redemption", "", -1, /**/ true,  "TSR"),
				new TestMoviesData("",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              false, true,  "The Shawshank Redemption", "", -1, /**/ true,  "TSR"),
				new TestMoviesData("S",      CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,  "HU"),
				new TestMoviesData("T",      CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,  "H"),
				new TestMoviesData("A",      CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ false, ""),
				new TestMoviesData("SHA",    CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,  "W"),

				new TestMoviesData("",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,  "THESAWNKUMR"),
				new TestMoviesData("",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              false, true,  "The Shawshank Summer",     "", -1, /**/ true,  "THESAWNKUMR"),
				new TestMoviesData("S",      CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,  "HU"),
				new TestMoviesData("SA",     CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ false,  ""),
				new TestMoviesData("T",      CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,   "H"),
				new TestMoviesData("T",      CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ true,   "H"),
				new TestMoviesData("T",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,              true,  true,  "The Shawshank Summer",     "", -1, /**/ false,  ""),
				new TestMoviesData("T",      CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,              false, true,  "The Shawshank Summer",     "", -1, /**/ true,   "H"),

				new TestMoviesData("",       CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "CR"),
				new TestMoviesData("",       CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE,            true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "C"),
				new TestMoviesData("",       CharListMatchType.STRING_START,   ElemFieldMatchType.ZYKLUS,           true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "R"),

				new TestMoviesData("C",      CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "H"),
				new TestMoviesData("C",      CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE,            true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "H"),
				new TestMoviesData("C",      CharListMatchType.STRING_START,   ElemFieldMatchType.ZYKLUS,           true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ false, ""),
				new TestMoviesData("CHRON",  CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "I"),
				new TestMoviesData("CHRON",  CharListMatchType.WORD_START,     ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "I"),
				new TestMoviesData("CHRON",  CharListMatchType.ANYWHERE,       ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ true,  "I"),
				new TestMoviesData("CHROS",  CharListMatchType.STRING_START,   ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ false, ""),
				new TestMoviesData("CHROS",  CharListMatchType.WORD_START,     ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ false, ""),
				new TestMoviesData("CHROS",  CharListMatchType.ANYWHERE,       ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Chroniken eines Kriegers", "Riddick", 4, /**/ false, ""),

				new TestMoviesData("B",       CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "A"),
				new TestMoviesData("BA",      CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "R"),
				new TestMoviesData("BR",      CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ false, ""),
				new TestMoviesData("BRU",     CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ false, ""),
				new TestMoviesData("B",       CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "A"),
				new TestMoviesData("BA",      CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "R"),
				new TestMoviesData("BR",      CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ false, ""),
				new TestMoviesData("BRU",     CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ false, ""),
				new TestMoviesData("B",       CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "AR"),
				new TestMoviesData("BA",      CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "R"),
				new TestMoviesData("BR",      CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "U"),
				new TestMoviesData("BRU",     CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Bärenbrüder 2", "Bärenbrüder", 2, /**/ true,  "D"),

				new TestMoviesData("DE",      CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ false,  ""),
				new TestMoviesData("DER",     CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ false,  ""),
				new TestMoviesData("DERGR",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ false,  ""),

				new TestMoviesData("DERGR",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("DERGRO",  CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("DERGROS", CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("GR",      CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("GRO",     CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("GROS",    CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("G",       CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "RA"),
				new TestMoviesData("GR",      CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("GRO",     CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("GROS",    CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("G",       CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "RA"),
				new TestMoviesData("DERGR",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("DERGRO",  CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("DERGROS", CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("GR",      CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("GRO",     CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("GROS",    CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("G",       CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der große Gatsby", "", -1, /**/ true,  "RA"),
				new TestMoviesData("GR",      CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "O"),
				new TestMoviesData("GRO",     CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "S"),
				new TestMoviesData("GROS",    CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "E"),
				new TestMoviesData("G",       CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der große Gatsby", "", -1, /**/ true,  "RA"),

				new TestMoviesData("#",       CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der 13te Krieger", "", -1, /**/ true,  "3"),
				new TestMoviesData("#;#",     CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der 13te Krieger", "", -1, /**/ true,  "t"),
				new TestMoviesData("#",       CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der 13te Krieger", "", -1, /**/ true,  "3t"),
				new TestMoviesData("D;E;R;#", CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der 13te Krieger", "", -1, /**/ false, ""),
				new TestMoviesData("D;E;R",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Der 13te Krieger", "", -1, /**/ false, ""),
				new TestMoviesData("D;E;R;#", CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der 13te Krieger", "", -1, /**/ true,  "3"),
				new TestMoviesData("D;E;R",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, false, true,  "Der 13te Krieger", "", -1, /**/ true,  "1"),

				new TestMoviesData("OCEAN",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEAN",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEAN",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEAN",   CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEAN",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEAN",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "S"),
				new TestMoviesData("OCEANS",  CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANS",  CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANS",  CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANS",  CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANS",  CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANS",  CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "E"),
				new TestMoviesData("OCEANSE", CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),
				new TestMoviesData("OCEANSE", CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),
				new TestMoviesData("OCEANSE", CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE_AND_ZYKLUS, true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),
				new TestMoviesData("OCEANSE", CharListMatchType.STRING_START,  ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),
				new TestMoviesData("OCEANSE", CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),
				new TestMoviesData("OCEANSE", CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Ocean's Eleven", "Ocean", 1, /**/ true,  "L"),

				new TestMoviesData("BT;UR",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "LA"),
				new TestMoviesData("BX;UR",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "L"),
				new TestMoviesData("XT;UR",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "A"),
				new TestMoviesData("XY;UR",   CharListMatchType.ANYWHERE,      ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ false, ""),
				new TestMoviesData("BT;UR",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "LA"),
				new TestMoviesData("BX;UR",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "L"),
				new TestMoviesData("XT;UR",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ true,  "A"),
				new TestMoviesData("XY;UR",   CharListMatchType.WORD_START,    ElemFieldMatchType.TITLE,            true,  true,  "Bullet Train", "", -1, /**/ false, ""),

				new TestMoviesData("AEI;VEFO",   CharListMatchType.ANYWHERE,   ElemFieldMatchType.TITLE,            true,  true,  "Grave of Fireflies", "", -1, /**/ true,  "ELSF"),
				new TestMoviesData("AEIO;VOF;E", CharListMatchType.ANYWHERE,   ElemFieldMatchType.TITLE,            true,  true,  "Grave of Fireflies", "", -1, /**/ true,  "O"),

				new TestMoviesData("",          CharListMatchType.ANYWHERE,     ElemFieldMatchType.ZYKLUS,           true,  true,  "Pig", "",           -1, /**/ false, ""),
				new TestMoviesData("",          CharListMatchType.ANYWHERE,     ElemFieldMatchType.ZYKLUS,           true,  true,  "Turbo", "New Kids",  2, /**/ true,  "NEWKIDS"),
				new TestMoviesData("",          CharListMatchType.WORD_START,   ElemFieldMatchType.ZYKLUS,           true,  true,  "Pig", "",           -1, /**/ false, ""),
				new TestMoviesData("",          CharListMatchType.WORD_START,   ElemFieldMatchType.ZYKLUS,           true,  true,  "Turbo", "New Kids",  2, /**/ true,  "NK"),
				new TestMoviesData("",          CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,           true,  true,  "Pig", "",           -1, /**/ false, ""),
				new TestMoviesData("",          CharListMatchType.STRING_START, ElemFieldMatchType.ZYKLUS,           true,  true,  "Turbo", "New Kids",  2, /**/ true,  "N"),

				new TestMoviesData("PIG",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PIG",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("P;I;G",     CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("PQ;IJ;EFG", CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),

				new TestMoviesData("IG",        CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("I;G",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IG",        CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("I;G",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IG",        CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("I;G",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IG",        CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            false, false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("I;G",       CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ true,  ""),
				new TestMoviesData("IG",        CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IG",        CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("I;G",       CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("IJ;EFG",    CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  false, "Pig", "", -1, /**/ false, ""),

				new TestMoviesData("PIGS",      CharListMatchType.STRING_START, ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("PIGS",      CharListMatchType.WORD_START,   ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
				new TestMoviesData("PIGS",      CharListMatchType.ANYWHERE,     ElemFieldMatchType.TITLE,            true,  true,  "Pig", "", -1, /**/ false, ""),
		};

		return CCStreams.iterate(data).map((idx, d) -> {
			var key = String.format("[%d]: {%s|%s|%d}@[%s]/(%s,%s,%b,%b) --> (%b, '%s')", idx, d.Title, d.Zyklus, d.ZyklusNumber, d.Charset, d.MatchType.toString(), d.Fields.toString(), d.ExcludeCommonWordStart, d.IgnoreNonFilterable, d.Match, d.NextChars);
			return new Object[]{key, d};
		}).toList();
	}

	private Collection<Object[]> testSeriesDataSupplier()
	{
		var data = new TestSeriesData[]{
				new TestSeriesData("",     CharListMatchType.STRING_START, false, false, "The Shawshank Redemption", /**/ true,  "T"),
				new TestSeriesData("T",    CharListMatchType.STRING_START, false, false, "The Shawshank Redemption", /**/ true,  "H"),
				new TestSeriesData("",     CharListMatchType.STRING_START, true,  false, "The Shawshank Redemption", /**/ true,  "S"),
				new TestSeriesData("T",    CharListMatchType.STRING_START, true,  false, "The Shawshank Redemption", /**/ false, ""),
				new TestSeriesData("S",    CharListMatchType.STRING_START, true,  false, "The Shawshank Redemption", /**/ true,  "H"),
				new TestSeriesData("",     CharListMatchType.STRING_START, false, true,  "The Shawshank Redemption", /**/ true,  "T"),
				new TestSeriesData("T",    CharListMatchType.STRING_START, false, true,  "The Shawshank Redemption", /**/ true,  "H"),
				new TestSeriesData("",     CharListMatchType.STRING_START, true,  true,  "The Shawshank Redemption", /**/ true,  "S"),
				new TestSeriesData("T",    CharListMatchType.STRING_START, true,  true,  "The Shawshank Redemption", /**/ false, ""),
				new TestSeriesData("S",    CharListMatchType.STRING_START, true,  true,  "The Shawshank Redemption", /**/ true,  "H"),

				new TestSeriesData("RED",  CharListMatchType.ANYWHERE,     true,  true,  "The Shawshank Redemption", /**/ true,  "E"),
				new TestSeriesData("SH",   CharListMatchType.ANYWHERE,     true,  true,  "The Shawshank Redemption", /**/ true,  "A"),
				new TestSeriesData("RET",  CharListMatchType.ANYWHERE,     true,  true,  "The Shawshank Redemption", /**/ false, ""),
		};

		return CCStreams
				.iterate(data)
				.flatten(p -> {
					var v = new ArrayList<Tuple<TestSeriesData, ElemFieldMatchType>>();
					v.add(Tuple.Create(p, ElemFieldMatchType.TITLE));
					v.add(Tuple.Create(p, ElemFieldMatchType.TITLE_AND_ZYKLUS));
					v.add(Tuple.Create(p.cloneNoMatch(), ElemFieldMatchType.ZYKLUS));
					return CCStreams.iterate(v);
				})
				.map((idx, dat) -> {
					var d = dat.Item1;
					var efmt = dat.Item2;
					var key = String.format("[%d.%s]: {%s}@[%s]/(%s,%s,%b,%b) --> (%b, '%s')", idx/3, idx%3, d.Title, d.Charset, d.MatchType.toString(), efmt.toString(), d.ExcludeCommonWordStart, d.IgnoreNonFilterable, d.Match, d.NextChars);
					return new Object[]{key, d, efmt};
				})
				.toList();
	}

	@Test
	@Parameters(method = "testMoviesDataSupplier")
	@TestCaseName("testMovies{0}")
	public void testMovies(String _f, TestMoviesData d) {

		var ml = createEmptyDB();

		var mov = ml.createNewEmptyMovie();
		mov.title().set(d.Title);
		mov.zyklus().set(d.Zyklus, d.ZyklusNumber);

		var dcs = new String[]{};
		if (!d.Charset.isEmpty()) {
			if (d.Charset.contains(";") || d.Charset.contains("#")) {
				dcs = Arrays.stream(d.Charset.split(";")).map(p -> p.equals("#") ? "0123456789" : p).toArray(String[]::new);
			} else {
				dcs = d.Charset.split("");
			}
		}

		var csf = CustomCharFilter.create(ml, dcs, d.MatchType, d.Fields, d.ExcludeCommonWordStart, d.IgnoreNonFilterable);

		var match = csf.isMatch(mov);
		var hs = new HashSet<Character>();
		csf.collectNextChars(mov, hs);

		assertEquals("isMatch", d.Match, match);

		var csExpected = Arrays.stream(d.NextChars.split("")).sorted().map(String::toUpperCase).collect(Collectors.joining(""));
		var csActual = hs.stream().map(Object::toString).sorted().map(String::toUpperCase).collect(Collectors.joining(""));

		assertEquals("nextChars", csExpected, csActual);
	}

	@Test
	@Parameters(method = "testSeriesDataSupplier")
	@TestCaseName("testSeries{0}")
	public void testSeries(String _f, TestSeriesData d, ElemFieldMatchType efmt) {

		var ml = createEmptyDB();

		var ser = ml.createNewEmptySeries();
		ser.title().set(d.Title);

		var dcs = new String[]{};
		if (!d.Charset.isEmpty()) {
			if (d.Charset.contains(";") || d.Charset.contains("#")) {
				dcs = Arrays.stream(d.Charset.split(";")).map(p -> p.equals("#") ? "0123456789" : p).toArray(String[]::new);
			} else {
				dcs = d.Charset.split("");
			}
		}

		var csf = CustomCharFilter.create(ml, dcs, d.MatchType, efmt, d.ExcludeCommonWordStart, d.IgnoreNonFilterable);

		var match = csf.isMatch(ser);
		var hs = new HashSet<Character>();
		csf.collectNextChars(ser, hs);

		assertEquals("isMatch", d.Match, match);

		var csExpected = Arrays.stream(d.NextChars.split("")).sorted().map(String::toUpperCase).collect(Collectors.joining(""));
		var csActual = hs.stream().map(Object::toString).sorted().map(String::toUpperCase).collect(Collectors.joining(""));

		assertEquals("nextChars", csExpected, csActual);
	}

}
