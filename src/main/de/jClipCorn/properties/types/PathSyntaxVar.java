package de.jClipCorn.properties.types;

import de.jClipCorn.util.Str;
import de.jClipCorn.util.filesystem.CCPath;

public class PathSyntaxVar {
	public final static PathSyntaxVar EMPTY = new PathSyntaxVar(Str.Empty, CCPath.Empty);

	public final String Key;
	public final CCPath Value;

	public PathSyntaxVar(String key, CCPath value) {
		Key   = key;
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
