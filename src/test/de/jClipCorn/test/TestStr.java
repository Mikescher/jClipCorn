package de.jClipCorn.test;

import de.jClipCorn.util.Str;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("nls")
public class TestStr extends ClipCornBaseTest {

	@Test
	public void testCommonPrefix() {
		assertEquals("", Str.getCommonPrefix(lst()));

		assertEquals("", Str.getCommonPrefix(lst("")));
		assertEquals("", Str.getCommonPrefix(lst("", "ABX", "ABC")));
		assertEquals("", Str.getCommonPrefix(lst("", "ABX", "AbC")));
		assertEquals("", Str.getCommonPrefix(lst("", "ABX", "Abc")));
		assertEquals("", Str.getCommonPrefix(lst("", "ABX", "abc")));
		assertEquals("", Str.getCommonPrefix(lst("", "abx", "abc")));

		assertEquals("AB", Str.getCommonPrefix(lst("ABX", "ABC")));
		assertEquals("A", Str.getCommonPrefix(lst("ABX", "AbC")));
		assertEquals("A", Str.getCommonPrefix(lst("ABX", "Abc")));
		assertEquals("AB", Str.getCommonPrefix(lst("ABX", "ABc")));
		assertEquals("", Str.getCommonPrefix(lst("ABX", "abc")));
		assertEquals("a", Str.getCommonPrefix(lst("aBX", "abc")));
		assertEquals("ab", Str.getCommonPrefix(lst("abX", "abc")));
		assertEquals("ab", Str.getCommonPrefix(lst("abx", "abc")));

		assertEquals("Hello", Str.getCommonPrefix(lst("Hello", "Hello World")));
		assertEquals("H", Str.getCommonPrefix(lst("Hello", "HELLO World")));
		assertEquals("HE", Str.getCommonPrefix(lst("HEllo", "HELLO World")));
		assertEquals("", Str.getCommonPrefix(lst("HEllo", "HELLO World", "hello")));

		assertEquals("H", Str.getCommonPrefix(lst("Hello", "Hello World", "H")));
		assertEquals("", Str.getCommonPrefix(lst("Hello", "Hello World", "h")));

		assertEquals("", Str.getCommonPrefix(lst("Hello", "Hello World", "", "H")));

		assertEquals("M", Str.getCommonPrefix(lst("Mamma", "Mia")));
		assertEquals("", Str.getCommonPrefix(lst("Mamma", "mia")));
		assertEquals("", Str.getCommonPrefix(lst("Hello", "World")));
		assertEquals("", Str.getCommonPrefix(lst("Hello", "world")));

		assertEquals("", Str.getCommonPrefix(lst("Hello", "Harambe", "World")));

		assertEquals("Hello world", Str.getCommonPrefix(lst("Hello world", "Hello world again", "Hello world the third time")));
		assertEquals("Hello ", Str.getCommonPrefix(lst("Hello world", "Hello WORLD again", "Hello world the third time")));
		assertEquals("", Str.getCommonPrefix(lst("hello world", "Hello WORLD again", "Hello world the third time")));
		assertEquals(" ", Str.getCommonPrefix(lst(" ", " ", " ")));
	}

	@Test
	public void testCommonPrefixIgnoreCase() {
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst()));

		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("", "ABX", "ABC")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("", "ABX", "AbC")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("", "ABX", "Abc")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("", "ABX", "abc")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("", "abx", "abc")));

		assertEquals("AB", Str.getCommonPrefixIgnoreCase(lst("ABX", "ABC")));
		assertEquals("AB", Str.getCommonPrefixIgnoreCase(lst("ABX", "AbC")));
		assertEquals("AB", Str.getCommonPrefixIgnoreCase(lst("ABX", "Abc")));
		assertEquals("AB", Str.getCommonPrefixIgnoreCase(lst("ABX", "ABc")));
		assertEquals("AB", Str.getCommonPrefixIgnoreCase(lst("ABX", "abc")));
		assertEquals("aB", Str.getCommonPrefixIgnoreCase(lst("aBX", "abc")));
		assertEquals("ab", Str.getCommonPrefixIgnoreCase(lst("abX", "abc")));
		assertEquals("ab", Str.getCommonPrefixIgnoreCase(lst("abx", "abc")));

		assertEquals("Hello", Str.getCommonPrefixIgnoreCase(lst("Hello", "Hello World")));
		assertEquals("Hello", Str.getCommonPrefixIgnoreCase(lst("Hello", "HELLO World")));
		assertEquals("HEllo", Str.getCommonPrefixIgnoreCase(lst("HEllo", "HELLO World")));
		assertEquals("HEllo", Str.getCommonPrefixIgnoreCase(lst("HEllo", "HELLO World", "hello")));

		assertEquals("H", Str.getCommonPrefixIgnoreCase(lst("Hello", "Hello World", "H")));
		assertEquals("H", Str.getCommonPrefixIgnoreCase(lst("Hello", "Hello World", "h")));

		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("Hello", "Hello World", "", "H")));

		assertEquals("M", Str.getCommonPrefixIgnoreCase(lst("Mamma", "Mia")));
		assertEquals("M", Str.getCommonPrefixIgnoreCase(lst("Mamma", "mia")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("Hello", "World")));
		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("Hello", "world")));

		assertEquals("", Str.getCommonPrefixIgnoreCase(lst("Hello", "Harambe", "World")));

		assertEquals("Hello world", Str.getCommonPrefixIgnoreCase(lst("Hello world", "Hello world again", "Hello world the third time")));
		assertEquals("Hello world", Str.getCommonPrefixIgnoreCase(lst("Hello world", "Hello WORLD again", "Hello world the third time")));
		assertEquals("hello world", Str.getCommonPrefixIgnoreCase(lst("hello world", "Hello WORLD again", "Hello world the third time")));
		assertEquals(" ", Str.getCommonPrefixIgnoreCase(lst(" ", " ", " ")));
	}

	private List<String> lst(String... v) {
		return List.of(v);
	}
}
