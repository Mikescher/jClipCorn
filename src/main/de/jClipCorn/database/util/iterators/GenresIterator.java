package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.columnTypes.CCGenre;
import de.jClipCorn.database.databaseElement.columnTypes.CCGenreList;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class GenresIterator extends CCSimpleStream<CCGenre> {
	private int pos = 0;
	private final CCGenreList list;
	
	public GenresIterator(CCGenreList gl) {
		list = gl;
	}

	@Override
	public CCGenre nextOrNothing(boolean first) {
		for(;;) {
			if (pos >= CCGenreList.getMaxListSize()) return finishStream();
			
			CCGenre g = list.getGenre(pos);
			pos++;
			
			if (g != null && g != CCGenre.GENRE_000) return g;
		}
	}

	@Override
	protected CCStream<CCGenre> cloneFresh() {
		return new GenresIterator(list);
	}
}
