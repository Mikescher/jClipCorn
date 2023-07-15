package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.datatypes.Tuple;
import de.jClipCorn.util.filesystem.FSPath;
import de.jClipCorn.util.helper.ApplicationHelper;
import de.jClipCorn.util.stream.CCStreams;

import java.util.Collections;
import java.util.List;

public class BinaryPathVar {

	public static class SingleBinaryPathVar {
		public final String Hostname;
		public final FSPath Path;
		public final String Args;
		public SingleBinaryPathVar(String hostname, FSPath value, String args) {
			Hostname = hostname;
			Path = value;
			Args = args;
		}
	}

	public final List<SingleBinaryPathVar> Values;

	public BinaryPathVar(List<SingleBinaryPathVar> values) {
		Values = Collections.unmodifiableList(values);
	}

	@Override
	public String toString() {
		return CCStreams.iterate(this.Values).map(p -> Str.format("({0}|{1}|{2})", p.Hostname, p.Path, p.Args)).stringjoin(p->p);
	}

	public Tuple<FSPath, String[]> getPathAndArgs() {
		var hostname = ApplicationHelper.getHostname();

		for (var sbpv : Values) {
		    if (Str.isNullOrEmpty(sbpv.Hostname) || sbpv.Hostname.equalsIgnoreCase(hostname)) {
				return Tuple.Create(sbpv.Path, FSPath.splitArguments(sbpv.Args));
			}
		}

		return Tuple.Create(FSPath.Empty, new String[0]);
	}

	public boolean existsAndCanExecute() {
		var v = getPathAndArgs();
		return !FSPath.isNullOrEmpty(v.Item1) && v.Item1.fileExists() && v.Item1.canExecute();
	}

}
