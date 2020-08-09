package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.formatter.PathFormatter;

public class NamedPathVar {
	public final static NamedPathVar EMPTY = new NamedPathVar(Str.Empty, Str.Empty, Str.Empty);

	public final String Name;
	public final String Path;
	public final String Arguments;

	public NamedPathVar(String name, String path, String args) {
		Name      = name;
		Path      = path;
		Arguments = args;
	}

	@Override
	public String toString() {
		return "[" + Name + "] := " + Path + " (" + Arguments + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public boolean isEmpty() {
		return Str.isNullOrWhitespace(Path);
	}

	public String getCaption() {
		if (Str.isNullOrWhitespace(Name)) return PathFormatter.getFilenameWithExt(Path); else return Name;
	}
}
