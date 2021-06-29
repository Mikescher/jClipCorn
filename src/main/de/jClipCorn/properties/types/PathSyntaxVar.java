package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.FSPath;

public class PathSyntaxVar {
	public final static PathSyntaxVar EMPTY = new PathSyntaxVar(Str.Empty, FSPath.Empty);

	public final String Key;
	public final FSPath Value;

	public PathSyntaxVar(String key, FSPath value) {
		Key = key;
		Value = value;
	}

	@Override
	public String toString() {
		return Key + " := " + Value; //$NON-NLS-1$
	}

	public String serialize() {
		return Str.toBase64(this.Key) + ";" + Str.toBase64(this.Value.toString());
	}
}
