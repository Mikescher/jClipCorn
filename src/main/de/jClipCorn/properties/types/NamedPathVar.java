package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;

public class NamedPathVar {
	public final static NamedPathVar EMPTY = new NamedPathVar(Str.Empty, FSPath.Empty, Str.Empty);

	public final String Name;
	public final FSPath Path;
	public final String Arguments;

	public NamedPathVar(String name, FSPath path, String args) {
		Name      = name;
		Path      = path;
		Arguments = args;
	}

	@Override
	public String toString() {
		return "[" + Name + "] := " + Path + " (" + Arguments + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	public boolean isEmpty() {
		return Path.isEmpty();
	}

	public String getCaption() {
		if (Str.isNullOrWhitespace(Name)) return Path.getFilenameWithExt(); else return Name;
	}

	public String serialize() {
		return Str.toBase64(" " + this.Name + " ") + ";" + Str.toBase64(" " + this.Path.toString() + " ") + ";" + Str.toBase64(" " + this.Arguments + " ");
	}
}
