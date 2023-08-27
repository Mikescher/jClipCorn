package de.jClipCorn.util.filesystem;

import de.jClipCorn.util.datatypes.Opt;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.stream.CCSimpleStream;
import de.jClipCorn.util.stream.CCStream;

import java.util.List;
import java.util.Stack;

public class FSPathDepthFirstIterator extends CCSimpleStream<FSPath> {
	private final FSPath root;
	private final Stack<Tuple<Integer, List<FSPath>>> stack;
	private final Opt<Integer> maxDepth;

	public FSPathDepthFirstIterator(FSPath root, Opt<Integer> maxDepth) {
		this.root     = root;
		this.maxDepth = maxDepth;
		this.stack    = new Stack<>();
	}

	@Override
	public FSPath nextOrNothing(boolean first) {

		if (first)
		{
			if (maxDepth.isPresent() && maxDepth.get() < 1) return finishStream();

			var entries = root.list().sortByProperty(FSPath::toAbsolutePathString, String::compareTo).toList();
			this.stack.push(Tuple.Create(1, entries));
		}

		while (true)
		{
			if (this.stack.empty()) return finishStream();

			var current = this.stack.peek();
			if (current.Item2.isEmpty()) { this.stack.pop(); continue; }

			var path = current.Item2.remove(0);
			if (path.isDirectory() && (maxDepth.isEmpty() || maxDepth.get() > current.Item1))
			{
				var entries = path.list().sortByProperty(FSPath::toAbsolutePathString, String::compareTo).toList();
				this.stack.push(Tuple.Create(current.Item1+1, entries));
			}

			return path;
		}
	}

	@Override
	protected CCStream<FSPath> cloneFresh() {
		return new FSPathDepthFirstIterator(root, maxDepth);
	}
}
