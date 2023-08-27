package de.jClipCorn.util.filesystem;

import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.ArrayDeque;
import java.util.Queue;

public class FSPathBreadthFirstIterator extends CCSimpleStream<FSPath> {
	private final FSPath root;
	private final Queue<Tuple<Integer, FSPath>> queue;
	private final Opt<Integer> maxDepth;

	public FSPathBreadthFirstIterator(FSPath root, Opt<Integer> maxDepth) {
		this.root     = root;
		this.maxDepth = maxDepth;
		this.queue    = new ArrayDeque<>();
	}

	@Override
	public FSPath nextOrNothing(boolean first) {

		if (first)
		{
			if (maxDepth.isPresent() && maxDepth.get() < 1) return finishStream();

			var entries = root.list().sortByProperty(FSPath::toAbsolutePathString, String::compareTo).toList();
			for (var e : entries) this.queue.add(Tuple.Create(1, e));
		}

		if (this.queue.isEmpty()) return finishStream();

		var current = this.queue.remove();
		var path = current.Item2;

		if (path.isDirectory() && (maxDepth.isEmpty() || maxDepth.get() > current.Item1))
		{
			var entries = path.list().sortByProperty(FSPath::toAbsolutePathString, String::compareTo).toList();
			for (var e : entries) this.queue.add(Tuple.Create(current.Item1+1, e));
		}

		return path;
	}

	@Override
	protected CCStream<FSPath> cloneFresh() {
		return new FSPathBreadthFirstIterator(root, maxDepth);
	}
}
