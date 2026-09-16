package de.jClipCorn.properties.types;

import de.jClipCorn.util.stream.CCStreams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathSyntaxVarList {
	public final static PathSyntaxVarList EMPTY = new PathSyntaxVarList(new ArrayList<>());

	public final List<PathSyntaxVar> Values;

	public PathSyntaxVarList(List<PathSyntaxVar> values) {
		Values = Collections.unmodifiableList(new ArrayList<>(values));
	}

	@Override
	public String toString() {
		return CCStreams.iterate(Values).stringjoin(PathSyntaxVar::toString, "; "); //$NON-NLS-1$
	}
}
