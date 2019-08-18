package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.jClipCorn.database.util.iterators.GenresIterator;
import de.jClipCorn.util.Str;
import de.jClipCorn.util.exceptions.EnumFormatException;
import de.jClipCorn.util.exceptions.GenreOverflowException;
import de.jClipCorn.util.stream.CCStream;

public class CCGenreList {
	public static final CCGenreList EMPTY = new CCGenreList();

	private final static long[] MASK = {0x00000000000000FFL, 
										0x000000000000FF00L, 
										0x0000000000FF0000L, 
										0x00000000FF000000L, 
										0x000000FF00000000L, 
										0x0000FF0000000000L, 
										0x00FF000000000000L, 
										0xFF00000000000000L};
	private final static int SIZE = 8; 	// Number of Genres in List
	private final static int GSIZE = 8;	// Bitsize of a Genre
	
	private final long genres;
	
	private CCGenreList() {
		genres = 0x0000000000000000L;
	}
	
	public CCGenreList(long lrep) {
		genres = lrep;
	}

	public CCGenreList(CCGenre genre1) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		_g = calcAddGenre(_g, genre4);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		_g = calcAddGenre(_g, genre4);
		_g = calcAddGenre(_g, genre5);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		_g = calcAddGenre(_g, genre4);
		_g = calcAddGenre(_g, genre5);
		_g = calcAddGenre(_g, genre6);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6, CCGenre genre7) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		_g = calcAddGenre(_g, genre4);
		_g = calcAddGenre(_g, genre5);
		_g = calcAddGenre(_g, genre6);
		_g = calcAddGenre(_g, genre7);
		genres = _g.get();
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6, CCGenre genre7, CCGenre genre8) {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		_g = calcAddGenre(_g, genre1);
		_g = calcAddGenre(_g, genre2);
		_g = calcAddGenre(_g, genre3);
		_g = calcAddGenre(_g, genre4);
		_g = calcAddGenre(_g, genre5);
		_g = calcAddGenre(_g, genre6);
		_g = calcAddGenre(_g, genre7);
		_g = calcAddGenre(_g, genre8);
		genres = _g.get();
	}

	public CCGenreList(List<CCGenre> data) {
		long _g = 0x0000000000000000L;
		
		for (CCGenre g : data) _g = calcAddGenreSafe(_g, g.asInt());

		genres = _g;
	}

	public static CCGenreList create(CCGenre... list) {
		return new CCGenreList(Arrays.asList(list));
	}

	private int getGenreInt(int idx) {
		return (int) ((genres & MASK[idx]) >> idx*GSIZE);
	}
	
	public CCGenre getGenre(int idx) {
		if (idx >= 0 && idx < SIZE) {
			return CCGenre.getWrapper().findOrFatalError(getGenreInt(idx));
		} else {
			return null;
		}
	}
	
	public List<CCGenre> getGenres() {
		List<CCGenre> result = new ArrayList<>();

		int sz = getGenreCount();
		for(int i = 0; i < sz; i++) {
			result.add(getGenre(i));
		}
		
		return result;
	}
	
	public int getGenreCount() {
		int i = 0;
		while(i < SIZE) {
			if (getGenreInt(i) == CCGenre.NO_GENRE) return i;
			i++;
		}
		return i;
	}
	
	public String asString() {
		return getGenres().stream().map(CCGenre::asString).collect(Collectors.joining("|")); //$NON-NLS-1$
	}
	
	/*
	 * This is not the same as x.asSorted().asString()
	 * 
	 * asSorted sorts based on the ID, this sorts based on translation
	 */
	public String asSortedString() {
		return getGenres().stream().sorted(CCGenre.getTextComparator()).map(CCGenre::asString).collect(Collectors.joining("|")); //$NON-NLS-1$
	}
	
	public String asSimpleString() {
		return getGenres().stream().map(g -> g + "").collect(Collectors.joining("|")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public String asSimpleComparableString() {
		return getGenres()
			.stream()
			.map(g -> g.asInt())
			.sorted()
			.map(g -> Integer.toString(g))
			.collect(Collectors.joining("|")); //$NON-NLS-1$
	}
	
	public CCGenreList getSorted() {
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		
		int count = getGenreCount();
		
		int actual = getGenre(0).asInt();
		
		for (int i = 0; i < count; i++) {
			if (getGenre(i).asInt() < actual) {
				actual = getGenre(i).asInt();
			}
		}
		
		_g = calcAddGenre(_g, actual);
		int last = actual;
		
		for (int i = 0; i < (count-1); i++) {
			actual = Integer.MAX_VALUE;
			for (int j = 0; j < count; j++) {
				if (getGenre(j).asInt() > last) {
					if (getGenre(j).asInt() < actual) {
						actual = getGenre(j).asInt();
					}
				}
			}
			_g = calcAddGenre(_g, actual);
			last = actual;
		}
		
		return new CCGenreList(_g.get());
	}
	
	public long getAllGenres() {
		return genres;
	}

	@Override
	public String toString() {
		return asString();
	}

	public static int compare(CCGenreList o1, CCGenreList o2) {
		return Long.compare(o1.getSorted().getAllGenres(), o2.getSorted().getAllGenres());
	}

	public static int getMaxListSize() {
		return SIZE;
	}
	
	public boolean includes(CCGenre g) {
		for (int i = 0; i < getGenreCount(); i++) {
			if (g.equals(getGenre(i))) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isEmpty() {
		return getGenreCount() == 0;
	}

	public boolean isFull() {
		return getGenreCount() == SIZE;
	}

	public CCStream<CCGenre> iterate() {
		return new GenresIterator(this);
	}

	@Override
	public int hashCode() {
		return asSimpleComparableString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof CCGenreList))
			return false;
		CCGenreList other = (CCGenreList) obj;
		return equals(other);
	}
	
	public boolean equals(CCGenreList other) {
		return asSimpleComparableString().equals(other.asSimpleComparableString());
	}
	
	private static long calcSetGenreInt(long rawval, int idx, int val) {
		return (rawval & ~MASK[idx]) | ((val*1L) << (idx*GSIZE));
	}
	
	private static Optional<Long> calcSetGenre(long rawval, int idx, int genre) {
		if (idx >= 0 && idx < SIZE) {
			return Optional.of(calcSetGenreInt(rawval, idx, genre));
		} else {
			return Optional.empty();
		}
	}
	
	private static Long calcAddGenreSafe(long rawval, int genre) {
		int size = calcGetGenreCount(rawval);
		if (size < SIZE) {
			return calcSetGenreInt(rawval, size, genre);
		} else {
			return rawval;
		}
	}
	
	private static Optional<Long> calcAddGenre(long rawval, int genre) {
		int size = calcGetGenreCount(rawval);
		if (size < SIZE) {
			return calcSetGenre(rawval, size, genre);
		} else {
			return Optional.empty();
		}
	}
	
	public static Optional<Long> calcAddGenre(long rawval, CCGenre val) {
		return calcAddGenre(rawval, val.asInt());
	}
	
	public static Optional<Long> calcAddGenre(Optional<Long> rawval, CCGenre val) {
		if (!rawval.isPresent()) return Optional.empty();
		return calcAddGenre(rawval.get(), val.asInt());
	}
	
	public static Optional<Long> calcAddGenre(Optional<Long> rawval, int val) {
		if (!rawval.isPresent()) return Optional.empty();
		return calcAddGenre(rawval.get(), val);
	}

	private static int calcGetGenreCount(long rawval) {
		int i = 0;
		while(i < SIZE) {
			if (calcGetGenreInt(rawval, i) == CCGenre.NO_GENRE) return i;
			i++;
		}
		return i;
	}
	
	private static int calcGetGenreInt(long rawval, int idx) {
		return (int) ((rawval & MASK[idx]) >> idx*GSIZE);
	}

	public CCGenreList getSetGenre(int idx, CCGenre genre) {
		Optional<Long> _g = calcSetGenre(genres, idx, genre.asInt());
		return _g.map(CCGenreList::new).orElse(null);
	}

	public CCGenreList getTryAddGenre(CCGenre genre){
		Optional<Long> _g = calcAddGenre(genres, genre);
		return _g.map(CCGenreList::new).orElse(this);
	}

	public CCGenreList getAddGenre(CCGenre genre){
		Optional<Long> _g = calcAddGenre(genres, genre.asInt());
		return _g.map(CCGenreList::new).orElse(null);
	}

	public String serialize() {
		return iterate().stringjoin(g -> String.valueOf(g.asInt()), ";"); //$NON-NLS-1$
	}

	public static CCGenreList deserialize(String v) throws EnumFormatException, GenreOverflowException
	{
		Optional<Long> _g = Optional.of(0x0000000000000000L);
		for (String str : v.split(";")) { //$NON-NLS-1$
			if (!Str.isNullOrWhitespace(str)) _g = calcAddGenre(_g, CCGenre.getWrapper().findOrException(Integer.parseInt(str)));
		}
		if (!_g.isPresent()) throw new GenreOverflowException();

		return new CCGenreList(_g.get());
	}

	public boolean shouldIgnoreBitrateInMediaInfo() {
		for (int i = 0; i < getGenreCount(); i++) {
			if (getGenre(i).shouldIgnoreBitrateInMediaInfo()) return true;
		}

		return false;
	}
}
