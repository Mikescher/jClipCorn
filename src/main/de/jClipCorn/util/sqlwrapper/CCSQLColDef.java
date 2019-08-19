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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		return isEqual((CCSQLColDef) o);
	}

	@Override
	public int hashCode() {
		int result = Name != null ? Name.hashCode() : 0;
		result = 31 * result + (Type != null ? Type.hashCode() : 0);
		result = 31 * result + (NonNullable ? 1 : 0);
		return result;
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
