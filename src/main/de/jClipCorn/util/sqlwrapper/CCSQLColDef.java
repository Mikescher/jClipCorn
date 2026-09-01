package de.jClipCorn.util.sqlwrapper;

import de.jClipCorn.util.Str;

public class CCSQLColDef {
	public final String Name;
	public final CCSQLType Type;
	public final boolean NonNullable;

	public CCSQLColDef(String n, CCSQLType t, boolean r) {
		Name        = n;
		Type        = t;
		NonNullable = r;
	}

	public boolean isNullable() {
		return !NonNullable;
	}

	// identity, not value: the same column name exists in both database files (main.MOVIES.ID and
	// userdata.MOVIES.ID) and a joined statement has to keep the two apart. Use isEqual() to compare
	// two definitions by value.

	@Override
	public boolean equals(Object o) {
		return this == o;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public String toString() {
		return Name+"("+Type+")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean isEqual(CCSQLColDef other) {
		if (other == null) return false;
		if (!Str.equals(Name, other.Name)) return false;
		if (Type != other.Type) return false;
		if (NonNullable != other.NonNullable) return false;

		return true;
	}
}
