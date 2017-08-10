package de.jClipCorn.database.databaseElement.columnTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.jClipCorn.database.util.iterators.GenresIterator;
import de.jClipCorn.util.stream.CCStream;

public class CCGenreList {
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
	
	private long genres;
	
	public CCGenreList() {
		genres = 0x0000000000000000L;
	}
	
	public CCGenreList(long lrep) {
		genres = lrep;
	}
	
	public CCGenreList(CCGenreList l) {
		genres = l.getAllGenres();
	}
	
	public CCGenreList(CCGenre genre1) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
		addGenre(genre6);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6, CCGenre genre7) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
		addGenre(genre6);
		addGenre(genre7);
	}
	
	public CCGenreList(CCGenre genre1, CCGenre genre2, CCGenre genre3, CCGenre genre4, CCGenre genre5, CCGenre genre6, CCGenre genre7, CCGenre genre8) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
		addGenre(genre6);
		addGenre(genre7);
		addGenre(genre8);
	}

	public CCGenreList(List<CCGenre> data) {
		genres = 0x0000000000000000L;
		
		for (CCGenre g : data) addGenre(g);
	}

	private int getGenreInt(int idx) {
		return (int) ((genres & MASK[idx]) >> idx*GSIZE);
	}
	
	private void setGenreInt(int idx, int val) {
		genres = (genres & ~MASK[idx]) | ((val*1L) << (idx*GSIZE));
	}

	public CCGenre getGenre(int idx) {
		if (idx >= 0 && idx < SIZE) {
			return CCGenre.getWrapper().find(getGenreInt(idx));
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
	
	public boolean setGenre(int idx, CCGenre val) {
		return setGenre(idx, val.asInt());
	}
	
	public boolean setGenre(int idx, int val) {
		if (idx >= 0 && idx < SIZE) {
			setGenreInt(idx, val);
			return true;
		} else {
			return false;
		}
	}
	
	public int getGenreCount() {
		int i = 0;
		while(i < SIZE) {
			if (getGenreInt(i) == CCGenre.NO_GENRE) return i;
			i++;
		}
		return i;
	}
	
	public boolean addGenre(CCGenre val) {
		return addGenre(val.asInt());
	}
	
	public boolean addGenre(int val) {
		int size = getGenreCount();
		if (size < GSIZE) {
			setGenre(size, val);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean removeGenre(int idx) {
		if (idx >= 0 && idx < SIZE) {
			idx++;
			while(idx < SIZE) {
				setGenreInt(idx-1, getGenreInt(idx));
				idx++;
			}
			setGenreInt(idx - 1, CCGenre.NO_GENRE);
			return true;
		} else {
			return false;
		}
	}
	
	public String asString() {
		return getGenres().stream().map(g -> g.asString()).collect(Collectors.joining("|")); //$NON-NLS-1$
	}
	
	/*
	 * This is not the same as x.asSorted().asString()
	 * 
	 * asSorted sorts based on the ID, this sorts based on translation
	 */
	public String asSortedString() {
		return getGenres().stream().sorted(CCGenre.getTextComparator()).map(g -> g.asString()).collect(Collectors.joining("|")); //$NON-NLS-1$
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
		CCGenreList result = new CCGenreList();
		
		int count = getGenreCount();
		
		int actual = getGenre(0).asInt();
		
		for (int i = 0; i < count; i++) {
			if (getGenre(i).asInt() < actual) {
				actual = getGenre(i).asInt();
			}
		}
		
		result.addGenre(actual);
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
			result.addGenre(actual);
			last = actual;
		}
		
		return result;
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

	public void clear() {
		genres = 0;
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
	public void set(CCGenreList other) {
		genres = other.genres;
	}

	public boolean equals(CCGenreList other) {
		return asSimpleComparableString().equals(other.asSimpleComparableString());
	}
}
