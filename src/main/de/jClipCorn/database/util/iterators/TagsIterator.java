package de.jClipCorn.database.util.iterators;

import de.jClipCorn.database.databaseElement.columnTypes.CCSingleTag;
import de.jClipCorn.database.databaseElement.columnTypes.CCTagList;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

public class TagsIterator extends CCSimpleStream<CCSingleTag> {
	private int pos = 0;
	private final CCTagList list;

	public TagsIterator(CCTagList tl) {
		list = tl;
	}

	@Override
	public CCSingleTag nextOrNothing(boolean first) {
		for(;;) {
			if (pos >= CCTagList.ACTIVETAGS) return finishStream();

			if (list.getTag(pos)) { return CCTagList.TAGS[pos++]; }
			pos++;
		}
	}

	@Override
	protected CCStream<CCSingleTag> cloneFresh() {
		return new TagsIterator(list);
	}
}
