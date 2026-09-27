package de.jClipCorn.properties.types;

import de.jClipCorn.util.filesystem.CCPath;
import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CCPathList {
	public final static CCPathList EMPTY = new CCPathList(new ArrayList<>());

	public final List<CCPath> Values;

	public CCPathList(List<CCPath> values) {
		Values = Collections.unmodifiableList(new ArrayList<>(values));
	}

	@Override
	public String toString() {
		return CCStreams.iterate(Values).stringjoin(CCPath::toString, "; "); //$NON-NLS-1$
	}
}
