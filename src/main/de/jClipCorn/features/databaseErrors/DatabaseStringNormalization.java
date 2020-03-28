package de.jClipCorn.features.databaseErrors;

import de.jClipCorn.util.datatypes.Tuple;

import java.util.Arrays;
import java.util.List;

public class DatabaseStringNormalization
{
	private final static List<Tuple<Character, Character>> CHAR_REPLACEMENTS = Arrays.asList
	(
		new Tuple<>('\u00A0', ' '), // NO-BREAK SPACE
		new Tuple<>('\u2000', ' '), // EN QUAD
		new Tuple<>('\u2001', ' '), // EM QUAD
		new Tuple<>('\u2002', ' '), // EN SPACE (nut)
		new Tuple<>('\u2003', ' '), // EM SPACE (mutton)
		new Tuple<>('\u2004', ' '), // THREE-PER-EM SPACE (thick space)
		new Tuple<>('\u2005', ' '), // FOUR-PER-EM SPACE (mid space)
		new Tuple<>('\u2006', ' '), // SIX-PER-EM SPACE
		new Tuple<>('\u2007', ' '), // FIGURE SPACE
		new Tuple<>('\u2008', ' '), // PUNCTUATION SPACE
		new Tuple<>('\u2009', ' '), // THIN SPACE
		new Tuple<>('\u200A', ' '), // HAIR SPACE
		new Tuple<>('\u202F', ' '), // NARROW NO-BREAK SPACE
		new Tuple<>('\u205F', ' '), // MEDIUM MATHEMATICAL SPACE
		new Tuple<>('\u3000', ' '), // IDEOGRAPHIC SPACE

		new Tuple<>('\u05BE', '-'), // HEBREW PUNCTUATION MAQAF
		new Tuple<>('\u1806', '-'), // MONGOLIAN SOFT HYPHEN
		new Tuple<>('\u2010', '-'), // HYPHEN
		new Tuple<>('\u2011', '-'), // NON-BREAKING HYPHEN
		new Tuple<>('\u2012', '-'), // FIGURE DASH
		new Tuple<>('\u2013', '-'), // EN DASH
		new Tuple<>('\u2014', '-'), // EM DASH
		new Tuple<>('\u2015', '-'), // HORIZONTAL BAR
		new Tuple<>('\u2E3A', '-'), // TWO-EM DASH
		new Tuple<>('\u2E3B', '-'), // THREE-EM DASH
		new Tuple<>('\u2E40', '-'), // DOUBLE HYPHEN
		new Tuple<>('\uFE58', '-'), // SMALL EM DASH
		new Tuple<>('\uFE63', '-'), // SMALL HYPHEN-MINUS
		new Tuple<>('\uFF0D', '-')  // FULLWIDTH HYPHEN-MINUS
	);

	public static boolean hasInvalidCharacters(String value)
	{
		for(var tpl : CHAR_REPLACEMENTS)
		{
			if (value.contains(Character.toString(tpl.Item1))) return true;
		}

		return false;
	}

	public static String fixInvalidCharacters(String value)
	{
		for(var tpl : CHAR_REPLACEMENTS) value = value.replace(tpl.Item1, tpl.Item2);

		return value;
	}
}
