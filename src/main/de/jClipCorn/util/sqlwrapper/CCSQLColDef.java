package de.jClipCorn.util.sqlwrapper;

public class CCSQLColDef {
	public final String Name;
	public final CCSQLType Type;

	public CCSQLColDef(String n, CCSQLType t) {
		Name = n;
		Type = t;
	}

	@Override
	public String toString() {
		return Name+"("+Type+")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}
