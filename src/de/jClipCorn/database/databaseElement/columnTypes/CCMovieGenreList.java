package de.jClipCorn.database.databaseElement.columnTypes;

public class CCMovieGenreList {
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
	
	public CCMovieGenreList() {
		genres = 0x0000000000000000L;
	}
	
	public CCMovieGenreList(long lrep) {
		genres = lrep;
	}
	
	public CCMovieGenreList(CCMovieGenreList l) {
		genres = l.getAllGenres();
	}
	
	public CCMovieGenreList(CCMovieGenre genre1) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3, CCMovieGenre genre4) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3, CCMovieGenre genre4, CCMovieGenre genre5) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3, CCMovieGenre genre4, CCMovieGenre genre5, CCMovieGenre genre6) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
		addGenre(genre6);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3, CCMovieGenre genre4, CCMovieGenre genre5, CCMovieGenre genre6, CCMovieGenre genre7) {
		genres = 0x0000000000000000L;
		addGenre(genre1);
		addGenre(genre2);
		addGenre(genre3);
		addGenre(genre4);
		addGenre(genre5);
		addGenre(genre6);
		addGenre(genre7);
	}
	
	public CCMovieGenreList(CCMovieGenre genre1, CCMovieGenre genre2, CCMovieGenre genre3, CCMovieGenre genre4, CCMovieGenre genre5, CCMovieGenre genre6, CCMovieGenre genre7, CCMovieGenre genre8) {
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

	private int getGenreInt(int idx) {
		return (int) ((genres & MASK[idx]) >> idx*GSIZE);
	}
	
	private void setGenreInt(int idx, int val) {
		genres = (genres & ~MASK[idx]) | ((val*1l) << (idx*GSIZE));
	}

	public CCMovieGenre getGenre(int idx) {
		if (idx >= 0 && idx < SIZE) {
			return CCMovieGenre.find(getGenreInt(idx));
		} else {
			return null;
		}
	}
	
	public boolean setGenre(int idx, CCMovieGenre val) {
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
			if (getGenreInt(i) == CCMovieGenre.NO_GENRE) return i;
			i++;
		}
		return i;
	}
	
	public boolean addGenre(CCMovieGenre val) {
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
			setGenreInt(idx - 1, CCMovieGenre.NO_GENRE);
			return true;
		} else {
			return false;
		}
	}
	
	public String asString() {
		String r = ""; //$NON-NLS-1$
		int sz = getGenreCount();
		for(int i = 0; i < sz; i++) {
			r += getGenre(i).asString();
			if ((i+1) < sz) {
				r += '|';
			}
		}
		return r;
	}
	
	public String asSimpleString() {
		String r = ""; //$NON-NLS-1$
		int sz = getGenreCount();
		for(int i = 0; i < sz; i++) {
			r += getGenre(i)+""; //$NON-NLS-1$
			if ((i+1) < sz) {
				r += '|';
			}
		}
		return r;
	}
	
	public CCMovieGenreList getSorted() {
		CCMovieGenreList result = new CCMovieGenreList();
		
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

	public static int compare(CCMovieGenreList o1, CCMovieGenreList o2) {
		return Long.compare(o1.getSorted().getAllGenres(), o2.getSorted().getAllGenres());
	}

	public void clear() {
		genres = 0;
	}
	
	public static int getMaxListSize() {
		return SIZE;
	}
}
